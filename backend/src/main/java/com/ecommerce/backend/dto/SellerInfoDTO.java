package com.ecommerce.backend.dto;

import com.ecommerce.backend.model.enums.Status;
import com.ecommerce.backend.model.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerInfoDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private String address;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
