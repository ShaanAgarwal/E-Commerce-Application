package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<Photo, String> {
}
