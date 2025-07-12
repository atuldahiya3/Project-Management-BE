package com.synct.synct.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String username;
    private String email;
    private String role;
}
