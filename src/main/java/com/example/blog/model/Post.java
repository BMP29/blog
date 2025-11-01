package com.example.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "post")
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
    @NotBlank
    @Column(name = "author")
    private String author;
    @NotBlank
    @Column(name = "content")
    private String content;
    @NotNull
    @Column(name = "createdAt")
    private LocalDate createdAt;
    @NotNull
    @Column(name = "updatedAt")
    private LocalDate updatedAt;

    public Post(String title, String author, String content, LocalDate createdAt, LocalDate updatedAt) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Post() {}

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
