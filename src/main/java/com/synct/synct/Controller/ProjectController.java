package com.synct.synct.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.synct.synct.Models.Project;
import com.synct.synct.Service.ProjectService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/api")
public class ProjectController {
    //Dependency Injection
    @Autowired
    ProjectService projectService;
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects(Authentication authentication) {
        try {
            List<Project> projects = projectService.readProjects(authentication);
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project, 
                                               Authentication authentication) {
        try {
            Project createdProject = projectService.createProject(project, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    // @DeleteMapping("projects/{id}")
    // public String deleteProject(@PathVariable Long id) {
    //     if(projectService.deleteProject(id)){
    //         return "Deleted Successfully";
    //     }else{
    //         return "Not found";
    //     }
    //     }
    
    
}
