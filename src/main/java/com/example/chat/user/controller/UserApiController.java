package com.example.chat.user.controller;

import com.example.chat.global.security.user.CustomUserDetails;
import com.example.chat.user.dto.UserCursorResponse;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.service.UserService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/users/check")
    public ResponseEntity<Map<String, Boolean>> checkIdDuplicate(@RequestParam("loginId") String loginId) {
        boolean isAvailable = !userService.existsByLoginId(loginId);
        Map<String, Boolean> response = Collections.singletonMap("isAvailable", isAvailable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/@me")
    public ResponseEntity<UserInfoProjection> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserInfoProjection userInfo = userService.getUserInfo(userDetails.getId());
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/users/@me/friends")
    public ResponseEntity<UserCursorResponse> getMyFriends(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size는 1 이상이어야 합니다.") int size
    ) {
        Long userId = userDetails.getId();
        UserCursorResponse myFriends = userService.getMyFriends(userId, cursor, size);
        return ResponseEntity.ok(myFriends);
    }
}
