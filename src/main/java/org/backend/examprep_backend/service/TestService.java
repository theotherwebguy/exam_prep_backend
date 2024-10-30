package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.TestDTO;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Test;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.backend.examprep_backend.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    public TestService(TestRepository testRepository, QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public Test createTestWithDetails(TestDTO testDTO) {
        // Step 1: Create Test and Save Basic Details
        Test test = new Test();
        test.setTestName(testDTO.getTestName());
        test.setTotalGrade(testDTO.getTotalGrade());
        test.setQuestionCount(testDTO.getQuestionCount());
        test.setInstructions(testDTO.getInstructions());
        test.setDueDate(testDTO.getDueDate());
        test.setTestDuration(testDTO.getTestDuration());

        // Step 2: Fetch Questions from Selected Topics
        List<Question> questions = testDTO.getTopicIds().stream()
                .flatMap(topicId -> questionRepository.findByTopic_TopicId(topicId).stream())
                .limit(testDTO.getQuestionCount()) // Limit questions to the required count
                .collect(Collectors.toList());

        // Step 3: Associate Questions with the Test
        questions.forEach(question -> question.setTest(test));
        test.setQuestions(questions);

        // Step 4: Save the Test with Questions
        return testRepository.save(test);
    }
}
