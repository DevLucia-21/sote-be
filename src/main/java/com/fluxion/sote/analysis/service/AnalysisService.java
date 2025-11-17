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
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.diary.repository.DiaryRepository;
import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.global.util.BirthYearUtil;
import com.fluxion.sote.global.util.SecurityUtil;
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
        LocalDate today = LocalDate.now(KST);

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

        // 결과 매핑 및 저장 (중복이어도 계속 저장됨)
        AnalysisResult r = mapResultFromBody(a, body);
        resultRepo.save(r);

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
            resultRepo.save(r);

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
     * 기존 Analysis가 있어도 그대로 사용하고
     * 없으면 새로 생성
     */
    private Analysis getOrCreateAnalysis(User user, Diary diary, LocalDate date) {
        return analysisRepo.findByUserAndAnalysisDate(user, date)
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
            r.setSelectedTrackTitle(Objects.toString(track.get("title"), null));
            r.setSelectedTrackArtist(Objects.toString(track.get("artist"), null));
            r.setSelectedTrackAlbum(Objects.toString(track.get("album"), null));
            r.setSelectedTrackGenre(Objects.toString(track.get("genre"), null));
            r.setSelectedTrackReason(Objects.toString(track.get("reason"), null));
            r.setSelectedTrackCoverImageUrl(Objects.toString(track.get("coverImageUrl"), null));
        }

        Object idxObj = body.get("selectedTrackIndex");
        if (idxObj instanceof Number n) {
            r.setSelectedTrackIndex(n.intValue());
        }

        return r;
    }
}
