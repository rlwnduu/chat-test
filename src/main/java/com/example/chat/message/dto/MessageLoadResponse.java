package com.example.chat.message.dto;

import com.example.chat.user.dto.UserInfoResponse;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageLoadResponse {

    private List<MessageView> messages;
    private Map<String, UserInfoResponse> userInfoDtoMap;
    private String nextCursor;
    private boolean hasNext;
}
