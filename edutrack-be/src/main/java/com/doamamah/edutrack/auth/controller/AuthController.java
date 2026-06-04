package com.doamamah.edutrack.auth.controller;

import com.doamamah.edutrack.auth.model.Student;
import com.doamamah.edutrack.auth.model.Teacher;
import com.doamamah.edutrack.auth.model.User;
import com.doamamah.edutrack.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller untuk endpoint autentikasi.
 * Menyediakan /api/auth/login dan /api/auth/logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     * Body: { "username": "...", "password": "..." }
     * Response 200: User JSON sesuai format yang diharapkan frontend.
     * Response 401: Jika kredensial salah.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username dan password wajib diisi."));
        }

        try {
            User user = authService.login(username, password);

            // Bangun response JSON sesuai format yang diharapkan frontend
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            // Tambahkan field spesifik berdasarkan tipe user
            if (user instanceof Teacher teacher) {
                response.put("teacherId", teacher.getTeacherId());
                response.put("specialization", teacher.getSpecialization());
                response.put("totalCourses", teacher.getTotalCourses());
            } else if (user instanceof Student student) {
                response.put("studentId", student.getStudentId());
                response.put("enrolledCourses", student.getEnrolledCourses());
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/logout
     * Endpoint sederhana — mengembalikan 200 OK.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout berhasil."));
    }
}
