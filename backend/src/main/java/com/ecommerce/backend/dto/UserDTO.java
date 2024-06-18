package com.ecommerce.backend.dto;

import com.ecommerce.backend.model.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
public class UserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserType userType;

}
