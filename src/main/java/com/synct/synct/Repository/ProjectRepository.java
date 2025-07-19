package com.synct.synct.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synct.synct.Models.Project;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findByCompanyId(Long companyId);
}
