package com.benchpress200.authpoc.controller;

import com.benchpress200.authpoc.controller.request.RegisterRequest;
import com.benchpress200.authpoc.controller.response.RegisterResponse;
import com.benchpress200.authpoc.entity.User;
import com.benchpress200.authpoc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(username)
                .password(password)
                .role("USER")
                .build();

        user = userRepository.save(user);

        RegisterResponse response = RegisterResponse.from(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
