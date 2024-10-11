package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByClasses(Classes classes);  // Find tests by class
}