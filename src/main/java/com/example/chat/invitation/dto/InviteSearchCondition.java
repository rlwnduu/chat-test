package com.example.chat.invitation.dto;

import com.example.chat.invitation.domain.InvitationStatus;
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

    private InvitationStatus status;

    private Long cursorId;

    @Builder.Default
    private int size = 20;
}
