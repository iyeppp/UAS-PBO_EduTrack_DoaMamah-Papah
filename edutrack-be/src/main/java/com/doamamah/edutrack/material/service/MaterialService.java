package com.doamamah.edutrack.material.service;

import com.doamamah.edutrack.material.model.CourseMaterial;
import com.doamamah.edutrack.material.repository.CourseMaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer untuk operasi CRUD materi pembelajaran.
 */
@Service
public class MaterialService {

    private final CourseMaterialRepository materialRepository;

    public MaterialService(CourseMaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    /**
     * Mengambil semua materi dari database.
     */
    public List<CourseMaterial> getAllMaterials() {
        return materialRepository.findAll();
    }

    /**
     * Mengambil materi berdasarkan ID.
     */
    public Optional<CourseMaterial> getMaterialById(Long id) {
        return materialRepository.findById(id);
    }

    /**
     * Menambahkan materi baru ke database.
     */
    public CourseMaterial addMaterial(CourseMaterial material) {
        return materialRepository.save(material);
    }

    /**
     * Memperbarui materi yang sudah ada di database.
     *
     * @param id ID materi yang akan diupdate
     * @param updatedData data materi yang baru
     * @return CourseMaterial yang sudah diupdate
     * @throws RuntimeException jika materi tidak ditemukan
     */
    public CourseMaterial updateMaterial(Long id, CourseMaterial updatedData) {
        CourseMaterial existing = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materi dengan ID " + id + " tidak ditemukan."));

        existing.setTitle(updatedData.getTitle());
        existing.setDescription(updatedData.getDescription());
        existing.setType(updatedData.getType());
        existing.setVideoUrl(updatedData.getVideoUrl());
        existing.setDurationMinutes(updatedData.getDurationMinutes());
        existing.setTextContent(updatedData.getTextContent());

        return materialRepository.save(existing);
    }

    /**
     * Menghapus materi berdasarkan ID.
     *
     * @param id ID materi yang akan dihapus
     * @throws RuntimeException jika materi tidak ditemukan
     */
    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Materi dengan ID " + id + " tidak ditemukan.");
        }
        materialRepository.deleteById(id);
    }
}
