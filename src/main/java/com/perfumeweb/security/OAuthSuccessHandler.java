package com.perfumeweb.security;

import com.perfumeweb.model.User;
import com.perfumeweb.security.JwtUtil;
import com.perfumeweb.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuthSuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        if (email == null) throw new RuntimeException("Google email missing");
        String name = oauthUser.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setName(name);
                    u.setRole("USER");
                    u.setProvider("GOOGLE");
                    u.setPassword("");
                    return userRepository.save(u);
                });

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getProvider()
        );

        response.sendRedirect(
                "http://localhost:3000/auth-success?token=" + token
        );
    }
}
