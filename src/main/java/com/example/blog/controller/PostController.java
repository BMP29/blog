package com.example.blog.controller;

import com.example.blog.DTOs.PostDTO;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity createNewPost(@Valid @RequestBody PostDTO postDTO) {
        PostDTO post = this.postService.createNewPost(postDTO);

        return ResponseEntity.ok(post);
    }
}
