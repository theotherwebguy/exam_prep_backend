package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.IndependentTestDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.model.IndependentTest;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.IndependentTestRepository;
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
public class IndependentTestService {

    @Autowired
    private IndependentTestRepository testRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;

    public IndependentTestDTO createTestWithDetails(IndependentTestDTO testDTO) {
        IndependentTest test = new IndependentTest();
        test.setTestName(testDTO.getTestName());

        // Save the test to generate ID
        test = testRepository.save(test);

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

        // Convert to DTO and set domains
        IndependentTestDTO createdTestDTO = convertToDTO(test, domainDTOList);
        createdTestDTO.setDomains(domainDTOList);

        return createdTestDTO;
    }

    private IndependentTestDTO convertToDTO(IndependentTest test, List<DomainDTO> domains) {
        IndependentTestDTO testDTO = new IndependentTestDTO();
        testDTO.setTestsId(test.getTestsId());
        testDTO.setTestName(test.getTestName());
        testDTO.setQuestionCount(test.getQuestionCount());
        testDTO.setDomains(domains);

        return testDTO;
    }

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

            List<DomainDTO> domainDTOList = course.getDomains().stream().map(domain -> {
                DomainDTO domainDTO = new DomainDTO();
                domainDTO.setDomainId(domain.getDomainId());
                domainDTO.setDomainName(domain.getDomainName());

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
