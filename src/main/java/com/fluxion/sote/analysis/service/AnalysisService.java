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
import com.fluxion.sote.global.util.BirthYearUtil;
import com.fluxion.sote.global.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final ObjectMapper om = new ObjectMapper();

    public AnalysisService(@Qualifier("aiRestTemplate") RestTemplate aiRestTemplate,
                           AiClientProperties props,
                           AnalysisRepository analysisRepo,
                           AnalysisResultRepository resultRepo,
                           ObjectMapper objectMapper) {
        this.aiRestTemplate = aiRestTemplate;
        this.props = props;
        this.analysisRepo = analysisRepo;
        this.resultRepo = resultRepo;
    }

    public AnalysisResponse run(AnalysisRequest req) {
        User user = SecurityUtil.getCurrentUser();
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDate today = LocalDate.now(KST);

        // 1) 분석 메타 저장 (하루 1회: (user_id, analysis_date) UNIQUE)
        Analysis a = new Analysis();
        a.setUser(user);
        a.setBirthYear(BirthYearUtil.extractYear(user.getBirthDate()));
        a.setAnalysisDate(today);

        try {
            a = analysisRepo.saveAndFlush(a);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> data = new HashMap<>();
            data.put("code", "ALREADY_ANALYZED_TODAY");
            data.put("message", "오늘 분석은 이미 완료되었습니다.");
            return new AnalysisResponse("error", "ALREADY_ANALYZED_TODAY", data);
        }

        // 2) AI 호출용 payload 구성
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", req != null ? req.getText() : null);
        payload.put("year", a.getBirthYear());  // DB에서 가져온 출생연도

        // 사용자의 선호 장르를 DB에서 조회
        List<String> preferredGenres = user.getMusicPreferences().stream()
                .map(Genre::getName)   // Genre 엔티티의 name 필드
                .toList();
        payload.put("user_preferred_genres", preferredGenres);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        String url = props.getBaseUrl() + props.getEndpoint();

        Map<String, Object> body;
        ResponseEntity<Map<String, Object>> resp;
        try {
            resp = aiRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            body = resp.getBody();
            if (!resp.getStatusCode().is2xxSuccessful() || body == null) {
                return new AnalysisResponse("error", "AI returned " + resp.getStatusCode(), body);
            }
        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("exception", ex.getClass().getSimpleName());
            err.put("message", ex.getMessage());
            return new AnalysisResponse("error", "AI call failed", err);
        }

        // 0) 최근 3일(오늘 제외) 선정곡 키 집합
        List<String> recentKeys = resultRepo.findRecentSelectedKeys(user.getId());
        Set<String> recentSet = new HashSet<>(recentKeys == null ? List.of() : recentKeys);

        // 1) 후보 파싱
        Object musicObj = body.get("music");
        List<Map<String, Object>> music = null;
        Map<String, Integer> originalIndexMap = null;

        if (musicObj instanceof List<?> list) {
            music = new ArrayList<>();
            originalIndexMap = new HashMap<>();
            int idx = 0;
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    Map<String, Object> t = new HashMap<>();
                    t.put("title", m.get("title"));
                    t.put("artist", m.get("artist"));
                    t.put("album", m.get("album"));
                    t.put("genre", m.get("genre"));
                    t.put("tempo", m.get("tempo"));
                    t.put("mood", m.get("mood"));
                    t.put("reason", m.get("reason"));
                    music.add(t);

                    String key = (Objects.toString(t.get("title"), "") + "||"
                            + Objects.toString(t.get("artist"), "")).toLowerCase(Locale.ROOT);
                    originalIndexMap.put(key, idx++);
                }
            }
        }

        // 2) 최근 3일 중복 제외
        List<Map<String, Object>> pool = music;
        if (music != null && !music.isEmpty()) {
            List<Map<String, Object>> filtered = new ArrayList<>();
            for (Map<String, Object> t : music) {
                String key = (Objects.toString(t.get("title"), "") + "||"
                        + Objects.toString(t.get("artist"), "")).toLowerCase(Locale.ROOT);
                if (!recentSet.contains(key)) filtered.add(t);
            }
            if (!filtered.isEmpty()) pool = filtered;
        }

        // 3) 랜덤 1곡 선택
        Map<String, Object> selectedTrack = null;
        Integer selectedIndexOriginal = null;
        if (pool != null && !pool.isEmpty()) {
            int pick = ThreadLocalRandom.current().nextInt(pool.size());
            selectedTrack = pool.get(pick);

            for (int i = 0; i < music.size(); i++) {
                Map<String, Object> t = music.get(i);
                if (Objects.equals(t.get("title"), selectedTrack.get("title"))
                        && Objects.equals(t.get("artist"), selectedTrack.get("artist"))) {
                    selectedIndexOriginal = i;
                    break;
                }
            }

            body.put("selectedTrack", selectedTrack);
            body.put("selectedTrackIndex", selectedIndexOriginal != null ? selectedIndexOriginal : pick);
            body.putIfAbsent("top_track", selectedTrack);
        }

        // 4) 결과 저장
        AnalysisResult r = new AnalysisResult();
        r.setAnalysis(a);

        Object emoObj = body.get("emotion");
        if (emoObj instanceof Map<?, ?> emo) {
            Object label = emo.get("label");
            Object score = emo.get("score");
            Object reason = emo.get("reason");
            r.setEmotionLabel(label != null ? String.valueOf(label) : null);
            if (score instanceof Number n) {
                r.setEmotionScore(BigDecimal.valueOf(n.doubleValue()).setScale(4, RoundingMode.HALF_UP));
            }
            r.setEmotionReason(reason != null ? String.valueOf(reason) : null);
        }

        if (musicObj != null) {
            try { r.setMusicJson(om.writeValueAsString(musicObj)); } catch (Exception ignore) {}
        }

        try {
            Map<String, Object> toPersist = new HashMap<>(body);
            toPersist.remove("selectedTrack");
            toPersist.remove("selectedTrackIndex");
            r.setAiResponse(om.writeValueAsString(toPersist));
        } catch (Exception ignore) {}

        if (selectedTrack != null) {
            r.setSelectedTrackTitle(Objects.toString(selectedTrack.get("title"), null));
            r.setSelectedTrackArtist(Objects.toString(selectedTrack.get("artist"), null));
            r.setSelectedTrackAlbum(Objects.toString(selectedTrack.get("album"), null));
            r.setSelectedTrackGenre(Objects.toString(selectedTrack.get("genre"), null));
            r.setSelectedTrackIndex(selectedIndexOriginal);
        }

        resultRepo.save(r);

        return new AnalysisResponse("ok", "success", body);
    }
}
