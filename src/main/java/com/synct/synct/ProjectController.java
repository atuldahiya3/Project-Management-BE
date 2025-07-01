package com.synct.synct;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class ProjectController {
    List<Project> projects = new ArrayList<>();
    ProjectService projectService;
    @GetMapping("projects")
    public List<Project> getProjects() {
        return projectService.readProjects();
    }

    @PostMapping("projects")
    public String createProject(@RequestBody Project project) {
        projectService.createProject(project);
        return "Saved Successfully";
    }
    
    
}
