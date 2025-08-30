package com.thanh1407.moneymanagement.controller;

import com.thanh1407.moneymanagement.dto.AuthDTO;
import com.thanh1407.moneymanagement.dto.ProfileDTO;
import com.thanh1407.moneymanagement.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO createdProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean activated = profileService.activateProfile(token);
        if(activated){
            return ResponseEntity.ok("Profile activated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation token");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO){
        try {
           Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
           return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            // Lỗi từ AppUserDetailsService
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", e.getMessage(),
                    "error", "ACCOUNT_NOT_ACTIVATED_OR_NOT_FOUND"
            ));
        } catch (IllegalArgumentException e) {
            // Lỗi từ ProfileService
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", e.getMessage(),
                    "error", "INVALID_CREDENTIALS"
            ));
        } catch (Exception e) {
            // Lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Authentication failed: " + e.getMessage(),
                    "error", "AUTHENTICATION_ERROR",
                    "type", e.getClass().getSimpleName()
            ));
        }
    }
}
