package com.example.chat.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(Long userId, Object message) {

        if (userId == null) {
            log.warn("알림 발송 실패: 수신자 ID(PK)가 null입니다. (메시지: {})", message);
            return;
        }

        String targetUser = String.valueOf(userId);
        messagingTemplate.convertAndSendToUser(
                targetUser,
                "/queue/notifications",
                message
        );
    }
}
