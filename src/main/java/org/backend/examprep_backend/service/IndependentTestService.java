package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.IndependentTestDTO;
import org.backend.examprep_backend.model.IndependentTest;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.repository.IndependentTestRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Fetch and create a test while fetching Domain, Topic, and Questions
    public IndependentTestDTO createTestWithDetails(IndependentTestDTO testDTO) {
        IndependentTest test = new IndependentTest();
        test.setTestName(testDTO.getTestName());
        test.setTotalGrading(testDTO.getTotalGrading());

        // Fetch the topic entity using the topicId
        Topic topic = topicRepository.findById(testDTO.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        // Set the topicId in the test entity (optional based on your design)
        test.setTopicId(topic.getTopicId());

        // Fetch the questions for this topic using the Topic entity
        List<Question> questions = questionRepository.findByTopic(topic);  // Pass the Topic entity
        test.setQuestionCount(questions.size());

        // Save the test entity
        IndependentTest createdTest = testRepository.save(test);

        // Convert the created entity to DTO and return, including question texts
        return convertToDTO(createdTest, questions);
    }

    // Fetch a test by ID and retrieve question texts using the topic entity
    public Optional<IndependentTestDTO> getTestById(Long testId) {
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
    private IndependentTestDTO convertToDTO(IndependentTest test, List<Question> questions) {
        IndependentTestDTO testDTO = new IndependentTestDTO();
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

    public List<IndependentTestDTO> getAllTests() {
        List<IndependentTest> tests = testRepository.findAll(); // Fetch all tests from the repository

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

}
