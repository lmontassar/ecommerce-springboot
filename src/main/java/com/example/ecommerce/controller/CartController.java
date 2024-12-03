package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(  @RequestParam String username  ) {
    User user = userService.findByUsername(username);
    return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestParam String username,
                                          @RequestParam Long productId,
                                          @RequestParam Integer quantity) {
        System.out.print(username);
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(cartService.addToCart(user, productId, quantity));
    }
    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeFromCart(@RequestParam String username,
                                               @RequestParam Long productId) {
        User user = userService.findByUsername(username);
        Cart updatedCart = cartService.removeFromCart(user, productId);
        return ResponseEntity.ok(updatedCart);
    }
    @GetMapping("/user/{username}")
    public ResponseEntity<Cart> getCartByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = cartService.getCart(user);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateCartItem(@RequestParam String username,
                                               @RequestParam Long productId,
                                               @RequestParam Integer quantity) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart updatedCart = cartService.updateCartItem(user, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/add2")
    public ResponseEntity<Cart> addToCart2(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Long productId = ((Number) payload.get("productId")).longValue();
        Integer quantity = ((Number) payload.get("quantity")).intValue();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(cartService.addToCart(user, productId, quantity));
    }

}