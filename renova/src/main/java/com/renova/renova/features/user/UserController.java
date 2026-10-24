package com.renova.renova.features.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    // POST /usuarios
    @PostMapping
    public void createUser(@RequestBody User user) throws Exception {
        UserService userService = new UserService();
        userService.registerUser(user);
    }

    // POST /usuarios/login
    @PostMapping("/login")
    public User login(@RequestBody User credentials) {
        UserService userService = new UserService();
        return userService.login(credentials);
    }

}