package com.example.blog.service;

import com.example.blog.DTOs.PostDTO;
import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostDTO createNewPost(PostDTO postDTO) {
        Post newPost = new Post(
                postDTO.title(),
                postDTO.author(),
                postDTO.content(),
                postDTO.createdAt(),
                postDTO.updatedAt()
        );

        this.postRepository.save(newPost);

        return new PostDTO(
                newPost.getTitle(),
                newPost.getAuthor(),
                newPost.getAuthor(),
                newPost.getCreatedAt(),
                newPost.getUpdatedAt()
        );
    }
}
