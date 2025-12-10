package com.example.blog.service;

import com.example.blog.DTOs.PageResponse;
import com.example.blog.DTOs.PostDTO;
import com.example.blog.DTOs.PostSummaryDTO;
import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public PageResponse<PagedModel<PostSummaryDTO>> getPosts(String nextCursor, int limit) {
        Page<PostSummaryDTO> postSlice = postRepository.findAfter(nextCursor, PageRequest.of(0, limit));

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
}
