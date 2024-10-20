package org.backend.examprep_backend.service;

import org.backend.examprep_backend.model.Answer;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.repository.AnswerRepository;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class DumpService {

    private static final Logger logger = Logger.getLogger(DumpService.class.getName());

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public void processDump(MultipartFile file) throws Exception {
        List<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream()))
                .lines()
                .collect(Collectors.toList());

        Question currentQuestion = null;
        List<Answer> answers = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("Q:")) { // New question
                if (currentQuestion != null) {
                    currentQuestion.setAnswers(answers);
                    questionRepository.save(currentQuestion); // Save the previous question
                }

                answers = new ArrayList<>();
                String questionText = line.substring(2).trim();
                currentQuestion = new Question();
                currentQuestion.setQuestionText(questionText);

            } else if (line.startsWith("A:")) { // New answer
                String[] answerParts = line.substring(2).split(",");
                if (answerParts.length < 2) continue; // Skip malformed lines

                String answerText = answerParts[0].trim();
                boolean isCorrect = Boolean.parseBoolean(answerParts[1].trim());

                Answer answer = new Answer();
                answer.setAnswerText(answerText);
                answer.setCorrect(isCorrect);
                answer.setQuestion(currentQuestion); // Link answer to the current question
                answers.add(answer);
            }
        }

        // Save the last question if it exists
        if (currentQuestion != null) {
            currentQuestion.setAnswers(answers);
            questionRepository.save(currentQuestion);
        }
    }
}