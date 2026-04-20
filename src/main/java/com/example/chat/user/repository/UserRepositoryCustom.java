package com.example.chat.user.repository;

import com.example.chat.user.dto.UserInfoProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface UserRepositoryCustom {

    Optional<UserInfoProjection> findUserInfoByIdCustom(Long userId);

    Slice<UserInfoProjection> findFriendsByUserIdWithCursorCustom(Long userId, Long cursor, Pageable pageable);
}