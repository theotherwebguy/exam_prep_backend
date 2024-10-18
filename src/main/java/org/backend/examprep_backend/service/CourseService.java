package org.backend.examprep_backend.service;

import jakarta.transaction.Transactional;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.*;
import org.backend.examprep_backend.model.*;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.CourseRepository;
import org.backend.examprep_backend.repository.DomainRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TopicRepository topicRepository;

    // Retrieve courses that have classes assigned to the lecturer
    public List<Course> getCoursesWithClassesByLecturer(Users lecturer) {
        List<Classes> classesList = classRepository.findByLecturer(lecturer);
        return classesList.stream()
                .map(Classes::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }

    public Course saveCourseWithDomainsAndTopics(CourseDTO courseDTO, MultipartFile imageFile) throws IOException {
        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseDescription(courseDTO.getCourseDescription());

        // If there's an image file, set it in the course
        if (imageFile != null && !imageFile.isEmpty()) {
            course.setImage(imageFile.getBytes());  // Save image as byte array
        }

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

    // Method to update a course and its associated domains and topics
    @Transactional
    public Course updateCourseWithDomainsAndTopics(Long courseId, CourseDTO courseDTO, MultipartFile imageFile) throws IOException {

        // Check if courseId is valid
        if (courseId == null) {
            throw new IllegalArgumentException("The course ID must not be null.");
        }

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        existingCourse.setCourseName(courseDTO.getCourseName());
        existingCourse.setCourseDescription(courseDTO.getCourseDescription());

        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("Image file received: " + imageFile.getOriginalFilename());
            byte[] imageData = imageFile.getBytes();
            existingCourse.setImage(imageData);
        }

        List<Domain> updatedDomains = new ArrayList<>();
        for (DomainDTO domainDTO : courseDTO.getDomains()) {
            Domain domain = domainRepository.findById(domainDTO.getDomainId())
                    .orElse(new Domain());
            domain.setDomainName(domainDTO.getDomainName());
            domain.setCourse(existingCourse);  // Set course reference in domain
            List<Topic> updatedTopics = new ArrayList<>();
            for (TopicDTO topicDTO : domainDTO.getTopics()) {
                Topic topic = topicRepository.findById(topicDTO.getTopicId())
                        .orElse(new Topic());
                topic.setTopicName(topicDTO.getTopicName());
                topic.setDomain(domain);  // Set domain reference in topic
                updatedTopics.add(topic);
            }

            domain.setTopics(updatedTopics);  // Link topics to domain
            updatedDomains.add(domain);  // Add domain to updated list
        }

        // Set updated domains in course
        existingCourse.setDomains(updatedDomains);

        // Save and return the updated course with its domains and topics
        return courseRepository.save(existingCourse);
    }

    // Method to get domains by courseId
    public List<Domain> getDomainsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return domainRepository.findByCourse(course);
    }

    // Method to get topics by domainId
    public List<Topic> getTopicsByDomain(Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + domainId));
        return topicRepository.findByDomain(domain);
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
    @Transactional
    public List<CourseWithClassesDTO> getAllCoursesWithClassesDTO() {
        List<Course> courses = courseRepository.findCourseWithClasses();

        return courses.stream().map(course -> {
            // Map Course to CourseWithClassesDTO
            CourseWithClassesDTO dto = new CourseWithClassesDTO();
            dto.setCourseId(course.getCourseId());
            dto.setCourseName(course.getCourseName());
            dto.setImage(course.getImage());
            dto.setCourseDescription(course.getCourseDescription());

            // Map each Class to ClassDTO
            List<ClassDTO> classDTOs = course.getClasses().stream().map(cls -> {
                ClassDTO classDTO = new ClassDTO();
                classDTO.setClassesId(cls.getClassesId());
                classDTO.setClassName(cls.getClassName());

                // Set optional fields if not null
                classDTO.setClassDescription(cls.getClassDescription());
                classDTO.setStartDate(cls.getStartDate());
                classDTO.setEndDate(cls.getEndDate());
                //classDTO.setUserId(cls.getLecturer() != null ? cls.getLecturer().getId() : null); // Use getLecturer() here

                if (cls.getLecturer() != null) {
                    classDTO.setLecturerName(cls.getLecturer().getFullNames()); // Adjust to your User class
                    classDTO.setLecturerEmail(cls.getLecturer().getEmail()); // Adjust to your User class
                }

                return classDTO;
            }).collect(Collectors.toList());

            dto.setClasses(classDTOs);
            return dto;
        }).collect(Collectors.toList());
    }





}

