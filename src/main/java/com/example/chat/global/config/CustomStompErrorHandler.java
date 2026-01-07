package com.example.chat.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CustomStompErrorHandler extends StompSubProtocolErrorHandler {

    /**
     * 클라이언트 메시지 처리 중 에러가 발생했을 때 호출됨
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        // 1. [핵심] 서버 콘솔에 에러 로그를 명확하게 찍음
        Throwable rootCause = ex.getCause() != null ? ex.getCause() : ex;
        log.error("🚨 STOMP Error Catch! Cause: {}", rootCause.getMessage());

         log.error("Stack Trace:", ex);

        // 2. 클라이언트에게 보낼 오류 응답 메시지 생성
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    // (옵션) 클라이언트에게 "연결 끊김" 알림을 보낼 때 내용을 커스텀하고 싶다면 사용
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
        // 예: 토큰 만료 에러라면 클라이언트에게 "Token Expired"라고 명시적으로 알려줌
        if (cause != null && cause.getMessage() != null && cause.getMessage().contains("Expired")) {
            return MessageBuilder.createMessage(
                    "Your Token is Expired!".getBytes(StandardCharsets.UTF_8),
                    errorHeaderAccessor.getMessageHeaders()
            );
        }
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}