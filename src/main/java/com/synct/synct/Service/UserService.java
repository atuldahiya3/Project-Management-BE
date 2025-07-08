package com.synct.synct.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.synct.synct.Models.User;
import com.synct.synct.Repository.UserRepository;

public class UserService {
    @Autowired
    private UserRepository userRepo;

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
