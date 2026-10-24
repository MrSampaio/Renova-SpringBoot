package com.renova.renova.features.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole userRole) {
        return userRole == null ? null : userRole.getRole();
    }

    @Override
    public UserRole convertToEntityAttribute(String value) {
        return value == null ? null : UserRole.fromRole(value);
    }
}