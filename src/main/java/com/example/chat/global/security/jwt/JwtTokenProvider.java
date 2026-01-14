package com.example.chat.global.security.jwt;

import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * @deprecated 세션 기반 인증으로 전환됨에 따라 더 이상 사용되지 않습니다.
 * 추후 모바일 앱 등에서 JWT가 다시 필요해질 경우를 대비해 코드는 유지합니다.
 */
@Deprecated
@Slf4j
@Component
public class JwtTokenProvider {

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 3600000L;  // 1시간
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 2592000000L; // 7일

    private final SecretKey key;

//    public JwtTokenProvider() {
//        this.key = Jwts.SIG.HS256.key().build();
//    }

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, String username) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", "access")
                .claim("jti", UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId, String username) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", "refresh")
                .claim("jti", UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    public Claims getPayload(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 -> "A003" 에러 코드로 변환해서 던짐
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException | SecurityException | IllegalArgumentException e) {
            // 위조되거나 잘못된 토큰 -> "A002" 에러 코드로 변환
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("JWT 처리 중 알 수 없는 에러 발생", e);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    public JwtPayload getClaims(String token) {
        Claims claims = getPayload(token);
        Long userId = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);
        return JwtPayload.create(userId, username);
    }

    public Long getId(String token) {
        return Long.parseLong(getPayload(token).getSubject());
    }

    public String getUsername(String token) {
        return getPayload(token).get("username", String.class);
    }

    public long getRemainingTime(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(key) // 서명 검증 키
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            long now = new Date().getTime();
            return expiration.getTime() - now;
        } catch (Exception e) {
            return 0;
        }
    }
}
