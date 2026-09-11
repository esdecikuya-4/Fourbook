<?php
// fourbook-web/index.php
session_start();
?>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Fourbook - Media Sosial SDN 4 Putrajawa</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
  <link rel="stylesheet" href="css/style.css?v=20260910c">
</head>
<body>

  <!-- ================= AUTH SCREEN (LOGIN & REGISTER) ================= -->
  <div id="auth-view-screen" class="fb-auth-overlay" style="display: none;">
    <div class="fb-auth-card">
      <div class="auth-header">
        <div class="auth-logo-badge">4</div>
        <h1 class="auth-title">fourbook</h1>
        <div class="auth-school-subtitle">SDN 4 Putrajawa</div>
        <p class="auth-tagline">Sdn 4 Putrajawa Online Community</p>

        <div class="auth-tabs-toggle">
          <button type="button" class="auth-tab-btn active" id="auth-tab-login-btn" onclick="toggleAuthTab('login')">
            <i class="fa-solid fa-right-to-bracket"></i> Masuk
          </button>
          <button type="button" class="auth-tab-btn" id="auth-tab-register-btn" onclick="toggleAuthTab('register')">
            <i class="fa-solid fa-user-plus"></i> Daftar Akun
          </button>
        </div>
      </div>

      <!-- FORM MASUK (LOGIN) -->
      <div id="auth-form-login" class="auth-form-body">
        <form onsubmit="handleAuthLogin(event)" style="display: flex; flex-direction: column; gap: 10px;">
          <div class="form-group">
            <label class="form-label">Username</label>
            <input type="text" id="login-username" class="form-input" placeholder="Masukkan username (contoh: admin)" required>
          </div>
          <div class="form-group">
            <label class="form-label">Kata Sandi</label>
            <input type="password" id="login-password" class="form-input" placeholder="Masukkan kata sandi..." required>
          </div>
          <button type="submit" class="btn-fb-primary" style="height: 42px; width: 100%; margin-top: 4px; font-size: 14px;">
            <i class="fa-solid fa-arrow-right-to-bracket"></i> Masuk ke Fourbook
          </button>
        </form>
      </div>

      <!-- FORM DAFTAR (REGISTER) -->
      <div id="auth-form-register" class="auth-form-body" style="display: none;">
        <form onsubmit="handleAuthRegister(event)" style="display: flex; flex-direction: column; gap: 10px;">
          <div class="form-group">
            <label class="form-label">Nama Lengkap</label>
            <input type="text" id="reg-fullname" class="form-input" placeholder="Contoh: Ahmad Fauzi" required>
          </div>
          <div class="form-group">
            <label class="form-label">Status / Peran</label>
            <select id="reg-role" class="form-select" onchange="handleRoleChange(this.value)">
              <option value="MURID">Siswa</option>
              <option value="WALI_KELAS">Guru</option>
            </select>
          </div>
          <div class="form-group" id="reg-class-group">
            <label class="form-label">Pilih Kelas (1 - 6)</label>
            <select id="reg-class" class="form-select">
              <option value="Kelas 1">Kelas 1</option>
              <option value="Kelas 2">Kelas 2</option>
              <option value="Kelas 3">Kelas 3</option>
              <option value="Kelas 4">Kelas 4</option>
              <option value="Kelas 5">Kelas 5</option>
              <option value="Kelas 6">Kelas 6</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">Username</label>
            <input type="text" id="reg-username" class="form-input" placeholder="Contoh: ahmad" required>
          </div>
          <div class="form-group">
            <label class="form-label">Kata Sandi</label>
            <input type="password" id="reg-password" class="form-input" placeholder="Buat kata sandi akun..." required>
          </div>
          <div class="form-group">
            <label class="form-label">Bio / Minat Singkat</label>
            <input type="text" id="reg-bio" class="form-input" placeholder="Contoh: Suka menggambar & matematika">
          </div>
          <button type="submit" class="btn-fb-primary" style="height: 42px; width: 100%; margin-top: 4px; font-size: 14px;">
            <i class="fa-solid fa-user-check"></i> Buat Akun Baru
          </button>
        </form>
      </div>

      <!-- ATTRIBUTION & SCHOOL BADGE FOOTER -->
      <div style="margin-top: 14px; padding: 10px 14px; background: rgba(241, 245, 249, 0.9); border-radius: 12px; text-align: center; border: 1px solid #CBD5E1; box-shadow: 0 2px 6px rgba(0,0,0,0.04);">
        <div style="font-size: 13px; font-weight: 800; color: #1E293B;">
          Created By : <span style="color: #1D4ED8;">Teten Kurniawan</span>
        </div>
        <div style="font-size: 11.5px; font-weight: 700; color: #15803D; margin-top: 3px;">
          <i class="fa-solid fa-school"></i> SDN 4 PUTRAJAWA &bull; Garut, Indonesia
        </div>
      </div>

      <!-- DOWNLOAD FOURBOOK ANDROID APK MENU (MENU PALING BAWAH) -->
      <a href="fourbook.apk" download="fourbook.apk" style="text-decoration: none; display: block; margin-top: 12px;">
        <div style="background: linear-gradient(135deg, #059669 0%, #10B981 100%); border-radius: 12px; padding: 10px 14px; display: flex; align-items: center; justify-content: space-between; border: 1px solid #34D399; box-shadow: 0 4px 14px rgba(16,185,129,0.25); cursor: pointer; transition: transform 0.15s ease;" onmouseover="this.style.transform='translateY(-2px)'" onmouseout="this.style.transform='translateY(0)'">
          <div style="display: flex; align-items: center; gap: 10px;">
            <div style="width: 38px; height: 38px; border-radius: 50%; background: #047857; display: flex; align-items: center; justify-content: center; color: #ffffff; font-size: 20px; border: 1.5px solid #6EE7B7; flex-shrink: 0;">
              <i class="fa-brands fa-android"></i>
            </div>
            <div style="text-align: left;">
              <div style="color: #ffffff; font-size: 13.5px; font-weight: 800; letter-spacing: -0.2px;">Download Fourbook Android</div>
              <div style="color: #D1FAE5; font-size: 11px; font-weight: 600;">Klik untuk unduh langsung file APK HP</div>
            </div>
          </div>
          <div style="background: #ffffff; color: #047857; padding: 6px 12px; border-radius: 8px; font-size: 12px; font-weight: 800; display: flex; align-items: center; gap: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); flex-shrink: 0;">
            <i class="fa-solid fa-download"></i> Unduh APK
          </div>
        </div>
      </a>
    </div>
  </div>

  <!-- ================= MAIN APPLICATION SCREEN ================= -->
  <div id="app-main-screen" style="display: none;">
    <!-- TOP HEADER -->
    <header class="fb-header">
      <div class="fb-brand" onclick="switchTab('beranda')">
        <div class="fb-logo-circle">4</div>
        <div class="fb-brand-title">
          <span class="fb-brand-name">Fourbook</span>
          <span class="fb-school-tag">SDN 4 PUTRAJAWA ONLINE COMMUNITY</span>
        </div>
      </div>

      <div class="fb-header-actions">
        <button class="fb-icon-btn" onclick="switchTab('pesan')" title="Pesan">
          <i class="fa-brands fa-facebook-messenger"></i>
        </button>
        <button class="fb-icon-btn" onclick="handleLogout()" title="Keluar / Logout" style="color: #FA3E3E;">
          <i class="fa-solid fa-arrow-right-from-bracket"></i>
        </button>
        <div class="avatar-circle my-avatar" style="width: 36px; height: 36px; font-size: 14px; cursor: pointer;" onclick="switchTab('profil')">
          <i class="fa-solid fa-user"></i>
        </div>
      </div>
    </header>

    <!-- NAVIGATION TABS -->
    <nav class="fb-nav-bar">
      <div class="fb-nav-container">
        <div class="nav-tab-item active" data-tab="beranda">
          <div class="nav-tab-icon"><i class="fa-solid fa-house"></i></div>
          <span class="nav-tab-label">Beranda</span>
        </div>
        <div class="nav-tab-item" data-tab="teman">
          <div class="nav-tab-icon"><i class="fa-solid fa-user-group"></i></div>
          <span class="nav-tab-label">Teman</span>
        </div>
        <div class="nav-tab-item" data-tab="pesan">
          <div class="nav-tab-icon"><i class="fa-solid fa-message"></i></div>
          <span class="nav-tab-label">Pesan</span>
        </div>
        <div class="nav-tab-item" data-tab="notifikasi">
          <div class="nav-tab-icon">
            <i class="fa-solid fa-bell"></i>
            <span class="badge-counter" id="nav-notif-badge" style="display: none;">0</span>
          </div>
          <span class="nav-tab-label">Notifikasi</span>
        </div>
        <div class="nav-tab-item" data-tab="profil">
          <div class="nav-tab-icon"><i class="fa-solid fa-user"></i></div>
          <span class="nav-tab-label">Profil</span>
        </div>
      </div>
    </nav>

    <!-- MAIN CONTAINER -->
    <main class="fb-main-layout">
      <div class="fb-content-wrapper">

      <!-- TAB 1: BERANDA -->
      <div id="view-beranda" class="tab-view-container">
        <!-- SDN 4 Hero / Community Header Banner -->
        <div class="sdn-hero-card">
          <div class="hero-glow-circle"></div>
          <div class="hero-left">
            <div class="hero-branding-wrapper">
              <div class="hero-logo-box">4</div>
              <div>
                <h2 class="hero-title-main">Fourbook</h2>
                <div class="hero-school-sub">SDN 4 PUTRAJAWA ONLINE COMMUNITY</div>
              </div>
            </div>
            <p class="hero-description">Wadah kreatifitas, prestasi, galeri karya, dan interaksi akrab seluruh warga sekolah.</p>
          </div>
          <div class="hero-right-actions">
            <div class="hero-stat-pill"><i class="fa-solid fa-circle-check text-green"></i> Portal Aktif</div>
          </div>
        </div>

        <!-- Admin Quick Actions Bar -->
        <div id="admin-actions-bar" style="display: none; gap: 8px; margin-top: 12px;">
          <button class="btn-fb-primary" style="flex: 1; background: #16A34A;" onclick="openModal('createJournalModal')">
            <i class="fa-solid fa-book-open"></i> + Jurnal Mengajar
          </button>
          <button class="btn-fb-primary" style="flex: 1; background: #D97706;" onclick="openModal('createQuizModal')">
            <i class="fa-solid fa-clipboard-question"></i> + Buat Kuis
          </button>
        </div>

        <!-- Composer Box -->
        <div class="fb-composer-card" style="margin-top: 12px;">
          <div class="composer-top">
            <div class="avatar-circle my-avatar">
              <i class="fa-solid fa-user"></i>
            </div>
            <div class="composer-input-fake" onclick="openModal('createPostModal')">
              Apa yang Anda pikirkan atau karya apa hari ini?
            </div>
          </div>
          <div class="composer-actions">
            <button class="composer-btn btn-photo" onclick="openModal('createPostModal')">
              <i class="fa-solid fa-image"></i> Foto / Karya
            </button>
            <button class="composer-btn btn-quiz" onclick="openQuizModal()">
              <i class="fa-solid fa-puzzle-piece"></i> Kuis & Nilai
            </button>
            <button class="composer-btn btn-journal" onclick="openJournalModal()">
              <i class="fa-solid fa-book"></i> Jurnal Guru
            </button>
          </div>
        </div>

        <!-- Category Filters -->
        <div class="filter-chips-row" style="margin-top: 14px;">
          <div class="filter-chip active" onclick="setPostCategoryFilter(this, 'SEMUA')"><i class="fa-solid fa-shapes"></i> Semua</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Akademik')"><i class="fa-solid fa-book-open"></i> Akademik</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Kesenian')"><i class="fa-solid fa-palette"></i> Kesenian</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Pengumuman')"><i class="fa-solid fa-bullhorn"></i> Pengumuman</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Olahraga')"><i class="fa-solid fa-futbol"></i> Olahraga</div>
        </div>

        <!-- Posts Feed -->
        <div id="posts-feed-container" style="display: flex; flex-direction: column; gap: 14px; margin-top: 14px;">
          <!-- Injected via JavaScript -->
        </div>
      </div>

      <!-- TAB 2: TEMAN (Members & Friends) -->
      <div id="view-teman" class="tab-view-container" style="display: none;">
        <div class="members-header-box">
          <h3 style="font-weight: 800; font-size: 18px; color: #050505;"><i class="fa-solid fa-people-roof" style="color: #1877F2;"></i> Direktori Warga Sekolah</h3>
          <p style="font-size: 12px; color: #65676B;">Temukan teman sekelas, guru, dan tambahkan ke daftar teman akrab Anda.</p>
          
          <div class="search-input-box">
            <i class="fa-solid fa-magnifying-glass" style="color: #65676B;"></i>
            <input type="text" placeholder="Cari nama siswa atau guru..." oninput="handleFriendsSearch(this.value)">
          </div>

          <div class="filter-chips-row">
            <div class="filter-chip friends-filter-chip active" data-filter="SEMUA" onclick="setFriendsFilter('SEMUA')">Semua Warga</div>
            <div class="filter-chip friends-filter-chip" data-filter="TEMAN" onclick="setFriendsFilter('TEMAN')">Teman Saya</div>
            <div class="filter-chip friends-filter-chip" data-filter="PERMINTAAN" onclick="setFriendsFilter('PERMINTAAN')">Permintaan Pertemanan</div>
          </div>
        </div>

        <div id="members-list-container" style="display: flex; flex-direction: column; gap: 12px; margin-top: 14px;">
          <!-- Injected via JavaScript -->
        </div>
      </div>

      <!-- TAB 3: PESAN (Chat Messenger) -->
      <div id="view-pesan" class="tab-view-container" style="display: none;">
        <div id="messages-view-container">
          <!-- Injected via JavaScript -->
        </div>
      </div>

      <!-- TAB 4: NOTIFIKASI -->
      <div id="view-notifikasi" class="tab-view-container" style="display: none;">
        <div class="members-header-box" style="display: flex; justify-content: space-between; flex-direction: row; align-items: center;">
          <div>
            <h3 style="font-weight: 800; font-size: 18px;"><i class="fa-solid fa-bell" style="color: #1877F2;"></i> Notifikasi</h3>
            <p style="font-size: 12px; color: #65676B;">Aktivitas terbaru kelas dan teman Anda.</p>
          </div>
          <div style="display: flex; gap: 6px;">
            <button class="btn-fb-secondary" style="font-size: 11px; padding: 6px 10px;" onclick="markAllNotificationsRead()">Tandai Dibaca</button>
            <button class="btn-fb-secondary" style="font-size: 11px; padding: 6px 10px; color: #FA3E3E;" onclick="clearAllNotifications()">Hapus Semua</button>
          </div>
        </div>

        <div id="notifs-list-container" style="display: flex; flex-direction: column; gap: 8px; margin-top: 14px;">
          <!-- Injected via JavaScript -->
        </div>
      </div>

      <!-- TAB 5: PROFIL -->
      <div id="view-profil" class="tab-view-container" style="display: none;">
        <div id="profile-view-container">
          <!-- Injected via JavaScript -->
        </div>
      </div>

    </div>
  </main>
  </div>

  <!-- MODAL: CREATE POST -->
  <div id="createPostModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title">Buat Postingan Baru</div>
        <button class="modal-close-btn" onclick="closeModal('createPostModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <form id="create-post-form" onsubmit="createNewPost(event)">
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">Judul Postingan (Opsional)</label>
            <input type="text" name="title" class="form-input" placeholder="Contoh: Praktikum IPA Hari Ini">
          </div>
          <div class="form-group">
            <label class="form-label">Kategori</label>
            <select name="category" class="form-select">
              <option value="Umum">Umum</option>
              <option value="Akademik">Akademik</option>
              <option value="Kesenian">Kesenian</option>
              <option value="Pengumuman">Pengumuman</option>
              <option value="Olahraga">Olahraga</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">Isi Postingan</label>
            <textarea name="description" class="form-textarea" rows="4" placeholder="Ceritakan kegiatan, pengalaman, atau informasi..." required></textarea>
          </div>
          <div class="form-group">
            <label class="form-label">Lampirkan Foto/Gambar (Opsional)</label>
            <input type="file" name="mediaFile" accept="image/*" class="form-input" onchange="previewPostImage(event)">
            <img id="post-image-preview" src="#" alt="Preview" style="display: none; width: 100%; border-radius: 8px; margin-top: 8px; max-height: 200px; object-fit: cover;">
          </div>
          <button type="submit" class="btn-fb-primary" style="height: 42px; width: 100%; font-size: 14px; margin-top: 6px;">
            <i class="fa-solid fa-paper-plane"></i> Bagikan Postingan
          </button>
        </div>
      </form>
    </div>
  </div>

  <!-- MODAL: QUIZZES LIST -->
  <div id="quizzesModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title"><i class="fa-solid fa-puzzle-piece" style="color: #F59E0B;"></i> Kuis & Ulangan Harian</div>
        <button class="modal-close-btn" onclick="closeModal('quizzesModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="modal-body" id="quizzes-modal-list">
        <!-- Injected via JS -->
      </div>
    </div>
  </div>

  <!-- MODAL: QUIZ TAKING -->
  <div id="quizTakingModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title">Pengerjaan Kuis</div>
        <button class="modal-close-btn" onclick="closeModal('quizTakingModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="modal-body" id="quiz-taking-container">
        <!-- Injected via JS -->
      </div>
    </div>
  </div>

  <!-- MODAL: CREATE QUIZ (Guru) -->
  <div id="createQuizModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title">Buat Kuis Baru (Wali Kelas)</div>
        <button class="modal-close-btn" onclick="closeModal('createQuizModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <form onsubmit="handleCreateQuizSubmit(event)">
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">Judul Kuis</label>
            <input type="text" id="cq-title" class="form-input" placeholder="Contoh: Ulangan Harian Bab 2 IPAS" required>
          </div>
          <div class="form-group">
            <label class="form-label">Mata Pelajaran</label>
            <input type="text" id="cq-subject" class="form-input" placeholder="Contoh: IPAS / Matematika" required>
          </div>
          <div class="form-group">
            <label class="form-label">Deskripsi & Petunjuk</label>
            <textarea id="cq-desc" class="form-textarea" rows="2" placeholder="Petunjuk pengerjaan kuis..."></textarea>
          </div>
          <div id="cq-questions-container" style="display: flex; flex-direction: column; gap: 10px;">
            <!-- Simple Question 1 -->
            <div style="background: #F8FAFC; padding: 10px; border-radius: 8px; border: 1px solid #E2E8F0;">
              <label class="form-label">Pertanyaan 1</label>
              <input type="text" class="form-input cq-q-text" placeholder="Tuliskan soal..." required style="margin-bottom: 6px;">
              <input type="text" class="form-input cq-opt-0" placeholder="Pilihan A (Jawaban Benar)" required style="margin-bottom: 4px;">
              <input type="text" class="form-input cq-opt-1" placeholder="Pilihan B" required style="margin-bottom: 4px;">
              <input type="text" class="form-input cq-opt-2" placeholder="Pilihan C" required style="margin-bottom: 4px;">
              <input type="text" class="form-input cq-opt-3" placeholder="Pilihan D" required>
            </div>
          </div>
          <button type="submit" class="btn-fb-primary" style="width: 100%; height: 42px; margin-top: 6px;"><i class="fa-solid fa-save"></i> Terbitkan Kuis</button>
        </div>
      </form>
    </div>
  </div>

  <!-- MODAL: JOURNALS LIST -->
  <div id="journalsModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title"><i class="fa-solid fa-book" style="color: #1877F2;"></i> Jurnal Pembelajaran Guru</div>
        <button class="modal-close-btn" onclick="closeModal('journalsModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="modal-body" id="journals-modal-list">
        <!-- Injected via JS -->
      </div>
    </div>
  </div>

  <!-- MODAL: CREATE JOURNAL (Guru) -->
  <div id="createJournalModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title">Input Jurnal Mengajar & Absensi</div>
        <button class="modal-close-btn" onclick="closeModal('createJournalModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <form onsubmit="handleCreateJournalSubmit(event)">
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">Tanggal</label>
            <input type="date" id="cj-date" class="form-input" value="<?= date('Y-m-d') ?>" required>
          </div>
          <div class="form-group">
            <label class="form-label">Mata Pelajaran</label>
            <input type="text" id="cj-subject" class="form-input" placeholder="Contoh: IPAS & Praktikum" required>
          </div>
          <div class="form-group">
            <label class="form-label">Materi Pokok / Bahasan</label>
            <input type="text" id="cj-topic" class="form-input" placeholder="Materi yang diajarkan..." required>
          </div>
          <div class="form-group">
            <label class="form-label">Uraian Kegiatan Pembelajaran</label>
            <textarea id="cj-activity" class="form-textarea" rows="3" placeholder="Deskripsi aktivitas kelas dan praktikum..." required></textarea>
          </div>
          <div class="form-group">
            <label class="form-label">Rekapitulasi Kehadiran Siswa</label>
            <input type="text" id="cj-attendance" class="form-input" value="Hadir: 5, Sakit: 0, Izin: 0, Alpa: 0" required>
          </div>
          <button type="submit" class="btn-fb-primary" style="width: 100%; height: 42px;"><i class="fa-solid fa-floppy-disk"></i> Simpan Jurnal</button>
        </div>
      </form>
    </div>
  </div>

  <!-- MODAL: EDIT PROFILE -->
  <div id="editProfileModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title">Edit Profil</div>
        <button class="modal-close-btn" onclick="closeModal('editProfileModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <form onsubmit="handleEditProfileSubmit(event)">
        <div class="modal-body">
          <!-- Photo Upload Section -->
          <div style="display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 12px; background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 12px; margin-bottom: 12px;">
            <div id="ep-photo-container" style="position: relative; width: 84px; height: 84px;">
              <div id="ep-avatar-fallback" class="avatar-circle" style="width: 84px; height: 84px; font-size: 32px; border: 3px solid #1877F2;">
                <i class="fa-solid fa-user"></i>
              </div>
              <img id="ep-photo-preview" src="#" alt="Preview" style="display: none; width: 84px; height: 84px; border-radius: 50%; object-fit: cover; border: 3px solid #1877F2;">
              <button type="button" onclick="document.getElementById('ep-photo-input').click()" style="position: absolute; bottom: 0; right: 0; width: 28px; height: 28px; border-radius: 50%; background: #1877F2; color: #fff; border: 2px solid #fff; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 11px;">
                <i class="fa-solid fa-camera"></i>
              </button>
            </div>
            
            <input type="file" id="ep-photo-input" accept="image/*" onchange="previewProfilePhoto(event)" style="display: none;">
            <input type="hidden" id="ep-remove-photo-flag" value="0">

            <div style="display: flex; gap: 8px;">
              <button type="button" class="btn-fb-secondary" style="font-size: 12px; padding: 6px 12px;" onclick="document.getElementById('ep-photo-input').click()">
                <i class="fa-solid fa-upload"></i> Unggah Foto Sendiri
              </button>
              <button type="button" class="btn-fb-secondary" id="ep-remove-photo-btn" onclick="removeProfilePhoto()" style="font-size: 12px; padding: 6px 10px; color: #FA3E3E; border-color: #FECACA; background: #FEF2F2; display: none;">
                <i class="fa-solid fa-trash-can"></i> Hapus Foto
              </button>
            </div>
            <div style="font-size: 11px; color: #64748B;">Pilih foto dari galeri/penyimpanan perangkat Anda</div>
          </div>

          <div class="form-group">
            <label class="form-label">Nama Lengkap</label>
            <input type="text" id="ep-name" class="form-input" required>
          </div>
          <div class="form-group">
            <label class="form-label">Tingkat Kelas / Posisi</label>
            <select id="ep-number" class="form-select">
              <option value="Kelas 1">Kelas 1</option>
              <option value="Kelas 2">Kelas 2</option>
              <option value="Kelas 3">Kelas 3</option>
              <option value="Kelas 4">Kelas 4</option>
              <option value="Kelas 5">Kelas 5</option>
              <option value="Kelas 6">Kelas 6</option>
              <option value="Guru">Guru</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">Bio / Status</label>
            <textarea id="ep-bio" class="form-textarea" rows="3"></textarea>
          </div>
          <button type="submit" class="btn-fb-primary" style="width: 100%; height: 42px;"><i class="fa-solid fa-check"></i> Simpan Perubahan</button>
        </div>
      </form>
    </div>
  </div>

  <!-- MODAL: USER PROFILE & ADD FRIEND -->
  <div id="userProfileModal" class="fb-modal-overlay">
    <div class="fb-modal-content" style="max-width: 460px;">
      <div class="modal-header">
        <div class="modal-title" id="upm-title">Profil Pengguna</div>
        <button class="modal-close-btn" onclick="closeModal('userProfileModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="modal-body" id="upm-content">
        <!-- Profile detail rendered via JS -->
      </div>
    </div>
  </div>

  <script src="js/app.js?v=20260910d"></script>
  <script>
    function setPostCategoryFilter(el, cat) {
      document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
      el.classList.add('active');
      state.filterCategory = cat;
      renderPosts();
    }

    function previewPostImage(event) {
      const input = event.target;
      const preview = document.getElementById('post-image-preview');
      if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
          preview.src = e.target.result;
          preview.style.display = 'block';
        }
        reader.readAsDataURL(input.files[0]);
      }
    }

    async function handleCreateJournalSubmit(e) {
      e.preventDefault();
      const formData = new FormData();
      formData.append('dateString', document.getElementById('cj-date').value);
      formData.append('subject', document.getElementById('cj-subject').value);
      formData.append('topic', document.getElementById('cj-topic').value);
      formData.append('activity', document.getElementById('cj-activity').value);
      formData.append('attendanceSummary', document.getElementById('cj-attendance').value);

      const res = await apiRequest('journal.php?action=create_journal', { method: 'POST', body: formData });
      if (res.status) {
        alert(res.message);
        closeModal('createJournalModal');
      } else {
        alert(res.message);
      }
    }

    async function handleCreateQuizSubmit(e) {
      e.preventDefault();
      const title = document.getElementById('cq-title').value;
      const subject = document.getElementById('cq-subject').value;
      const desc = document.getElementById('cq-desc').value;

      const qText = document.querySelector('.cq-q-text').value;
      const opt0 = document.querySelector('.cq-opt-0').value;
      const opt1 = document.querySelector('.cq-opt-1').value;
      const opt2 = document.querySelector('.cq-opt-2').value;
      const opt3 = document.querySelector('.cq-opt-3').value;

      const questions = [
        {
          id: 1,
          questionText: qText,
          options: [opt0, opt1, opt2, opt3],
          correctIndex: 0
        }
      ];

      const formData = new FormData();
      formData.append('title', title);
      formData.append('subject', subject);
      formData.append('description', desc);
      formData.append('questions', JSON.stringify(questions));

      const res = await apiRequest('quizzes.php?action=create_quiz', { method: 'POST', body: formData });
      if (res.status) {
        alert(res.message);
        closeModal('createQuizModal');
      } else {
        alert(res.message);
      }
    }

    function previewProfilePhoto(event) {
      const input = event.target;
      const preview = document.getElementById('ep-photo-preview');
      const fallback = document.getElementById('ep-avatar-fallback');
      const removeBtn = document.getElementById('ep-remove-photo-btn');
      const removeFlag = document.getElementById('ep-remove-photo-flag');

      if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
          preview.src = e.target.result;
          preview.style.display = 'block';
          fallback.style.display = 'none';
          removeBtn.style.display = 'inline-flex';
          removeFlag.value = '0';
        }
        reader.readAsDataURL(input.files[0]);
      }
    }

    function removeProfilePhoto() {
      const preview = document.getElementById('ep-photo-preview');
      const fallback = document.getElementById('ep-avatar-fallback');
      const input = document.getElementById('ep-photo-input');
      const removeBtn = document.getElementById('ep-remove-photo-btn');
      const removeFlag = document.getElementById('ep-remove-photo-flag');

      input.value = '';
      preview.src = '#';
      preview.style.display = 'none';
      fallback.style.display = 'flex';
      removeBtn.style.display = 'none';
      removeFlag.value = '1';
    }

    function prepareEditProfileModal() {
      const u = state.currentUser;
      if (!u) return;

      document.getElementById('ep-name').value = u.fullName || '';
      document.getElementById('ep-number').value = u.studentNumber || '';
      document.getElementById('ep-bio').value = u.bio || '';
      document.getElementById('ep-remove-photo-flag').value = '0';
      document.getElementById('ep-photo-input').value = '';

      const preview = document.getElementById('ep-photo-preview');
      const fallback = document.getElementById('ep-avatar-fallback');
      const removeBtn = document.getElementById('ep-remove-photo-btn');

      fallback.style.backgroundColor = getHexColor(u.avatarColor);
      fallback.innerHTML = `<i class="fa-solid ${getAvatarIcon(u.avatarIcon)}"></i>`;

      if (u.customPhotoUri && u.customPhotoUri.trim() !== '') {
        preview.src = u.customPhotoUri;
        preview.style.display = 'block';
        fallback.style.display = 'none';
        removeBtn.style.display = 'inline-flex';
      } else {
        preview.style.display = 'none';
        fallback.style.display = 'flex';
        removeBtn.style.display = 'none';
      }

      openModal('editProfileModal');
    }

    async function handleEditProfileSubmit(e) {
      e.preventDefault();
      const formData = new FormData();
      formData.append('fullName', document.getElementById('ep-name').value);
      formData.append('studentNumber', document.getElementById('ep-number').value);
      formData.append('bio', document.getElementById('ep-bio').value);

      const photoInput = document.getElementById('ep-photo-input');
      if (photoInput.files && photoInput.files[0]) {
        formData.append('photoFile', photoInput.files[0]);
      }
      formData.append('removePhoto', document.getElementById('ep-remove-photo-flag').value);

      const res = await apiRequest('auth.php?action=update_profile', { method: 'POST', body: formData });
      if (res.status && res.data.user) {
        state.currentUser = res.data.user;
        // Update user in allUsers list as well
        const idx = state.allUsers.findIndex(u => u.id === res.data.user.id);
        if (idx !== -1) state.allUsers[idx] = res.data.user;

        alert(res.message || 'Profil berhasil diperbarui!');
        closeModal('editProfileModal');
        updateUserUI();
        renderProfileView();
        fetchPosts();
      } else {
        alert(res.message || 'Gagal memperbarui profil');
      }
    }
  </script>
</body>
</html>
