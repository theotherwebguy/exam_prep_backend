package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmailOrContactNumber(String email, String contactNumber);



    // Custom query to find users by role name
    @Query("SELECT u FROM Users u JOIN u.role r WHERE r.name = :roleName")
    List<Users> findAllByRoleName(@Param("roleName") String roleName);

}
