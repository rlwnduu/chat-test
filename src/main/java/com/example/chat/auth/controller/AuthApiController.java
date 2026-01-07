package com.example.chat.auth.controller;

import com.example.chat.auth.dto.LoginRequest;
import com.example.chat.auth.dto.AuthTokensResponse;
import com.example.chat.auth.service.AuthService;
import com.example.chat.global.security.jwt.JwtTokenResolver;
import com.example.chat.user.dto.UserCreateRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;

    private final JwtTokenResolver jwtTokenResolver;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserCreateRequest userCreateRequest) {
        authService.registerUser(userCreateRequest);
        Map<String, String> response = Map.of("message", "회원가입이 성공적으로 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthTokensResponse authTokensResponse = authService.authenticateUser(loginRequest);
        return responseTokens(authTokensResponse, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String accessToken = jwtTokenResolver.resolveToken(request)
                .orElseThrow();

        authService.logout(accessToken);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String extractedRefreshToken = extractRefreshToken(request);
        AuthTokensResponse authTokensResponse = authService.reissueToken(extractedRefreshToken);
        return responseTokens(authTokensResponse, response);
    }

    private ResponseEntity<?> responseTokens(AuthTokensResponse authTokensResponse, HttpServletResponse response) {
        String accessToken = authTokensResponse.getAccessToken();
        String refreshToken = authTokensResponse.getRefreshToken();

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(2592000);
        refreshTokenCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(Collections.singletonMap("accessToken", accessToken));
    }

    private String extractRefreshToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(this::isRefreshToken)
                .map(Cookie::getValue)
                .findAny()
                .orElseThrow();
    }

    private boolean isRefreshToken(Cookie cookie) {
        return "refreshToken".equals(cookie.getName());
    }
}
