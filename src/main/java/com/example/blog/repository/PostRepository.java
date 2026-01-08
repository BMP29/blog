package com.example.blog.repository;

import com.example.blog.DTOs.PostSummaryDTO;
import com.example.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @Query("select p.id, p.title, p.author.id, p.author.username, p.createdAt, p.updatedAt from Post p where p.id < :cursorValue order by p.id desc")
    Page<PostSummaryDTO> findAfter(@Param("cursorValue") String cursorValue, Pageable pageable);

}
