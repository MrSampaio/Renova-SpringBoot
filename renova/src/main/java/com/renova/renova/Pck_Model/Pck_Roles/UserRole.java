package com.renova.renova.Pck_Model.Pck_Roles;

public enum UserRole {
    FUNCIONARIO("funcionario"),
    CLIENTE("cliente");

    private String role;

    UserRole(String role){
        this.role = role;
    }
}
