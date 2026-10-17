package com.renova.renova.Pck_Controller;

import com.renova.renova.Pck_Model.UsuarioModel;
import com.renova.renova.Pck_Persistencia.UsuarioPersistencia;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsuarioControl {

    @PostMapping("/cadastrar-usuario")
    public void cadastrarUsuario(@RequestBody UsuarioModel usuario) throws Exception{
        UsuarioModel cadastroUsuario = new UsuarioModel();

        cadastroUsuario.setEmail(usuario.getEmail());
        cadastroUsuario.setSenha(usuario.getSenha());
        cadastroUsuario.setTelefone(usuario.getTelefone());
        cadastroUsuario.setNome(usuario.getNome());
        cadastroUsuario.setTipoUsuario(usuario.getTipoUsuario());

        UsuarioPersistencia cadastroPersistencia = new UsuarioPersistencia();
        cadastroPersistencia.CadastroUsuario(cadastroUsuario);

    }

    @GetMapping("/realizar-login")
    public UsuarioModel realizarLogin(String email, String senha) {

        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail(email);
        usuario.setSenha(senha);

        UsuarioPersistencia persistencia = new UsuarioPersistencia();
        return persistencia.login(usuario);
    }
}
