-- =====================================================================
-- EduTrack - Data Awal (Seed Data)
-- File ini dieksekusi otomatis oleh Spring Boot saat aplikasi pertama kali dijalankan.
-- Menggunakan MERGE INTO agar data tidak duplikat jika aplikasi di-restart.
-- =====================================================================

-- ===== SEED USERS =====
-- Password: 123 (plain text, sesuai kebutuhan tugas)

MERGE INTO users (id, role, username, password, full_name, email, student_id, enrolled_courses, teacher_id, specialization, total_courses)
KEY(username) VALUES
(1, 'STUDENT', 'siswa', '123', 'Budi Santoso', 'budi@demo.com', 'STD001', 4, NULL, NULL, 0);

MERGE INTO users (id, role, username, password, full_name, email, student_id, enrolled_courses, teacher_id, specialization, total_courses)
KEY(username) VALUES
(2, 'TEACHER', 'guru', '123', 'Ibu Sari Dewi', 'sari@demo.com', NULL, 0, 'TCH001', 'Pemrograman Java', 4);

-- ===== SEED COURSE MATERIALS =====

MERGE INTO course_materials (id, title, description, type, video_url, duration_minutes, text_content)
KEY(id) VALUES
(1, 'Pengantar Pemrograman Berorientasi Objek',
 'Pengantar konsep-konsep dasar OOP: kelas, objek, enkapsulasi, dan lebih banyak lagi.',
 'VIDEO', 'https://www.youtube.com/watch?v=grEKMHGYyns', 45, NULL);

MERGE INTO course_materials (id, title, description, type, video_url, duration_minutes, text_content)
KEY(id) VALUES
(2, 'Konsep Inheritance dalam Java',
 'Penjelasan lengkap tentang pewarisan (inheritance) di Java dengan contoh nyata.',
 'TEXT', NULL, 0,
 'Inheritance (Pewarisan) adalah salah satu pilar utama OOP.

Dalam Java, kelas dapat mewarisi properti dan metode dari kelas lain menggunakan kata kunci ''extends''.

Contoh:
public class Hewan {
    private String nama;
    public void bersuara() { System.out.println("..."); }
}

public class Anjing extends Hewan {
    @Override
    public void bersuara() { System.out.println("Guk!"); }
}

Dengan inheritance, kelas Anjing mewarisi semua anggota dari Hewan dan dapat meng-override perilaku yang perlu diubah.

Keuntungan Inheritance:
1. Reusability - kode dapat digunakan kembali
2. Extendability - mudah dikembangkan
3. Polymorphism - mendukung polimorfisme');

MERGE INTO course_materials (id, title, description, type, video_url, duration_minutes, text_content)
KEY(id) VALUES
(3, 'Polymorphism dan Dynamic Dispatch',
 'Memahami polimorfisme di Java melalui method overriding dan dynamic dispatch.',
 'VIDEO', 'https://www.youtube.com/watch?v=U8yjyEs40gI', 60, NULL);

MERGE INTO course_materials (id, title, description, type, video_url, duration_minutes, text_content)
KEY(id) VALUES
(4, 'Abstraksi dengan Abstract Class & Interface',
 'Pelajari cara menggunakan abstract class dan interface untuk menerapkan abstraksi.',
 'TEXT', NULL, 0,
 'Abstraksi adalah konsep menyembunyikan detail implementasi dan hanya menampilkan fungsionalitas yang relevan kepada pengguna.

Di Java, abstraksi dapat dicapai dengan:
1. Abstract Class
2. Interface

Abstract Class:
- Tidak dapat diinstansiasi langsung
- Dapat memiliki metode abstrak dan non-abstrak
- Gunakan kata kunci ''abstract''

Interface:
- Semua metode secara default adalah abstrak (sebelum Java 8)
- Dapat memiliki default methods (Java 8+)
- Satu kelas dapat mengimplementasikan banyak interface');
