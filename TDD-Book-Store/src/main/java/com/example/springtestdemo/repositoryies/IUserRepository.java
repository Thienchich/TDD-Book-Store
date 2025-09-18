package com.example.springtestdemo.repositoryies;

import com.example.springtestdemo.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository {
    User findByEmail(String mail);
    User findByUsername(String username);
    User save(User any);
}
