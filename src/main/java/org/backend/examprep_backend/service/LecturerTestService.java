//package org.backend.examprep_backend.service;
//
//import org.backend.examprep_backend.dto.*;
//import org.backend.examprep_backend.model.*;
//import org.backend.examprep_backend.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class LecturerTestService {
//
//    @Autowired
//    private TestRepository testRepository;
//
//    @Autowired
//    private QuestionRepository questionRepository;
//
//    @Autowired
//    private TopicRepository topicRepository;
//
//    @Autowired
//    private AnswerRepository answerRepository;
//
//    @Transactional
//    public TestDTO createLecturerTest(LecturerTestCreationRequestDTO request) {
//        // Create a new Test entity and set its properties
//        Test test = new Test();
//        test.setName(request.getTestName());
//        test.setDueDate(request.getDueDate());
//        test.setDuration(request.getDuration());
//        test.setInstruction(request.getInstruction());
//        test.setTotalGrade(request.getTotalGrade());
//
//        List<DomainDTO> domainDTOList = new ArrayList<>();
//        int totalQuestionCount = 0;
//
//        for (Map.Entry<Long, Integer> entry : request.getTopicQuestionCount().entrySet()) {
//            Long topicId = entry.getKey();
//            Integer questionCount = entry.getValue();
//
//            Topic topic = topicRepository.findById(topicId)
//                    .orElseThrow(() -> new RuntimeException("Topic not found"));
//
//            List<Question> questions = questionRepository.findByTopic(topic)
//                    .stream()
//                    .filter(Question::isModerated)
//                    .limit(questionCount)
//                    .collect(Collectors.toList());
//
//            List<TestQuestion> testQuestions = new ArrayList<>();
//            for (Question question : questions) {
//                testQuestions.add(createTestQuestion(test, question));
//            }
//            test.getTestQuestions().addAll(testQuestions);
//            totalQuestionCount += questions.size();
//
//            DomainDTO domainDTO = createDomainDTO(topic, questions);
//            domainDTOList.add(domainDTO);
//        }
//
//        test.setQuestionCount(totalQuestionCount);
//        testRepository.save(test);
//
//        return mapToTestDTO(test, domainDTOList);
//    }
//
//    private TestQuestion createTestQuestion(Test test, Question question) {
//        TestQuestion testQuestion = new TestQuestion();
//        testQuestion.setTest(test);
//        testQuestion.setQuestion(question);
//        testQuestion.setIsCorrect(false);
//        testQuestion.setScore(0);
//
//        List<Answer> answers = answerRepository.findByQuestion(question);
//        question.setAnswers(answers);
//        return testQuestion;
//    }
//
//    private DomainDTO createDomainDTO(Topic topic, List<Question> questions) {
//        TopicDTO topicDTO = new TopicDTO();
//        topicDTO.setTopicId(topic.getTopicId());
//        topicDTO.setTopicName(topic.getTopicName());
//
//        List<QuestionDTO> questionDTOs = questions.stream()
//                .map(this::mapQuestionToDTO)
//                .collect(Collectors.toList());
//        topicDTO.setQuestions(questionDTOs);
//
//        DomainDTO domainDTO = new DomainDTO();
//        domainDTO.setDomainId(topic.getDomain().getDomainId());
//        domainDTO.setDomainName(topic.getDomain().getDomainName());
//        domainDTO.setTopics(Collections.singletonList(topicDTO));
//
//        return domainDTO;
//    }
//
//    private QuestionDTO mapQuestionToDTO(Question question) {
//        QuestionDTO questionDTO = new QuestionDTO();
//        questionDTO.setQuestionId(question.getQuestionId());
//        questionDTO.setQuestionText(question.getQuestionText());
//        questionDTO.setTopicId(question.getTopic().getTopicId());
//        questionDTO.setTopicName(question.getTopic().getTopicName());
//        questionDTO.setDomainId(question.getTopic().getDomain().getDomainId());
//        questionDTO.setDomainName(question.getTopic().getDomain().getDomainName());
//        questionDTO.setQuestionType(question.getQuestionType());
//        questionDTO.setInstruction(question.getInstruction());
//        questionDTO.setPdfFileUrl(question.getPdfFileUrl());
//
//        List<AnswerDTO> answerDTOs = question.getAnswers().stream()
//                .map(answer -> new AnswerDTO(answer.getAnswerId(), answer.getAnswerText(), answer.isCorrect(), answer.getAnswerDescription()))
//                .collect(Collectors.toList());
//        questionDTO.setAnswers(answerDTOs);
//
//        return questionDTO;
//    }
//
//    private TestDTO mapToTestDTO(Test test, List<DomainDTO> domainDTOList) {
//        TestDTO testDTO = new TestDTO();
//        testDTO.setTestId(test.getId());
//        testDTO.setTestName(test.getName());
//        testDTO.setDueDate(test.getDueDate());
//        testDTO.setDuration(test.getDuration());
//        testDTO.setInstruction(test.getInstruction());
//        testDTO.setTotalGrade(test.getTotalGrade());
//        testDTO.setDomains(domainDTOList);
//        return testDTO;
//    }
//}
