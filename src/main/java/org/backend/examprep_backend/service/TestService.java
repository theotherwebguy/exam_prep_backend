package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.TestDTO;
import org.backend.examprep_backend.model.Test;
import org.backend.examprep_backend.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    private final TestRepository testRepository;

    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Transactional
    public Test generateTest(TestDTO testDTO) {
        Test test = new Test();
        test.setTestName(testDTO.getTestName());
        test.setDueDate(testDTO.getDueDate());
        test.setTestDuration(testDTO.getTestDuration());
        test.setTestInstructions(testDTO.getInstructions());
        test.setTotalGrading(testDTO.getTotalGrade());
        test.setSelectedTopics(testDTO.getSelectedTopics());
        test.setTotalWeight(testDTO.getTotalWeight());

        return testRepository.save(test);  // Save to the database
    }
}

