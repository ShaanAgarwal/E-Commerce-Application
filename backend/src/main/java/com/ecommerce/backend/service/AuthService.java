package com.ecommerce.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.backend.dto.LoginDTO;
import com.ecommerce.backend.dto.UserDTO;
import com.ecommerce.backend.exception.EmailAlreadyExistsException;
import com.ecommerce.backend.exception.InvalidCredentialsException;
import com.ecommerce.backend.model.Photo;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.PhotoType;
import com.ecommerce.backend.model.enums.Status;
import com.ecommerce.backend.repository.PhotoRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(UserDTO userDTO, MultipartFile file) throws IOException {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = createUser(userDTO);
        User savedUser = userRepository.save(user);
        String photoUrl = uploadPhotoToCloudinary(file);
        Photo photo = createPhoto(photoUrl);
        photo.setUserId(savedUser.getId());
        Photo savedPhoto = photoRepository.save(photo);
        savedUser.setPhoto(savedPhoto);
        userRepository.save(savedUser);
        return savedUser;
    }

    public String login(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());
        User user = userOptional.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return jwtUtil.generateToken(user.getEmail());
    }

    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElseThrow(() -> new InvalidCredentialsException("User not found with email: " + email));
    }

    private Photo createPhoto(String photoUrl) {
        Photo photo = new Photo();
        photo.setUrl(photoUrl);
        photo.setPhotoType(PhotoType.USER);
        photo.setStatus(Status.ACTIVE);
        photo.setDescription("User Photo");
        LocalDateTime now = LocalDateTime.now();
        photo.setCreatedAt(now);
        photo.setUpdatedAt(now);
        return photo;
    }

    private User createUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setUserType(userDTO.getUserType());
        user.setStatus(Status.ACTIVE);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private String uploadPhotoToCloudinary(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }
}
