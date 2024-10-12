package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopic(Topic topic);  // Find questions by topic

    Page<Question> findByTopic(Topic topic, Pageable pageable);  // Find paginated questions by topic
}
