package com.example.chat.message.event;

import com.example.chat.global.redis.RedisStreamService;
import com.example.chat.message.dto.MessageEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisStreamService redisStreamService;

    /**
     * 트랜잭션 커밋이 성공한 직후에 실행됩니다.
     * phase = TransactionPhase.AFTER_COMMIT (기본값)
     */
    @Async // (선택사항) 웹소켓 전송이 메인 스레드를 잡지 않게 비동기 처리 권장
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageSentEvent(MessageSentEvent event) {
        MessageEventPayload payload = event.getPayload();

        try {
            redisStreamService.publish(payload);
            
        } catch (Exception e) {
            log.error("Failed to publish message to Redis Stream", e);
            // 여기서 실패해도 DB 트랜잭션은 이미 커밋되었으므로 롤백되지 않음
            // 필요 시 재시도 로직(Retry) 추가 가능
        }
    }
}
