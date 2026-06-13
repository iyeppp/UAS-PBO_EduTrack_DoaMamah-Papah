package com.doamamah.edutrack.auth.controller;

import com.doamamah.edutrack.auth.model.User;
import com.doamamah.edutrack.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller untuk mengelola data user secara umum.
 * Endpoint ini terpisah dari auth, difokuskan pada operasi terkait manajemen user
 * seperti mengambil daftar siswa atau pengajar.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GET /api/users/students
     * Mendapatkan daftar semua pengguna dengan role Student.
     *
     * @return List user yang bertipe Student
     */
    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(userRepository.findAllStudents());
    }
}
