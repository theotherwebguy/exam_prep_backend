package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Domain;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.ModeratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModeratorServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ModeratorService moderatorService;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize mock user with courses, domains, and topics
        mockUser = new Users();
        mockUser.setId(1L);
        mockUser.setCourses(new HashSet<>(Set.of(createMockCourse(1L, "Math"), createMockCourse(2L, "Science"))));
    }

    private Course createMockCourse(Long id, String name) {
        Course course = new Course();
        course.setCourseId(id);
        course.setCourseName(name);
        course.setCourseDescription(name + " course description");
        course.setImage(name.getBytes());  // Mock byte array for image

        Domain domain = createMockDomain(1L, "Domain1");
        course.setDomains(List.of(domain));  // Assuming domains are List in your model

        return course;
    }

    private Domain createMockDomain(Long id, String name) {
        Domain domain = new Domain();
        domain.setDomainId(id);
        domain.setDomainName(name);
        domain.setTopics(List.of(createMockTopic(1L, "Topic1"), createMockTopic(2L, "Topic2"))); // Assuming topics are List
        return domain;
    }

    private Topic createMockTopic(Long id, String name) {
        Topic topic = new Topic();
        topic.setTopicId(id);
        topic.setTopicName(name);
        return topic;
    }

    @Test
    void getCoursesByUserId_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        List<CourseDTO> result = moderatorService.getCoursesByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Check first course details
        CourseDTO mathCourse = result.get(0);
        assertEquals("Math", mathCourse.getCourseName());
        assertEquals("Math course description", mathCourse.getCourseDescription());
        assertArrayEquals("Math".getBytes(), mathCourse.getImage());

        // Check domains and topics in the first course
        List<DomainDTO> domains = mathCourse.getDomains();
        assertEquals(1, domains.size());
        DomainDTO domain1 = domains.get(0);
        assertEquals("Domain1", domain1.getDomainName());

        List<TopicDTO> topics = domain1.getTopics();
        assertEquals(2, topics.size());
        assertEquals("Topic1", topics.get(0).getTopicName());
        assertEquals("Topic2", topics.get(1).getTopicName());
    }

    @Test
    void getCoursesByUserId_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            moderatorService.getCoursesByUserId(1L);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getCoursesByUserId_UserWithNoCourses() {
        // Arrange
        mockUser.setCourses(Collections.emptySet()); // Ensure Set<Course> is used
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        List<CourseDTO> result = moderatorService.getCoursesByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
