package org.backend.examprep_backend.controller;
import org.backend.examprep_backend.model.Domain;
import org.backend.examprep_backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/domains")
public class DomainController {

    @Autowired
    private CourseService courseService;

    // Get all domains by course ID
    @GetMapping("/course/{courseId}")
    public List<Domain> getDomainsByCourseId(@PathVariable Long courseId) {
        return courseService.getDomainsByCourse(courseId);
    }
}
