package com.thanh1407.moneymanagement.service;

import com.thanh1407.moneymanagement.dto.AuthDTO;
import com.thanh1407.moneymanagement.dto.ProfileDTO;
import com.thanh1407.moneymanagement.entity.ProfileEntity;
import com.thanh1407.moneymanagement.repository.ProfileRepository;
import com.thanh1407.moneymanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    // private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        // Tự động kích hoạt tài khoản ngay khi đăng ký
        newProfile.setIsActive(true);
        newProfile = profileRepository.save(newProfile);
        
        // Comment out email sending functionality - không cần gửi email kích hoạt nữa
        /*
        //send activate email
        String activationLink =  "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Money Manager Account";
        String body = "Click Here " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        */
        
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .email(profileDTO.getEmail())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .isActive(true) // Mặc định kích hoạt ngay
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity entity) {
        return ProfileDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken) // Optional<ProfileEntity>
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }) // Optional<Boolean>
                .orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        }else{
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            System.out.println("Attempting authentication for email: " + authDTO.getEmail());
            System.out.println("Password provided (raw): " + authDTO.getPassword());

            // Kiểm tra tài khoản trước khi authenticate
            ProfileEntity user = profileRepository.findByEmail(authDTO.getEmail()).orElse(null);
            if (user != null) {
                System.out.println("User found in database:");
                System.out.println("- Email: " + user.getEmail());
                System.out.println("- IsActive: " + user.getIsActive());
                System.out.println("- Password hash: " + user.getPassword());

                // Tự động kích hoạt tài khoản nếu chưa được kích hoạt (cho dev testing)
                if (user.getIsActive() == null || !user.getIsActive()) {
                    System.out.println("Account not activated, auto-activating for development...");
                    user.setIsActive(true);
                    profileRepository.save(user);
                    System.out.println("Account activated successfully");
                }
            } else {
                System.err.println("User not found in database for email: " + authDTO.getEmail());
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            System.out.println("Authentication successful for: " + authDTO.getEmail());

            //Generate token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            System.out.println("Token generated successfully");

            ProfileDTO userProfile = getPublicProfile(authDTO.getEmail());
            System.out.println("User profile retrieved successfully");

            return Map.of(
                    "token", token,
                    "user", userProfile
            );
        }catch (org.springframework.security.authentication.BadCredentialsException e){
            System.err.println("Bad credentials for email: " + authDTO.getEmail());
            System.err.println("BadCredentialsException details: " + e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }catch (org.springframework.security.authentication.DisabledException e){
            System.err.println("Account disabled for email: " + authDTO.getEmail());
            throw new IllegalArgumentException("Account is disabled");
        }catch (Exception e){
            System.err.println("Authentication error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("Authentication failed: " + e.getMessage());
        }
    }
}
