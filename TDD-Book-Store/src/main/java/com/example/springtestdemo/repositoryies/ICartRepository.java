package com.example.springtestdemo.repositoryies;

import com.example.springtestdemo.models.Cart;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ICartRepository {
    Optional<Cart> findById(Long id);
    Cart save(Cart any);
}