package com.doamamah.edutrack.quiz.model;

import com.doamamah.edutrack.auth.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "attempt_date", nullable = false)
    private LocalDateTime attemptDate;

    public QuizAttempt() {
    }

    public QuizAttempt(Quiz quiz, User student, int score, LocalDateTime attemptDate) {
        this.quiz = quiz;
        this.student = student;
        this.score = score;
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

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(LocalDateTime attemptDate) {
        this.attemptDate = attemptDate;
    }
}
