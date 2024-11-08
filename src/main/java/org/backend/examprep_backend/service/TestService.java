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
    private TestAnswerRepository testAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestQuestionRepository testQuestionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestSubmissionRepository testSubmissionRepository;

    @Autowired
    private AnswerSubmissionRepository answerSubmissionRepository;


    @Transactional
    public TestDTO createTest(TestCreationRequestDTO request, Long studentId) {
        // Fetch student
        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        int totalQuestionCount = 0;
        for (Integer count : request.getTopicQuestionCount().values()) {
            totalQuestionCount += count; // Summing the question counts
        }

        if (totalQuestionCount <= 0) {
            throw new IllegalArgumentException("The total question count must be greater than zero.");
        }

        // Create a new Test and set properties
        Test test = new Test();
        test.setName(request.getTestName());
        test.setQuestionCount(totalQuestionCount); // Set the total question count
        test.setStudent(student);

        // Save the Test entity first
       test = testRepository.save(test); // Persist the Test entity here before using it in TestQuestions

        List<DomainDTO> domainDTOList = new ArrayList<>();


        // Validate topic-question count
        if (request.getTopicQuestionCount() == null || request.getTopicQuestionCount().isEmpty()) {
            throw new RuntimeException("No topics specified for the test.");
        }

        // Loop over each topic and generate questions for the test
        for (Map.Entry<Long, Integer> entry : request.getTopicQuestionCount().entrySet()) {
            Long topicId = entry.getKey();
            Integer questionCount = entry.getValue();

            // Fetch topic
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found"));

            // Fetch moderated questions for the given topic
            List<Question> questions = questionRepository.findByTopic(topic)
                    .stream()
                    .filter(Question::isModerated)
                    .limit(questionCount)
                    .collect(Collectors.toList());

            // Limit to the number of requested questions
            List<Question> limitedQuestions = questions.stream()
                    .limit(questionCount)
                    .collect(Collectors.toList());

            // Add TestQuestions to the Test
            List<TestQuestion> testQuestions = new ArrayList<>();
            for (Question question : limitedQuestions) {
                TestQuestion testQuestion = createTestQuestion(test, question); // Passing already saved test
                testQuestions.add(testQuestion);
            }

            // Add test questions to the test
            test.getTestQuestions().addAll(testQuestions);
            totalQuestionCount += limitedQuestions.size();

            // Create domainDTO for response mapping
            DomainDTO domainDTO = createDomainDTO(topic, limitedQuestions);
            domainDTOList.add(domainDTO);
        }

        // Set total question count BEFORE saving the test
        test.setQuestionCount(totalQuestionCount);

        // Save the test again (to update the question count)
        test = testRepository.save(test); // Re-save the test with updated question count

        // Return mapped TestDTO
        return mapToTestDTO(test, domainDTOList);
    }

    // Create TestQuestion and save associated TestAnswers
    private TestQuestion createTestQuestion(Test test, Question question) {
        TestQuestion testQuestion = new TestQuestion();
        testQuestion.setTest(test); // Test is now persisted when saving TestQuestion
        testQuestion.setQuestion(question);
        testQuestion.setIsCorrect(false);
        testQuestion.setScore(0);

        // Save the TestQuestion first before associating TestAnswers
        testQuestion = testQuestionRepository.save(testQuestion); // Now saved

        // Associate answers with test question
        for (Answer answer : question.getAnswers()) {
            TestAnswer testAnswer = new TestAnswer();
            testAnswer.setTestQuestion(testQuestion); // Now that TestQuestion is saved, we can associate it
            testAnswer.setAnswerText(answer.getAnswerText());
            testAnswer.setIsCorrect(answer.isCorrect());
            testAnswerRepository.save(testAnswer); // Save each test answer
        }

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

    @Transactional
    public void submitTest(Long testId, Long studentId, List<AnswerSubmissionDTO> answerSubmissions) {
        // Step 1: Retrieve the test and student
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Create TestSubmission entity
        TestSubmission testSubmission = new TestSubmission();
        testSubmission.setTest(test);
        testSubmission.setStudent(student);
        testSubmission.setScore((int) calculateScore(answerSubmissions)); // Casting double to int
        testSubmission.setSubmitted(true);  // Mark as submitted
        testSubmissionRepository.save(testSubmission);

        // Step 2: Create AnswerSubmission entities based on the provided answerSubmissions list
        List<AnswerSubmission> answerSubmissionList = new ArrayList<>();
        for (AnswerSubmissionDTO dto : answerSubmissions) {

            // Find the question and answer from their IDs
            Question question = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            TestAnswer testAnswer = testAnswerRepository.findById(dto.getAnswerId())
                    .orElseThrow(() -> new RuntimeException("Answer not found"));

            // Create an AnswerSubmission entity
            AnswerSubmission answerSubmission = new AnswerSubmission();
            answerSubmission.setQuestion(question);
            answerSubmission.setAnswer(testAnswer);
            answerSubmission.setSelected(dto.isSelected()); // Use isSelected from AnswerSubmissionDTO

            // Set correctness of the answer based on whether it is correct or not
            answerSubmission.setIsCorrect(testAnswer.isCorrect());
            answerSubmission.setTestSubmission(testSubmission);

            // Add to the list for bulk saving
            answerSubmissionList.add(answerSubmission);
        }

        // Step 3: Bulk save all answer submissions
        if (!answerSubmissionList.isEmpty()) {
            answerSubmissionRepository.saveAll(answerSubmissionList);
        }
    }

    private double calculateScore(List<AnswerSubmissionDTO> answerSubmissions) {
        double score = 0;
        for (AnswerSubmissionDTO dto : answerSubmissions) {
            // Here, you can compare the selected answer with the correct answer
            TestAnswer answer = testAnswerRepository.findById(dto.getAnswerId())
                    .orElseThrow(() -> new RuntimeException("Answer not found"));

            if (dto.isSelected() && answer.isCorrect()) {
                score += 1; // Assuming correct answers give 1 point, adjust if needed
            }
        }
        return score;
    }



    @Transactional
    public TestDTO lecturerCreateTest(LecturerTestCreationRequestDTO request, Long lecturerId) {
        // Fetch lecturer (renamed variable for clarity)
        Users lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        // Fetch class
        Classes linkedClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        int totalQuestionCount = 0;
        for (Integer count : request.getTopicQuestionCount().values()) {
            totalQuestionCount += count;
        }

        if (totalQuestionCount <= 0) {
            throw new IllegalArgumentException("The total question count must be greater than zero.");
        }

        // Create a new Test and set properties
        Test test = new Test();
        test.setName(request.getTestName());
        test.setDueDate(request.getDueDate());
        test.setDuration(request.getDuration());
        test.setInstruction(request.getInstruction());
        test.setTotalGrade(request.getTotalGrade());
        test.setQuestionCount(totalQuestionCount);
        test.setStudent(lecturer);
        test.setClassAssigned(linkedClass); // Link the test to the specified class

        // Persist the Test entity
        test = testRepository.save(test);

        List<DomainDTO> domainDTOList = new ArrayList<>();

        if (request.getTopicQuestionCount() == null || request.getTopicQuestionCount().isEmpty()) {
            throw new RuntimeException("No topics specified for the test.");
        }

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

            List<TestQuestion> testQuestions = new ArrayList<>();
            for (Question question : questions) {
                TestQuestion testQuestion = createTestQuestion(test, question);
                testQuestions.add(testQuestion);
            }

            test.getTestQuestions().addAll(testQuestions);

            DomainDTO domainDTO = createDomainDTO(topic, questions);
            domainDTOList.add(domainDTO);
        }

        test = testRepository.save(test); // Persist the test with the updated data

        return mapToTestDTO(test, domainDTOList);
    }


}


