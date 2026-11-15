package com.renova.renova.features.auth;

import com.renova.renova.features.auth.dto.LoginRequest;
import com.renova.renova.features.auth.dto.CostumerRegistrationRequest;
import com.renova.renova.features.user.User;
import com.renova.renova.features.user.UserRepository;
import com.renova.renova.features.user.UserRole;
import com.renova.renova.security.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private static final String JWT_COOKIE_NAME = "jwt_token";

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    UserRepository repository;

    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest data, HttpServletResponse response) {
        var emailPassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authManager.authenticate(emailPassword);

        var token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));

        this.addJwtCookie(response, token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register-costumer")
    public ResponseEntity<Void> register(@RequestBody @Valid CostumerRegistrationRequest data, HttpServletResponse response) {
        if (this.repository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.email(), encryptedPassword, data.name(), data.phone(), UserRole.CUSTOMER);

        this.repository.save(newUser);

        var token = tokenService.generateToken(newUser);

        this.addJwtCookie(response, token);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        ResponseCookie cleanCookie = ResponseCookie.from(JWT_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false) // mudar para true em produção
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());

        return ResponseEntity.ok().build();
    }

    private void addJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false) // mudar para true em produção (HTTPS)
                .path("/")
                .maxAge(7200)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}