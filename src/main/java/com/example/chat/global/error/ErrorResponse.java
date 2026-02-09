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
    private final List<CustomFieldError> errors;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, BindingResult bindingResult) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .errors(CustomFieldError.of(bindingResult))
                        .build());
    }

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
