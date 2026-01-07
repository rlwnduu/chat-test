package com.example.chat.channel.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ChannelMemberCursorMapper {

    private static final String SEPARATOR = "||";

    public static String toCursor(String nickname, Long id) {
        if (nickname == null || id == null) return null;
        String value = nickname + SEPARATOR + id;
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static CursorData fromCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return new CursorData(null, null);
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|\\|");
            if (parts.length != 2) throw new IllegalArgumentException();

            return new CursorData(parts[0], Long.parseLong(parts[1]));
        } catch (Exception e) {
            return new CursorData(null, null);
        }
    }

    public record CursorData(String nickname, Long id) {}
}
