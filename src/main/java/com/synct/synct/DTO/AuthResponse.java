package com.synct.synct.DTO;

import com.synct.synct.Models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserResponse user;
}