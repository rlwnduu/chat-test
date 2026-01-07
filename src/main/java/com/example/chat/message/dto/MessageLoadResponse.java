package com.example.chat.message.dto;

import com.example.chat.user.dto.UserInfoResponse;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MessageLoadResponse {

    private List<MessageView> messages;

    private Map<String, UserInfoResponse> userInfoDtoMap;

    private String nextCursor;

    private boolean hasNext;

    public MessageLoadResponse(List<MessageView> messages,
                               Map<String, UserInfoResponse> userInfoDtoMap,
                               String nextCursor,
                               boolean hasNext
    ) {
        this.messages = messages;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
        this.userInfoDtoMap = userInfoDtoMap;
    }
}
