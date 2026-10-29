package com.renova.renova.features.auth;

import com.renova.renova.features.auth.dto.AuthResponse;
import com.renova.renova.features.auth.dto.LoginRequest;
import com.renova.renova.features.auth.dto.CostumerRegistrationRequest;
import com.renova.renova.features.user.User;
import com.renova.renova.features.user.UserRepository;
import com.renova.renova.features.user.UserRole;
import com.renova.renova.security.TokenService;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

@RestController
@RequestMapping("auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    UserRepository repository;

    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest data) {
        var emailPassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authManager.authenticate(emailPassword);

        var token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));

        // criação do cookie HTTP-Only
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true); // oculta do JavaScript
        cookie.setSecure(false); // MUDAR PARA TRUE QUANDO FOR PRA PRODUÇÃO
        cookie.setPath("/"); // libera para todas as rotas da API
        cookie.setMaxAge(7200); // duraçåo de duas horas

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register-costumer")
    public ResponseEntity register(@RequestBody @Valid CostumerRegistrationRequest data) {
        if (this.repository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.email(), encryptedPassword, data.name(), data.phone(), UserRole.CUSTOMER);

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
