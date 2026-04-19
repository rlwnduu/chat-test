package com.example.chat.message.dto;

import com.example.chat.user.dto.UserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageLoadResponse {

    private List<MessageResponse> messages;
    private Map<String, UserInfoResponse> userInfoDtoMap;
    private String nextCursor;
    private boolean hasNext;
}
