package com.renova.renova.features.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    // POST /usuarios
    @PostMapping
    public void cadastrarUsuario(@RequestBody User usuario) throws Exception {
        UserService persistencia = new UserService();
        persistencia.CadastroUsuario(usuario);
    }

    // POST /usuarios/login
    @PostMapping("/login")
    public User realizarLogin(@RequestBody User credenciais) {
        UserService persistencia = new UserService();
        return persistencia.login(credenciais);
    }


}