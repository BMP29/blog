package com.example.blog.mappers;

import com.example.blog.DTOs.PostDTO;
import com.example.blog.model.Post;

public class PostMapper {
    public static PostDTO toDTO(Post post) {
        if(post == null) return null;

        return new PostDTO(
                post.getTitle(),
                post.getAuthor(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
