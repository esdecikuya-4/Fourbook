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
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <!-- ================= AUTH SCREEN (LOGIN & REGISTER) ================= -->
  <div id="auth-view-screen" class="fb-auth-overlay" style="display: none;">
    <div class="fb-auth-card">
      <div class="auth-header">
        <div class="auth-logo-badge">4</div>
        <h1 class="auth-title">fourbook</h1>
        <div class="auth-school-subtitle">SDN 4 Putrajawa</div>
        <p class="auth-tagline">Ruang Kolaborasi & Media Sosial Resmi Siswa dan Guru</p>

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
            <label class="form-label">Username / Nama Panggilan</label>
            <input type="text" id="login-username" class="form-input" placeholder="Contoh: dimas / teten" required>
          </div>
          <div class="form-group">
            <label class="form-label">Kata Sandi</label>
            <input type="password" id="login-password" class="form-input" placeholder="Masukkan kata sandi..." required>
          </div>
          <button type="submit" class="btn-fb-primary" style="height: 42px; width: 100%; margin-top: 4px; font-size: 14px;">
            <i class="fa-solid fa-arrow-right-to-bracket"></i> Masuk ke Fourbook
          </button>
        </form>

        <!-- PILIH AKUN CEPAT / DEMO -->
        <div class="auth-demo-accounts-box">
          <div class="auth-demo-title">Atau Masuk Cepat (Akun Sekolah):</div>
          <div class="demo-account-chip" onclick="quickLoginUser(1)">
            <div class="demo-chip-avatar" style="background: #1877F2;"><i class="fa-solid fa-chalkboard-user"></i></div>
            <div class="demo-chip-info">
              <div class="demo-chip-name">Teten Kurniawan, S.Pd.</div>
              <div class="demo-chip-role">Wali Kelas (Admin) &bull; NIP. 198507122010011015</div>
            </div>
          </div>
          <div class="demo-account-chip" onclick="quickLoginUser(2)">
            <div class="demo-chip-avatar" style="background: #D97706;"><i class="fa-solid fa-star"></i></div>
            <div class="demo-chip-info">
              <div class="demo-chip-name">Dimas Aditya Pratama</div>
              <div class="demo-chip-role">Ketua Kelas &bull; No. Absen: 01</div>
            </div>
          </div>
          <div class="demo-account-chip" onclick="quickLoginUser(3)">
            <div class="demo-chip-avatar" style="background: #EC4899;"><i class="fa-solid fa-palette"></i></div>
            <div class="demo-chip-info">
              <div class="demo-chip-name">Siti Nurhaliza</div>
              <div class="demo-chip-role">Murid &bull; No. Absen: 02</div>
            </div>
          </div>
          <div class="demo-account-chip" onclick="quickLoginUser(4)">
            <div class="demo-chip-avatar" style="background: #3B82F6;"><i class="fa-solid fa-futbol"></i></div>
            <div class="demo-chip-info">
              <div class="demo-chip-name">Rizky Alfaridzi</div>
              <div class="demo-chip-role">Murid &bull; No. Absen: 03</div>
            </div>
          </div>
        </div>
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
            <select id="reg-role" class="form-select">
              <option value="MURID">Siswa / Murid</option>
              <option value="KETUA_KELAS">Ketua Kelas</option>
              <option value="WALI_KELAS">Wali Kelas / Guru</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">No. Absen / NIP</label>
            <input type="text" id="reg-number" class="form-input" placeholder="Contoh: No. Absen: 05" required>
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
    </div>
  </div>

  <!-- ================= MAIN APPLICATION SCREEN ================= -->
  <div id="app-main-screen" style="display: none;">
    <!-- TOP HEADER -->
    <header class="fb-header">
      <div class="fb-brand" onclick="switchTab('beranda')">
        <div class="fb-logo-circle">4</div>
        <div class="fb-brand-title">
          <span class="fb-brand-name">fourbook</span>
          <span class="fb-school-tag">SDN 4 Putrajawa</span>
        </div>
      </div>

      <div class="fb-header-actions">
        <button class="fb-icon-btn" onclick="openModal('switchUserModal')" title="Ganti Akun">
          <i class="fa-solid fa-users"></i>
        </button>
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
        <!-- SDN 4 Hero -->
        <div class="sdn-hero-card">
          <div class="hero-left">
            <h2>SDN 4 Putrajawa</h2>
            <p>Ruang Kolaborasi & Media Sosial Resmi Siswa dan Guru</p>
          </div>
          <div class="hero-badge">T.A. 2026/2027</div>
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
        <div class="filter-chips-row" style="margin-top: 12px;">
          <div class="filter-chip active" onclick="setPostCategoryFilter(this, 'SEMUA')">Semua</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Akademik')">Akademik</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Kesenian')">Kesenian</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Pengumuman')">Pengumuman</div>
          <div class="filter-chip" onclick="setPostCategoryFilter(this, 'Olahraga')">Olahraga</div>
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

  <!-- MODAL: SWITCH USER (Demo Accounts) -->
  <div id="switchUserModal" class="fb-modal-overlay">
    <div class="fb-modal-content">
      <div class="modal-header">
        <div class="modal-title">Ganti Akun Pengguna</div>
        <button class="modal-close-btn" onclick="closeModal('switchUserModal')"><i class="fa-solid fa-xmark"></i></button>
      </div>
      <div class="modal-body" id="switch-user-list">
        <p style="font-size: 12px; color: #65676B;">Pilih profil untuk masuk sebagai Guru Wali Kelas, Ketua Kelas, atau Siswa:</p>
        <div style="display: flex; flex-direction: column; gap: 8px; margin-top: 8px;">
          <div class="member-card" style="cursor: pointer; padding: 10px 14px;" onclick="switchUserAccount(1)">
            <div class="member-card-top">
              <div class="avatar-circle" style="background-color: #1877F2; width: 40px; height: 40px;"><i class="fa-solid fa-chalkboard-user"></i></div>
              <div>
                <div style="font-weight: bold; font-size: 13px;">Teten Kurniawan, S.Pd.</div>
                <div style="font-size: 11px; color: #1877F2;">Wali Kelas (Admin) &bull; NIP. 198507122010011015</div>
              </div>
            </div>
          </div>
          <div class="member-card" style="cursor: pointer; padding: 10px 14px;" onclick="switchUserAccount(2)">
            <div class="member-card-top">
              <div class="avatar-circle" style="background-color: #D97706; width: 40px; height: 40px;"><i class="fa-solid fa-star"></i></div>
              <div>
                <div style="font-weight: bold; font-size: 13px;">Dimas Aditya Pratama</div>
                <div style="font-size: 11px; color: #D97706;">Ketua Kelas &bull; No. Absen: 01</div>
              </div>
            </div>
          </div>
          <div class="member-card" style="cursor: pointer; padding: 10px 14px;" onclick="switchUserAccount(3)">
            <div class="member-card-top">
              <div class="avatar-circle" style="background-color: #EC4899; width: 40px; height: 40px;"><i class="fa-solid fa-palette"></i></div>
              <div>
                <div style="font-weight: bold; font-size: 13px;">Siti Nurhaliza</div>
                <div style="font-size: 11px; color: #65676B;">Murid &bull; No. Absen: 02</div>
              </div>
            </div>
          </div>
          <div class="member-card" style="cursor: pointer; padding: 10px 14px;" onclick="switchUserAccount(4)">
            <div class="member-card-top">
              <div class="avatar-circle" style="background-color: #3B82F6; width: 40px; height: 40px;"><i class="fa-solid fa-futbol"></i></div>
              <div>
                <div style="font-weight: bold; font-size: 13px;">Rizky Alfaridzi</div>
                <div style="font-size: 11px; color: #65676B;">Murid &bull; No. Absen: 03</div>
              </div>
            </div>
          </div>
        </div>
      </div>
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
          <div class="form-group">
            <label class="form-label">Nama Lengkap</label>
            <input type="text" id="ep-name" class="form-input" required>
          </div>
          <div class="form-group">
            <label class="form-label">No. Absen / NIP</label>
            <input type="text" id="ep-number" class="form-input" required>
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

  <script src="js/app.js"></script>
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

    async function handleEditProfileSubmit(e) {
      e.preventDefault();
      const formData = new FormData();
      formData.append('fullName', document.getElementById('ep-name').value);
      formData.append('studentNumber', document.getElementById('ep-number').value);
      formData.append('bio', document.getElementById('ep-bio').value);

      const res = await apiRequest('auth.php?action=update_profile', { method: 'POST', body: formData });
      if (res.status) {
        alert(res.message);
        closeModal('editProfileModal');
        location.reload();
      }
    }
  </script>
</body>
</html>
