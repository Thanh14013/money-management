package com.thanh1407.moneymanagement.service;

import com.thanh1407.moneymanagement.entity.ProfileEntity;
import com.thanh1407.moneymanagement.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Loading user details for email: " + email);

        ProfileEntity existingProfile =  profileRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.err.println("User not found with email: " + email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        System.out.println("User found: " + existingProfile.getEmail());
        System.out.println("User active status: " + existingProfile.getIsActive());
        System.out.println("User password (encoded): " + existingProfile.getPassword());

        // Kiểm tra tài khoản có được kích hoạt hay không
        if (existingProfile.getIsActive() == null || !existingProfile.getIsActive()) {
            System.err.println("Account is not activated for email: " + email);
            throw new UsernameNotFoundException("Account is not activated");
        }

        System.out.println("Returning UserDetails for: " + existingProfile.getEmail());
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
