package com.ecommerce.backend.controller;

import com.ecommerce.backend.exception.ForbiddenException;
import com.ecommerce.backend.exception.UnauthorizedException;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.service.UserService;
import com.ecommerce.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String tokenHeader) {
        String token = getTokenFromHeader(tokenHeader);
        jwtUtil.validateToken(token);
        String userType = jwtUtil.extractUserType(token);
        String userStatus = jwtUtil.extractUserStatus(token);
        if (!"ADMIN".equals(userType)) {
            throw new ForbiddenException("Access denied. Only admin users can access this endpoint");
        }
        if(!"ACTIVE".equals(userType)) {
            throw new ForbiddenException("Access denied. You are denied access to this resource");
        }
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    private String getTokenFromHeader(String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Unauthorized. Missing or invalid Authorization header");
        }
        return tokenHeader.substring(7);
    }
}
