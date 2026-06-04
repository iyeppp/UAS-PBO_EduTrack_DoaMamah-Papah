package com.doamamah.edutrack.material.repository;

import com.doamamah.edutrack.material.model.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository untuk akses data CourseMaterial dari database.
 */
@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
}
