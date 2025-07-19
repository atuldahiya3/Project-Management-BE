package com.synct.synct.Config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.synct.synct.Service.CustomUserDetailsService;
import com.synct.synct.Util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired 
    private JwtUtil jwtUtil;
    
    @Autowired 
    private CustomUserDetailsService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        
        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Request Path: " + req.getServletPath());
        System.out.println("Request URL: " + req.getRequestURL());
        
        // Skip JWT validation for public endpoints
        String path = req.getServletPath();
        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            System.out.println("Skipping JWT validation for public endpoint");
            chain.doFilter(req, res);
            return;
        }

        final String authHeader = req.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);
        
        String token = null;
        String username = null;

        // Extract token from Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Extracted Token: " + token.substring(0, Math.min(token.length(), 50)) + "...");
            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("Extracted Username: " + username);
            } catch (Exception e) {
                System.out.println("Invalid JWT token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No Bearer token found in Authorization header");
        }

        // If token is valid and no authentication is set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Attempting to authenticate user: " + username);
            try {
                UserDetails userDetails = userService.loadUserByUsername(username);
                System.out.println("User details loaded: " + userDetails.getUsername());
                
                // Validate token
                if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                    System.out.println("Token is valid, setting authentication");
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication set successfully");
                } else {
                    System.out.println("Token validation failed");
                }
            } catch (Exception e) { 
                System.out.println("Authentication failed: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Username is null or authentication already set");
        }

        System.out.println("=== End JWT Filter Debug ===");
        chain.doFilter(req, res);
    }
}