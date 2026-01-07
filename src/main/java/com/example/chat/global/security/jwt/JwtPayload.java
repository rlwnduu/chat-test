package com.example.chat.global.security.jwt;

import lombok.Getter;

public class JwtPayload {

    @Getter
    Long userId;

    @Getter
    String username;

    public static JwtPayload create(Long userId, String username) {
        JwtPayload jwtPayload = new JwtPayload();
        jwtPayload.userId = userId;
        jwtPayload.username = username;
        return jwtPayload;
    }
}
