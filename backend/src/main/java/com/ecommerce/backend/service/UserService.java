package com.ecommerce.backend.service;

import com.ecommerce.backend.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
}