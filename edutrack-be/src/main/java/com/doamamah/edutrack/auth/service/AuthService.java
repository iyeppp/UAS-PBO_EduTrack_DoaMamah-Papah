package com.doamamah.edutrack.auth.service;

import com.doamamah.edutrack.auth.model.User;
import com.doamamah.edutrack.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer untuk logika autentikasi pengguna.
 * Mencari user di database H2 dan memvalidasi password.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Memproses login berdasarkan username dan password.
     *
     * @param username nama pengguna
     * @param password kata sandi (plain text)
     * @return User jika kredensial valid
     * @throws RuntimeException jika username tidak ditemukan atau password salah
     */
    public User login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Username tidak ditemukan.");
        }

        User user = optionalUser.get();

        // Perbandingan password plain text (sesuai kebutuhan tugas)
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Password salah.");
        }

        return user;
    }
}
