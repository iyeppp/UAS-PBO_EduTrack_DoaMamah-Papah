package com.doamamah.edutrack.quiz.controller;

import com.doamamah.edutrack.quiz.model.Quiz;
import com.doamamah.edutrack.quiz.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller untuk operasi CRUD kuis.
 * Menyediakan endpoint /api/quizzes.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * GET /api/quizzes
     * Mengambil semua kuis dari database.
     */
    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    /**
     * GET /api/quizzes/{id}
     * Mengambil kuis berdasarkan ID beserta pertanyaannya.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id)
                .map(quiz -> ResponseEntity.ok((Object) quiz))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Kuis dengan ID " + id + " tidak ditemukan.")));
    }

    /**
     * POST /api/quizzes
     * Membuat kuis baru beserta pertanyaannya.
     */
    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        Quiz saved = quizService.createQuiz(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT /api/quizzes/{id}
     * Memperbarui kuis yang sudah ada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @RequestBody Quiz quiz) {
        try {
            Quiz updated = quizService.updateQuiz(id, quiz);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/quizzes/{id}
     * Menghapus kuis berdasarkan ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return ResponseEntity.ok(Map.of("message", "Kuis berhasil dihapus."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/quizzes/{id}/submit
     * Menyimpan hasil pengerjaan kuis siswa.
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitQuizAttempt(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Long studentId = Long.valueOf(payload.get("studentId").toString());
            int score = Integer.parseInt(payload.get("score").toString());
            com.doamamah.edutrack.quiz.model.QuizAttempt attempt = quizService.submitAttempt(id, studentId, score);
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/quizzes/attempts
     * Mendapatkan semua riwayat kuis siswa.
     */
    @GetMapping("/attempts")
    public ResponseEntity<?> getAllAttempts() {
        return ResponseEntity.ok(mapAttempts(quizService.getAllAttempts()));
    }

    /**
     * GET /api/quizzes/{id}/attempts
     * Mendapatkan riwayat siswa untuk kuis tertentu.
     */
    @GetMapping("/{id}/attempts")
    public ResponseEntity<?> getAttemptsByQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(mapAttempts(quizService.getAttemptsByQuiz(id)));
    }

    /**
     * GET /api/quizzes/student/{studentId}/attempts
     * Mendapatkan riwayat kuis untuk siswa tertentu.
     */
    @GetMapping("/student/{studentId}/attempts")
    public ResponseEntity<?> getAttemptsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(mapAttempts(quizService.getAttemptsByStudent(studentId)));
    }

    private List<Map<String, Object>> mapAttempts(List<com.doamamah.edutrack.quiz.model.QuizAttempt> attempts) {
        return attempts.stream().map(attempt -> Map.of(
            "id", attempt.getId(),
            "score", attempt.getScore(),
            "attemptDate", attempt.getAttemptDate().toString(),
            "quiz", Map.of("id", attempt.getQuiz().getId(), "title", attempt.getQuiz().getTitle()),
            "student", Map.of("id", attempt.getStudent().getId(), "fullName", attempt.getStudent().getFullName())
        )).toList();
    }
}
