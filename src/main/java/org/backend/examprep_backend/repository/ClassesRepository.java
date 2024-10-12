package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, Long> {
    List<Classes> findByLecturer(Users lecturer);
    List<Classes> findByCourse_CourseId(Long courseId);
}
