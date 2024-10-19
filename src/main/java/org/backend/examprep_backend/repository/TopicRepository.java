package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByDomain_DomainId(Long domainId); // Make sure to use the correct casing
}
