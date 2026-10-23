package com.renova.renova.Pck_Controller;

import com.renova.renova.Pck_Model.Pck_Entities.UsuarioModel;
import com.renova.renova.Pck_Persistencia.UsuarioPersistencia;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioControl {

    // POST /usuarios
    @PostMapping
    public void cadastrarUsuario(@RequestBody UsuarioModel usuario) throws Exception {
        UsuarioPersistencia persistencia = new UsuarioPersistencia();
        persistencia.CadastroUsuario(usuario);
    }

    // POST /usuarios/login
    @PostMapping("/login")
    public UsuarioModel realizarLogin(@RequestBody UsuarioModel credenciais) {
        UsuarioPersistencia persistencia = new UsuarioPersistencia();
        return persistencia.login(credenciais);
    }
}