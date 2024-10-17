package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
