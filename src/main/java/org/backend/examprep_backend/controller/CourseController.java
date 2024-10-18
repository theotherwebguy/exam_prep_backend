package org.backend.examprep_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.backend.examprep_backend.dto.*;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Domain;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @PostMapping(value = "/saveCourse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Course> saveCourseWithImage(
            @RequestPart("courseDetails") String courseDetailsJson,
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        // Deserialize courseDetails
        ObjectMapper objectMapper = new ObjectMapper();
        CourseDTO courseDTO = objectMapper.readValue(courseDetailsJson, CourseDTO.class);

        Course createdCourse = courseService.saveCourseWithDomainsAndTopics(courseDTO, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }


    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Course> updateCourseWithImage(
            @PathVariable Long courseId,
            @RequestPart("courseDetails") String courseDetailsJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CourseDTO courseDTO = objectMapper.readValue(courseDetailsJson, CourseDTO.class);



        Course updatedCourse = courseService.updateCourseWithDomainsAndTopics(courseId, courseDTO, imageFile);

        return ResponseEntity.ok(updatedCourse);
    }




    // New: Delete an existing course
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();

        // Create a list of CourseDTO to return the data in the desired format
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for (Course course : courses) {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(course.getCourseId());
            courseDTO.setCourseName(course.getCourseName());
            courseDTO.setCourseDescription(course.getCourseDescription());
            courseDTO.setImage(course.getImage()); // Handling image as byte[]

            // Add domains and topics to the DTO
            List<DomainDTO> domainDTOList = new ArrayList<>();
            for (Domain domain : course.getDomains()) {
                DomainDTO domainDTO = new DomainDTO();
                domainDTO.setDomainName(domain.getDomainName());
                domainDTO.setDomainId(domain.getDomainId());

                List<TopicDTO> topicDTOList = new ArrayList<>();
                for (Topic topic : domain.getTopics()) {
                    TopicDTO topicDTO = new TopicDTO();
                    topicDTO.setTopicName(topic.getTopicName());
                    topicDTO.setTopicId(topic.getTopicId());
                    topicDTOList.add(topicDTO);
                }
                domainDTO.setTopics(topicDTOList);
                domainDTOList.add(domainDTO);
            }
            courseDTO.setDomains(domainDTOList);

            courseDTOList.add(courseDTO);
        }

        return ResponseEntity.ok(courseDTOList);
    }

    // Get course by ID
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Convert Course entity to CourseDTO
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setCourseName(course.getCourseName());
        courseDTO.setCourseDescription(course.getCourseDescription());
        courseDTO.setImage(course.getImage()); // Handling image as byte[]

        // Add domains and topics to the DTO
        List<DomainDTO> domainDTOList = new ArrayList<>();
        for (Domain domain : course.getDomains()) {
            DomainDTO domainDTO = new DomainDTO();
            domainDTO.setDomainName(domain.getDomainName());
            domainDTO.setDomainId(domain.getDomainId());

            List<TopicDTO> topicDTOList = new ArrayList<>();
            for (Topic topic : domain.getTopics()) {
                TopicDTO topicDTO = new TopicDTO();
                topicDTO.setTopicName(topic.getTopicName());
                topicDTO.setTopicId(topic.getTopicId());
                topicDTOList.add(topicDTO);
            }
            domainDTO.setTopics(topicDTOList);
            domainDTOList.add(domainDTO);
        }
        courseDTO.setDomains(domainDTOList);

        return ResponseEntity.ok(courseDTO);
    }

    @GetMapping("/with-classes")
    public ResponseEntity<List<CourseWithClassesDTO>> getCoursesWithClasses() {
        List<CourseWithClassesDTO> courses = courseService.getAllCoursesWithClassesDTO();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/classes/{courseId}")
    public ResponseEntity<CourseWithClassesDTO> getCourseWithClasses(@PathVariable Long courseId) {
        CourseWithClassesDTO courseWithClasses = courseService.getCourseWithClassesDTOUsingID(courseId);
        return ResponseEntity.ok(courseWithClasses);
    }

}
