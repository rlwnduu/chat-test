package com.example.chat.global.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Getter
@RequiredArgsConstructor
public class StompPrincipal implements Principal {
    private final String name;
}
