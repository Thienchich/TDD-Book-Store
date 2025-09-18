package com.example.springtestdemo.repositoryies;

import com.example.springtestdemo.models.Book;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IBookRepository {
    Optional<Book> findById(Long id);
    Book save(Book book);
}