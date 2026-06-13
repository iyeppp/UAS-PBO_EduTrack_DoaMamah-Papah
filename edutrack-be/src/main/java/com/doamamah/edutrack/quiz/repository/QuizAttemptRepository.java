package com.doamamah.edutrack.quiz.repository;

import com.doamamah.edutrack.quiz.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository untuk akses data QuizAttempt (percobaan kuis) dari database.
 * Menyediakan query untuk mengambil riwayat pengerjaan kuis berdasarkan kuis, siswa,
 * serta statistik rata-rata skor dan partisipasi.
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    // Untuk mengecek riwayat nilai berdasarkan kuis (untuk guru melihat hasil)
    List<QuizAttempt> findByQuizIdOrderByAttemptDateDesc(Long quizId);
    
    // Semua riwayat nilai (bisa digabung/dilihat guru)
    List<QuizAttempt> findAllByOrderByAttemptDateDesc();

    // Rata-rata nilai (seluruh percobaan)
    @Query("SELECT AVG(q.score) FROM QuizAttempt q")
    Double getAverageScore();

    // Hitung jumlah siswa berbeda yang pernah mencoba kuis (untuk partisipasi)
    @Query("SELECT COUNT(DISTINCT q.student.id) FROM QuizAttempt q")
    Long countDistinctStudentsAttempted();

    // Mengambil riwayat nilai kuis berdasarkan ID siswa
    List<QuizAttempt> findByStudentId(Long studentId);

    // Mengambil riwayat nilai kuis tertentu untuk siswa tertentu
    Optional<QuizAttempt> findByQuizIdAndStudentId(Long quizId, Long studentId);
}
