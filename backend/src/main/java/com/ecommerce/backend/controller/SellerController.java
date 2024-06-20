package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.SellerDTO;
import com.ecommerce.backend.dto.SellerInfoDTO;
import com.ecommerce.backend.model.Seller;
import com.ecommerce.backend.service.SellerService;
import com.ecommerce.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerDTO sellerDTO;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registerSeller/{id}")
    public ResponseEntity<?> registerSeller(@PathVariable("id") String id, @RequestBody SellerDTO seller) {
        Seller newSeller = sellerService.registerSeller(id, seller);
        return ResponseEntity.ok(newSeller);
    }

    @GetMapping("/getAllSellers")
    public ResponseEntity<?> getAllSellers(@RequestHeader("Authorization") String tokenHeader) {
        String token = jwtUtil.getTokenFromHeader(tokenHeader);
        jwtUtil.validateToken(token);
        jwtUtil.validateAdminAccess(token);
        List<SellerInfoDTO> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

}