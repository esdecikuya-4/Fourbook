# Panduan Instalasi Fourbook Flat-File CMS di cPanel Hosting

**Fourbook Flat-File CMS** adalah sistem media sosial dan ruang kolaborasi sekolah resmi **SDN 4 Putrajawa** berbasis PHP & Flat-File JSON. Aplikasi ini **tidak membutuhkan database MySQL / SQL Server**, sehingga sangat ringan, cepat, dan siap dijalankan di paket hosting cPanel manapun (PHP 7.4 / 8.0 / 8.1 / 8.2 / 8.3).

---

## 📁 Struktur Berkas

```text
public_html/ (atau subdomain Anda)
├── api/
│   ├── helper.php            # Fungsi pembantu JSON & Session
│   ├── auth.php              # Login, register, ganti akun, edit profil
│   ├── posts.php             # Postingan, upload foto, Like/Reaksi, Komentar
│   ├── friends.php           # Sistem Tambah Teman & Konfirmasi Pertemanan
│   ├── notifications.php     # Notifikasi Realtime & Penanda Dibaca
│   ├── messages.php          # Chat / Messenger Siswa & Guru
│   ├── quizzes.php           # Kuis & Nilai Siswa
│   └── journal.php           # Jurnal Guru & Rekap Kehadiran
├── data/
│   ├── .htaccess             # Keamanan (Melindungi data JSON agar tidak bisa diakses publik)
│   ├── users.json            # Akun Guru & Siswa
│   ├── posts.json            # Data Postingan & Komentar
│   ├── friendships.json      # Hubungan Pertemanan
│   ├── notifications.json    # Notifikasi
│   ├── messages.json         # Obrolan Chat
│   ├── quizzes.json          # Kuis
│   ├── quiz_submissions.json # Nilai Ulangan Siswa
│   └── journals.json         # Jurnal Harian Guru
├── uploads/                  # Folder penyimpanan foto & berkas postingan
├── css/
│   └── style.css             # Tema Facebook Modern Fourbook Blue (#1877F2)
├── js/
│   └── app.js                # Logika Aplikasi Interaktif Single-Page (SPA)
├── index.php                 # Halaman Utama Fourbook
└── README.md
```

---

## 🚀 Langkah-Langkah Upload ke cPanel

1. **Kompresi Folder**:
   - Buka folder `fourbook-web`.
   - Pilih semua berkas di dalamnya, lalu kompres menjadi file `fourbook-web.zip`.

2. **Login ke cPanel Hosting**:
   - Buka URL cPanel Anda (contoh: `https://namadomainanda.com:2083`).
   - Masuk ke menu **File Manager** (Pengelola Berkas).

3. **Unggah Berkas**:
   - Masuk ke folder `public_html` (atau folder subdomain Anda, misal `public_html/fourbook`).
   - Klik tombol **Upload**, lalu pilih file `fourbook-web.zip`.

4. **Ekstrak Berkas**:
   - Klik kanan pada `fourbook-web.zip` di File Manager, pilih **Extract** (Ekstrak).
   - Pastikan berkas seperti `index.php`, folder `api/`, `data/`, `css/`, dan `js/` berada di lokasi root domain/subdomain yang diinginkan.

5. **Set Izin Akses Folder (Permission)**:
   - Pastikan folder `data/` dan `uploads/` memiliki permission **755** (atau **775** jika diperlukan oleh konfigurasi server web hosting Anda) agar PHP dapat membaca dan menulis data JSON serta file upload foto.

6. **Selesai & Siap Digunakan!**:
   - Buka website Anda di browser (contoh: `https://namadomainanda.com` atau `https://fourbook.namadomainanda.com`).
   - Anda dapat langsung mencoba beralih akun antara **Pak Teten Kurniawan (Wali Kelas / Admin)**, **Dimas (Ketua Kelas)**, **Siti**, dan siswa lainnya melalui tombol **Ganti Akun** di kanan atas.

---

## 💡 Fitur-Fitur yang Tersedia

1. **Beranda & Postingan**:
   - Membagikan status teks, karya foto (upload gambar), dan kategori (Akademik, Kesenian, Olahraga, Pengumuman).
   - Tombol Suka/Like dan kolom komentar interaktif.
2. **Tambah Teman & Direktori Siswa**:
   - Melihat direktori seluruh warga sekolah SDN 4 Putrajawa.
   - Mengirim permintaan pertemanan, menerima konfirmasi, atau membatalkan pertemanan.
   - Filter *Semua Warga*, *Teman Saya*, dan *Permintaan Masuk*.
3. **Pesan Pribadi (Messenger)**:
   - Obrolan chat interaktif antara siswa dan guru.
4. **Notifikasi Real-time**:
   - Notifikasi otomatis untuk postingan baru dari guru, like, komentar baru, pesan masuk, dan permintaan pertemanan.
5. **Kuis & Ulangan Harian**:
   - Wali Kelas dapat membuat soal kuis pilihan ganda.
   - Siswa dapat mengerjakan langsung di web dan mendapatkan nilai otomatis.
6. **Jurnal Pembelajaran Guru**:
   - Pencatatan materi harian guru dan rekapitulasi kehadiran siswa.
