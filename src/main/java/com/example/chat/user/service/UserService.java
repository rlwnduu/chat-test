package com.example.chat.user.service;

import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import com.example.chat.user.domain.ProfileColor;
import com.example.chat.user.domain.User;
import com.example.chat.user.dto.UserCreateRequest;
import com.example.chat.user.dto.UserCursorResponse;
import com.example.chat.user.dto.UserInfoProjection;
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

        // [수정] 이미 존재하는 아이디 -> BusinessException (M005 or M002)
        if (userRepository.existsByLoginId(loginId)) {
            // ErrorCode에 LOGIN_ID_DUPLICATION을 추가하거나, EMAIL_DUPLICATION을 사용하세요.
            throw new BusinessException(ErrorCode.LOGIN_ID_DUPLICATION);
        }

        String encodedPassword = passwordEncoder.encode(userCreateRequest.getPassword());
        String initialUsername = generateInitialUsername();
        String initialNickname = initialUsername;
        String initialProfileIconColor = ProfileColor.getRandomHexCode();

        User user = new User(loginId,
                encodedPassword,
                initialUsername,
                initialNickname,
                initialProfileIconColor
        );
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public UserInfoProjection getUserInfo(Long userId) {
        return userRepository.findUserInfoById(userId)
                // [수정] 존재하지 않는 유저 -> BusinessException (M001)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserCursorResponse getMyFriends(Long userId, String cursor, int size) {
        Long longCursor = null;
        try {
            longCursor = cursor != null ? Long.parseLong(cursor) : null;
        } catch (NumberFormatException e) {
            // [수정] 커서 포맷 에러 -> BusinessException (G001: 잘못된 입력값)
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<UserInfoProjection> slice = userRepository.findFriendsByUserIdWithCursor(userId, longCursor, pageable);

        List<UserInfoProjection> friends = slice.getContent();
        boolean hasNext = slice.hasNext();
        String nextCursor = null;

        if (hasNext && !friends.isEmpty()) {
            nextCursor = friends.get(friends.size() - 1).getId();
        }

        return new UserCursorResponse(friends, nextCursor, hasNext);
    }

    private String generateInitialUsername() {
        String randomPart = RandomStringUtils.randomAlphanumeric(6);
        return "User_" + randomPart;
    }
}

