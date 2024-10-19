package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
