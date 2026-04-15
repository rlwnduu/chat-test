package com.example.chat.user.service;

import com.example.chat.global.dto.PageResponse;
import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.user.domain.Role;
import com.example.chat.user.domain.User;
import com.example.chat.user.dto.UserCreateRequest;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.dto.UserInfoResponse;
import com.example.chat.user.mapper.UserMapper;
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
    private final UserMapper userMapper;

    @Transactional
    public void createUser(UserCreateRequest userCreateRequest) {
        String loginId = userCreateRequest.getLoginId();

        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.LOGIN_ID_DUPLICATION);
        }

        String encodedPassword = passwordEncoder.encode(userCreateRequest.getPassword());
        String initialUsername = generateInitialUsername();

        User user = userMapper.toEntity(userCreateRequest, encodedPassword, initialUsername);
        user.addRole(Role.USER);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        UserInfoProjection userInfoProjection = userRepository.findUserInfoById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return userMapper.toResponse(userInfoProjection);
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
                .map(userMapper::toResponse)
                .toList();
        boolean hasNext = slice.hasNext();
        String nextCursor = null;

        if (hasNext && !friends.isEmpty()) {
            nextCursor = String.valueOf(friends.get(friends.size() - 1).getId());
        }

        return new PageResponse<>(friends, nextCursor, hasNext);
    }

    private String generateInitialUsername() {
        String randomPart = RandomStringUtils.randomAlphanumeric(6);
        return "User_" + randomPart;
    }
}
