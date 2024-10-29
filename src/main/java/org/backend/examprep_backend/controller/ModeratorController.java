package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.service.IndependentTestService;
import org.backend.examprep_backend.service.ModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Add this annotation to define the class as a REST controller
@RequestMapping("/api/moderator") // Optional: Define a base request mapping

public class ModeratorController {

    @Autowired
    private ModeratorService moderatorService;

    //fetch course by independent student id
    @GetMapping("/{moderatorId}/courses")
    @Transactional
    public ResponseEntity<List<CourseDTO>> getCoursesByUserId(
            @PathVariable Long moderatorId) { // Removed the includeImage parameter

        List<CourseDTO> courses = moderatorService.getCoursesByUserId(moderatorId);

        // Map to CourseDTO
        List<CourseDTO> courseDTOList = courses.stream().map(course -> {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(course.getCourseId());
            courseDTO.setCourseName(course.getCourseName());
            courseDTO.setCourseDescription(course.getCourseDescription());
            courseDTO.setDomains(course.getDomains()); // Now includes domains and topics
            courseDTO.setImage(course.getImage()); // Always include image

            return courseDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(courseDTOList);
    }

}
