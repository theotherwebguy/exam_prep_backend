package org.backend.examprep_backend.controller;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @Autowired
    private CourseService courseService;

    // Get all topics by domain ID
    @GetMapping("/domain/{domainId}")
    public List<Topic> getTopicsByDomainId(@PathVariable Long domainId) {
        return courseService.getTopicsByDomain(domainId);
    }
}

