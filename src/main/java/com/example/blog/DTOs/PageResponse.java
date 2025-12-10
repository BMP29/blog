package com.example.blog.DTOs;

public record PageResponse<T>(
        T content,
        String previousPageCursor,
        String nextPageCursor
) { }