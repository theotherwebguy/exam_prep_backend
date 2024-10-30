package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Answer;
import org.backend.examprep_backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);

}
