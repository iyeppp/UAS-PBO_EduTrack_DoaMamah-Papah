package com.doamamah.edutrack.quiz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Entity JPA untuk tabel 'quiz_questions'.
 * Menyimpan pertanyaan pilihan ganda beserta jawaban yang benar.
 */
@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Teks pertanyaan tidak boleh kosong")
    @Column(name = "question_text", nullable = false, columnDefinition = "CLOB")
    private String questionText;

    @NotBlank(message = "Opsi A tidak boleh kosong")
    @Column(name = "option_a", nullable = false)
    private String optionA;

    @NotBlank(message = "Opsi B tidak boleh kosong")
    @Column(name = "option_b", nullable = false)
    private String optionB;

    @NotBlank(message = "Opsi C tidak boleh kosong")
    @Column(name = "option_c", nullable = false)
    private String optionC;

    @NotBlank(message = "Opsi D tidak boleh kosong")
    @Column(name = "option_d", nullable = false)
    private String optionD;

    /**
     * Index jawaban yang benar (0 = A, 1 = B, 2 = C, 3 = D).
     */
    @Min(value = 0, message = "Index jawaban benar minimal 0")
    @Max(value = 3, message = "Index jawaban benar maksimal 3")
    @Column(name = "correct_option_index", nullable = false)
    private int correctOptionIndex;

    /**
     * Relasi Many-to-One ke Quiz.
     * JsonIgnore agar tidak terjadi infinite recursion saat serialisasi JSON.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    public QuizQuestion() {}

    public QuizQuestion(String questionText, String optionA, String optionB,
                        String optionC, String optionD, int correctOptionIndex) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOptionIndex = correctOptionIndex;
    }

    // --- GETTERS ---
    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public Quiz getQuiz() { return quiz; }

    // --- SETTERS ---
    public void setId(Long id) { this.id = id; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
