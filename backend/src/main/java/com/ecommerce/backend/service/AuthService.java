package com.ecommerce.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.backend.dto.UserDTO;
import com.ecommerce.backend.exception.EmailAlreadyExistsException;
import com.ecommerce.backend.model.Photo;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.PhotoType;
import com.ecommerce.backend.model.enums.Status;
import com.ecommerce.backend.repository.PhotoRepository;
import com.ecommerce.backend.repository.UserRepository;
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

    @Transactional
    public User registerUser(UserDTO userDTO, MultipartFile file) throws IOException {
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Upload photo to Cloudinary
        String photoUrl = uploadPhotoToCloudinary(file);

        // Create Photo object
        Photo photo = new Photo();
        photo.setUrl(photoUrl);
        photo.setPhotoType(PhotoType.USER);
        photo.setStatus(Status.ACTIVE);
        photo.setDescription("User Photo");
        photo.setCreatedAt(LocalDateTime.now());
        photo.setUpdatedAt(LocalDateTime.now());

        // Save Photo
        Photo savedPhoto = photoRepository.save(photo);

        // Create User object with saved Photo
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setUserType(userDTO.getUserType());
        user.setStatus(Status.ACTIVE);
        user.setPhoto(savedPhoto);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save User
        return userRepository.save(user);
    }

    private String uploadPhotoToCloudinary(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }
}
