package com.example.chat.invitation.repository;

import com.example.chat.invitation.domain.ChannelInvite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelInviteRepository extends JpaRepository<ChannelInvite, Long>, ChannelInviteRepositoryCustom {
}
