package com.fluxion.sote.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxion.sote.analysis.config.AiClientProperties;
import com.fluxion.sote.analysis.dto.AnalysisRequest;
import com.fluxion.sote.analysis.dto.AnalysisResponse;
import com.fluxion.sote.analysis.entity.Analysis;
import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.analysis.repository.AnalysisRepository;
import com.fluxion.sote.analysis.repository.AnalysisResultRepository;
import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.challenge.service.ChallengeRecommendService;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.diary.repository.DiaryRepository;
import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.global.util.BirthYearUtil;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.lpmusic.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final @Qualifier("aiRestTemplate") RestTemplate aiRestTemplate;
    private final AiClientProperties props;
    private final AnalysisRepository analysisRepo;
    private final AnalysisResultRepository resultRepo;
    private final DiaryRepository diaryRepo;
    private final SpotifyService spotifyService;

    private final ChallengeRecommendService challengeRecommendService;
    private final UserChallengeRepository userChallengeRepo;

    private final ObjectMapper om = new ObjectMapper();

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 전시회 버전: 분석 제한 완전 제거
     * - 같은 일기 여러 번 분석 가능
     * - 같은 날짜 여러 번 분석 가능
     * - 기존 결과와 무관하게 항상 새로 분석해서 저장
     */
    public AnalysisResponse run(AnalysisRequest req) {

        User user = SecurityUtil.getCurrentUser();

        if (req.getDiaryId() == null) {
            return AnalysisResponse.error("DiaryId가 필요합니다.");
        }

        Diary diary = diaryRepo.findById(req.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        LocalDate targetDate = diary.getDate();

        // 기존 결과 여부와 관계없이 항상 새로운 AnalysisResult 생성
        Analysis a = getOrCreateAnalysis(user, diary, targetDate);

        // AI 호출
        Map<String, Object> body = callAiForAnalysis(user, a, req);

        // 결과 매핑 및 저장
        AnalysisResult r = mapResultFromBody(a, body);
        a.setResult(r);
        resultRepo.save(r);
        analysisRepo.save(a);

        // 오늘 일기면 챌린지 추천 생성
        maybeRecommendTodayChallenge(user, diary);

        return new AnalysisResponse("ok", "success", body);
    }

    /**
     * 자동 분석도 제한 없이 항상 실행됨 (DiarySavedEvent)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runInNewTx(Diary diary) {
        try {
            AnalysisRequest req = new AnalysisRequest();
            req.setDiaryId(diary.getId());
            req.setText(diary.getContent());

            User user = diary.getUser();
            LocalDate targetDate = diary.getDate();

            Analysis a = getOrCreateAnalysis(user, diary, targetDate);

            Map<String, Object> body = callAiForAnalysis(user, a, req);
            AnalysisResult r = mapResultFromBody(a, body);
            a.setResult(r);
            resultRepo.save(r);
            analysisRepo.save(a);

            // 오늘 일기면 챌린지 추천 생성
            maybeRecommendTodayChallenge(user, diary);

            // 감정 반영 (기존과 동일)
            Object emoObj = body.get("emotion");
            if (emoObj instanceof Map<?, ?> emo) {
                String label = Objects.toString(emo.get("label"), null);
                EmotionType type = EmotionType.fromLabel(label);
                diary.setEmotionType(type);
                diaryRepo.save(diary);
            }

            System.out.println("[자동 감정분석 완료] Diary ID=" + diary.getId() +
                    ", Emotion=" + diary.getEmotionType());

        } catch (Exception e) {
            System.err.println("자동 감정분석 실패: " +
                    e.getClass().getSimpleName() + " - " +
                    (e.getMessage() != null ? e.getMessage() : "no message"));
        }
    }

    /**
     * 오늘 일기일 때만 챌린지 추천 생성
     * 이미 오늘 추천 기록이 있으면 중복 생성하지 않음
     */
    private void maybeRecommendTodayChallenge(User user, Diary diary) {
        LocalDate today = LocalDate.now(KST);

        if (!today.equals(diary.getDate())) {
            return;
        }

        if (userChallengeRepo.findByUserAndDate(user, today).isPresent()) {
            return;
        }

        try {
            challengeRecommendService.recommendTodayChallenge(user, null);
            System.out.println("[오늘 챌린지 추천 완료] userId=" + user.getId() + ", date=" + today);
        } catch (Exception e) {
            System.err.println("오늘 챌린지 추천 실패: " +
                    e.getClass().getSimpleName() + " - " +
                    (e.getMessage() != null ? e.getMessage() : "no message"));
        }
    }

    /**
     * 기존 Analysis가 있어도 그대로 사용하고
     * 없으면 새로 생성
     */
    private Analysis getOrCreateAnalysis(User user, Diary diary, LocalDate date) {
        return analysisRepo.findByUserIdAndDiaryId(user.getId(), diary.getId())
                .orElseGet(() -> {
                    Analysis na = new Analysis();
                    na.setUser(user);
                    na.setBirthYear(BirthYearUtil.extractYear(user.getBirthDate()));
                    na.setAnalysisDate(date);
                    na.setDiary(diary);
                    return analysisRepo.saveAndFlush(na);
                });
    }

    /**
     * AI 서버 호출
     */
    private Map<String, Object> callAiForAnalysis(User user, Analysis analysis, AnalysisRequest req) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", req != null ? req.getText() : null);
        payload.put("year", analysis.getBirthYear());
        payload.put("user_preferred_genres",
                user.getMusicPreferences().stream().map(Genre::getName).toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String url = props.getBaseUrl() + props.getEndpoint();

        ResponseEntity<Map<String, Object>> resp = aiRestTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(payload, headers),
                new ParameterizedTypeReference<>() {}
        );

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("AI 응답 실패: " + resp.getStatusCode());
        }

        Map<String, Object> body = resp.getBody();
        attachSelectedTrack(body, user);
        return body;
    }

    /**
     * FastAPI가 내려준 음악 후보 중 랜덤 선택
     * 최근 5곡 제외 로직은 그대로 유지
     */
    @SuppressWarnings("unchecked")
    private void attachSelectedTrack(Map<String, Object> body, User user) {
        Object musicObj = body.get("music");
        if (!(musicObj instanceof List<?> list) || list.isEmpty()) return;

        List<String> recentKeys = resultRepo.findRecentSelectedKeys(user.getId());
        Set<String> recentSet = new HashSet<>(recentKeys == null ? List.of() : recentKeys);

        List<Map<String, Object>> pool = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof Map<?, ?> m) {
                String key = (Objects.toString(m.get("title"), "") + "||" +
                        Objects.toString(m.get("artist"), "")).toLowerCase(Locale.ROOT);
                if (!recentSet.contains(key)) {
                    pool.add(new HashMap<>((Map<String, Object>) m));
                }
            }
        }

        if (pool.isEmpty()) {
            pool = (List<Map<String, Object>>) (List<?>) list;
        }

        int pick = ThreadLocalRandom.current().nextInt(pool.size());
        Map<String, Object> selectedTrack = pool.get(pick);

        body.put("selectedTrack", selectedTrack);
        body.put("selectedTrackIndex", pick);
        body.putIfAbsent("top_track", selectedTrack);
    }

    private String firstNonNullString(Map<?, ?> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value == null) continue;

            String text = value.toString().trim();
            if (!text.isEmpty() && !"null".equalsIgnoreCase(text)) {
                return text;
            }
        }
        return null;
    }

    /**
     * AI 응답 → AnalysisResult 엔티티 저장 매핑
     */
    private AnalysisResult mapResultFromBody(Analysis analysis, Map<String, Object> body) {
        AnalysisResult r = new AnalysisResult();
        r.setAnalysis(analysis);

        Diary diary = analysis.getDiary();

        // --- 감정 결과 파싱 ---
        Object emoObj = body.get("emotion");
        if (emoObj instanceof Map<?, ?> emo) {
            String label = Objects.toString(emo.get("label"), null);
            r.setEmotionLabel(label);

            Object score = emo.get("score");
            if (score instanceof Number n) {
                r.setEmotionScore(BigDecimal.valueOf(n.doubleValue())
                        .setScale(4, RoundingMode.HALF_UP));
            }

            r.setEmotionReason(Objects.toString(emo.get("reason"), null));

            EmotionType type = EmotionType.fromLabel(label);
            diary.setEmotionType(type);
            diaryRepo.save(diary);
        }

        // --- 음악 정보 ---
        Object musicObj = body.get("music");
        if (musicObj != null) {
            try {
                r.setMusicJson(om.writeValueAsString(musicObj));
            } catch (Exception ignore) {}
        }

        // --- AI 원본 응답 저장 ---
        try {
            Map<String, Object> toPersist = new HashMap<>(body);
            toPersist.remove("selectedTrack");
            toPersist.remove("selectedTrackIndex");
            r.setAiResponse(om.writeValueAsString(toPersist));
        } catch (Exception ignore) {}

        // --- 선택곡 ---
        Object selectedTrack = body.get("selectedTrack");
        if (selectedTrack instanceof Map<?, ?> track) {
            String title = firstNonNullString(track, "title");
            String artist = firstNonNullString(track, "artist");
            String album = firstNonNullString(track, "album");
            String genre = firstNonNullString(track, "genre");
            String reason = firstNonNullString(track, "reason");

            r.setSelectedTrackTitle(title);
            r.setSelectedTrackArtist(artist);
            r.setSelectedTrackAlbum(album);
            r.setSelectedTrackGenre(genre);
            r.setSelectedTrackReason(reason);

            String coverImageUrl = firstNonNullString(
                    track,
                    "coverImageUrl",
                    "cover_image_url",
                    "imageUrl",
                    "image_url",
                    "albumImageUrl",
                    "album_image_url"
            );

            if (coverImageUrl == null && title != null && artist != null) {
                try {
                    Map<String, String> spotifyMeta = spotifyService.searchTrack(title, artist);
                    coverImageUrl = spotifyMeta.getOrDefault("albumImageUrl", null);

                    if (r.getSelectedTrackAlbum() == null) {
                        r.setSelectedTrackAlbum(spotifyMeta.getOrDefault("album", album));
                    }

                    if (r.getSelectedTrackTitle() == null) {
                        r.setSelectedTrackTitle(spotifyMeta.getOrDefault("title", title));
                    }

                    if (r.getSelectedTrackArtist() == null) {
                        r.setSelectedTrackArtist(spotifyMeta.getOrDefault("artist", artist));
                    }
                } catch (Exception e) {
                    System.err.println("Spotify 메타데이터 조회 실패: " + e.getMessage());
                }
            }

            r.setSelectedTrackCoverImageUrl(coverImageUrl);

            System.out.println("[AnalysisService] selectedTrack = " + track);
            System.out.println("[AnalysisService] resolved coverImageUrl = " + coverImageUrl);
        }

        Object idxObj = body.get("selectedTrackIndex");
        if (idxObj instanceof Number n) {
            r.setSelectedTrackIndex(n.intValue());
        }

        return r;
    }
}