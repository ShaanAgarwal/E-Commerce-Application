package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Seller;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SellerRepository extends MongoRepository<Seller, String> {
}
