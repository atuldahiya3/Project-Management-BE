package com.synct.synct;

import java.util.List;

public interface ProjectService {
    Project createProject(Project project);
    List<Project> readProjects();
    boolean deleteProject(Long id);
}
