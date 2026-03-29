    // ...existing code...
package com.example.quizplatform.service;

import com.example.quizplatform.entity.Question;
import com.example.quizplatform.entity.Quiz;
import com.example.quizplatform.repository.QuestionRepository;
import com.example.quizplatform.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {
        public Question updateQuestion(Long questionId, Question updated) {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            question.setQuestionText(updated.getQuestionText());
            question.setOptionA(updated.getOptionA());
            question.setOptionB(updated.getOptionB());
            question.setOptionC(updated.getOptionC());
            question.setOptionD(updated.getOptionD());
            question.setCorrectAnswers(updated.getCorrectAnswers());
            return questionRepository.save(question);
        }

        public void deleteQuestion(Long questionId) {
            questionRepository.deleteById(questionId);
        }
    public Integer getQuizDuration(Long quizId) {
        return quizRepository.findById(quizId)
                .map(Quiz::getDuration)
                .orElse(10); // fallback to 10 minutes if not set
    }

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public Quiz createQuiz(String title, Integer duration) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDuration(duration);
        return quizRepository.save(quiz);
    }

    public Question addQuestion(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> searchQuizzes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return quizRepository.findAll();
        }
        return quizRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }

    public List<Question> getQuestionsByQuiz(Long quizId) {
        return questionRepository.findByQuiz_Id(quizId);
    }

    public List<Question> getShuffledQuestionsByQuiz(Long quizId) {
        List<Question> questions = questionRepository.findByQuiz_Id(quizId);
        Collections.shuffle(questions);
        return questions;
    }

    public int calculateScore(Long quizId, Map<Long, List<String>> submittedAnswers) {
        List<Question> questions = questionRepository.findByQuiz_Id(quizId);

        int score = 0;

        for (Question question : questions) {
            List<String> userAnswers = submittedAnswers.get(question.getId());

            if (userAnswers == null) continue;

            Set<String> correctSet = new HashSet<>(
                    Arrays.asList(question.getCorrectAnswers().split(","))
            );

            Set<String> userSet = new HashSet<>(userAnswers);

            if (correctSet.equals(userSet)) {
                score++;
            }
        }

        return score;
    }
}