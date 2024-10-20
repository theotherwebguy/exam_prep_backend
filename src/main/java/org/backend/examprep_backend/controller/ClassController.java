package org.backend.examprep_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.ClassDTO;
import org.backend.examprep_backend.dto.ClassResponseDTO;
import org.backend.examprep_backend.dto.EnrolledClassDTO;
import org.backend.examprep_backend.dto.UserDto;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    //private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

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

    @PostMapping(value = "/{courseId}/addClassAndStudents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addClassAndStudents(
            @PathVariable Long courseId,
            @RequestPart("classDetails") String classDetailsJson,  // JSON part
            @RequestPart("file") MultipartFile file) {             // File part
        try {
            // Parse the classDetails JSON string into a ClassDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Ensure the module is registered
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            ClassDTO classDTO = objectMapper.readValue(classDetailsJson, ClassDTO.class);

            // Call the service to add class and students
            Classes createdClass = classService.addClassAndStudents(courseId, classDTO, file);

            return ResponseEntity.ok("Class and students successfully added.");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add class and students: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping
//    public ResponseEntity<List<Classes>> getAllClasses() {
//        List<Classes> classes = classService.getAllClasses();
//        return ResponseEntity.ok(classes);
//    }

//    @GetMapping("/course/{courseId}")
//    public ResponseEntity<List<Classes>> getClassesByCourseId(@PathVariable Long courseId) {
//        List<Classes> classes = classService.getClassesByCourseId(courseId);
//        return ResponseEntity.ok(classes);
//    }

//    @GetMapping("/{classId}")
//    public ResponseEntity<Classes> getClassById(@PathVariable Long classId) {
//        Classes classEntity = classService.getClassById(classId);
//        return ResponseEntity.ok(classEntity);
//    }

//    @PutMapping("/{classId}")
//    public ResponseEntity<ClassDTO> updateClass(@PathVariable Long classId, @RequestBody ClassDTO classDTO) {
//        try {
//            // Call the service method to update the class
//            Classes updatedClass = classService.updateClass(classId, classDTO);
//
//            // Map the updated class to ClassDTO (this can also be done in the service layer)
//            ClassDTO responseDTO = new ClassDTO();
//            responseDTO.setClassesId(updatedClass.getClassesId());
//            responseDTO.setClassName(updatedClass.getClassName());
//            responseDTO.setClassDescription(updatedClass.getClassDescription());
//            responseDTO.setStartDate(updatedClass.getStartDate());
//            responseDTO.setEndDate(updatedClass.getEndDate());
//
//            if (updatedClass.getLecturer() != null) {
//                responseDTO.setUserId(updatedClass.getLecturer().getId());
//                responseDTO.setLecturerName(updatedClass.getLecturer().getFullNames());
//            }
//
//            if (updatedClass.getCourse() != null) {
//                responseDTO.setCourseName(updatedClass.getCourse().getCourseName()); // Set course name
//            }
//            // Add students to the response using UserDto
//
//            return ResponseEntity.ok(responseDTO);
//        } catch (ResourceNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @DeleteMapping("/{classId}")
//    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
//        try {
//            classService.deleteClass(classId);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (ResourceNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/with-students")
    public ResponseEntity<List<ClassResponseDTO>> getAllClassesWithStudents() {
        List<ClassResponseDTO> classes = classService.getAllClassesWithStudents();
        return ResponseEntity.ok(classes);
    }


    @GetMapping("/enrolled")
    public ResponseEntity<List<EnrolledClassDTO>> getEnrolledClasses(@RequestParam Long studentId) {
        List<EnrolledClassDTO> enrolledClasses = classService.getEnrolledClasses(studentId);
        return ResponseEntity.ok(enrolledClasses);
    }
}