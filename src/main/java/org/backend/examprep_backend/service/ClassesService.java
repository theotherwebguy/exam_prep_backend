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

    // Get classes by courseId
    public List<Classes> getClassesByCourseId(Long courseId) {
        return classesRepository.findByCourse_CourseId(courseId);
    }

    // Add a new class
    public Classes addClass(Classes newClass) {
        return classesRepository.save(newClass);
    }

    // Update class
    public Classes updateClass(Long classId, Classes updatedClass) {
        Classes existingClass = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        existingClass.setClassName(updatedClass.getClassName());
        // Other updates
        return classesRepository.save(existingClass);
    }

    // Delete class
    public void deleteClass(Long classId) {
        Classes existingClass = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        classesRepository.delete(existingClass);
    }
}
