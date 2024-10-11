package org.backend.examprep_backend.repository;

import org.apache.catalina.User;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByLecturer(Users lecturer);  // Assuming Course has a relation to the lecturer
}

