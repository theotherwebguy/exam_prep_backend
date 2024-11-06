package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Classes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentClassRepository extends CrudRepository<Classes, Long> {

    @Query("SELECT c.classesId FROM Users u JOIN u.studentClasses c WHERE u.id = :studentId")
    List<Long> findClassIdsByStudentId(Long studentId);
}
