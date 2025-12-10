package com.example.blog.controller;

import com.example.blog.DTOs.PageResponse;
import com.example.blog.DTOs.PostDTO;
import com.example.blog.DTOs.PostSummaryDTO;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/post")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDTO> createNewPost(@Valid @RequestBody PostDTO postDTO) {
        PostDTO post = this.postService.createNewPost(postDTO);

        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PagedModel<PostSummaryDTO>>> getPosts(
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(defaultValue = "9999999999", required = false) String nextCursor
    ) {
        PageResponse<PagedModel<PostSummaryDTO>> postPage = postService.getPosts(nextCursor, size);

        return ResponseEntity.ok(postPage);
    }
}
