package com.example.quizplatform.controller;

import com.example.quizplatform.entity.Question;
import com.example.quizplatform.service.QuizService;
import com.example.quizplatform.service.SubmissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final QuizService quizService;
    private final SubmissionService submissionService;

    public StudentController(QuizService quizService, SubmissionService submissionService) {
        this.quizService = quizService;
        this.submissionService = submissionService;
    }

    @GetMapping("/dashboard")
    public String showStudentDashboard(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("quizzes", quizService.searchQuizzes(keyword));
        } else {
            model.addAttribute("quizzes", quizService.getAllQuizzes());
        }
        model.addAttribute("keyword", keyword);
        return "student-dashboard";
    }

    @GetMapping("/quiz/{quizId}")
    public String attemptQuiz(@PathVariable Long quizId, Model model) {
        List<Question> questions = quizService.getShuffledQuestionsByQuiz(quizId);
        Integer duration = quizService.getQuizDuration(quizId);
        model.addAttribute("questions", questions);
        model.addAttribute("quizId", quizId);
        model.addAttribute("duration", duration);
        return "quiz-attempt";
    }

    @PostMapping("/quiz/{quizId}/submit")
    public String submitQuiz(@PathVariable Long quizId,
                             @RequestParam Map<String, String> allParams,
                             Principal principal,
                             Model model) {

        Map<Long, List<String>> submittedAnswers = allParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("answers["))
                .collect(java.util.stream.Collectors.groupingBy(
                        entry -> Long.parseLong(
                                entry.getKey().substring(
                                        entry.getKey().indexOf('[') + 1,
                                        entry.getKey().indexOf(']')
                                )
                        ),
                        java.util.stream.Collectors.mapping(
                                Map.Entry::getValue,
                                java.util.stream.Collectors.toList()
                        )
                ));

        int score = quizService.calculateScore(quizId, submittedAnswers);
        int totalQuestions = quizService.getQuestionsByQuiz(quizId).size();

        submissionService.saveSubmission(
                principal.getName(),
                quizId,
                score,
                totalQuestions
        );

        model.addAttribute("score", score);
        model.addAttribute("totalQuestions", totalQuestions);

        return "result";
    }
    @GetMapping("/history")
    public String studentHistory(Principal principal, Model model) {
        String email = principal.getName();
        model.addAttribute("submissions", submissionService.getSubmissionsByStudent(email));
        return "student-history";
    }
}