package com.example.quizplatform.controller;

import com.example.quizplatform.entity.Department;
import com.example.quizplatform.entity.Question;
import com.example.quizplatform.entity.Quiz;
import com.example.quizplatform.entity.Submission;
import com.example.quizplatform.repository.DepartmentRepository;
import com.example.quizplatform.service.QuizService;
import com.example.quizplatform.service.SubmissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DepartmentRepository departmentRepository;
    private final QuizService quizService;
    private final SubmissionService submissionService;

    public AdminController(DepartmentRepository departmentRepository,
                           QuizService quizService,
                           SubmissionService submissionService) {
        this.departmentRepository = departmentRepository;
        this.quizService = quizService;
        this.submissionService = submissionService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/manage-quizzes")
    public String showManageQuizzes(@RequestParam(value = "search", required = false) String search,
                                    Model model) {
        List<Quiz> quizzes;

        if (search != null && !search.trim().isEmpty()) {
            quizzes = quizService.searchQuizzes(search.trim());
        } else {
            quizzes = quizService.getAllQuizzes();
        }

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("search", search);
        return "admin-manage-quizzes";
    }

    @GetMapping("/quiz/create")
    public String showCreateQuizPage(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "create-quiz";
    }

    @PostMapping("/quiz/create")
    public String createQuiz(@RequestParam("title") String title,
                             @RequestParam("duration") Integer duration,
                             @RequestParam("departmentId") Long departmentId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Quiz quiz = quizService.createQuiz(title, duration, department);
        return "redirect:/admin/quiz/" + quiz.getId() + "/add-question";
    }

    @GetMapping("/quiz/{quizId}/add-question")
    public String showAddQuestionPage(@PathVariable Long quizId,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        try {
            quizService.getQuizDuration(quizId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quiz not found or has been deleted.");
            return "redirect:/admin/manage-quizzes";
        }

        model.addAttribute("quizId", quizId);
        model.addAttribute("question", new Question());
        model.addAttribute("questions", quizService.getQuestionsByQuiz(quizId));
        return "add-question";
    }

    @PostMapping("/quiz/{quizId}/add-question")
    public String addQuestion(@PathVariable Long quizId,
                              @ModelAttribute Question question) {
        quizService.addQuestion(quizId, question);
        return "redirect:/admin/quiz/" + quizId + "/add-question";
    }

    @GetMapping("/quiz/{quizId}/edit-question/{questionId}")
    public String showEditQuestionPage(@PathVariable Long quizId,
                                       @PathVariable Long questionId,
                                       Model model) {
        Question question = quizService.getQuestionsByQuiz(quizId).stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found"));

        model.addAttribute("quizId", quizId);
        model.addAttribute("question", question);
        model.addAttribute("questions", quizService.getQuestionsByQuiz(quizId));
        model.addAttribute("editMode", true);

        return "add-question";
    }

    @PostMapping("/quiz/{quizId}/edit-question/{questionId}")
    public String updateQuestion(@PathVariable Long quizId,
                                 @PathVariable Long questionId,
                                 @ModelAttribute Question question) {
        quizService.updateQuestion(questionId, question);
        return "redirect:/admin/quiz/" + quizId + "/add-question";
    }

    @PostMapping("/quiz/{quizId}/delete-question/{questionId}")
    public String deleteQuestion(@PathVariable Long quizId,
                                 @PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return "redirect:/admin/quiz/" + quizId + "/add-question";
    }

    @GetMapping("/quiz/{quizId}/submissions")
    public String viewQuizSubmissions(@PathVariable Long quizId, Model model) {
        List<Submission> submissions = submissionService.getSubmissionsByQuiz(quizId);
        model.addAttribute("submissions", submissions);
        model.addAttribute("quizId", quizId);
        return "quiz-submissions";
    }

    @GetMapping("/departments")
    public String showDepartmentsPage(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin-departments";
    }

    @PostMapping("/departments/add")
    public String addDepartment(@RequestParam("name") String name,
                                RedirectAttributes redirectAttributes) {

        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department name cannot be empty.");
            return "redirect:/admin/departments";
        }

        String cleanName = name.trim().toUpperCase();

        if (departmentRepository.existsByNameIgnoreCase(cleanName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department already exists.");
            return "redirect:/admin/departments";
        }

        Department department = new Department();
        department.setName(cleanName);
        departmentRepository.save(department);

        redirectAttributes.addFlashAttribute("successMessage", "Department added successfully.");
        return "redirect:/admin/departments";
    }

    @GetMapping("/analytics")
    public String showAnalytics(Model model) {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        List<Double> avgScores = new ArrayList<>();
        List<Integer> attempts = new ArrayList<>();
        List<String> quizTitles = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            avgScores.add(submissionService.getAverageScoreForQuiz(quiz.getId()));
            attempts.add(submissionService.getAttemptCountForQuiz(quiz.getId()));
            quizTitles.add(quiz.getTitle());
        }

        List<Object[]> topStudents = submissionService.getTopStudents(5);

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("avgScores", avgScores);
        model.addAttribute("attempts", attempts);
        model.addAttribute("quizTitles", quizTitles);
        model.addAttribute("topStudents", topStudents);

        return "admin-analytics";
    }
}