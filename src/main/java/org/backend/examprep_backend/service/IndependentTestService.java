package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.*;
import org.backend.examprep_backend.model.*;
import org.backend.examprep_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Autowired
    private TestReviewRepository testReviewRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public IndependentTestDTO createTestWithDetails(IndependentTestDTO testDTO, Long studentId) {
        IndependentTest test = new IndependentTest();
        test.setTestName(testDTO.getTestName());
        test.setQuestionCount(testDTO.getQuestionCount());
        test = testRepository.save(test);

        IndependentTest finalTest = test;
        List<DomainDTO> domainDTOList = testDTO.getTopicIds().stream()
                .map(topicId -> {
                    Topic topic = topicRepository.findById(topicId)
                            .orElseThrow(() -> new RuntimeException("Topic not found"));

                    List<Question> questions = questionRepository.findByTopic(topic)
                            .stream()
                            .limit(testDTO.getQuestionCount())
                            .collect(Collectors.toList());

                    questions.forEach(question -> {
                        List<Answer> fetchedAnswers = answerRepository.findByQuestion(question);
                        question.setAnswers(fetchedAnswers);
                    });

                    questions.forEach(question -> {
                        TestReview testReview = new TestReview();
                        testReview.setTestId(finalTest.getTestsId());
                        testReview.setStudentId(studentId);
                        testReview.setQuestionId(question.getQuestionId());
                        testReview.setSelectedAnswerId(null);
                        testReview.setIsCorrect(false);
                        testReview.setScore(0);
                        testReviewRepository.save(testReview);
                    });

                    TopicDTO topicDTO = new TopicDTO();
                    topicDTO.setTopicId(topic.getTopicId());
                    topicDTO.setTopicName(topic.getTopicName());
                    topicDTO.setQuestions(questions.stream()
                            .map(q -> {
                                QuestionDTO questionDTO = new QuestionDTO();
                                questionDTO.setQuestionId(q.getQuestionId());
                                questionDTO.setQuestionText(q.getQuestionText());

                                questionDTO.setAnswers(q.getAnswers().stream()
                                        .map(a -> {
                                            AnswerDTO answerDTO = new AnswerDTO();
                                            answerDTO.setAnswerId(a.getAnswerId());
                                            answerDTO.setAnswerText(a.getAnswerText());
                                            return answerDTO;
                                        })
                                        .collect(Collectors.toList()));
                                return questionDTO;
                            })
                            .collect(Collectors.toList()));

                    DomainDTO domainDTO = new DomainDTO();
                    domainDTO.setDomainId(topic.getDomain().getDomainId());
                    domainDTO.setDomainName(topic.getDomain().getDomainName());
                    domainDTO.setTopics(List.of(topicDTO));

                    return domainDTO;
                })
                .collect(Collectors.toList());

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

    public List<TestReviewDTO> findByStudentId(Long studentId) {
        List<TestReview> reviews = testReviewRepository.findByStudentId(studentId);
        return reviews.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private TestReviewDTO convertToDTO(TestReview review) {
        TestReviewDTO dto = new TestReviewDTO();
        dto.setId(review.getId());
        dto.setStudentId(review.getStudentId());
        dto.setTestId(review.getTestId());
        dto.setQuestionId(review.getQuestionId());
        dto.setSelectedAnswerId(review.getSelectedAnswerId());
        dto.setIsCorrect(review.getIsCorrect());
        dto.setScore(review.getScore());
        return dto;
    }
}
