package com.renova.renova.features.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idUsuario;
    @Getter
    private String nome;
    @Getter
    private String email;
    private String senha;
    @Getter
    private String telefone;
    @Getter
    @Setter
    private UserRole userRole;

    public String getSenha() {
        return senha;
    }

    public void setEmail(String email) {
        // regex que define como um e-mail válido deve parecer
        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        // verifica se o email é nulo e se base com os requisitos do regex
        if (email != null && email.matches(regexEmail)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("E-mail com formato inválido! Use o padrão nome@email.com.");
        }
    }

    public void setSenha(String senha) {

        if(senha.isEmpty() || senha == null || senha.length() < 5){
            throw new IllegalArgumentException("Senha Inválida! Informe uma senha com pelo menos 5 dígitos.");
        } else{
            this.senha = senha;
        }
    }

    public void setTelefone(String telefone) {
        if (telefone.length() != 11){
            throw new IllegalArgumentException("Número de telefone inválido! Informe um telefone com o 11 digitos.");
        } else{
            this.telefone = telefone;
        }

    }

    public void setNome(String nome) {
        if(nome == null || nome.isEmpty() || nome.length() <= 0){
            throw new IllegalArgumentException("O campo de 'nome' é obrigatório!");
        } else{
            this.nome = nome;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.userRole == UserRole.FUNCIONARIO) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return nome;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
