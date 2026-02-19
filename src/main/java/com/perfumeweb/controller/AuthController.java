package com.perfumeweb.controller;

import com.perfumeweb.dto.*;
import com.perfumeweb.model.User;
import com.perfumeweb.repository.UserRepository;
import com.perfumeweb.service.OtpService;
import com.perfumeweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ signup
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @RequestBody UserRegisterRequest request) {

        return ResponseEntity.ok(userService.register(request));
    }

    // ✅ login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody UserLoginRequest request) {

        return ResponseEntity.ok(userService.login(request));
    }

    // ✅ reset password with OTP
    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("password");

        if (!otpService.verify(email, otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password changed");
    }

    // ✅ forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.ok("Account does not exist");
        }

        if (!user.getProvider().equals("LOCAL")) {
            return ResponseEntity.ok(
                    "This account was created using a social login (" +
                            user.getProvider() +
                            "). Please sign in using that provider."
            );
        }

        otpService.createOtp(email);

        return ResponseEntity.ok("OTP sent successfully");
    }

    // ✅ profile update
    @PutMapping("/profile")
    public User updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Principal principal
    ) {
        return userService.updateProfile(principal.getName(), request);
    }

    // ✅ password change FINAL
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal principal
    ) {
        try {

            userService.changePassword(
                    principal.getName(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );

            return ResponseEntity.ok("Password updated");

        } catch (RuntimeException e) {

            if ("Wrong current password".equals(e.getMessage())) {
                return ResponseEntity.badRequest().body("Wrong current password");
            }

            if ("OAuth user cannot change password".equals(e.getMessage())) {
                return ResponseEntity.badRequest().body("Google login users cannot change password");
            }

            return ResponseEntity.status(500).body("Server error");
        }
    }
}
