package com.renova.renova.features.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Getter
    private String name;

    @Getter
    @Column(unique = true)
    @NotNull
    private String email;

    private String password;

    @Getter
    @Column(columnDefinition = "CHAR(11)")
    private String phone;

    @Getter
    @Setter
    @Convert(converter = UserRoleConverter.class)
    private UserRole userRole;

    public User(String email, String password, String name, String phone, UserRole userRole) {
        this.setEmail(email);
        this.setPassword(password);
        this.setName(name);
        this.setPhone(phone);
        this.userRole = userRole;
    }

    public void setEmail(String email) {
        // regex that defines what a valid e-mail looks like
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        // checks that the e-mail is not null and matches the regex
        if (email != null && email.matches(emailRegex)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid e-mail format! Use the pattern name@email.com.");
        }
    }

    public void setPassword(String password) {

        if (password.isEmpty() || password == null || password.length() < 5) {
            throw new IllegalArgumentException("Invalid password! Enter a password with at least 5 characters.");
        } else {
            this.password = password;
        }
    }

    public void setPhone(String phone) {
        if (phone.length() != 11) {
            throw new IllegalArgumentException("Invalid phone number! Enter a phone number with 11 digits.");
        } else {
            this.phone = phone;
        }

    }

    public void setName(String name) {
        if (name == null || name.isEmpty() || name.length() <= 0) {
            throw new IllegalArgumentException("The 'name' field is required!");
        } else {
            this.name = name;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.userRole == UserRole.EMPLOYEE) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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