package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmailAndTenantId(String email, String tenantId);
}
