package com.example.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "post")
@Data
public class Post {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "postSeq"
    )
    @SequenceGenerator(
            name = "postSeq",
            sequenceName = "postSeq",
            allocationSize = 1
    )
    private Long id;
    @NotBlank
    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotBlank
    @Column(name = "content")
    private String content;
    @NotNull
    @Column(name = "createdAt")
    private LocalDate createdAt;
    @NotNull
    @Column(name = "updatedAt")
    private LocalDate updatedAt;

    public Post(String title, User author, String content, LocalDate createdAt, LocalDate updatedAt) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Post() {}
}
