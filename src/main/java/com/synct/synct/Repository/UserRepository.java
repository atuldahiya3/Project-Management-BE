package com.synct.synct.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synct.synct.Models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
