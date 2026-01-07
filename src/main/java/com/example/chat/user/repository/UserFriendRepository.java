package com.example.chat.user.repository;

import com.example.chat.user.domain.User;
import com.example.chat.user.domain.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {

    boolean existsByUserAAndUserB(User userA, User userB);
}
