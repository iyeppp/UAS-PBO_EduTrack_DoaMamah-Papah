package com.doamamah.edutrack.material.model;

import com.doamamah.edutrack.auth.model.Student;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity JPA untuk tabel 'material_progress'.
 * Menyimpan catatan progres belajar siswa — menandai materi mana yang sudah dibaca/ditonton.
 *
 * <p>Setiap kombinasi {@code student_id} dan {@code material_id} bersifat unik,
 * sehingga satu materi hanya ditandai sekali per siswa.</p>
 */
@Entity
@Table(name = "material_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "material_id"})
})
public class MaterialProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private CourseMaterial material;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public MaterialProgress() {
    }

    /**
     * Constructor untuk membuat progress baru.
     * Waktu dilihat ({@code viewedAt}) diset otomatis ke waktu saat ini.
     *
     * @param student siswa yang membaca/menonton materi
     * @param material materi yang diakses
     */
    public MaterialProgress(Student student, CourseMaterial material) {
        this.student = student;
        this.material = material;
        this.viewedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public CourseMaterial getMaterial() {
        return material;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }
}
