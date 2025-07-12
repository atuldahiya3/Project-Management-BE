package com.synct.synct.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String login;
    private String username;
    private String email;
    private String password;
    private String role;
    private String country;
    private String name;
    private String phone;
}
