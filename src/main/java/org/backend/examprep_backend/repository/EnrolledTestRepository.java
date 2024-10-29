package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.EnrolledTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrolledTestRepository extends JpaRepository<EnrolledTest, Long> {
}
