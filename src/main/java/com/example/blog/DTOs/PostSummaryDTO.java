package com.example.blog.DTOs;

import java.time.LocalDate ;

public record PostSummaryDTO(
        Long id,
        String title,
        Long authorId,
        String authorName,
        LocalDate  createdAt,
        LocalDate  updatedAt
) { }
