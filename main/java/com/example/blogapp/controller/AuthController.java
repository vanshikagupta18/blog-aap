package com.example.blogapp.controller;

import com.example.blogapp.entity.User;
import com.example.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // ✅ Allow React frontend
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ✅ Register new user (email + password)
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return "User already exists with this email!";
        }

        userRepository.save(user);
        return "User registered successfully!";
    }

    // ✅ Normal login with email + password
    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent() &&
                existingUser.get().getPassword().equals(user.getPassword())) {
            return "Login successful!";
        } else {
            return "Invalid credentials!";
        }
    }

    // ✅ Google login/register endpoint
    @PostMapping("/google")
    public String googleLogin(@RequestBody User googleUser) {
        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByEmail(googleUser.getEmail());

        if (existingUser.isPresent()) {
            // If exists, just welcome them
            return "Welcome back, " + existingUser.get().getName() + "!";
        } else {
            // Create a new user entry (no password)
            User newUser = new User();
            newUser.setName(googleUser.getName());
            newUser.setEmail(googleUser.getEmail());
            newUser.setPassword("GOOGLE_USER"); // placeholder, not used
            userRepository.save(newUser);
            return "New Google user registered: " + newUser.getName();
        }
    }
}
