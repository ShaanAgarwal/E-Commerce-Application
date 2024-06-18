package com.ecommerce.backend.model;

import com.ecommerce.backend.model.enums.PhotoType;
import com.ecommerce.backend.model.enums.Status;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "photo")
public class Photo {

    @Id
    private String id;
    private String url;
    private PhotoType photoType;
    private Status status;
    private String description;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
