package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.SellerDTO;
import com.ecommerce.backend.dto.SellerInfoDTO;
import com.ecommerce.backend.exception.ForbiddenException;
import com.ecommerce.backend.exception.InvalidCredentialsException;
import com.ecommerce.backend.exception.UserAlreadyExistsException;
import com.ecommerce.backend.exception.UserDoesNotExistException;
import com.ecommerce.backend.model.Seller;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.UserType;
import com.ecommerce.backend.repository.SellerRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    public Seller registerSeller(String id, SellerDTO seller) {
        validateSellerDTO(seller);
        User user = findUserById(id);
        validateUserIsSeller(user);
        checkIfSellerAlreadyExists(id);
        return createAndSaveSeller(id, seller);
    }

    public List<SellerInfoDTO> getAllSellers() {
        List<User> sellers = userRepository.findByUserType(UserType.SELLER);
        List<SellerInfoDTO> sellerInfoList = new ArrayList<>();

        for (User seller : sellers) {
            Optional<Seller> optionalSeller = sellerRepository.findById(seller.getId());
            if (optionalSeller.isPresent()) {
                Seller sellerEntity = optionalSeller.get();
                SellerInfoDTO sellerInfoDTO = new SellerInfoDTO(
                        seller.getId(),
                        seller.getFirstName(),
                        seller.getLastName(),
                        seller.getEmail(),
                        sellerEntity.getCompanyName(),
                        sellerEntity.getAddress(),
                        seller.getStatus(),
                        seller.getCreatedAt(),
                        seller.getUpdatedAt()
                );
                sellerInfoList.add(sellerInfoDTO);
            }
        }

        return sellerInfoList;
    }

    private void validateSellerDTO(SellerDTO seller) {
        if (seller.getAddress() == null || seller.getCompanyName() == null) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    private User findUserById(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserDoesNotExistException("User with the given id does not exist");
        }
        return optionalUser.get();
    }

    private void validateUserIsSeller(User user) {
        if (!user.getUserType().name().equals("SELLER")) {
            throw new ForbiddenException("You are not allowed to access this resource");
        }
    }

    private void checkIfSellerAlreadyExists(String id) {
        Optional<Seller> optionalSeller = sellerRepository.findById(id);
        if (optionalSeller.isPresent()) {
            throw new UserAlreadyExistsException("Seller already exists with the given id");
        }
    }

    private Seller createAndSaveSeller(String id, SellerDTO seller) {
        LocalDateTime now = LocalDateTime.now();
        Seller newSeller = new Seller();
        newSeller.setId(id);
        newSeller.setAddress(seller.getAddress());
        newSeller.setCompanyName(seller.getCompanyName());
        newSeller.setCreatedAt(now);
        newSeller.setUpdatedAt(now);
        sellerRepository.save(newSeller);
        return newSeller;
    }
}
