package com.perfumeweb.controller;

import com.perfumeweb.dto.ChangePasswordRequest;
import com.perfumeweb.model.User;
import com.perfumeweb.repository.UserRepository;
import com.perfumeweb.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // ================= GET PROFILE =================
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @RequestBody User updated,
            Principal principal
    ) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // only editable fields
        if (updated.getName() != null) {
            user.setName(updated.getName());
        }

        if (updated.getPhone() != null) {
            user.setPhone(updated.getPhone());
        }

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    // ================= CHANGE PASSWORD =================
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal principal
    ) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        userService.changePassword(
                principal.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok("Password updated");
    }

}
