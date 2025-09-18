package com.example.springtestdemo.services;

import com.example.springtestdemo.models.User;
import com.example.springtestdemo.repositoryies.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final IUserRepository userRepository;
    private final Set<Long> loggedInUserIds = new HashSet<>();

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        loggedInUserIds.add(user.getId());
        return user;
    }

    public boolean isLoggedIn(Long userId) {
        return loggedInUserIds.contains(userId);
    }

    public void logout(Long userId) {
        loggedInUserIds.remove(userId);
    }
}
