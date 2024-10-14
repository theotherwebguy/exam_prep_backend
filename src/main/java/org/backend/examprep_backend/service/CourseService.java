package org.backend.examprep_backend.service;

import jakarta.transaction.Transactional;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.model.*;
import org.backend.examprep_backend.repository.ClassesRepository;
import org.backend.examprep_backend.repository.CourseRepository;
import org.backend.examprep_backend.repository.DomainRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private TopicRepository topicRepository;

    // Retrieve courses that have classes assigned to the lecturer
    public List<Course> getCoursesWithClassesByLecturer(Users lecturer) {
        List<Classes> classesList = classesRepository.findByLecturer(lecturer);
        return classesList.stream()
                .map(Classes::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }

    public Course saveCourseWithDomainsAndTopics(CourseDTO courseDTO) {
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseDescription(courseDTO.getCourseDescription());
        course.setImage(courseDTO.getImage());

        List<Domain> domainEntities = new ArrayList<>();
        for (DomainDTO domainDTO : courseDTO.getDomains()) {
            Domain domain = new Domain();
            domain.setDomainName(domainDTO.getDomainName());
            domain.setCourse(course);  // Link course to domain

            List<Topic> topicEntities = new ArrayList<>();
            for (TopicDTO topicDTO : domainDTO.getTopics()) {
                Topic topic = new Topic();
                topic.setTopicName(topicDTO.getTopicName());
                topic.setDomain(domain);  // Link domain to topic
                topicEntities.add(topic);
            }

            domain.setTopics(topicEntities);
            domainEntities.add(domain);
        }

        course.setDomains(domainEntities);
        return courseRepository.save(course);  // Cascade save will save everything
    }

    public Course updateCourseWithDomainsAndTopics(Long courseId, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        existingCourse.setCourseName(courseDTO.getCourseName());
        existingCourse.setCourseDescription(courseDTO.getCourseDescription());
        existingCourse.setImage(courseDTO.getImage());

        List<Domain> updatedDomains = new ArrayList<>();
        for (DomainDTO domainDTO : courseDTO.getDomains()) {
            Domain domain = domainRepository.findById(domainDTO.getDomainId())
                    .orElse(new Domain());
            domain.setDomainName(domainDTO.getDomainName());
            domain.setCourse(existingCourse);  // Set course reference

            List<Topic> updatedTopics = new ArrayList<>();
            for (TopicDTO topicDTO : domainDTO.getTopics()) {
                Topic topic = topicRepository.findById(topicDTO.getTopicId())
                        .orElse(new Topic());
                topic.setTopicName(topicDTO.getTopicName());
                topic.setDomain(domain);  // Set domain reference
                updatedTopics.add(topic);
            }

            domain.setTopics(updatedTopics);
            updatedDomains.add(domain);
        }

        existingCourse.setDomains(updatedDomains);
        return courseRepository.save(existingCourse);
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        courseRepository.delete(course);  // Cascade delete will handle domains and topics
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }




}

