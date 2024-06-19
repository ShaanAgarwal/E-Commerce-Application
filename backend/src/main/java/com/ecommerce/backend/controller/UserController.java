package com.ecommerce.backend.controller;

import com.ecommerce.backend.exception.ForbiddenException;
import com.ecommerce.backend.exception.UnauthorizedException;
import com.ecommerce.backend.exception.UserDoesNotExistException;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.service.UserService;
import com.ecommerce.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String tokenHeader) {
        String token = jwtUtil.getTokenFromHeader(tokenHeader);
        jwtUtil.validateToken(token);
        jwtUtil.validateAdminAccess(token);

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getSingleUser/{userId}")
    public ResponseEntity<?> getSingleUser(@RequestHeader("Authorization") String tokenHeader, @PathVariable("userId") String userId) {
        String token = jwtUtil.getTokenFromHeader(tokenHeader);
        jwtUtil.validateToken(token);
        jwtUtil.validateAdminAccess(token);

        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()) {
            throw new UserDoesNotExistException("User with the given userId does not exist.");
        }

        return ResponseEntity.ok(user.get());
    }
}
