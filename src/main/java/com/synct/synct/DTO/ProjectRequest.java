package com.synct.synct.DTO;

import java.util.Set;

import lombok.Data;

@Data
public class ProjectRequest {
    private String title;
    private String description;
    private Set<Integer> employees;
    private String deadline;
}
