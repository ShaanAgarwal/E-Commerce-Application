package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.UserType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    List<User> findByUserType(UserType userType);
}
