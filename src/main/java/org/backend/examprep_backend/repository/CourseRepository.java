package org.backend.examprep_backend.repository;

import org.apache.catalina.User;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.classes")
    List<Course> findCourseWithClasses();

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.classes WHERE c.courseId = :courseId")
    Optional<Course> findCourseWithClasses(@Param("courseId") Long courseId);
}

