package org.backend.examprep_backend.service;

import jakarta.transaction.Transactional;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.model.*;
import org.backend.examprep_backend.repository.ClassesRepository;
import org.backend.examprep_backend.repository.CourseRepository;
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
    private ClassesRepository classesRepository;

    // Retrieve courses that have classes assigned to the lecturer
    public List<Course> getCoursesWithClassesByLecturer(Users lecturer) {
        List<Classes> classesList = classesRepository.findByLecturer(lecturer);
        return classesList.stream()
                .map(Classes::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }


    @Transactional
    public Course createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseDescription(courseDTO.getCourseDescription());
        course.setImage(courseDTO.getImage());

        List<Domain> domains = new ArrayList<>();
        for (DomainDTO domainDTO : courseDTO.getDomains()) {
            Domain domain = new Domain();
            domain.setDomainName(domainDTO.getDomainName());
            domain.setCourse(course);

            List<Topic> topics = new ArrayList<>();
            for (String topicName : domainDTO.getTopics()) {
                Topic topic = new Topic();
                topic.setTopicName(topicName);
                topic.setDomain(domain);
                topics.add(topic);
            }
            domain.setTopics(topics);
            domains.add(domain);
        }

        course.setDomains(domains);
        return courseRepository.save(course);
    }
    @Transactional
    public Course updateCourse(Long courseId, CourseDTO courseDTO) {
        // Find existing course by ID
        return courseRepository.findById(courseId).map(existingCourse -> {
            // Update basic course details
            existingCourse.setCourseName(courseDTO.getCourseName());
            existingCourse.setCourseDescription(courseDTO.getCourseDescription());
            existingCourse.setImage(courseDTO.getImage());

            // Handle domains and topics
            List<Domain> newDomains = new ArrayList<>();

            // Map of domain names to domain entities for existing course domains
            List<Domain> existingDomains = existingCourse.getDomains();
            List<String> newDomainNames = courseDTO.getDomains().stream()
                    .map(DomainDTO::getDomainName).collect(Collectors.toList());

            // Remove domains that are not in the updated list
            existingDomains.removeIf(domain -> !newDomainNames.contains(domain.getDomainName()));

            for (DomainDTO domainDTO : courseDTO.getDomains()) {
                Domain existingDomain = existingDomains.stream()
                        .filter(d -> d.getDomainName().equals(domainDTO.getDomainName()))
                        .findFirst().orElse(null);

                if (existingDomain == null) {
                    Domain newDomain = new Domain();
                    newDomain.setDomainName(domainDTO.getDomainName());
                    newDomain.setCourse(existingCourse);

                    List<Topic> topics = new ArrayList<>();
                    for (String topicName : domainDTO.getTopics()) {
                        Topic topic = new Topic();
                        topic.setTopicName(topicName);
                        topic.setDomain(newDomain);
                        topics.add(topic);
                    }
                    newDomain.setTopics(topics);
                    newDomains.add(newDomain);
                } else {
                    // Existing domain, update it
                    existingDomain.setDomainName(domainDTO.getDomainName());

                    // Update topics for this domain
                    List<String> newTopics = domainDTO.getTopics();
                    existingDomain.getTopics().removeIf(topic -> !newTopics.contains(topic.getTopicName()));

                    for (String topicName : newTopics) {
                        boolean topicExists = existingDomain.getTopics().stream()
                                .anyMatch(t -> t.getTopicName().equals(topicName));
                        if (!topicExists) {
                            Topic newTopic = new Topic();
                            newTopic.setTopicName(topicName);
                            newTopic.setDomain(existingDomain);
                            existingDomain.getTopics().add(newTopic);
                        }
                    }
                }
            }

            existingCourse.getDomains().addAll(newDomains);

            // Save and return the updated course
            return courseRepository.save(existingCourse);
        }).orElseThrow(() -> new RuntimeException("Course not found"));
    }
    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
    @Transactional
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

}

