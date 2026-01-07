package com.example.chat.global.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {

    private final List<T> data;

    private final String nextCursor;

    private final boolean hasNext;

    public PageResponse(List<T> data, String nextCursor, boolean hasNext) {
        this.data = data;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }
}