package com.renova.renova.features.auth;

import com.renova.renova.features.auth.dto.AuthResponse;
import com.renova.renova.features.auth.dto.LoginRequest;
import com.renova.renova.features.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {


    @Autowired
    AuthenticationManager authManager;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest data){
        var emailPassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authManager.authenticate(emailPassword);

        return ResponseEntity.ok().build();
    }

}
