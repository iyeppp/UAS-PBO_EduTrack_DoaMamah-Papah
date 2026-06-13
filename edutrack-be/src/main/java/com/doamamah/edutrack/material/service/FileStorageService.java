package com.doamamah.edutrack.material.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service untuk mengelola penyimpanan file attachment materi.
 *
 * <p>File yang diunggah disimpan di direktori {@code uploads/} di root project.
 * Nama file diganti dengan UUID unik untuk menghindari konflik penamaan.</p>
 *
 * @see com.doamamah.edutrack.material.controller.MaterialController#uploadFile
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Menyimpan file yang diunggah ke direktori penyimpanan.
     * Nama file asli diganti dengan UUID unik untuk menghindari konflik.
     *
     * @param file file yang diunggah melalui multipart request
     * @return nama file unik yang tersimpan (UUID + ekstensi asli)
     * @throws RuntimeException jika terjadi kesalahan saat menyimpan file
     */
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown_file");
        String fileExtension = "";
        
        int lastIndex = originalFileName.lastIndexOf('.');
        if (lastIndex > 0) {
            fileExtension = originalFileName.substring(lastIndex);
        }
        
        // Generate unique file name
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
