package com.example.chat.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String loginId;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}