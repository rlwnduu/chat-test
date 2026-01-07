package com.example.chat.global.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();

    private final int status;
    private final String code;
    private final String message;
    private final List<CustomFieldError> errors; // 유효성 검사 실패 시 필드 에러 목록

    // 1. 일반 에러 응답 생성 메서드
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // 2. 유효성 검사(@Valid) 실패 시 응답 생성 메서드
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, BindingResult bindingResult) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .errors(CustomFieldError.of(bindingResult)) // 필드 에러 목록 추가
                        .build());
    }

    // 내부 클래스: 필드 에러 정보 (ex: email 필드는 필수입니다)
    @Getter
    @AllArgsConstructor
    public static class CustomFieldError {
        private String field;
        private String value;
        private String reason;

        public static List<CustomFieldError> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new CustomFieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}
