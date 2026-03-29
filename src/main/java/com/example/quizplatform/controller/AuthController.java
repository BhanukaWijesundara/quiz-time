package com.example.quizplatform.controller;

import com.example.quizplatform.dto.RegisterRequest;
import com.example.quizplatform.repository.DepartmentRepository;
import com.example.quizplatform.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;
    private final DepartmentRepository departmentRepository;

    public AuthController(AuthService authService, DepartmentRepository departmentRepository) {
        this.authService = authService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        model.addAttribute("departments", departmentRepository.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterRequest request) {
        authService.registerUser(request);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}