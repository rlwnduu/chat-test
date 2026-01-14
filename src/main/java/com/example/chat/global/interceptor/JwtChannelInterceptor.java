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
import org.springframework.stereotype.Component;

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

        // [추가] 모든 STOMP 메시지 처리 시 SessionId를 MDC에 저장 (로그 추적용)
        String sessionId = accessor.getSessionId();
        MDC.put("sessionId", sessionId);

        StompCommand command = accessor.getCommand();

        // 1. 연결 요청 (CONNECT)
        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor, sessionId);
        }
        // 2. 구독 요청 (SUBSCRIBE) - 채팅방 입장 로그
        else if (StompCommand.SUBSCRIBE.equals(command)) {
            String destination = accessor.getDestination();
            log.info("[WS-SUBSCRIBE] Destination: {}", destination);
        }
        // 3. 연결 해제 (DISCONNECT) - 퇴장 로그
        else if (StompCommand.DISCONNECT.equals(command)) {
            log.info("[WS-DISCONNECT] 연결 종료");
            // Disconnect 시에는 여기서 MDC를 지워주는 것이 좋음 (Optional)
            MDC.clear();
        }

        return message;
    }

    // CONNECT 로직 분리
    private void handleConnect(StompHeaderAccessor accessor, String sessionId) {
        log.info("[WS-CONNECT] 연결 요청 감지: SessionId={}", sessionId);

        try {
            String token = jwtTokenResolver.resolveToken(accessor)
                    .orElseThrow(() -> new BusinessException(ErrorCode.EMPTY_TOKEN));

            String username = jwtTokenProvider.getUsername(token);

            // [추가] 인증 성공 시 MDC에 userId 저장 -> 이후 로그에 [userId: testUser] 자동 포함
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


//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtChannelInterceptor implements ChannelInterceptor {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final JwtTokenResolver jwtTokenResolver;
//    private final UserDetailsService userDetailsService; // CustomUserDetailsService 주입
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
//            return message;
//        }
//
//        String token = jwtTokenResolver.resolveToken(accessor)
//                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
//
//        String username = jwtTokenProvider.getUsername(token);
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        accessor.setUser(authentication);
//
//        return message;
//    }
//}
