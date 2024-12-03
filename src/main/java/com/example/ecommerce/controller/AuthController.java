package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.security.JwtTokenProvider;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;


    @PostMapping("/admin-login")
    public ResponseEntity<?> authenticateAdmin(@RequestBody User loginUser) {
        if ("admin".equals(loginUser.getUsername()) && "admin".equals(loginUser.getPassword())) {
            String token = tokenProvider.generateToken("admin");
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", "ADMIN");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin credentials");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        String token = tokenProvider.generateToken(newUser.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User loginUser) {
        User user = userService.findByUsername(loginUser.getUsername());
        if (user != null && userService.checkPassword(loginUser.getPassword(), user.getPassword())) {
            String token = tokenProvider.generateToken(user.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("Invalid username or password");
    }
}
