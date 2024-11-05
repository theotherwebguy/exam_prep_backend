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

//    @Autowired
//    private IndependentTestRepository testRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private TestReviewRepository testReviewRepository;

    @Autowired
    private AnswerRepository answerRepository;

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

//    public List<TestReviewDTO> findByStudentId(Long studentId) {
//        List<TestReview> reviews = testReviewRepository.findByStudentId(studentId);
//        return reviews.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    private TestReviewDTO convertToDTO(TestReview review) {
//        TestReviewDTO dto = new TestReviewDTO();
//        dto.setId(review.getId());
//        dto.setStudentId(review.getStudentId());
//        dto.setTestId(review.getTestId());
//        dto.setQuestionId(review.getQuestionId());
//        dto.setSelectedAnswerId(review.getSelectedAnswerId());
//        dto.setIsCorrect(review.getIsCorrect());
//        dto.setScore(review.getScore());
//        return dto;
//    }
}
