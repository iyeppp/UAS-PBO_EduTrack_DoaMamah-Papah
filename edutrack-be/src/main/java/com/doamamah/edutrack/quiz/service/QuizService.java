package com.doamamah.edutrack.quiz.service;

import com.doamamah.edutrack.auth.model.Teacher;
import com.doamamah.edutrack.auth.model.User;
import com.doamamah.edutrack.auth.repository.UserRepository;
import com.doamamah.edutrack.quiz.model.Quiz;
import com.doamamah.edutrack.quiz.model.QuizAttempt;
import com.doamamah.edutrack.quiz.model.QuizQuestion;
import com.doamamah.edutrack.quiz.repository.QuizAttemptRepository;
import com.doamamah.edutrack.quiz.repository.QuizRepository;
import com.doamamah.edutrack.exception.InvalidInputException;
import com.doamamah.edutrack.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer untuk operasi CRUD kuis.
 */
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository, 
                       QuizAttemptRepository attemptRepository,
                       UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    /**
     * Mengambil semua kuis dari database.
     */
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    /**
     * Mengambil kuis berdasarkan ID.
     */
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    /**
     * Membuat kuis baru beserta pertanyaannya.
     */
    public Quiz createQuiz(Quiz quiz) {
        // Pastikan relasi bidirectional terjaga
        if (quiz.getQuestions() != null) {
            for (QuizQuestion q : quiz.getQuestions()) {
                q.setQuiz(quiz);
            }
        }
        return quizRepository.save(quiz);
    }

    /**
     * Membuat kuis baru dengan ownership pengajar.
     */
    public Quiz createQuiz(Quiz quiz, Long teacherId) {
        if (quiz.getQuestions() != null) {
            for (QuizQuestion q : quiz.getQuestions()) {
                q.setQuiz(quiz);
            }
        }
        if (teacherId != null) {
            User user = userRepository.findById(teacherId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pengajar", teacherId));
            if (!(user instanceof Teacher teacher)) {
                throw new InvalidInputException("User dengan ID " + teacherId + " bukan pengajar.");
            }
            quiz.setTeacher(teacher);
        }
        return quizRepository.save(quiz);
    }

    /**
     * Mengambil kuis berdasarkan daftar teacher IDs (untuk siswa).
     */
    public List<Quiz> getQuizzesByTeacherIds(List<Long> teacherIds) {
        if (teacherIds == null || teacherIds.isEmpty()) return List.of();
        return quizRepository.findByTeacherIdIn(teacherIds);
    }

    /**
     * Mengambil kuis berdasarkan teacher ID (untuk pengajar).
     */
    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        return quizRepository.findByTeacherId(teacherId);
    }

    /**
     * Mengupdate kuis yang sudah ada.
     */
    public Quiz updateQuiz(Long id, Quiz updatedData) {
        Quiz existing = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kuis", id));

        existing.setTitle(updatedData.getTitle());
        existing.setDescription(updatedData.getDescription());
        existing.setDifficulty(updatedData.getDifficulty());

        // Hapus pertanyaan lama dan tambahkan yang baru
        existing.getQuestions().clear();
        if (updatedData.getQuestions() != null) {
            for (QuizQuestion q : updatedData.getQuestions()) {
                q.setQuiz(existing);
                existing.getQuestions().add(q);
            }
        }

        return quizRepository.save(existing);
    }

    private String serializeAnswerList(List<Integer> answers) {
        if (answers == null) {
            return null;
        }
        return answers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Menghapus kuis berdasarkan ID.
     */
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kuis", id);
        }
        
        // Hapus semua riwayat pengerjaan yang merujuk ke kuis ini (menghindari Foreign Key violation)
        List<QuizAttempt> attempts = attemptRepository.findByQuizIdOrderByAttemptDateDesc(id);
        if (!attempts.isEmpty()) {
            attemptRepository.deleteAll(attempts);
        }

        quizRepository.deleteById(id);
    }

    /**
     * Menyimpan skor kuis siswa.
     */
    public QuizAttempt submitAttempt(Long quizId, Long studentId, int score, List<Integer> answers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Kuis", quizId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Siswa", studentId));

        String answersJson = serializeAnswerList(answers);
        Optional<QuizAttempt> existingAttempt = attemptRepository.findByQuizIdAndStudentId(quizId, studentId);

        if (existingAttempt.isPresent()) {
            QuizAttempt attempt = existingAttempt.get();
            attempt.setScore(score);
            attempt.setAnswersJson(answersJson);
            attempt.setAttemptDate(LocalDateTime.now());
            return attemptRepository.save(attempt);
        }

        QuizAttempt attempt = new QuizAttempt(
                quiz, student, score, answersJson, LocalDateTime.now());
        return attemptRepository.save(attempt);
    }

    public QuizAttempt submitAttempt(Long quizId, Long studentId, int score) {
        return submitAttempt(quizId, studentId, score, null);
    }

    public Optional<QuizAttempt> getAttemptByQuizAndStudent(Long quizId, Long studentId) {
        return attemptRepository.findByQuizIdAndStudentId(quizId, studentId);
    }

    /**
     * Mengambil riwayat skor kuis berdasarkan kuis (untuk guru).
     */
    public List<QuizAttempt> getAttemptsByQuiz(Long quizId) {
        return attemptRepository.findByQuizIdOrderByAttemptDateDesc(quizId);
    }

    /**
     * Mengambil semua riwayat skor (untuk guru).
     */
    public List<QuizAttempt> getAllAttempts() {
        return attemptRepository.findAllByOrderByAttemptDateDesc();
    }

    /**
     * Mengambil riwayat skor kuis berdasarkan ID siswa.
     */
    public List<QuizAttempt> getAttemptsByStudent(Long studentId) {
        return attemptRepository.findByStudentId(studentId);
    }
}
