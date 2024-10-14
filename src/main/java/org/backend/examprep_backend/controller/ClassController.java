package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.ClassDTO;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private StudentExcelParserService studentExcelParserService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClassService classService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;// Assuming you have a RoleService to fetch roles from DB

    @PostMapping(value = "/addStudents", consumes = "multipart/form-data")
    public ResponseEntity<String> addStudents(
            @RequestPart("file") MultipartFile file) {
        try {
            Optional<Role> studentRoleOpt = roleService.getRoleById(1L);
            if (studentRoleOpt.isEmpty()) {
                return new ResponseEntity<>("Student role not found", HttpStatus.BAD_REQUEST);
            }
            Role studentRole = studentRoleOpt.get();
            List<Users> users = studentExcelParserService.extractStudentsFromExcel(file, studentRole);
            userService.saveAll(users);

            return ResponseEntity.ok("Students successfully uploaded and processed");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to upload students: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{courseId}")
    public ResponseEntity<Classes> addClass(@PathVariable Long courseId, @RequestBody ClassDTO classDTO) {
        Classes createdClass = classService.addClass(courseId, classDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
    }
}
