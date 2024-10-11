package org.backend.examprep_backend.service;

import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    public List<Classes> getClassesByCourse(Course course) {
        return classesRepository.findByCourse(course);
    }

    // Add a new class
    public Classes addClass(Classes newClass) {
        return classesRepository.save(newClass);
    }

    // Update an existing class
    public Classes updateClass(Long classId, Classes updatedClass) {
        Classes existingClass = classesRepository.findById(classId).orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        existingClass.setClassName(updatedClass.getClassName());
        return classesRepository.save(existingClass);
    }

    // Delete a class
    public void deleteClass(Long classId) {
        classesRepository.deleteById(classId);
    }
}