package com.example.chat.invitation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FriendRequestCursorResponse {

    private List<FriendRequestResponse> friendRequests;

    private String nextCursor;

    private boolean hasNext;

    public FriendRequestCursorResponse(List<FriendRequestResponse> friendRequests, String nextCursor, boolean hasNext) {
        this.friendRequests = friendRequests;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }
}
