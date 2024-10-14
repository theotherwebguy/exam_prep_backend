package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.ClassService;
import org.backend.examprep_backend.service.CourseService;
import org.backend.examprep_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lecturer")
public class LecturerController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClassService classService;

    // Get courses with classes assigned to a lecturer
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCoursesWithClasses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<Users> lecturer = userService.findUserByEmail(username);

            if (lecturer.isPresent()) {
                List<Course> courses = courseService.getCoursesWithClassesByLecturer(lecturer.get());
                return ResponseEntity.ok(courses);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }
    }

}
