package com.doamamah.edutrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception yang dilemparkan ketika terjadi duplikasi resource.
 *
 * <p>Contoh penggunaan:</p>
 * <ul>
 *   <li>Username sudah terdaftar saat registrasi</li>
 *   <li>Siswa sudah terdaftar di kelas pengajar yang sama</li>
 * </ul>
 *
 * <p>Secara otomatis menghasilkan HTTP 409 Conflict.</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
