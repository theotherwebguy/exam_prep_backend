package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.DomainDTO;
import org.backend.examprep_backend.dto.TopicDTO;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ModeratorService {

    private UserRepository userRepository;

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

            // Fetch and set domains and topics
            List<DomainDTO> domainDTOList = course.getDomains().stream().map(domain -> {
                DomainDTO domainDTO = new DomainDTO();
                domainDTO.setDomainId(domain.getDomainId());
                domainDTO.setDomainName(domain.getDomainName());

                // Fetch and set topics for each domain
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
}
