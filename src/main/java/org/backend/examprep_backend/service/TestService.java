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
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestAttemptAnswerRepository testAttemptAnswerRepository;

    @Transactional
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
        System.out.println("Test ID in DTO: " + testDTO.getTestId()); // Debug log
        return testDTO;
    }

    @Transactional
    public TestAttemptDTO startTest(Long testId, Long studentId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TestAttempt testAttempt = new TestAttempt();
        testAttempt.setTest(test);
        testAttempt.setStudent(student);
        testAttempt.setCompleted(false);
        testAttemptRepository.save(testAttempt);

        List<QuestionDTO> questionDTOs = test.getTestQuestions().stream()
                .map(testQuestion -> mapQuestionToDTO(testQuestion.getQuestion()))
                .collect(Collectors.toList());

        TestAttemptDTO testAttemptDTO = new TestAttemptDTO();
        testAttemptDTO.setTestId(test.getId());
        testAttemptDTO.setTestName(test.getName());
        testAttemptDTO.setQuestions(questionDTOs);

        return testAttemptDTO;
    }

    @Transactional
    public void submitAnswers(Long testAttemptId, List<TestAttemptAnswerDTO> answers) {
        // Fetch the TestAttempt entity by its ID
        TestAttempt testAttempt = testAttemptRepository.findById(testAttemptId)
                .orElseThrow(() -> new RuntimeException("Test attempt not found"));

        int score = 0;  // Initialize score to 0

        // Loop through each answer provided by the frontend
        for (TestAttemptAnswerDTO answerDTO : answers) {
            // Fetch the question entity by its ID
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            // Fetch the selected answer by its ID (could be null if unanswered)
            Answer selectedAnswer = answerRepository.findById(answerDTO.getSelectedAnswerId())
                    .orElse(null);

            // Determine if the selected answer is correct
            boolean isCorrect = selectedAnswer != null && selectedAnswer.isCorrect();

            // Create a new TestAttemptAnswer entity to link the student's answer with the test attempt
            TestAttemptAnswer testAttemptAnswer = new TestAttemptAnswer();
            testAttemptAnswer.setTestAttempt(testAttempt);  // Associate with the test attempt
            testAttemptAnswer.setQuestion(question);  // Associate with the question
            testAttemptAnswer.setSelectedAnswer(selectedAnswer);  // Store the selected answer
            testAttemptAnswer.setIsCorrect(isCorrect);  // Set if the answer is correct

            // If the answer is correct, increment the score
            if (isCorrect) score++;

            // Save the test attempt answer to the database
            testAttemptAnswerRepository.save(testAttemptAnswer);
        }
        // Once all answers are saved, update the test attempt with the final score
        testAttempt.setScore(score);
        testAttempt.setCompleted(true);  // Mark the test as completed
        testAttemptRepository.save(testAttempt);  // Save the updated test attempt
    }

    @Transactional
    public List<TestReviewDTO> reviewTest(Long testAttemptId) {
        TestAttempt testAttempt = testAttemptRepository.findById(testAttemptId)
                .orElseThrow(() -> new RuntimeException("Test attempt not found with id: " + testAttemptId));


        List<TestReviewDTO> reviewDTOs = new ArrayList<>();

        for (TestAttemptAnswer attemptAnswer : testAttempt.getAnswers()) {
            Question question = attemptAnswer.getQuestion();
            TestReviewDTO reviewDTO = new TestReviewDTO();
            reviewDTO.setQuestionId(question.getQuestionId());
            reviewDTO.setQuestionText(question.getQuestionText());

            // Retrieve all answers for the question
            List<AnswerDTO> answerDTOs = question.getAnswers().stream().map(answer -> {
                AnswerDTO answerDTO = new AnswerDTO();
                answerDTO.setAnswerId(answer.getAnswerId());
                answerDTO.setAnswerText(answer.getAnswerText());
                answerDTO.setIsCorrect(answer.isCorrect());
                return answerDTO;
            }).collect(Collectors.toList());

            reviewDTO.setAnswers(answerDTOs);

            // Set the selected answer and correct answer
            reviewDTO.setSelectedAnswerId(attemptAnswer.getSelectedAnswer() != null ?
                    attemptAnswer.getSelectedAnswer().getAnswerId() : null);
            reviewDTO.setCorrectAnswerId(question.getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .findFirst()
                    .map(Answer::getAnswerId)
                    .orElse(null));

            reviewDTO.setCorrect(attemptAnswer.getIsCorrect());
            reviewDTOs.add(reviewDTO);
        }

        return reviewDTOs;
    }

    @Transactional
    public TestDTO createLecturerTest(LecturerTestCreationRequestDTO request) {
        Classes classAssigned = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        Test test = new Test();
        test.setName(request.getTestName());
        test.setDueDate(request.getDueDate());
        test.setDuration(request.getDuration());
        test.setInstruction(request.getInstruction());
        test.setTotalGrade(request.getTotalGrade());
        test.setClassAssigned(classAssigned);

        List<TestQuestion> testQuestions = new ArrayList<>();
        int totalQuestionCount = 0;

        for (Map.Entry<Long, Integer> entry : request.getTopicQuestionCount().entrySet()) {
            Long topicId = entry.getKey();
            Integer questionCount = entry.getValue();

            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            List<Question> questions = questionRepository.findByTopic(topic)
                    .stream()
                    .filter(Question::isModerated)
                    .limit(questionCount)
                    .collect(Collectors.toList());

            for (Question question : questions) {
                testQuestions.add(createTestQuestion(test, question));
            }

            totalQuestionCount += questions.size();
        }

        test.setQuestionCount(totalQuestionCount);
        test.getTestQuestions().addAll(testQuestions);
        testRepository.save(test);

        return mapToTestDTO(test);
    }

    private TestDTO mapToTestDTO(Test test) {
        TestDTO testDTO = new TestDTO();
        testDTO.setTestId(test.getId());
        testDTO.setTestName(test.getName());
        testDTO.setDueDate(test.getDueDate());
        testDTO.setDuration(test.getDuration());
        testDTO.setInstruction(test.getInstruction());
        testDTO.setTotalGrade(test.getTotalGrade());
        return testDTO;
    }
}


