package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
