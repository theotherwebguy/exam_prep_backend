package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.CourseRepository;
import org.backend.examprep_backend.repository.DomainRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.backend.examprep_backend.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private DomainRepository domainRepository;
    @Mock
    private ClassRepository classRepository;
    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCoursesWithClassesByLecturer() {
        Users lecturer = new Users(); // Populate with necessary details
        Classes classEntity = new Classes();
        Course course = new Course();
        classEntity.setCourse(course);

        when(classRepository.findByLecturer(lecturer)).thenReturn(List.of(classEntity));

        List<Course> courses = courseService.getCoursesWithClassesByLecturer(lecturer);

        assertNotNull(courses);
        assertEquals(1, courses.size());
        assertEquals(course, courses.get(0));
    }

    @Test
    void testSaveCourseWithDomainsAndTopics() throws Exception {
        // Set up courseDTO with non-null domains list
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setDomains(new ArrayList<>()); // Initialize as empty or add mock domains if necessary

        MultipartFile imageFile = mock(MultipartFile.class);
        when(imageFile.isEmpty()).thenReturn(false);
        when(imageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        Course savedCourse = new Course();
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        Course result = courseService.saveCourseWithDomainsAndTopics(courseDTO, imageFile);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }


    @Test
    void testGetDomainsByCourse_CourseNotFound() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getDomainsByCourse(courseId));
    }

    @Test
    void testDeleteCourse_CourseNotFound() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.deleteCourse(courseId));
    }

    @Test
    void testDeleteCourse_Success() {
        Long courseId = 1L;
        Course course = new Course();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.deleteCourse(courseId);

        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void testGetCourseById_CourseNotFound() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(courseId));
    }

    @Test
    void testGetCourseById_Success() {
        Long courseId = 1L;
        Course course = new Course();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(courseId);

        assertNotNull(result);
        assertEquals(course, result);
    }

    // Additional tests for other methods can follow the same pattern
}
