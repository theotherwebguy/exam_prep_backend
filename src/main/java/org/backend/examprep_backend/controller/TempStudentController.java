package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.TempStudentDTO;
import org.backend.examprep_backend.model.TempoStudent;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.TempStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;









@RestController
@RequestMapping("/api/temp-students")
public class TempStudentController {

    @Autowired
    private TempStudentService tempStudentService;

    // Register a temporary student
    @PostMapping("/register")
    public ResponseEntity<?> registerTempStudent(@RequestBody TempStudentDTO tempStudentDto) {
        try {
            TempoStudent tempStudent = tempStudentService.registerTempStudent(tempStudentDto);
            return ResponseEntity.ok(tempStudent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred during registration.");
        }
    }

    // Approve or deny a temporary student
    @PutMapping("/approve-temp-student/{userId}")
    public ResponseEntity<?> approveTempStudent(@PathVariable Long userId, @RequestParam boolean isApproved) {
        return tempStudentService.approveTempStudent(userId, isApproved);
    }

    // Get all temporary students
    @GetMapping
    public ResponseEntity<?> getAllTempStudents() {
        try {
            List<TempoStudent> tempStudents = tempStudentService.getAllTempStudents();
            return ResponseEntity.ok(tempStudents);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unable to retrieve temporary students.");
        }
    }

    // Get a temporary student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTempStudentById(@PathVariable Long id) {
        try {
            TempoStudent tempStudent = tempStudentService.getTempStudentById(id);
            return ResponseEntity.ok(tempStudent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update a temporary student by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTempStudent(@PathVariable Long id, @RequestBody TempStudentDTO tempStudentDto) {
        try {
            TempoStudent updatedTempStudent = tempStudentService.updateTempStudent(id, tempStudentDto);
            return ResponseEntity.ok(updatedTempStudent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the temporary student.");
        }
    }

    // Delete a temporary student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTempStudent(@PathVariable Long id) {
        try {
            tempStudentService.deleteTempStudent(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the temporary student.");
        }
    }
}
