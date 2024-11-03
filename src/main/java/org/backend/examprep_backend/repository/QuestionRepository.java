package org.backend.examprep_backend.repository;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopic(Topic topic);  // Find questions by topic

    List<Question> findByTopicAndIsModeratedTrue(Topic topic);

    Page<Question> findByTopic(Topic topic, Pageable pageable);  // Find paginated questions by topic

    List<Question> findByTopic_TopicId(Long topicId); // Fetch questions by topicId

    @Query("SELECT q FROM Question q " +
            "JOIN q.topic t " +
            "JOIN t.domain d " +
            "JOIN d.course c " +
            "WHERE q.isModerated = false AND c.courseId = :courseId")
    List<Question> findUnmoderatedQuestionsByCourseId(@Param("courseId") Long courseId);
}
