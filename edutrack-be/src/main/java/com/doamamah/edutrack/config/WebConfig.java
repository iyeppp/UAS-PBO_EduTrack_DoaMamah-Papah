package com.doamamah.edutrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Konfigurasi Web MVC Spring Boot.
 * <p>Kelas ini mengonfigurasi resource handler statis agar file yang diunggah
 * (seperti attachment materi) dapat diakses langsung via URL HTTP.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Mendaftarkan resource handler untuk direktori 'uploads'.
     * <p>Mapping URL {@code /uploads/**} ke direktori fisik lokal {@code uploads/}
     * di root aplikasi.</p>
     *
     * @param registry objek ResourceHandlerRegistry untuk mendaftarkan handler
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the 'uploads' directory to the web so frontend can download files
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}
