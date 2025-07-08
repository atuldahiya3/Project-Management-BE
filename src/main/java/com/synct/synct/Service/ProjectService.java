package com.synct.synct.Service;

import java.util.List;

import com.synct.synct.Models.Project;

public interface ProjectService {
    Project createProject(Project project);
    List<Project> readProjects();
    boolean deleteProject(Long id);
}
