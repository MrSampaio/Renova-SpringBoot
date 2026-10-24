package com.renova.renova.features.auth.dto;

import com.renova.renova.features.user.UserRole;

public record CostumerRegistrationRequest(String name, String email, String password, String phone, UserRole role) {
}
