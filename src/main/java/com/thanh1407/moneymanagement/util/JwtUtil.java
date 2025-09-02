package com.thanh1407.moneymanagement.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${api.secret.key}")
    private String SECRET_KEY;

    // Thời gian sống của token (1 tiếng)
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // Cache signing key để đảm bảo consistency
    private SecretKey cachedSigningKey;

    // Tạo secure key - luôn đảm bảo đủ 256 bits và consistent
    private SecretKey getSigningKey() {
        if (cachedSigningKey != null) {
            return cachedSigningKey;
        }

        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

        // Nếu key đủ dài (>= 32 bytes = 256 bits), sử dụng trực tiếp
        if (keyBytes.length >= 32) {
            cachedSigningKey = Keys.hmacShaKeyFor(keyBytes);
        } else {
            // Key quá ngắn - tạo key t�� chuỗi bằng cách pad/hash
            System.out.println("Warning: Secret key too short (" + keyBytes.length * 8 + " bits), extending key");
            // Tạo key 32 bytes từ secret bằng cách lặp lại
            byte[] extendedKey = new byte[32];
            for (int i = 0; i < 32; i++) {
                extendedKey[i] = keyBytes[i % keyBytes.length];
            }
            cachedSigningKey = Keys.hmacShaKeyFor(extendedKey);
        }

        return cachedSigningKey;
    }

    // Hàm generate token từ username
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // lưu username vào subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // thời gian phát hành
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // thời gian hết hạn
                .signWith(getSigningKey()) // ký bằng secure key
                .compact();
    }

    // Trích xuất email/username từ JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Trích xuất claims từ token
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Trích xuất tất cả claims từ token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Kiểm tra token có hết hạn chưa
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Trích xuất thời gian hết hạn
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Validate token
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean isUsernameValid = extractedUsername.equals(username);
            boolean isTokenNotExpired = !isTokenExpired(token);
            
            System.out.println("Token validation - Username match: " + isUsernameValid + 
                              ", Not expired: " + isTokenNotExpired);
            
            return isUsernameValid && isTokenNotExpired;
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return false;
        }
    }
}
