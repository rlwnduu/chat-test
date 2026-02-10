package com.example.chat.user.dto;

import lombok.*;

@Getter
@Builder
@ToString(exclude = "password")
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String loginId;
    private String password;
}


