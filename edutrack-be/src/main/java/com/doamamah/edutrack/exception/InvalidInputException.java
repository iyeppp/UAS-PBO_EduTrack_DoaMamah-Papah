package com.doamamah.edutrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception yang dilemparkan ketika input dari pengguna tidak valid.
 *
 * <p>Contoh penggunaan:</p>
 * <ul>
 *   <li>Field wajib tidak diisi (username, password, dll.)</li>
 *   <li>Kredensial login salah</li>
 *   <li>Tipe user tidak sesuai (bukan siswa/pengajar)</li>
 * </ul>
 *
 * <p>Secara otomatis menghasilkan HTTP 400 Bad Request.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
