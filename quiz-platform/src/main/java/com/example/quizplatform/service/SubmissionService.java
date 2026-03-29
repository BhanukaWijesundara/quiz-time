    // ...existing code...
package com.example.quizplatform.service;

import com.example.quizplatform.repository.SubmissionRepository;
import com.example.quizplatform.entity.Quiz;
import com.example.quizplatform.entity.Submission;
import com.example.quizplatform.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {

    // Analytics: Average score per quiz
    public double getAverageScoreForQuiz(Long quizId) {
        List<Submission> submissions = submissionRepository.findByQuiz_Id(quizId);
        if (submissions.isEmpty()) return 0.0;
        return submissions.stream().mapToInt(Submission::getScore).average().orElse(0.0);
    }

    // Analytics: Number of attempts per quiz
    public int getAttemptCountForQuiz(Long quizId) {
        return submissionRepository.findByQuiz_Id(quizId).size();
    }

    // Analytics: Top students by score (across all quizzes)
    public List<Object[]> getTopStudents(int limit) {
        // Returns [studentEmail, totalScore]
        return submissionRepository.findTopStudents().stream().limit(limit).toList();
    }

    private final SubmissionRepository submissionRepository;
    private final QuizRepository quizRepository;

    public SubmissionService(SubmissionRepository submissionRepository, QuizRepository quizRepository) {
        this.submissionRepository = submissionRepository;
        this.quizRepository = quizRepository;
    }

    public Submission saveSubmission(String studentEmail, Long quizId, int score, int totalQuestions) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Submission submission = new Submission();
        submission.setStudentEmail(studentEmail);
        submission.setQuiz(quiz);
        submission.setScore(score);
        submission.setTotalQuestions(totalQuestions);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public List<Submission> getSubmissionsByQuiz(Long quizId) {
        return submissionRepository.findByQuiz_Id(quizId);
    }

    public List<Submission> getSubmissionsByStudent(String studentEmail) {
        return submissionRepository.findByStudentEmail(studentEmail);
    }
}