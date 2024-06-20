package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.SellerDTO;
import com.ecommerce.backend.model.Seller;
import com.ecommerce.backend.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerDTO sellerDTO;

    @Autowired
    private SellerService sellerService;

    @PostMapping("/registerSeller/{id}")
    public ResponseEntity<?> registerSeller(@PathVariable("id") String id, @RequestBody SellerDTO seller) {
        Seller newSeller = sellerService.registerSeller(id, seller);
        return ResponseEntity.ok(newSeller);
    }

}