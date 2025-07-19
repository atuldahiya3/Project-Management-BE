package com.synct.synct.Controller;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synct.synct.DTO.AuthRequest;
import com.synct.synct.DTO.AuthResponse;
import com.synct.synct.DTO.RegisterResponse;
import com.synct.synct.DTO.UserResponse;
import com.synct.synct.Models.User;
import com.synct.synct.Repository.UserRepository;
import com.synct.synct.Service.EmailService;
import com.synct.synct.Util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom random = new SecureRandom();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .country(request.getCountry())
                .phone(request.getPhone())
                .name(request.getName())
                .build();

        userRepository.save(newUser);

        // No need to re-fetch, just use the saved instance (newUser)
        UserResponse userResponse = new UserResponse(
                newUser.getId(),
                newUser.getName(),
                newUser.getPhone(),
                newUser.getCountry(),
                newUser.getEmail(),
                newUser.getUsername(),
                newUser.getRole());
        
        emailService.sendEmail(request.getEmail(), "Welcome to synct",
                "Your credentials to login are:\nUsername: " + newUser.getUsername() + "\nEmail: " + request.getEmail()
                        + "\nPassword: " + request.getPassword() + "\nYou can login via email/username");
        
        RegisterResponse registerResponse = new RegisterResponse("User Registered Successfully", userResponse);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/register-employee")
    public ResponseEntity<?> registerEmployee(@RequestBody AuthRequest authRequest, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> currUser = userRepository.findByUsername(username);
        if (currUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        User currentUser = currUser.get();
        if (!currentUser.getRole().equals("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can register employees");
        }
        
        if (userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }

        // Generate username from email
        String generatedUsername = generateUsernameFromEmail(authRequest.getEmail());
        
        // Check if generated username already exists, if so, add numbers
        String finalUsername = generatedUsername;
        int counter = 1;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = generatedUsername + counter;
            counter++;
        }

        // Generate random password
        String randomPassword = generateRandomPassword(8);

        // Set company ID to current admin's ID
        authRequest.setCompanyId(currentUser.getId());

        // Create employee user
        User newEmployee = User.builder()
                .username(finalUsername)
                .email(authRequest.getEmail())
                .password(passwordEncoder.encode(randomPassword))
                .role("employee")
                .country(authRequest.getCountry())
                .phone(authRequest.getPhone())
                .name(authRequest.getName())
                .companyId(currentUser.getId())
                .build();

        userRepository.save(newEmployee);

        // Create user response
        UserResponse userResponse = new UserResponse(
                newEmployee.getId(),
                newEmployee.getName(),
                newEmployee.getPhone(),
                newEmployee.getCountry(),
                newEmployee.getEmail(),
                newEmployee.getUsername(),
                newEmployee.getRole());

        // Send email with credentials
        String emailSubject = "Welcome to " + currentUser.getName() + " - Your Account Details";
        String emailBody = String.format(
                "Welcome to the team!\n\n" +
                "Your account has been created by your company admin: %s\n\n" +
                "Your login credentials are:\n" +
                "Username: %s\n" +
                "Email: %s\n" +
                "Temporary Password: %s\n\n" +
                "Please change your password after your first login.\n" +
                "You can login using either your username or email address.\n\n" +
                "Best regards,\nSynct Team",
                currentUser.getName(),
                finalUsername,
                authRequest.getEmail(),
                randomPassword
        );

        emailService.sendEmail(authRequest.getEmail(), emailSubject, emailBody);

        RegisterResponse registerResponse = new RegisterResponse("Employee registered successfully", userResponse);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest request) {
        try {
            User user = userRepository.findByUsername(request.getLogin())
                    .or(() -> userRepository.findByEmail(request.getLogin()))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("Found user: " + user.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()));

            // Use user.getUsername() instead of request.getUsername()
            String token = jwtUtil.generateToken(user.getUsername());
            System.out.println("Generated token for user: " + user.getUsername());
            System.out.println("Token: " + token);

            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getPhone(),
                    user.getCountry(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getRole());

            AuthResponse authResponse = new AuthResponse(token, userResponse);
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    // Helper method to generate username from email
    private String generateUsernameFromEmail(String email) {
        return email.substring(0, email.indexOf('@')).toLowerCase();
    }

    // Helper method to generate random password
    private String generateRandomPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}