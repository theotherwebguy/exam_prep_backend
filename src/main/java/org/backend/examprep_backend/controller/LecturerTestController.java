//package org.backend.examprep_backend.controller;
//
//import org.backend.examprep_backend.dto.LecturerTestCreationRequestDTO;
//import org.backend.examprep_backend.dto.TestDTO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/lecturer-tests")
//public class LecturerTestController {
//
//    @Autowired
//    private LecturerTestService lecturerTestService;
//
//    // Endpoint for lecturers to create tests for students
//    @PostMapping("/create")
//    public ResponseEntity<TestDTO> createTestForStudents(@RequestBody LecturerTestCreationRequestDTO request) {
//        try {
//            TestDTO testDTO = lecturerTestService.createLecturerTest(request);
//            return new ResponseEntity<>(testDTO, HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//}
