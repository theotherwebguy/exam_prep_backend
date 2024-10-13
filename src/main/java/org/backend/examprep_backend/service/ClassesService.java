package org.backend.examprep_backend.service;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassesService {

    @Autowired
    private ClassesRepository classesRepository;

    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }

    public Optional<Classes> getClassById(Long id) {
        return classesRepository.findById(id);
    }

    public Classes createClass(Classes classes) {
        return classesRepository.save(classes);
    }

    public void deleteClass(Long id) {
        classesRepository.deleteById(id);
    }
}
