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

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(UserDTO userDTO, MultipartFile file) throws IOException {
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Create User object
        User user = createUser(userDTO);

        // Save User to generate userId
        User savedUser = userRepository.save(user);

        // Upload photo to Cloudinary
        String photoUrl = uploadPhotoToCloudinary(file);

        // Create Photo object
        Photo photo = createPhoto(photoUrl);

        // Set userId in Photo entity
        photo.setUserId(savedUser.getId()); // Set the generated userId from savedUser

        // Save Photo
        Photo savedPhoto = photoRepository.save(photo);

        // Set the saved photo in user and update user
        savedUser.setPhoto(savedPhoto);
        userRepository.save(savedUser);

        return savedUser;
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
