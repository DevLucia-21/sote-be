package com.fluxion.sote.setting.scheduler;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.repository.DiaryRepository;
import com.fluxion.sote.setting.enums.NotificationType;
import com.fluxion.sote.setting.repository.NotificationSettingRepository;
import com.fluxion.sote.setting.service.FCMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NotificationSettingRepository notificationSettingRepository;
    private final DiaryRepository diaryRepository;
    private final FCMService fcmService;

    /**
     * 오늘 일기 작성 리마인더
     * 매일 22:00 KST에 DIARY 알림을 켠 사용자 중
     * 오늘 일기를 작성하지 않은 사용자에게 발송
     */
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void sendDiaryReminder() {
        LocalDate today = LocalDate.now(KST);

        List<User> users = notificationSettingRepository
                .findUsersByNotificationType(NotificationType.DIARY);

        log.info("[DIARY 알림 스케줄러 시작] date={}, targetCount={}", today, users.size());

        for (User user : users) {
            try {
                boolean alreadyWritten = diaryRepository.existsByUserAndDate(user, today);

                if (alreadyWritten) {
                    log.info("[DIARY 알림 스킵] 이미 일기 작성 완료 - userId={}, date={}",
                            user.getId(), today);
                    continue;
                }

                fcmService.sendNotificationToAllDevices(
                        user,
                        "오늘의 일기를 남겨볼까요?",
                        "하루가 끝나기 전에 오늘의 감정을 기록해보세요."
                );

                log.info("[DIARY 알림 발송 요청 완료] userId={}, date={}",
                        user.getId(), today);

            } catch (Exception e) {
                log.error("[DIARY 알림 발송 실패] userId={}, date={}",
                        user.getId(), today, e);
            }
        }

        log.info("[DIARY 알림 스케줄러 종료] date={}", today);
    }
}