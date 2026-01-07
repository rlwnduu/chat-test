package com.example.chat.auth.service;

import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.global.redis.RedisService;
import com.example.chat.file.service.FileService;
import com.example.chat.global.security.user.CustomUserDetails;
import com.example.chat.auth.dto.LoginRequest;
import com.example.chat.auth.dto.AuthTokensResponse;
import com.example.chat.global.security.jwt.JwtTokenProvider;
import com.example.chat.user.dto.UserCreateRequest;
import com.example.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisService redisService;

    private final UserService userService;

    private final FileService fileService;

    @Transactional
    public void registerUser(UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
    }

    @Transactional
    public AuthTokensResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLoginId(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = userDetails.getId();
        String username = userDetails.getUsername();
        return issue(userId, username);
    }

    @Transactional
    public void logout(String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        if (redisService.hasKey("RT:" + userId)) {
            redisService.deleteValues("RT:" + userId);
        }

        long remainingTime = jwtTokenProvider.getRemainingTime(accessToken);
        if (remainingTime > 0) {
            redisService.setValues(
                    "BL:" + accessToken,
                    "logout",
                    Duration.ofMillis(remainingTime)
            );
        }
    }

    @Transactional
    public AuthTokensResponse reissueToken(String refreshToken) {
        // 1. 토큰에서 사용자 ID 추출 (여기서도 파싱 에러가 날 수 있지만, 일단 넘어갑니다)
        Long userId = jwtTokenProvider.getId(refreshToken);

        // 2. Redis에서 저장된 리프레시 토큰 조회
        String storedRefreshToken = redisService.getValues("RT:" + userId);

        // 3. 토큰 검증 로직 변경 부분
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            // [변경 전]
            // throw new RuntimeException("토큰 정보가 일치하지 않습니다.");

            // [변경 후]
            // 정의해둔 ErrorCode.INVALID_TOKEN (유효하지 않은 토큰)을 사용합니다.
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        return issue(userId, username);
    }

    private AuthTokensResponse issue(Long userId, String username) {
        String accessToken = jwtTokenProvider.createAccessToken(userId, username);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId, username);

        redisService.setValues("RT:" + userId,
                refreshToken,
                Duration.ofMillis(JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME));

        return new AuthTokensResponse(accessToken, refreshToken);
    }
}
