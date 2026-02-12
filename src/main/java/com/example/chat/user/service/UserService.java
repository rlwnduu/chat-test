package com.example.chat.user.service;

import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.user.domain.ProfileColor;
import com.example.chat.user.domain.User;
import com.example.chat.user.dto.UserCreateRequest;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.dto.UserInfoResponse;
import com.example.chat.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserCreateRequest userCreateRequest) {
        String loginId = userCreateRequest.getLoginId();

        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.LOGIN_ID_DUPLICATION);
        }

        String encodedPassword = passwordEncoder.encode(userCreateRequest.getPassword());
        String initialUsername = generateInitialUsername();
        String initialNickname = initialUsername;
        String initialProfileIconColor = ProfileColor.getRandomHexCode();

        User user = User.create(userCreateRequest, encodedPassword, initialUsername, initialProfileIconColor);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public UserInfoProjection getUserInfo(Long userId) {
        return userRepository.findUserInfoById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public PageResponse<UserInfoResponse> getMyFriends(Long userId, String cursor, int size) {
        Long longCursor = null;
        try {
            longCursor = cursor != null ? Long.parseLong(cursor) : null;
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<UserInfoProjection> slice = userRepository.findFriendsByUserIdWithCursor(userId, longCursor, pageable);

        List<UserInfoResponse> friends = slice.getContent().stream()
                .map(UserInfoResponse::new)
                .toList();
        boolean hasNext = slice.hasNext();
        String nextCursor = null;

        if (hasNext && !friends.isEmpty()) {
            nextCursor = String.valueOf(friends.get(friends.size() - 1).getUserId());
        }

        return new PageResponse<>(friends, nextCursor, hasNext);
    }

    private String generateInitialUsername() {
        String randomPart = RandomStringUtils.randomAlphanumeric(6);
        return "User_" + randomPart;
    }
}
