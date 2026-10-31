package com.renova.renova.features.user;

import com.renova.renova.features.user.dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    // retorna os dados do usuario logado
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal User loggedUser) {
        // O loggedUser já contém as informações descodificadas do token!
        return ResponseEntity.ok(userService.getProfile(loggedUser.getEmail()));
    }

    // atualiza os dados do usuario logado
    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            @AuthenticationPrincipal User loggedUser,
            @RequestBody @Valid UserUpdateDTO data) {

        User updatedUser = userService.updateProfile(loggedUser.getEmail(), data);
        return ResponseEntity.ok(updatedUser);
    }
}