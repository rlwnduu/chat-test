package com.example.chat.invitation.dto;

import lombok.Getter;

@Getter
public class CreateFriendRequest {

    private String username;

    public CreateFriendRequest() {
    }

    public CreateFriendRequest(String username) {
        this.username = username;
    }
}
