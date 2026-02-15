package com.example.chat.invitation.dto;

import com.example.chat.invitation.domain.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteSearchCondition {

    private Long userId;

    private RequestStatus status;

    private Long cursorId;

    @Builder.Default
    private int size = 20;
}
