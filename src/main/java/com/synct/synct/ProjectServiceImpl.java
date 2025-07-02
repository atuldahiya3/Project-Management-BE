package com.synct.synct;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService{
    List<Project> projects = new ArrayList<>();
    @Override
    public Project createProject(Project project) {
        projects.add(project);
        return project;
    }

    @Override
    public List<Project> readProjects() {
        return projects;
    }

    @Override
    public boolean deleteProject(Long id) {
        projects.remove(id);
        return true;
    }
    
}
