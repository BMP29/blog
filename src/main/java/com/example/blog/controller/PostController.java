package com.example.blog.controller;

import com.example.blog.DTOs.*;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/posts")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDTO> createNewPost(@Valid @RequestBody CreatePostDTO input) {
        PostDTO post = this.postService.createNewPost(input);

        return new ResponseEntity<PostDTO>(post, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PagedModel<PostSummaryDTO>>> getPosts(
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(defaultValue = "9999999999", required = false) String nextCursor
    ) {
        PageResponse<PagedModel<PostSummaryDTO>> postPage = postService.getPosts(nextCursor, size);

        return ResponseEntity.ok(postPage);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable long postId) {
        PostDTO post = this.postService.getPostById(postId);

        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostDTO> alterPost(@PathVariable Long postId, @Valid @RequestBody AlterPostDTO data) {
        PostDTO post =this.postService.alterPost(postId, data);

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        this.postService.deletePost(postId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
