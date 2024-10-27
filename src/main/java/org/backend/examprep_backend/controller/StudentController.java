package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.StudentClassCourseDTO;
import org.backend.examprep_backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{studentId}/details")
    public ResponseEntity<StudentClassCourseDTO> getCourseDetailsForStudent(@PathVariable Long studentId) {
        StudentClassCourseDTO studentDetails = studentService.getCourseDetailsForStudent(studentId);
        return ResponseEntity.ok(studentDetails);
    }

}

