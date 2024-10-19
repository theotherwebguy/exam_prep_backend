package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Classes, Long> {
    List<Classes> findByLecturer(Users lecturer);
    List<Classes> findByCourse(Course course);

    @EntityGraph(attributePaths = "students")
    List<Classes> findAll();

    @Query("""
        SELECT c FROM Classes c 
        LEFT JOIN FETCH c.students s 
        WHERE s.role.name = 'STUDENT'
        """)
    List<Classes> findAllClassesWithStudents();

}
