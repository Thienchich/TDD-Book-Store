package com.example.springtestdemo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private int id;
    private User user;
    private Book book;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
