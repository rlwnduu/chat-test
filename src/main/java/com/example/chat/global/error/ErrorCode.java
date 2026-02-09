package com.example.chat.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ======================================================================
    // 1. Global (공통 예외 - 코드 prefix: G)
    // ======================================================================
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G001", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "G002", "잘못된 HTTP 메서드 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G999", "서버 내부 오류가 발생했습니다."),

    // ======================================================================
    // 2. Auth (인증/인가 - 코드 prefix: A)
    // ======================================================================
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A001", "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "토큰이 존재하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A005", "해당 리소스에 접근 권한이 없습니다."),

    // ======================================================================
    // 3. Member (회원 관련 - 코드 prefix: M)
    // ======================================================================
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회원입니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "M002", "이미 사용 중인 이메일입니다."),
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT, "M003", "이미 사용 중인 닉네임입니다."),
    ALREADY_DELETED_MEMBER(HttpStatus.BAD_REQUEST, "M004", "이미 탈퇴한 회원입니다."),
    LOGIN_ID_DUPLICATION(HttpStatus.CONFLICT, "M005", "이미 사용 중인 아이디입니다."),

    // ======================================================================
    // 4. Relationship (친구 관계 - 코드 prefix: R)
    // ======================================================================
    ALREADY_FRIEND(HttpStatus.CONFLICT, "R001", "이미 친구 관계입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "존재하지 않는 친구 요청입니다."),
    NOT_FRIEND_RELATION(HttpStatus.BAD_REQUEST, "R003", "친구 관계가 아닙니다 (DM 발송 불가)."),
    BLOCKED_MEMBER(HttpStatus.FORBIDDEN, "R004", "차단된 사용자입니다."),
    SELF_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "R005", "자신에게 친구 요청을 보낼 수 없습니다."),

    // ======================================================================
    // 5. Chat Room (채팅방 - 코드 prefix: C)
    // ======================================================================
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 채팅방입니다."),
    ALREADY_JOINED_ROOM(HttpStatus.CONFLICT, "C002", "이미 참여 중인 채팅방입니다."),
    ROOM_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "C003", "채팅방 정원이 초과되었습니다."),
    KICKED_MEMBER(HttpStatus.FORBIDDEN, "C004", "추방당하여 재입장이 불가능합니다."),
    CLOSED_ROOM(HttpStatus.BAD_REQUEST, "C005", "종료되거나 삭제된 채팅방입니다."),
    INVALID_INVITATION_CODE(HttpStatus.BAD_REQUEST, "C006", "유효하지 않은 초대 코드입니다."),
    NOT_ROOM_MEMBER(HttpStatus.FORBIDDEN, "C007", "채팅방 참여자가 아닙니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "C008", "초대장을 찾을 수 없습니다."),

    // ======================================================================
    // 6. Message & File (메시지/파일 - 코드 prefix: MS)
    // ======================================================================
    MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MS001", "메시지 전송에 실패했습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "MS002", "파일 크기가 제한을 초과했습니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "MS003", "지원하지 않는 파일 형식입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
