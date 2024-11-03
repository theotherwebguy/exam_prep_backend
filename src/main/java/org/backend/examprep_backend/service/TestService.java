package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.dto.TestCreationRequestDTO;
import org.backend.examprep_backend.dto.TestDTO;
import org.backend.examprep_backend.model.*;
import org.backend.examprep_backend.repository.TopicRepository;
import org.backend.examprep_backend.repository.TestRepository;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.backend.examprep_backend.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TopicRepository topicRepository;

    public TestDTO createTest(TestCreationRequestDTO request, Long studentId) {
        // Create a new Test entity
        Test test = new Test();
        test.setName(request.getTestName());

        // List to hold domains for the response DTO
        List<DomainDTO> domainDTOList = new ArrayList<>();
        int totalQuestionCount = 0;  // Initialize a counter for the total number of questions

        // Loop through each topicId and question count pair
        for (Map.Entry<Long, Integer> entry : request.getTopicQuestionCount().entrySet()) {
            Long topicId = entry.getKey();
            Integer questionCount = entry.getValue();

            // Retrieve the Topic entity by topicId
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            // Fetch questions for the topic
            List<Question> questions = questionRepository.findByTopic(topic);
            List<Question> limitedQuestions = new ArrayList<>();

            // Limit the number of questions fetched based on the requested question count
            for (int i = 0; i < Math.min(questionCount, questions.size()); i++) {
                limitedQuestions.add(questions.get(i));
            }

            // Create TestQuestion entries and link them to the test
            List<TestQuestion> testQuestions = new ArrayList<>();
            for (Question question : limitedQuestions) {
                testQuestions.add(createTestQuestion(test, question));
            }
            test.getTestQuestions().addAll(testQuestions);

            // Update the total question count
            totalQuestionCount += limitedQuestions.size();

            // Map to TopicDTO and DomainDTO for response
            DomainDTO domainDTO = createDomainDTO(topic, limitedQuestions);
            domainDTOList.add(domainDTO);
        }

        // Set the total question count to the test entity
        test.setQuestionCount(totalQuestionCount); // Set the count here

        // Save the modified test entity with associated questions
        testRepository.save(test);

        // Map to TestDTO for response
        return mapToTestDTO(test, domainDTOList);
    }


    // Helper method to create TestQuestion entries
    private TestQuestion createTestQuestion(Test test, Question question) {
        TestQuestion testQuestion = new TestQuestion();
        testQuestion.setTest(test);
        testQuestion.setQuestion(question);
        testQuestion.setIsCorrect(false);
        testQuestion.setScore(0);

        // Populate question with its answers for later DTO mapping
        List<Answer> answers = answerRepository.findByQuestion(question);
        question.setAnswers(answers);

        return testQuestion;
    }

    // Helper method to create DomainDTO from a Topic and its Questions
    private DomainDTO createDomainDTO(Topic topic, List<Question> questions) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setTopicId(topic.getTopicId());
        topicDTO.setTopicName(topic.getTopicName());

        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            questionDTOs.add(mapQuestionToDTO(question));
        }
        topicDTO.setQuestions(questionDTOs);

        DomainDTO domainDTO = new DomainDTO();
        domainDTO.setDomainId(topic.getDomain().getDomainId());
        domainDTO.setDomainName(topic.getDomain().getDomainName());
        domainDTO.setTopics(Collections.singletonList(topicDTO));

        return domainDTO;
    }

    // Helper method to map Question to QuestionDTO
// Helper method to map Question to QuestionDTO
    private QuestionDTO mapQuestionToDTO(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestionId(question.getQuestionId());
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setTopicId(question.getTopic().getTopicId());
        questionDTO.setTopicName(question.getTopic().getTopicName());
        questionDTO.setDomainId(question.getTopic().getDomain().getDomainId());
        questionDTO.setDomainName(question.getTopic().getDomain().getDomainName());
        questionDTO.setQuestionType(question.getQuestionType());
        questionDTO.setInstruction(question.getInstruction());
        questionDTO.setPdfFileUrl(question.getPdfFileUrl());

        List<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : question.getAnswers()) {
            answerDTOs.add(new AnswerDTO(answer.getAnswerId(), answer.getAnswerText(), answer.isCorrect(), answer.getAnswerDescription()));
        }
        questionDTO.setAnswers(answerDTOs);

        return questionDTO;
    }


    // Helper method to map Test entity to TestDTO for response
    private TestDTO mapToTestDTO(Test test, List<DomainDTO> domainDTOList) {
        TestDTO testDTO = new TestDTO();
        testDTO.setTestId(test.getId());
        testDTO.setTestName(test.getName());
        testDTO.setDomains(domainDTOList);
        return testDTO;
    }
}


