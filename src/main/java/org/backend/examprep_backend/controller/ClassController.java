package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassesService classesService;

    @GetMapping
    public List<Classes> getAllClasses() {
        return classesService.getAllClasses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classes> getClassById(@PathVariable Long id) {
        return classesService.getClassById(id)
                .map(classes -> new ResponseEntity<>(classes, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Classes> createClass(@RequestBody Classes classes) {
        Classes createdClass = classesService.createClass(classes);
        return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classesService.deleteClass(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
