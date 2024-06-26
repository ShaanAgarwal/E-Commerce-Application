package com.ecommerce.backend.util;

import com.ecommerce.backend.exception.ForbiddenException;
import com.ecommerce.backend.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    public String generateToken(String email, String userType, String userStatus) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userType", userType)
                .claim("userStatus", userStatus)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUserType(String token) {
        return (String) extractClaims(token).get("userType");
    }

    public String extractUserStatus(String token) {
        return (String) extractClaims(token).get("userStatus");
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getTokenFromHeader(String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Unauthorized. Missing or invalid Authorization header");
        }
        return tokenHeader.substring(7);
    }

    public void validateAdminAccess(String token) {
        String userType = extractUserType(token);
        String userStatus = extractUserStatus(token);

        if (!"ADMIN".equals(userType)) {
            throw new ForbiddenException("Access denied. Only admin users can access this endpoint");
        }
        if (!"ACTIVE".equals(userStatus)) {
            throw new ForbiddenException("Access denied. You are denied access to this resource");
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
