package com.doamamah.edutrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception yang dilemparkan ketika resource yang diminta tidak ditemukan di database.
 *
 * <p>Contoh penggunaan:</p>
 * <ul>
 *   <li>User dengan ID tertentu tidak ada</li>
 *   <li>Materi atau kuis yang dicari tidak ditemukan</li>
 *   <li>Enrollment tidak ditemukan</li>
 * </ul>
 *
 * <p>Secara otomatis menghasilkan HTTP 404 Not Found.</p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " dengan ID " + id + " tidak ditemukan.");
    }
}
