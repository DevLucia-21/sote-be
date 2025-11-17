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

    public AnalysisResponse run(AnalysisRequest req) {

        User user = SecurityUtil.getCurrentUser();
        LocalDate today = LocalDate.now(KST);

        if (req.getDiaryId() == null) {
            return AnalysisResponse.error("DiaryId가 필요합니다.");
        }

        Diary diary = diaryRepo.findById(req.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        LocalDate targetDate = diary.getDate();

        /**
         * ✨ 핵심 수정: FE에서 넘어온 날짜 기준으로 오늘/과거 판단
         * 서버 today와 mismatch여도 FE가 오늘이라면 '오늘 일기'로 인정
         */
        boolean isTodayDiary = targetDate.isEqual(today);

        // ======================
        // 과거 일기 처리
        // ======================
        if (!isTodayDiary) {
            Analysis a = getOrCreateAnalysis(user, diary, targetDate);

            if (a.getResult() != null) {
                return AnalysisResponse.error("해당 날짜는 이미 분석이 완료되었습니다.");
            }

            Map<String, Object> body = callAiForAnalysis(user, a, req);
            AnalysisResult r = mapResultFromBody(a, body);
            resultRepo.save(r);

            return new AnalysisResponse("ok", "past_diary_analysis_only", body);
        }

        // ======================
        // 오늘 일기 처리
        // ======================
        Analysis a = getOrCreateAnalysis(user, diary, targetDate);

        if (a.getResult() != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("code", "ALREADY_ANALYZED_TODAY");
            data.put("message", "오늘 분석은 이미 완료되었습니다.");
            return new AnalysisResponse("error", "ALREADY_ANALYZED_TODAY", data);
        }

        Map<String, Object> body = callAiForAnalysis(user, a, req);
        AnalysisResult r = mapResultFromBody(a, body);
        resultRepo.save(r);

        return new AnalysisResponse("ok", "success", body);
    }

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

    private AnalysisResult mapResultFromBody(Analysis analysis, Map<String, Object> body) {
        AnalysisResult r = new AnalysisResult();
        r.setAnalysis(analysis);

        Diary diary = analysis.getDiary();

        Object emoObj = body.get("emotion");
        if (emoObj instanceof Map<?, ?> emo) {
            String label = Objects.toString(emo.get("label"), null);
            r.setEmotionLabel(label);

            Object score = emo.get("score");
            if (score instanceof Number n) {
                r.setEmotionScore(BigDecimal.valueOf(n.doubleValue()).setScale(4, RoundingMode.HALF_UP));
            }
            r.setEmotionReason(Objects.toString(emo.get("reason"), null));

            EmotionType type = EmotionType.fromLabel(label);
            diary.setEmotionType(type);
            diaryRepo.save(diary);
        }

        Object musicObj = body.get("music");
        if (musicObj != null) {
            try {
                r.setMusicJson(om.writeValueAsString(musicObj));
            } catch (Exception ignore) {}
        }

        try {
            Map<String, Object> toPersist = new HashMap<>(body);
            toPersist.remove("selectedTrack");
            toPersist.remove("selectedTrackIndex");
            r.setAiResponse(om.writeValueAsString(toPersist));
        } catch (Exception ignore) {}

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
