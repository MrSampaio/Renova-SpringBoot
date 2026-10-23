package com.renova.renova.features.user;

public enum UserRole {
    FUNCIONARIO("funcionario"),
    CLIENTE("cliente");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
