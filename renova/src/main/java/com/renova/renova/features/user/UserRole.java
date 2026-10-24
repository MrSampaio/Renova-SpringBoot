package com.renova.renova.features.user;

public enum UserRole {
    EMPLOYEE("EMPLOYEE"),
    CUSTOMER("CUSTOMER");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static UserRole fromRole(String role) {
        for (UserRole userRole : values()) {
            if (userRole.role.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + role);
    }
}