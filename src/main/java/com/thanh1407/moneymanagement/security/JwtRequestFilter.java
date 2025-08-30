package com.thanh1407.moneymanagement.security;

import com.thanh1407.moneymanagement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;
        
        // Debug: In ra request info
        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Auth Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("Extracted JWT: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
            
            try {
                email = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted email from JWT: " + email);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                System.err.println("JWT Token has expired: " + e.getMessage());
                // Không return ở đây, để filter chain tiếp tục
                email = null;
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                System.err.println("Invalid JWT format: " + e.getMessage());
                email = null;
            } catch (io.jsonwebtoken.security.SignatureException e) {
                System.err.println("JWT signature validation failed: " + e.getMessage());
                email = null;
            } catch (Exception e) {
                System.err.println("Error extracting username from JWT: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                email = null;
            }
        } else {
            System.out.println("No Authorization header or not Bearer token");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("User details loaded for: " + email);

                if (jwtUtil.validateToken(jwt, email)) {
                    System.out.println("Token validated successfully for: " + email);
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.err.println("Token validation failed for: " + email);
                }
            } catch (UsernameNotFoundException e) {
                System.err.println("User not found or not active: " + e.getMessage());
            }
        }
        
        System.out.println("Authentication status: " + 
            (SecurityContextHolder.getContext().getAuthentication() != null ? "AUTHENTICATED" : "NOT_AUTHENTICATED"));
        System.out.println("========================");

        filterChain.doFilter(request, response);
    }
}
