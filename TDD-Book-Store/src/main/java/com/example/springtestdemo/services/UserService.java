package com.example.springtestdemo.services;

import com.example.springtestdemo.models.User;
import com.example.springtestdemo.repositoryies.IUserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    IUserRepository userRepository;
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if(this.userRepository.findByEmail(user.getEmail()) == null)
        {
            return this.userRepository.save(user);
        }
        else
        {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
