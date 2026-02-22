package com.perfumeweb.service;

import com.perfumeweb.dto.AuthResponse;
import com.perfumeweb.dto.ChangePasswordRequest;
import com.perfumeweb.dto.ProfileUpdateRequest;
import com.perfumeweb.dto.UserLoginRequest;
import com.perfumeweb.dto.UserRegisterRequest;
import com.perfumeweb.model.Role;
import com.perfumeweb.model.User;
import com.perfumeweb.repository.UserRepository;
import com.perfumeweb.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ================= REGISTER =================
    public AuthResponse register(UserRegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Email already registered: {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 🔥 ENUM ROLE
        user.setRole(Role.USER);

        user.setProvider("LOCAL");

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getName(),
                user.getRole().name(),   // 🔥 convert enum to string
                user.getProvider()
        );

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());

        log.info("User registered + auto login: {}", user.getEmail());

        return response;
    }

    // ================= LOGIN =================
    public AuthResponse login(UserLoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getName(),
                    user.getRole().name(),   // 🔥 enum fix
                    user.getProvider()
            );

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setRole(user.getRole().name());

            log.info("Login successful: {}", request.getEmail());

            return response;

        } catch (AuthenticationException e) {
            log.warn("Login failed: {}", request.getEmail());
            throw new org.springframework.security.authentication
                    .BadCredentialsException("Invalid credentials");
        }
    }

    // ================= PROFILE UPDATE =================
    public User updateProfile(String email, ProfileUpdateRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        return userRepository.save(user);
    }

    // ================= PASSWORD DTO ENTRY =================
    public void changePassword(String email, ChangePasswordRequest request) {
        changePassword(email, request.getCurrentPassword(), request.getNewPassword());
    }

    // ================= PASSWORD CORE LOGIC =================
    public void changePassword(String email, String current, String newPass) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Google login users cannot change password
        if (!"LOCAL".equals(user.getProvider())) {
            throw new RuntimeException("OAuth user cannot change password");
        }

        // verify current password
        if (!passwordEncoder.matches(current, user.getPassword())) {
            throw new RuntimeException("WRONG_PASSWORD");
        }

        // update password
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);
    }
}
