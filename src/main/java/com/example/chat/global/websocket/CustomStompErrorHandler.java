package com.example.chat.global.websocket;

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

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable rootCause = ex.getCause() != null ? ex.getCause() : ex;
        log.error("🚨 STOMP Error Catch! Cause: {}", rootCause.getMessage());
        log.error("Stack Trace:", ex);
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
        if (cause != null && cause.getMessage() != null && cause.getMessage().contains("Expired")) {
            return MessageBuilder.createMessage(
                    "Your Token is Expired!".getBytes(StandardCharsets.UTF_8),
                    errorHeaderAccessor.getMessageHeaders()
            );
        }
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}