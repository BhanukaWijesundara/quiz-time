package com.example.quizplatform.repository;

import com.example.quizplatform.entity.Department;
import com.example.quizplatform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByTitleContainingIgnoreCase(String title);
    List<Quiz> findByDepartment(Department department);
    List<Quiz> findByDepartmentAndTitleContainingIgnoreCase(Department department, String title);
}