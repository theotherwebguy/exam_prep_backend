package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TempoStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TempStudentRepository extends JpaRepository<TempoStudent, Long> {
    Optional<TempoStudent> findByEmail(String email);
    void delete(TempoStudent tempStudent);
}
