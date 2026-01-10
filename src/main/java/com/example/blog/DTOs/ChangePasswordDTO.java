package com.example.blog.DTOs;

public record ChangePasswordDTO(
        String newPassoword,
        String oldPassword
) {
}
