package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.EnrolledTestDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.model.EnrolledTest;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.EnrolledTestRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.backend.examprep_backend.repository.UserRepository;
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

    public EnrolledTestDTO createTestWithDetails(EnrolledTestDTO testDTO) {
        EnrolledTest test = new EnrolledTest();
        test.setTestName(testDTO.getTestName());

        List<DomainDTO> domainDTOList = testDTO.getTopicIds().stream()
                .map(topicId -> {
                    Topic topic = topicRepository.findById(topicId)
                            .orElseThrow(() -> new RuntimeException("Topic not found"));

                    List<Question> questions = questionRepository.findByTopic(topic)
                            .stream()
                            .limit(testDTO.getQuestionCount())
                            .collect(Collectors.toList());

                    TopicDTO topicDTO = new TopicDTO();
                    topicDTO.setTopicId(topic.getTopicId());
                    topicDTO.setTopicName(topic.getTopicName());
                    topicDTO.setQuestions(questions.stream()
                            .map(Question::getQuestionText)
                            .collect(Collectors.toList()));

                    DomainDTO domainDTO = new DomainDTO();
                    domainDTO.setDomainId(topic.getDomain().getDomainId());
                    domainDTO.setDomainName(topic.getDomain().getDomainName());
                    domainDTO.setTopics(List.of(topicDTO));

                    return domainDTO;
                })
                .collect(Collectors.toList());

        EnrolledTestDTO createdTestDTO = convertToDTO(test, domainDTOList);
        createdTestDTO.setDomains(domainDTOList);

        return createdTestDTO;
    }

    private EnrolledTestDTO convertToDTO(EnrolledTest test, List<DomainDTO> domains) {
        EnrolledTestDTO testDTO = new EnrolledTestDTO();
        testDTO.setTestsId(test.getTestsId());
        testDTO.setTestName(test.getTestName());
//        testDTO.setDomainId(test.getDomainId());
        testDTO.setQuestionCount(test.getQuestionCount());
        testDTO.setDomains(domains);

        return testDTO;
    }


//    public List<IndependentTestDTO> getAllTests() {
//        List<IndependentTest> tests = testRepository.findAll(); // Fetch all tests from the repository
//
//        // Convert the list of tests to DTOs
//        return tests.stream()
//                .map(test -> {
//                    // Fetch the topic for each test to include in the DTO
//                    Topic topic = topicRepository.findById(test.getTopicId())
//                            .orElseThrow(() -> new RuntimeException("Topic not found"));
//
//                    // Fetch questions based on the Topic entity
//                    List<Question> questions = questionRepository.findByTopic(topic);
//                    return convertToDTO(test, questions);
//                })
//                .collect(Collectors.toList());
//    }





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