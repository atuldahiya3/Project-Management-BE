package com.synct.synct;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class ProjectController {
    //Dependency Injection
    @Autowired
    ProjectService projectService;
    @GetMapping("projects")
    public List<Project> getProjects() {
        return projectService.readProjects();
    }

    @PostMapping("projects")
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
        
    }
    @DeleteMapping("projects/{id}")
    public String deleteProject(@PathVariable Long id) {
        if(projectService.deleteProject(id)){
            return "Deleted Successfully";
        }else{
            return "Not found";
        }
        }
    
    
}
