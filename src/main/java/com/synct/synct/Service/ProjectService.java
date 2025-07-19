package com.synct.synct.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.synct.synct.Models.Project;
import com.synct.synct.Models.User;
import com.synct.synct.Repository.ProjectRepository;
import com.synct.synct.Repository.UserRepository;

@Service

public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository; 
    @Autowired
    private UserRepository userRepository;

    public Project createProject(Project project, Authentication authentication){
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        project.setCompanyId(currentUser.getId());

        return projectRepository.save(project);

    }
    public List<Project> readProjects(Authentication authentication){
        String username = authentication.getName();
        User currentUser=userRepository.findByUsername(username)
        .orElseThrow(()-> new RuntimeException("User not found"));
        
        return projectRepository.findByCompanyId(currentUser.getId());
    }
    boolean deleteProject(Long id){
        return true;
    }
}
