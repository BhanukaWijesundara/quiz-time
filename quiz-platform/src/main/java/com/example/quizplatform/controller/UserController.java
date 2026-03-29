package com.example.quizplatform.controller;

import com.example.quizplatform.entity.User;
import com.example.quizplatform.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/profile")
	public String userProfile(Authentication authentication, Model model) {
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElse(null);
		model.addAttribute("user", user);
		return "user-profile";
	}
}
