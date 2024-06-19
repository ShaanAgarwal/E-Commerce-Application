package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.LoginDTO;
import com.ecommerce.backend.dto.UserDTO;
import com.ecommerce.backend.exception.EmailAlreadyExistsException;
import com.ecommerce.backend.exception.ForbiddenException;
import com.ecommerce.backend.exception.InvalidCredentialsException;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam("userDTO") String userDTOString, @RequestParam("file") MultipartFile file) {
        try {
            // Deserialize userDTOString to UserDTO object
            UserDTO userDTO = objectMapper.readValue(userDTOString, UserDTO.class);

            // Validate required fields in userDTO
            if (userDTO.getFirstName() == null || userDTO.getLastName() == null || userDTO.getEmail() == null || userDTO.getPassword() == null || userDTO.getUserType() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required user fields"));
            }

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            User registeredUser = authService.registerUser(userDTO, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid JSON for UserDTO"));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            // Authenticate user and generate JWT token
            String token = authService.login(loginDTO);

            // Fetch user details by email
            User user = authService.getUserByEmail(loginDTO.getEmail());

            if(!user.getStatus().toString().equals("ACTIVE")) {
                throw new ForbiddenException("You are not allowed to login in the system");
            }

            // Prepare response with user details and token
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
