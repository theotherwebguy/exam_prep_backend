package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.*;
import org.backend.examprep_backend.model.*;
import org.backend.examprep_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TopicRepository topicRepository;


    @Transactional
    public TestDTO createTest(TestCreationRequestDTO request, Long studentId) {
        // Create a new Test entity
        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Test test = new Test();
        test.setName(request.getTestName());
        test.setStudent(student); // Associate the student with the test

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

            //List<Question> questions = questionRepository.findByTopicAndIsModeratedTrue(topic);
            List<Question> questions = questionRepository.findByTopic(topic)
                    .stream()
                    .filter(Question::isModerated)
                    .limit(questionCount)
                    .collect(Collectors.toList());

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
        test.setQuestionCount(totalQuestionCount);

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

    @Transactional
    public TestDTO startWriting(Long testId, Long studentId) {
        // Step 1: Retrieve the test by its ID
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        // Verify if the student is eligible to take this test (placeholder check)
        if (!checkStudentEligibility(test, studentId)) {
            throw new RuntimeException("Student is not eligible to take this test.");
        }

        // Step 2: Retrieve all test questions and prepare them for response
        // Step 3: Map questions and their answers into QuestionDTO objects
        List<QuestionDTO> questionDTOList = test.getTestQuestions().stream()
                .map(testQuestion -> {
                    Question question = testQuestion.getQuestion();
                    QuestionDTO questionDTO = mapQuestionToDTO(question); // Map question details
                    questionDTO.setAnswers(question.getAnswers().stream()
                            .map(answer -> new AnswerDTO(answer.getAnswerId(), answer.getAnswerText(), answer.isCorrect(), answer.getAnswerDescription()))
                            .collect(Collectors.toList())); // Add possible answers
                    return questionDTO;
                })
                .collect(Collectors.toList());
        // Step 4: Build TestDTO with test details and list of questions
        TestDTO testDTO = new TestDTO();
        testDTO.setTestId(test.getId());
        testDTO.setTestName(test.getName());
        testDTO.setInstruction(test.getInstruction());
        testDTO.setDueDate(test.getDueDate());
        testDTO.setDuration(test.getDuration());
        testDTO.setQuestionCount(test.getQuestionCount());
        testDTO.setQuestions(questionDTOList); // Add populated questions

        return testDTO;
    }

    // Helper method to check if the student is eligible to take the test
    private boolean checkStudentEligibility(Test test, Long studentId) {
        // Example check: Ensure the student is linked to the test's course or lecturer
        // For simplicity, returning true (no check implemented here)
        return true;
    }





    // Helper method to create DomainDTO from a Topic and its Questions
    private DomainDTO createDomainDTO(Topic topic, List<Question> questions) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setTopicId(topic.getTopicId());
        topicDTO.setTopicName(topic.getTopicName());

        List<QuestionDTO> questionDTOs = questions.stream()
                .map(this::mapQuestionToDTO)
                .collect(Collectors.toList());
        topicDTO.setQuestions(questionDTOs);

        DomainDTO domainDTO = new DomainDTO();
        domainDTO.setDomainId(topic.getDomain().getDomainId());
        domainDTO.setDomainName(topic.getDomain().getDomainName());
        domainDTO.setTopics(Collections.singletonList(topicDTO));

        return domainDTO;
    }
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

        List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                .map(answer -> new AnswerDTO(answer.getAnswerId(), answer.getAnswerText(), answer.isCorrect(), answer.getAnswerDescription()))
                .collect(Collectors.toList());
        questionDTO.setAnswers(answerDTOs);

        return questionDTO;
    }
    // Helper method to map Test entity to TestDTO for response
    private TestDTO mapToTestDTO(Test test, List<DomainDTO> domainDTOList) {
        TestDTO testDTO = new TestDTO();
        testDTO.setTestId(test.getId());
        testDTO.setTestName(test.getName());
        testDTO.setDuration(test.getDuration());
        testDTO.setDueDate(test.getDueDate());
        testDTO.setInstruction(test.getInstruction());
        testDTO.setTotalGrade(test.getTotalGrade());
        testDTO.setDomains(domainDTOList);
        System.out.println("Test ID in DTO: " + testDTO.getTestId()); // Debug log

        // Map all test questions to a flat list if questions field is separate
        List<QuestionDTO> questionDTOList = test.getTestQuestions().stream()
                .map(testQuestion -> mapQuestionToDTO(testQuestion.getQuestion()))
                .collect(Collectors.toList());

        testDTO.setQuestions(questionDTOList); // Ensure questions are set
        return testDTO;
    }



}


