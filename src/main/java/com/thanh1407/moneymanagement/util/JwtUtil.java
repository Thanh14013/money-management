package com.thanh1407.moneymanagement.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${api.secret.key}")
    private String SECRET_KEY;

    // Thời gian sống của token (10 phút)
    private final long EXPIRATION_TIME = 1000 * 60 * 10;

    // Tạo secure key - luôn đảm bảo đủ 256 bits
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

        // Nếu key quá ngắn (< 32 bytes = 256 bits), tạo key mới an toàn
        if (keyBytes.length < 32) {
            System.out.println("Warning: Secret key too short (" + keyBytes.length * 8 + " bits), generating secure key");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        return Keys.hmacShaKeyFor(keyBytes);
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
}
