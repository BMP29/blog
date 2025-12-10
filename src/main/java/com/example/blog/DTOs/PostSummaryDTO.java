package com.example.blog.DTOs;

import java.time.LocalDate;

public record PostSummaryDTO(
        Long id,
        String title,
        String author,
        LocalDate createdAt,
        LocalDate updatedAt
) { }
