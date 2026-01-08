package com.example.blog.mappers;

import com.example.blog.DTOs.PostDTO;
import com.example.blog.model.Post;

import java.time.LocalDate;

public class PostMapper {
    public static PostDTO toDTO(Post post) {
        if(post == null) return null;

        return new PostDTO(
                post.getTitle(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public static Post map(PostDTO patchValues, Post post) {
        if(patchValues.title() != null) {
            post.setTitle(patchValues.title());
        }

        if(patchValues.content() != null) {
            post.setContent(patchValues.content());
        }

        post.setUpdatedAt(LocalDate.now());

        return post;
    }
}
