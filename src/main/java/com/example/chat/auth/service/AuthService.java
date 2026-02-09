package com.example.chat.auth.service;

import com.example.chat.user.dto.UserCreateRequest;
import com.example.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    @Transactional
    public void registerUser(UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
    }
}
