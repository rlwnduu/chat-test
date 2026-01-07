package com.example.chat.global.interceptor;

import com.example.chat.global.security.jwt.JwtTokenProvider;
import com.example.chat.global.security.jwt.JwtTokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // (1) Slf4j 로거 추가
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
     private final JwtTokenResolver jwtTokenResolver;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        log.debug("WebSocket 핸드셰이크 시작: {}", request.getURI());


        String token = request.getHeaders().getFirst("Authorization");
        if (token == null) {
            log.info("헤더없음");
            return false;
        }

        token = jwtTokenResolver.removeBearerFix(token);
            try {
                String username = jwtTokenProvider.getUsername(token);
                attributes.put("userPrincipal", username);
                return true;
            } catch (Exception e) {
                log.warn("토큰 검증 또는 subject 추출 중 예외 발생: {}", e.getMessage());
            }

        response.setStatusCode(HttpStatus.UNAUTHORIZED); // 401 응답
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // (3) 핸드셰이크 *이후* 최종 결과 로깅
        if (exception == null) {
            log.info("WebSocket 핸드셰이크 성공, 파이프가 열렸습니다. URI: {}", request.getURI());
        } else {
            log.error("WebSocket 핸드셰이크 실패, 파이프가 열리지 않았습니다. URI: {}. 예외: {}", request.getURI(), exception.getMessage());
        }
    }
}
