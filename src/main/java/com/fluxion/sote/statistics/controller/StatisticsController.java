import com.fluxion.sote.statistics.dto.*;
import com.fluxion.sote.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 1) 일기
    @GetMapping("/diary")
    public ResponseEntity<?> getDiaryStats(
            @RequestParam String period,
            @RequestParam(required = false) String month
    ) {
        return ResponseEntity.ok(statisticsService.getDiaryStats(period, month));
    }

    // 2) 감정 분석
    @GetMapping("/analysis")
    public ResponseEntity<AnalysisStatsResponse> getAnalysisStats(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getAnalysisStats(period));
    }

    // 3) 챌린지 완료율 (주간)
    @GetMapping("/challenges/completion-rate")
    public ResponseEntity<ChallengeCompletionResponse> getChallengeCompletion(
            @RequestParam String period,
            @RequestParam(required = false) String week
    ) {
        return ResponseEntity.ok(statisticsService.getChallengeCompletion(period, week));
    }

    // 4) 챌린지 감정 (월간)
    @GetMapping("/challenges/emotion-performance")
    public ResponseEntity<ChallengeEmotionPerformanceResponse> getChallengeEmotionPerformance(
            @RequestParam String period,
            @RequestParam(required = false) String month
    ) {
        return ResponseEntity.ok(statisticsService.getChallengeEmotionPerformance(period, month));
    }

    // 5) 전체 뱃지
    @GetMapping("/challenges/badges")
    public ResponseEntity<ChallengeBadgeResponse> getChallengeBadges(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getChallengeBadges(period));
    }

    // 6) 음악 (월간)
    @GetMapping("/music")
    public ResponseEntity<MusicStatsResponse> getMusicStats(
            @RequestParam String period,
            @RequestParam(required = false) String month
    ) {
        return ResponseEntity.ok(statisticsService.getMusicStats(period, month));
    }

    // 7) 키워드 랭킹 (월간)
    @GetMapping("/keywords/ranking")
    public ResponseEntity<KeywordRankingResponse> getKeywordRanking(
            @RequestParam String period,
            @RequestParam(required = false) String month
    ) {
        return ResponseEntity.ok(statisticsService.getKeywordRanking(period, month));
    }

    // 8) 키워드 감정 랭킹 (전체)
    @GetMapping("/keywords/emotion-ranking")
    public ResponseEntity<KeywordEmotionRankingResponse> getKeywordEmotionRanking(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getKeywordEmotionRanking(period));
    }

    // 9) 키워드 탐색 (전체)
    @GetMapping("/keywords/explore")
    public ResponseEntity<KeywordExploreResponse> getKeywordExplore(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getKeywordExplore(period));
    }
}
