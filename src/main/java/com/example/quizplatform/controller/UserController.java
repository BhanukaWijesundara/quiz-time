package com.example.quizplatform.controller;

import com.example.quizplatform.entity.User;
import com.example.quizplatform.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/profile")
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public String userProfile(Authentication authentication, Model model) {
		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElse(null);
		model.addAttribute("user", user);
		return "admin-profile";
	}

	@PostMapping("/upload-picture")
	public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
									   Authentication authentication,
									   RedirectAttributes redirectAttributes) {
		try {
			if (file.isEmpty()) {
				redirectAttributes.addFlashAttribute("uploadError", "Please select an image.");
				return "redirect:/admin/profile";
			}

			String email = authentication.getName();
			User user = userRepository.findByEmail(email).orElse(null);

			if (user == null) {
				redirectAttributes.addFlashAttribute("uploadError", "User not found.");
				return "redirect:/admin/profile";
			}

			String uploadDir = System.getProperty("user.dir") + "/uploads/profile/";
			Files.createDirectories(Paths.get(uploadDir));

			String originalFilename = file.getOriginalFilename();
			String safeFileName = System.currentTimeMillis() + "_" + originalFilename;

			Path filePath = Paths.get(uploadDir, safeFileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			user.setProfilePicture(safeFileName);
			userRepository.save(user);

			return "redirect:/admin/profile";

		} catch (IOException e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadError", "Failed to upload picture.");
			return "redirect:/admin/profile";
		}
	}
}