package com.ecommerce.backend.service;

import com.ecommerce.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(String userId);
}