package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Domain;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.AnswerRepository;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.IndependentTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class IndependentTestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private IndependentTestService independentTestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCoursesByUserId() {
        // Arrange
        Long userId = 1L;

        // Setting up mock user with associated courses, domains, and topics
        Users mockUser = new Users();
        mockUser.setId(userId);

        Course mockCourse = new Course();
        mockCourse.setCourseId(101L);
        mockCourse.setCourseName("Sample Course");
        mockCourse.setCourseDescription("Sample Course Description");
        mockCourse.setImage(new byte[]{1, 2, 3}); // Mock byte array for image

        Domain mockDomain = new Domain();
        mockDomain.setDomainId(201L);
        mockDomain.setDomainName("Sample Domain");

        Topic mockTopic = new Topic();
        mockTopic.setTopicId(301L);
        mockTopic.setTopicName("Sample Topic");

        // Set associations
        mockDomain.setTopics(Collections.singletonList(mockTopic));
        mockCourse.setDomains(Collections.singletonList(mockDomain));
        mockUser.setCourses(Collections.singleton(mockCourse));

        // Define repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        List<CourseDTO> courseDTOList = independentTestService.getCoursesByUserId(userId);

        // Assert
        assertNotNull(courseDTOList);
        assertEquals(1, courseDTOList.size());

        CourseDTO courseDTO = courseDTOList.get(0);
        assertEquals(mockCourse.getCourseId(), courseDTO.getCourseId());
        assertEquals(mockCourse.getCourseName(), courseDTO.getCourseName());
        assertEquals(mockCourse.getCourseDescription(), courseDTO.getCourseDescription());
        assertEquals(mockCourse.getImage(), courseDTO.getImage());

        List<DomainDTO> domainDTOList = courseDTO.getDomains();
        assertEquals(1, domainDTOList.size());

        DomainDTO domainDTO = domainDTOList.get(0);
        assertEquals(mockDomain.getDomainId(), domainDTO.getDomainId());
        assertEquals(mockDomain.getDomainName(), domainDTO.getDomainName());

        List<TopicDTO> topicDTOList = domainDTO.getTopics();
        assertEquals(1, topicDTOList.size());

        TopicDTO topicDTO = topicDTOList.get(0);
        assertEquals(mockTopic.getTopicId(), topicDTO.getTopicId());
        assertEquals(mockTopic.getTopicName(), topicDTO.getTopicName());
    }
}

