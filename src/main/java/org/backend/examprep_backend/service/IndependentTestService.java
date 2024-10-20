package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.IndependentTestDTO;
import org.backend.examprep_backend.model.IndependentTest;
import org.backend.examprep_backend.model.Domain;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.repository.IndependentTestRepository;
import org.backend.examprep_backend.repository.DomainRepository;
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
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // Convert DTO to Entity and create test while fetching Domain, Topic, and Questions
    public IndependentTestDTO createTestWithDetails(IndependentTestDTO testDTO) {
        IndependentTest test = new IndependentTest(); // Create a new Test entity
        test.setTestName(testDTO.getTestName());
        test.setTotalGrading(testDTO.getTotalGrading());

        // Fetch domain and topic based on the IDs provided in the DTO
        Domain domain = domainRepository.findById(testDTO.getDomainId())
                .orElseThrow(() -> new RuntimeException("Domain not found"));
        Topic topic = topicRepository.findById(testDTO.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        // Set the domain and topic IDs in the test entity
        test.setDomainId(domain.getDomainId());
        test.setTopicId(topic.getTopicId());

        // Fetch the questions for this topic and count them
        List<Question> questions = questionRepository.findByTopic(topic);
        test.setQuestionCount(questions.size());

        // Save test entity
        IndependentTest createdTest = testRepository.save(test);

        // Convert the created entity to DTO and return
        return convertToDTO(createdTest);
    }

    // Helper method to convert Test entity to TestDTO
    private IndependentTestDTO convertToDTO(IndependentTest test) {
        IndependentTestDTO testDTO = new IndependentTestDTO();
        testDTO.setTestsId(test.getTestsId());
        testDTO.setTestName(test.getTestName());
        testDTO.setTotalGrading(test.getTotalGrading());
        testDTO.setDomainId(test.getDomainId());
        testDTO.setTopicId(test.getTopicId());
        testDTO.setQuestionCount(test.getQuestionCount());
        return testDTO;
    }

    // Convert Entity to DTO (after fetching from DB)
    public Optional<IndependentTestDTO> getTestById(Long testId) {
        return testRepository.findById(testId).map(this::convertToDTO);
    }

    // Fetch all tests and convert each entity to DTO
    public List<IndependentTestDTO> getAllTests() {
        return testRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
