package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.model.Answer;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.QuestionType;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.backend.examprep_backend.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
    }

//    @Test
//    void testUpdateQuestionWithPdf_Success() throws IOException {
//        // Arrange
//        Long questionId = 1L;
//        QuestionDTO questionDTO = new QuestionDTO();
//        questionDTO.setQuestionText("Updated Question");
//        questionDTO.setInstruction("Updated Instruction");
//        questionDTO.setTopicId(1L);
//
//        // Mocking existing question
//        Question existingQuestion = new Question();
//        existingQuestion.setQuestionId(questionId);
//        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion));
//
//        // Mocking topic
//        Topic mockTopic = new Topic();
//        mockTopic.setTopicId(1L);
//        when(topicRepository.findById(1L)).thenReturn(Optional.of(mockTopic));
//
//        // Mock file
//        MockMultipartFile pdfFile = new MockMultipartFile(
//                "file", "newfile.pdf", "application/pdf", "New Content".getBytes()
//        );
//
//        // Spy on questionService to skip actual file saving
//        QuestionService spyQuestionService = spy(questionService);
//        doNothing().when(spyQuestionService).saveFileToLocalDirectory(any(), anyString());
//
//        // Act
//        spyQuestionService.updateQuestionWithPdf(questionId, questionDTO, pdfFile);
//
//        // Assert
//        verify(questionRepository, times(1)).save(any(Question.class));
//    }
//
//    @Test
//    void testSaveQuestionWithInvalidPdf_ThrowsException() {
//        QuestionDTO questionDTO = new QuestionDTO();
//        questionDTO.setQuestionText("Sample Question");
//
//        MockMultipartFile invalidFile = new MockMultipartFile(
//                "file", "test.txt", "text/plain", "Invalid content".getBytes()
//        );
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            questionService.saveQuestionWithPdf(questionDTO, invalidFile);
//        });
//    }

    @Test
    void testGetQuestionsByTopicId() {
        Long topicId = 1L;
        List<Question> mockQuestions = new ArrayList<>();
        mockQuestions.add(new Question());
        when(questionRepository.findByTopic_TopicId(topicId)).thenReturn(mockQuestions);

        List<Question> result = questionService.getQuestionsByTopicId(topicId);

        assertEquals(mockQuestions.size(), result.size());
        verify(questionRepository, times(1)).findByTopic_TopicId(topicId);
    }

//    @Test
//    void testGetUnmoderatedQuestionsByCourseId() {
//        Long courseId = 1L;
//        List<Question> mockQuestions = new ArrayList<>();
//        Question mockQuestion = new Question();
//        mockQuestion.setQuestionId(1L);
//        mockQuestion.setQuestionText("Unmoderated Question");
//        mockQuestions.add(mockQuestion);
//
//        when(questionRepository.findUnmoderatedQuestionsByCourseId(courseId)).thenReturn(mockQuestions);
//
//        List<QuestionDTO> result = questionService.getUnmoderatedQuestionsByCourseId(courseId);
//
//        assertEquals(1, result.size());
//        assertEquals("Unmoderated Question", result.get(0).getQuestionText());
//        verify(questionRepository, times(1)).findUnmoderatedQuestionsByCourseId(courseId);
//    }

//    @Test
//    void testUpdateQuestionWithPdf_Success() throws IOException {
//        Long questionId = 1L;
//        QuestionDTO questionDTO = new QuestionDTO();
//        questionDTO.setQuestionText("Updated Question");
//        questionDTO.setInstruction("Updated Instruction");
//        questionDTO.setTopicId(1L);
//
//        Question existingQuestion = new Question();
//        existingQuestion.setQuestionId(questionId);
//
//        Topic mockTopic = new Topic();
//        mockTopic.setTopicId(1L);
//        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion));
//        when(topicRepository.findById(1L)).thenReturn(Optional.of(mockTopic));
//
//        MockMultipartFile pdfFile = new MockMultipartFile(
//                "file", "newfile.pdf", "application/pdf", "New Content".getBytes()
//        );
//
//        questionService.updateQuestionWithPdf(questionId, questionDTO, pdfFile);
//
//        verify(questionRepository, times(1)).save(any(Question.class));
//    }

    @Test
    void testGetQuestionById() {
        Long questionId = 1L;
        Question mockQuestion = new Question();
        mockQuestion.setQuestionId(questionId);
        mockQuestion.setQuestionText("Sample Question");
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));

        Question result = questionService.getQuestionById(questionId);

        assertNotNull(result);
        assertEquals("Sample Question", result.getQuestionText());
        verify(questionRepository, times(1)).findById(questionId);
    }
}
