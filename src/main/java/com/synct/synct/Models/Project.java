package com.synct.synct.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long companyId;
    @NotBlank(message = "Title is required")
    private String title;
    @Size(max = 500, message = "Description too long")
    private String description;
    private Set<Integer> employees;
    private LocalDateTime deadline;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
}