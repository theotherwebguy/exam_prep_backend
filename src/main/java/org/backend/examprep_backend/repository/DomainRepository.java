package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Long> {
}
