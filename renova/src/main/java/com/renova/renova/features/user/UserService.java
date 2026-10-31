package com.renova.renova.features.user;

import com.renova.renova.features.user.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public User getProfile(String email) {
        return repository.findByEmail(email);
    }

    public User updateProfile(String email, UserUpdateDTO data) {
        User user = repository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found.");

        user.setName(data.name());
        user.setPhone(data.phone());

        return repository.save(user);
    }
}