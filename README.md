# EduTrack (Tugas PBO LAB UAS)

Proyek ini mengusung tema Edukasi dan dibangun menggunakan arsitektur pemisahan *Frontend* dan *Backend*:
1. **edutrack-be**: Backend menggunakan Spring Boot REST API
2. **edutrack-fe**: Frontend menggunakan aplikasi GUI JavaFX

## Prasyarat
Sebelum menjalankan proyek, pastikan Anda sudah menginstal:
- **Java JDK 21**
- **Apache Maven** (Untuk menjalankan Frontend)
- **Git**

## Cara Menjalankan Proyek

### 1. Clone Repository
```bash
git clone https://github.com/iyeppp/edutrack.git
cd edutrack
```

### 2. Menjalankan Backend (Spring Boot)
Buka terminal dan masuk ke folder `edutrack-be`, kemudian jalankan perintah *Maven Wrapper*:
```bash
cd edutrack-be

# Untuk pengguna Windows
.\mvnw.cmd spring-boot:run

# Untuk pengguna Mac/Linux
./mvnw spring-boot:run
```

### 3. Menjalankan Frontend (JavaFX)
Buka terminal *baru* (agar terminal backend tetap berjalan), masuk ke folder `edutrack-fe`, dan jalankan perintah:
```bash
cd edutrack-fe
mvn clean javafx:run
```
Jika berhasil, jendela aplikasi GUI EduTrack akan muncul dengan tulisan "Aplikasi EduTrack" dan tombol "Mulai".
