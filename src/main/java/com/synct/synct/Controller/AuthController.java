package com.synct.synct.Controller;

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
        User user= userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse= new UserResponse(user.getId(),user.getName(), user.getPhone(), user.getCountry(), user.getEmail(), user.getUsername(), user.getRole());
        RegisterResponse registerResponse= new RegisterResponse("User Registered Successfully", userResponse);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            User user=userRepository.findByUsername(request.getLogin()).or(()->userRepository.findByEmail(request.getLogin())).orElseThrow(()->new RuntimeException("User not found"));
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()));

            String token = jwtUtil.generateToken(request.getUsername());
            UserResponse userResponse= new UserResponse(user.getId(),user.getName(), user.getPhone(), user.getCountry(), user.getEmail(), user.getUsername(), user.getRole());
            AuthResponse authResponse=new AuthResponse(token,userResponse);
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
