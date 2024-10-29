package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.EnrolledTestDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.model.EnrolledTest;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrolledTestService {

    @Autowired
    private EnrolledTestRepository testRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    // Fetch and create a test while fetching Domain, Topic, and Questions
    public EnrolledTestDTO createTestWithDetails(EnrolledTestDTO testDTO) {
        EnrolledTest test = new EnrolledTest();
        test.setTestName(testDTO.getTestName());
        test.setTotalGrading(testDTO.getTotalGrading());

        // Fetch the topic entity using the topicId
        Topic topic = topicRepository.findById(testDTO.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        // Set the topicId in the test entity (optional based on your design)
        test.setTopicId(topic.getTopicId());

        // Fetch the class entity if applicable and set it
//        Class enrolledClass = classRepository.findById(testDTO.getClassId()) // Add class field to DTO
//                .orElseThrow(() -> new RuntimeException("Class not found"));
//        test.setClassId(enrolledClass.getClassId());

        // Fetch the questions for this topic using the Topic entity
        List<Question> questions = questionRepository.findByTopic(topic);  // Pass the Topic entity
        test.setQuestionCount(questions.size());

        // Save the test entity
        EnrolledTest createdTest = testRepository.save(test);

        // Convert the created entity to DTO and return, including question texts
        return convertToDTO(createdTest, questions);
    }

    // Fetch a test by ID and retrieve question texts using the topic entity
    public Optional<EnrolledTestDTO> getTestById(Long testId) {
        return testRepository.findById(testId).map(test -> {
            // Fetch the topic entity using topicId
            Topic topic = topicRepository.findById(test.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            // Fetch questions based on the Topic entity
            List<Question> questions = questionRepository.findByTopic(topic);
            return convertToDTO(test, questions);
        });
    }

    // Helper method to convert Test entity to DTO (assuming this method is already defined)
    private EnrolledTestDTO convertToDTO(EnrolledTest test, List<Question> questions) {
        EnrolledTestDTO testDTO = new EnrolledTestDTO();
        testDTO.setTestsId(test.getTestsId());
        testDTO.setTestName(test.getTestName());
        testDTO.setTotalGrading(test.getTotalGrading());
        testDTO.setDomainId(test.getDomainId());
        testDTO.setTopicId(test.getTopicId());
        testDTO.setQuestionCount(test.getQuestionCount());

        // Add the question texts to the DTO
        List<String> questionTexts = questions.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());
        testDTO.setQuestionTexts(questionTexts);

        return testDTO;
    }

    public List<EnrolledTestDTO> getAllTests() {
        List<EnrolledTest> tests = testRepository.findAll(); // Fetch all tests from the repository

        // Convert the list of tests to DTOs
        return tests.stream()
                .map(test -> {
                    // Fetch the topic for each test to include in the DTO
                    Topic topic = topicRepository.findById(test.getTopicId())
                            .orElseThrow(() -> new RuntimeException("Topic not found"));

                    // Fetch questions based on the Topic entity
                    List<Question> questions = questionRepository.findByTopic(topic);
                    return convertToDTO(test, questions);
                })
                .collect(Collectors.toList());
    }


    // Method to fetch courses for a specific user by their ID
    @Transactional
    public List<CourseDTO> getCoursesByUserId(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getCourses().stream().map(course -> {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(course.getCourseId());
            courseDTO.setCourseName(course.getCourseName());
            courseDTO.setCourseDescription(course.getCourseDescription());
            courseDTO.setImage(course.getImage());

            // Fetch and set domains and topics
            List<DomainDTO> domainDTOList = course.getDomains().stream().map(domain -> {
                DomainDTO domainDTO = new DomainDTO();
                domainDTO.setDomainId(domain.getDomainId());
                domainDTO.setDomainName(domain.getDomainName());

                // Fetch and set topics for each domain
                List<TopicDTO> topicDTOList = domain.getTopics().stream().map(topic -> {
                    TopicDTO topicDTO = new TopicDTO();
                    topicDTO.setTopicId(topic.getTopicId());
                    topicDTO.setTopicName(topic.getTopicName());
                    return topicDTO;
                }).collect(Collectors.toList());

                domainDTO.setTopics(topicDTOList);
                return domainDTO;
            }).collect(Collectors.toList());

            courseDTO.setDomains(domainDTOList);
            return courseDTO;
        }).collect(Collectors.toList());
    }

}
