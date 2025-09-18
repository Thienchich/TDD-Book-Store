package com.example.springtestdemo.services;

import com.example.springtestdemo.models.*;
import com.example.springtestdemo.repositoryies.IBookRepository;
import com.example.springtestdemo.repositoryies.ICartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private ICartRepository cartRepository;

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private Book book;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder().id(1L).build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        book = Book.builder()
                .id(10L)
                .stockQuantity(5)
                .build();
    }

    @Test
    void should_throw_exception_when_creating_cart_if_user_not_logged_in() {
        when(authService.isLoggedIn(user.getId())).thenReturn(false);

        assertThatThrownBy(() -> cartService.createCart(user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User not logged in");
    }

    @Test
    void should_create_cart_when_user_logged_in() {
        when(authService.isLoggedIn(user.getId())).thenReturn(true);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart created = cartService.createCart(user);

        assertThat(created.getUser()).isEqualTo(user);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void should_throw_exception_when_adding_to_cart_if_cart_not_found() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(1L, 10L, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cart not found");
    }

    @Test
    void should_throw_exception_when_user_not_logged_in_adding_to_cart() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(authService.isLoggedIn(user.getId())).thenReturn(false);

        assertThatThrownBy(() -> cartService.addToCart(cart.getId(), book.getId(), 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User not logged in");
    }

    @Test
    void should_throw_exception_when_book_not_found_adding_to_cart() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(authService.isLoggedIn(user.getId())).thenReturn(true);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart(cart.getId(), book.getId(), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found");
    }


    @Test
    void should_add_new_cart_item_with_max_quantity_if_requested_more_than_stock() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(authService.isLoggedIn(user.getId())).thenReturn(true);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int requestedQuantity = 10; // more than stock (5)
        Cart updatedCart = cartService.addToCart(cart.getId(), book.getId(), requestedQuantity);

        assertThat(updatedCart.getItems()).hasSize(1);
        CartItem item = updatedCart.getItems().get(0);
        assertThat(item.getQuantity()).isEqualTo(book.getStockQuantity());
    }

    @Test
    void should_checkout_and_reduce_stock_and_clear_cart() {
        CartItem item = CartItem.builder().book(book).quantity(2).build();
        cart.getItems().add(item);

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.checkout(cart.getId());

        assertThat(book.getStockQuantity()).isEqualTo(3);
        assertThat(cart.getItems()).isEmpty();

        verify(bookRepository).save(book);
        verify(cartRepository).save(cart);
    }

    @Test
    void should_cancel_cart_and_clear_items() {
        CartItem item = CartItem.builder().book(book).quantity(2).build();
        cart.getItems().add(item);

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.cancel(cart.getId());

        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }
}
