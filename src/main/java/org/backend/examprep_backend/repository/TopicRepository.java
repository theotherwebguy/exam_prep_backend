package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Domain;
import org.backend.examprep_backend.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByDomain(Domain domain);  // Find topics by domain
}
