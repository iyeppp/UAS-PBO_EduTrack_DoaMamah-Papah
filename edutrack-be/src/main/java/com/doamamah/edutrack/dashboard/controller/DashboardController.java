package com.doamamah.edutrack.dashboard.controller;

import com.doamamah.edutrack.auth.repository.UserRepository;
import com.doamamah.edutrack.material.repository.CourseMaterialRepository;
import com.doamamah.edutrack.quiz.repository.QuizAttemptRepository;
import com.doamamah.edutrack.quiz.repository.QuizRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*") // Izinkan akses dari frontend JavaFX jika perlu HTTP CORS
public class DashboardController {

    private final UserRepository userRepository;
    private final CourseMaterialRepository materialRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;

    public DashboardController(UserRepository userRepository,
                               CourseMaterialRepository materialRepository,
                               QuizRepository quizRepository,
                               QuizAttemptRepository attemptRepository) {
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalMaterials = materialRepository.count();
        long totalQuizzes = quizRepository.count();
        long totalStudents = userRepository.countStudents();
        long totalTeachers = userRepository.countTeachers();
        
        // Data partisipasi kuis
        long distinctStudentsAttempted = attemptRepository.countDistinctStudentsAttempted();
        double participationRate = totalStudents > 0 ? ((double) distinctStudentsAttempted / totalStudents) * 100 : 0;
        
        // Rata-rata nilai kuis seluruh kelas
        Double avgScore = attemptRepository.getAverageScore();
        if (avgScore == null) avgScore = 0.0;
        
        long totalAttempts = attemptRepository.count();

        stats.put("totalMaterials", totalMaterials);
        stats.put("totalQuizzes", totalQuizzes);
        stats.put("totalStudents", totalStudents);
        stats.put("totalTeachers", totalTeachers);
        stats.put("participationRate", participationRate);
        stats.put("averageQuizScore", avgScore);
        stats.put("totalQuizAttempts", totalAttempts);

        return ResponseEntity.ok(stats);
    }
}
