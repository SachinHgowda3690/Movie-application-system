package com.controller;

import com.model.User;
import com.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String,String> body, HttpServletResponse res) {
        String username = body.get("username"), email = body.get("email"), password = body.get("password");

        if (userRepo.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email already registered"));
        }

        User user = new User();
        userRepo.save(user);

        String token = Jwts.builder()
                .claim("userId", user.getId())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        Cookie cookie = new Cookie("jwt-streamify", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24*60*60);
        cookie.setPath("/");
        res.addCookie(cookie);

        user.setPassword("");
        return ResponseEntity.status(201).body(Map.of("success", true, "user", user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body, HttpServletResponse res) {
        String email = body.get("email"), password = body.get("password");
        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null || !encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Invalid credentials"));
        }

        String token = Jwts.builder()
                .claim("userId", user.getId())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        Cookie cookie = new Cookie("jwt-streamify", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24*60*60);
        cookie.setPath("/");
        res.addCookie(cookie);

        user.setPassword("");
        return ResponseEntity.ok(Map.of("success", true, "user", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("jwt-streamify", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        res.addCookie(cookie);
        return ResponseEntity.ok(Map.of("success", true, "message", "Logout successful"));
    }

    @GetMapping("/authCheck")
    public ResponseEntity<?> authCheck(HttpServletRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(Map.of("success", true, "user", user));
        }
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
    }
}