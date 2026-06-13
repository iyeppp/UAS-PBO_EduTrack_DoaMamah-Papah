package com.doamamah.edutrack.enrollment.service;

import com.doamamah.edutrack.enrollment.model.Enrollment;
import com.doamamah.edutrack.auth.model.Student;
import com.doamamah.edutrack.auth.model.Teacher;
import com.doamamah.edutrack.auth.model.User;
import com.doamamah.edutrack.enrollment.repository.EnrollmentRepository;
import com.doamamah.edutrack.auth.repository.UserRepository;
import com.doamamah.edutrack.exception.DuplicateResourceException;
import com.doamamah.edutrack.exception.InvalidInputException;
import com.doamamah.edutrack.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer untuk logika enrollment (siswa mendaftar ke kelas pengajar).
 */
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Mendaftarkan siswa ke kelas pengajar.
     */
    public Enrollment enrollStudent(Long studentId, Long teacherId) {
        // Cek apakah sudah terdaftar
        if (enrollmentRepository.findByStudentIdAndTeacherId(studentId, teacherId).isPresent()) {
            throw new DuplicateResourceException("Siswa sudah terdaftar di kelas pengajar ini.");
        }

        User studentUser = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Siswa", studentId));
        User teacherUser = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Pengajar", teacherId));

        if (!(studentUser instanceof Student)) {
            throw new InvalidInputException("User bukan siswa.");
        }
        if (!(teacherUser instanceof Teacher)) {
            throw new InvalidInputException("User bukan pengajar.");
        }

        Enrollment enrollment = new Enrollment((Student) studentUser, (Teacher) teacherUser);
        return enrollmentRepository.save(enrollment);
    }

    /**
     * Mengeluarkan siswa dari kelas pengajar.
     */
    @Transactional
    public void unenrollStudent(Long studentId, Long teacherId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndTeacherId(studentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment tidak ditemukan."));
        enrollmentRepository.delete(enrollment);
    }

    /**
     * Mengambil daftar pengajar yang diikuti oleh siswa.
     */
    public List<Teacher> getEnrolledTeachers(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(Enrollment::getTeacher)
                .collect(Collectors.toList());
    }

    /**
     * Mengambil daftar ID pengajar yang diikuti oleh siswa.
     */
    public List<Long> getEnrolledTeacherIds(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(e -> e.getTeacher().getId())
                .collect(Collectors.toList());
    }

    /**
     * Mengambil daftar siswa di kelas pengajar.
     */
    public List<Student> getEnrolledStudents(Long teacherId) {
        return enrollmentRepository.findByTeacherId(teacherId)
                .stream()
                .map(Enrollment::getStudent)
                .collect(Collectors.toList());
    }

    /**
     * Menghitung jumlah siswa di kelas pengajar.
     */
    public long countStudentsByTeacher(Long teacherId) {
        return enrollmentRepository.countByTeacherId(teacherId);
    }
}
