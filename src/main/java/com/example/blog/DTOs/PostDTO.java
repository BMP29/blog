package com.example.blog.DTOs;

import java.time.LocalDate;

public record PostDTO(
        String title,
        String author,
        String content,
        LocalDate createdAt,
        LocalDate updatedAt
)  {
}
