// ...existing code...
package com.example.quizplatform.repository;
import org.springframework.data.jpa.repository.Query;

import com.example.quizplatform.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
        @Query("SELECT s.studentEmail, SUM(s.score) as totalScore FROM Submission s GROUP BY s.studentEmail ORDER BY totalScore DESC")
        List<Object[]> findTopStudents();
    List<Submission> findByQuiz_Id(Long quizId);
    List<Submission> findByStudentEmail(String studentEmail);
}