package com.example.chat.user.repository;

import com.example.chat.user.domain.User;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.dto.UserInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByUsername(String username);

    List<User> findByIdIn(Collection<String> ids);

    @Query("SELECT " +
            "   CAST(u.id AS String) as id, " +
            "   u.username as username, " +
            "   u.nickname as nickname, " +
            "   u.profileImageUrl as profileImageUrl, " +
            "   u.profileIconColor as profileIconColor " +
            "FROM User u WHERE u.id = :userId")
    Optional<UserInfoProjection> findUserInfoById(@Param("userId") Long userId);

    @Query("SELECT " +
            "   CAST(u.id AS String) as id, " +
            "   u.username as username, " +
            "   u.nickname as nickname, " +
            "   u.profileImageUrl as profileImageUrl, " +
            "   u.profileIconColor as profileIconColor " +
            "FROM User u WHERE u.id IN (" +
            " (SELECT uf.userB.id FROM UserFriend uf WHERE uf.userA.id = :userId) " +
            " UNION " +
            " (SELECT uf.userA.id FROM UserFriend uf WHERE uf.userB.id = :userId)" +
            ") " +
            "AND (:cursor IS NULL OR u.id < :cursor)")
    Slice<UserInfoProjection> findFriendsByUserIdWithCursor(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
