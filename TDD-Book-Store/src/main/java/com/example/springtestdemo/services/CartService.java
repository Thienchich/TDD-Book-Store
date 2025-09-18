package com.example.springtestdemo.services;

import com.example.springtestdemo.models.*;
import com.example.springtestdemo.repositoryies.IBookRepository;
import com.example.springtestdemo.repositoryies.ICartRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final ICartRepository cartRepository;
    private final IBookRepository bookRepository;
    private final AuthService authService;

    public CartService(ICartRepository cartRepository, IBookRepository bookRepository, AuthService authService) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.authService = authService;
    }

    public Cart createCart(User user) {
        if (user == null || !authService.isLoggedIn(user.getId())) {
            throw new IllegalStateException("User not logged in");
        }
        Cart cart = Cart.builder().user(user).build();
        return cartRepository.save(cart);
    }

    public Cart addToCart(Long cartId, Long bookId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        User user = cart.getUser();
        if (user == null || !authService.isLoggedIn(user.getId())) {
            throw new IllegalStateException("User not logged in");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        int allowedQuantity = Math.min(quantity, book.getStockQuantity());

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(allowedQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .book(book)
                    .quantity(allowedQuantity)
                    .build();
            cart.getItems().add(newItem);
        }
        return cartRepository.save(cart);
    }


    public void checkout(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            int newStock = book.getStockQuantity() - item.getQuantity();
            book.setStockQuantity(newStock);
            bookRepository.save(book);
        }

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void cancel(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
