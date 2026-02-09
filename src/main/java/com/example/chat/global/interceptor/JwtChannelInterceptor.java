package com.example.chat.global.interceptor;


import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.global.security.jwt.JwtTokenProvider;
import com.example.chat.global.security.jwt.JwtTokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 세션 기반 인증으로 변경됨에 따라 더 이상 사용하지 않습니다.
 * WebSocketConfig에서 제거되었습니다.
 */
@Deprecated
@Slf4j
//@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        String sessionId = accessor.getSessionId();
        MDC.put("sessionId", sessionId);

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor, sessionId);
        }
        else if (StompCommand.SUBSCRIBE.equals(command)) {
            String destination = accessor.getDestination();
            log.info("[WS-SUBSCRIBE] Destination: {}", destination);
        }
        else if (StompCommand.DISCONNECT.equals(command)) {
            log.info("[WS-DISCONNECT] 연결 종료");
            MDC.clear();
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor, String sessionId) {
        log.info("[WS-CONNECT] 연결 요청 감지: SessionId={}", sessionId);

        try {
            String token = jwtTokenResolver.resolveToken(accessor)
                    .orElseThrow(() -> new BusinessException(ErrorCode.EMPTY_TOKEN));

            String username = jwtTokenProvider.getUsername(token);

            MDC.put("userId", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);

            log.info("[WS-CONNECT] 인증 성공: username={}", username);

        } catch (BusinessException e) {
            log.warn("[WS-CONNECT] 인증 실패 ({}) : {}", e.getErrorCode().getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[WS-CONNECT] 알 수 없는 에러", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}