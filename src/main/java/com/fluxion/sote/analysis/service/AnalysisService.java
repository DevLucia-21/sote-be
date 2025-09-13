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
import com.fluxion.sote.global.util.BirthYearUtil;
import com.fluxion.sote.global.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AnalysisService {

    private final RestTemplate aiRestTemplate;
    private final AiClientProperties props;
    private final AnalysisRepository analysisRepo;
    private final AnalysisResultRepository resultRepo;
    private final DiaryRepository diaryRepo;
    private final ObjectMapper om = new ObjectMapper();

    public AnalysisService(
            @Qualifier("aiRestTemplate") RestTemplate aiRestTemplate,
            AiClientProperties props,
            AnalysisRepository analysisRepo,
            AnalysisResultRepository resultRepo,
            DiaryRepository diaryRepo
    ) {
        this.aiRestTemplate = aiRestTemplate;
        this.props = props;
        this.analysisRepo = analysisRepo;
        this.resultRepo = resultRepo;
        this.diaryRepo = diaryRepo;
    }

    /**
     * 일기 기반 감정분석 실행
     * - 오늘 일기: 챌린지/보상 연계
     * - 과거 일기: 분석 결과 저장만 수행
     */
    public AnalysisResponse run(AnalysisRequest req) {
        User user = SecurityUtil.getCurrentUser();
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDate today = LocalDate.now(KST);

        // ✅ Diary 반드시 조회
        if (req.getDiaryId() == null) {
            return AnalysisResponse.error("DiaryId가 필요합니다.");
        }
        Diary diary = diaryRepo.findById(req.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        LocalDate targetDate = diary.getDate();

        // ================== 과거 일기 처리 ==================
        if (!targetDate.isEqual(today)) {
            Analysis a = getOrCreateAnalysis(user, diary, targetDate);
            if (a.getResult() != null) {
                return AnalysisResponse.error("해당 날짜는 이미 분석이 완료되었습니다.");
            }

            Map<String, Object> body = callAiForAnalysis(user, a, req);
            AnalysisResult r = mapResultFromBody(a, body);
            resultRepo.save(r);

            // ✅ selectedTrack / top_track 포함된 상태로 반환
            return new AnalysisResponse("ok", "past_diary_analysis_only", body);
        }

        // ================== 오늘 일기 처리 ==================
        Analysis a = getOrCreateAnalysis(user, diary, today);
        if (a.getResult() != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("code", "ALREADY_ANALYZED_TODAY");
            data.put("message", "오늘 분석은 이미 완료되었습니다.");
            return new AnalysisResponse("error", "ALREADY_ANALYZED_TODAY", data);
        }

        Map<String, Object> body = callAiForAnalysis(user, a, req);
        AnalysisResult r = mapResultFromBody(a, body);
        resultRepo.save(r);

        // ✅ 오늘 일기 → 이후 ChallengeRecommendService에서 사용
        return new AnalysisResponse("ok", "success", body);
    }

    // ================== 헬퍼 메서드 ==================

    /**
     * Analysis 엔티티 조회 또는 생성
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
     * AI 서버 호출하여 분석 결과 수신
     */
    private Map<String, Object> callAiForAnalysis(User user, Analysis analysis, AnalysisRequest req) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", req != null ? req.getText() : null);
        payload.put("year", analysis.getBirthYear());
        payload.put("user_preferred_genres", user.getMusicPreferences().stream()
                .map(Genre::getName)
                .toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String url = props.getBaseUrl() + props.getEndpoint();

        ResponseEntity<Map<String, Object>> resp = aiRestTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                new ParameterizedTypeReference<>() {}
        );

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("AI 응답 실패: " + resp.getStatusCode());
        }

        Map<String, Object> body = resp.getBody();

        // ✅ 후보곡 중 하나를 selectedTrack/top_track으로 지정
        attachSelectedTrack(body, user);

        return body;
    }

    /**
     * AI 응답에서 랜덤 1곡을 selectedTrack/top_track으로 설정
     */
    private void attachSelectedTrack(Map<String, Object> body, User user) {
        Object musicObj = body.get("music");
        if (!(musicObj instanceof List<?> list) || list.isEmpty()) return;

        // 최근 3일간 곡 키 조회
        List<String> recentKeys = resultRepo.findRecentSelectedKeys(user.getId());
        Set<String> recentSet = new HashSet<>(recentKeys == null ? List.of() : recentKeys);

        // 후보 필터링 (최근 곡 제외)
        List<Map<String, Object>> pool = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof Map<?, ?> m) {
                String key = (Objects.toString(m.get("title"), "") + "||"
                        + Objects.toString(m.get("artist"), "")).toLowerCase(Locale.ROOT);
                if (!recentSet.contains(key)) {
                    // ✅ 타입 캐스팅 후 새 HashMap으로 추가
                    pool.add(new HashMap<>((Map<String, Object>) m));
                }
            }
        }
        if (pool.isEmpty()) {
            pool = (List<Map<String, Object>>) (List<?>) list; // fallback
        }

        // 랜덤 선택
        int pick = ThreadLocalRandom.current().nextInt(pool.size());
        Map<String, Object> selectedTrack = pool.get(pick);

        // 원본 인덱스 찾기
        int originalIdx = -1;
        for (int i = 0; i < list.size(); i++) {
            Map<?, ?> candidate = (Map<?, ?>) list.get(i);
            if (Objects.equals(candidate.get("title"), selectedTrack.get("title"))
                    && Objects.equals(candidate.get("artist"), selectedTrack.get("artist"))) {
                originalIdx = i;
                break;
            }
        }

        // 응답 body에 저장
        body.put("selectedTrack", selectedTrack);
        body.put("selectedTrackIndex", originalIdx >= 0 ? originalIdx : pick);
        body.putIfAbsent("top_track", selectedTrack);
    }

    /**
     * AI 응답(body)을 AnalysisResult 엔티티로 변환
     */
    private AnalysisResult mapResultFromBody(Analysis analysis, Map<String, Object> body) {
        AnalysisResult r = new AnalysisResult();
        r.setAnalysis(analysis);

        // 감정 결과
        Object emoObj = body.get("emotion");
        if (emoObj instanceof Map<?, ?> emo) {
            r.setEmotionLabel(Objects.toString(emo.get("label"), null));
            Object score = emo.get("score");
            if (score instanceof Number n) {
                r.setEmotionScore(BigDecimal.valueOf(n.doubleValue()).setScale(4, RoundingMode.HALF_UP));
            }
            r.setEmotionReason(Objects.toString(emo.get("reason"), null));
        }

        // 음악 전체 후보 JSON 저장
        Object musicObj = body.get("music");
        if (musicObj != null) {
            try {
                r.setMusicJson(om.writeValueAsString(musicObj));
            } catch (Exception ignore) {}
        }

        // AI 원본 응답 저장 (selectedTrack 제외)
        try {
            Map<String, Object> toPersist = new HashMap<>(body);
            toPersist.remove("selectedTrack");
            toPersist.remove("selectedTrackIndex");
            r.setAiResponse(om.writeValueAsString(toPersist));
        } catch (Exception ignore) {}

        // 선택된 곡 저장
        Object selectedTrack = body.get("selectedTrack");
        if (selectedTrack instanceof Map<?, ?> track) {
            r.setSelectedTrackTitle(Objects.toString(track.get("title"), null));
            r.setSelectedTrackArtist(Objects.toString(track.get("artist"), null));
            r.setSelectedTrackAlbum(Objects.toString(track.get("album"), null));
            r.setSelectedTrackGenre(Objects.toString(track.get("genre"), null));

            Object idx = body.get("selectedTrackIndex");
            if (idx instanceof Number n) {
                r.setSelectedTrackIndex(n.intValue());
            }
        }

        return r;
    }
}
