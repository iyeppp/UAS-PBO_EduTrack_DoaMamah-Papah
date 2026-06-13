package com.doamamah.edutrack.quiz.model;

import com.doamamah.edutrack.auth.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity JPA untuk tabel 'quiz_attempts'.
 * Menyimpan hasil pengerjaan kuis oleh siswa, termasuk skor dan jawaban.
 *
 * <p>Setiap siswa hanya dapat memiliki satu percobaan per kuis.
 * Jika siswa mengulang kuis, data percobaan sebelumnya akan diperbarui.</p>
 *
 * <p>Field {@code answersJson} menyimpan daftar index jawaban siswa
 * dalam format JSON array string, contoh: {@code "[1,2,0,3]"}.</p>
 */
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User student;

    @Column(nullable = false)
    private int score;

    @Column(name = "answers_json", columnDefinition = "CLOB")
    private String answersJson;

    @Column(name = "attempt_date", nullable = false)
    private LocalDateTime attemptDate;

    public QuizAttempt() {
    }

    /**
     * Constructor untuk membuat percobaan kuis baru.
     *
     * @param quiz       kuis yang dikerjakan
     * @param student    siswa yang mengerjakan
     * @param score      skor yang diperoleh (0-100)
     * @param answersJson daftar jawaban dalam format JSON array
     * @param attemptDate waktu pengerjaan kuis
     */
    public QuizAttempt(Quiz quiz, User student, int score, String answersJson, LocalDateTime attemptDate) {
        this.quiz = quiz;
        this.student = student;
        this.score = score;
        this.answersJson = answersJson;
        this.attemptDate = attemptDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(LocalDateTime attemptDate) {
        this.attemptDate = attemptDate;
    }
}
