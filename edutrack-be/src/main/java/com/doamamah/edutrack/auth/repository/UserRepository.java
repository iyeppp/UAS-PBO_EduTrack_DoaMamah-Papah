package com.doamamah.edutrack.auth.repository;

import com.doamamah.edutrack.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository untuk akses data User (Student/Teacher) dari database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Mencari user berdasarkan username.
     * Digunakan saat proses login.
     */
    Optional<User> findByUsername(String username);
}
