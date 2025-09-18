package com.example.springtestdemo.services;


import com.example.springtestdemo.models.User;
import com.example.springtestdemo.repositoryies.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private IUserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(IUserRepository.class);
        authService = new AuthService(userRepository);
    }

    @Test
    public void testLogin_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        User loggedInUser = authService.login("testuser", "password");

        assertEquals(user.getId(), loggedInUser.getId());
        assertTrue(authService.isLoggedIn(user.getId()));
    }

    @Test
    public void testLogin_InvalidPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        when(userRepository.findByUsername("username")).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login("username", "passwordsai");
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }


    @Test
    public void testLogout() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        when(userRepository.findByUsername("username")).thenReturn(user);

        authService.login("username", "password");
        assertTrue(authService.isLoggedIn(1L));

        authService.logout(1L);
        assertFalse(authService.isLoggedIn(1L));
    }
}
