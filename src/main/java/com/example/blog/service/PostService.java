package com.example.blog.service;

import com.example.blog.DTOs.*;
import com.example.blog.mappers.PostMapper;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) { this.postRepository = postRepository; }

    public PostDTO createNewPost(CreatePostDTO input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Post newPost = new Post(
                input.title(),
                currentUser,
                input.content(),
                LocalDate.now(),
                LocalDate.now()
        );

        this.postRepository.save(newPost);

        return PostMapper.toDTO(newPost);
    }


    public PageResponse<PagedModel<PostSummaryDTO>> getAuthorPosts(String nextCursor, int limit, Long authorId) {
        Page<PostSummaryDTO> postSlice = postRepository.findAuthorPosts(nextCursor, PageRequest.of(0, limit), authorId);

        if (!postSlice.hasContent()) {
            return new PageResponse<>(null, null, null);
        }

        List<PostSummaryDTO> posts = postSlice.getContent();

        PagedModel<PostSummaryDTO> page = new PagedModel<>(postSlice);

        return new PageResponse<>(
                page,
                posts.get(0).id().toString(),
                posts.get(posts.size() - 1).id().toString()
        );
    }

    public PostDTO getPostById(Long id) {
        Optional<Post> postOptional = this.postRepository.findById(id);

        if(postOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.");

        return PostMapper.toDTO(postOptional.get());
    }

    public PostDTO alterPost(Long postId, @Valid AlterPostDTO data) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Post> postOptional = this.postRepository.findById(postId);

        if(postOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.");

        if(currentUser.getId() != postOptional.get().getAuthor().getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Post newPost = PostMapper.map(data, postOptional.get());

        newPost = this.postRepository.save(newPost);

        return PostMapper.toDTO(newPost);
    }

    public void deletePost(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Post> postOptional = this.postRepository.findById(postId);

        if(postOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found.");

        if(currentUser.getId() != postOptional.get().getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        this.postRepository.delete(postOptional.get());
    }
}
