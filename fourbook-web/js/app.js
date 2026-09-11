// fourbook-web/js/app.js
let state = {
  currentUser: null,
  allUsers: [],
  activeTab: 'beranda',
  posts: [],
  friendships: [],
  notifications: [],
  unreadNotifsCount: 0,
  unreadMessagesCount: 0,
  activeChatPartner: null,
  chatMessages: [],
  quizzes: [],
  journals: [],
  filterCategory: 'SEMUA',
  friendsTabFilter: 'SEMUA',
  searchQuery: ''
};

// INITIALIZATION
document.addEventListener('DOMContentLoaded', async () => {
  await loadCurrentUser();
  initNavigation();
  initPolling();
});

async function apiRequest(endpoint, options = {}) {
  try {
    const res = await fetch(`api/${endpoint}`, options);
    return await res.json();
  } catch (err) {
    console.error('API Error:', err);
    return { status: false, message: 'Koneksi gagal' };
  }
}

async function loadCurrentUser() {
  const res = await apiRequest('auth.php?action=get_current');
  if (res.status && res.data.user) {
    state.currentUser = res.data.user;
    state.allUsers = res.data.all_users || [];
    showAppScreen();
    updateUserUI();
    switchTab('beranda');
    fetchNotifications();
    fetchFriendships();
  } else {
    state.currentUser = null;
    state.allUsers = res.data ? (res.data.all_users || []) : [];
    showAuthScreen();
  }
}

function showAuthScreen() {
  const authScreen = document.getElementById('auth-view-screen');
  const appScreen = document.getElementById('app-main-screen');
  if (authScreen) authScreen.style.display = 'flex';
  if (appScreen) appScreen.style.display = 'none';
}

function showAppScreen() {
  const authScreen = document.getElementById('auth-view-screen');
  const appScreen = document.getElementById('app-main-screen');
  if (authScreen) authScreen.style.display = 'none';
  if (appScreen) appScreen.style.display = 'block';
}

function toggleAuthTab(tab) {
  const loginForm = document.getElementById('auth-form-login');
  const regForm = document.getElementById('auth-form-register');
  const loginBtn = document.getElementById('auth-tab-login-btn');
  const regBtn = document.getElementById('auth-tab-register-btn');

  if (tab === 'login') {
    if (loginForm) loginForm.style.display = 'flex';
    if (regForm) regForm.style.display = 'none';
    if (loginBtn) loginBtn.classList.add('active');
    if (regBtn) regBtn.classList.remove('active');
  } else {
    if (loginForm) loginForm.style.display = 'none';
    if (regForm) regForm.style.display = 'flex';
    if (loginBtn) loginBtn.classList.remove('active');
    if (regBtn) regBtn.classList.add('active');
  }
}

async function handleAuthLogin(e) {
  e.preventDefault();
  const username = document.getElementById('login-username').value.trim();
  const password = document.getElementById('login-password').value.trim();

  if (!username || !password) {
    alert('Silakan masukkan username dan kata sandi');
    return;
  }

  const formData = new FormData();
  formData.append('username', username);
  formData.append('password', password);

  const res = await apiRequest('auth.php?action=login', { method: 'POST', body: formData });
  if (res.status && res.data.user) {
    state.currentUser = res.data.user;
    showAppScreen();
    updateUserUI();
    switchTab('beranda');
    fetchNotifications();
    fetchFriendships();
  } else {
    alert(res.message || 'Username atau kata sandi tidak cocok');
  }
}

function handleRoleChange(role) {
  const classGroup = document.getElementById('reg-class-group');
  if (classGroup) {
    if (role === 'MURID') {
      classGroup.style.display = 'block';
    } else {
      classGroup.style.display = 'none';
    }
  }
}

async function handleAuthRegister(e) {
  e.preventDefault();
  const fullName = document.getElementById('reg-fullname').value.trim();
  const role = document.getElementById('reg-role').value;
  const classSelect = document.getElementById('reg-class');
  const selectedClass = (role === 'MURID' && classSelect) ? classSelect.value : '';
  const studentNumber = (role === 'MURID') ? selectedClass : 'Guru';
  const username = document.getElementById('reg-username').value.trim();
  const password = document.getElementById('reg-password').value.trim();
  const bioInput = document.getElementById('reg-bio');
  const bio = (bioInput && bioInput.value.trim()) ? bioInput.value.trim() : (role === 'MURID' ? `Siswa ${selectedClass} SDN 4 Putrajawa` : 'Guru SDN 4 Putrajawa');

  if (!fullName || !username || !password) {
    alert('Nama lengkap, username, dan kata sandi wajib diisi');
    return;
  }

  const formData = new FormData();
  formData.append('fullName', fullName);
  formData.append('role', role);
  formData.append('studentNumber', studentNumber);
  formData.append('username', username);
  formData.append('password', password);
  formData.append('bio', bio);

  const res = await apiRequest('auth.php?action=register', { method: 'POST', body: formData });
  if (res.status && res.data.user) {
    alert('Pendaftaran berhasil! Selamat datang di Fourbook.');
    state.currentUser = res.data.user;
    showAppScreen();
    updateUserUI();
    switchTab('beranda');
    fetchNotifications();
    fetchFriendships();
  } else {
    alert(res.message || 'Gagal mendaftar akun');
  }
}

async function handleLogout() {
  if (confirm('Apakah Anda yakin ingin keluar dari akun Fourbook?')) {
    await apiRequest('auth.php?action=logout');
    state.currentUser = null;
    showAuthScreen();
  }
}

function updateUserUI() {
  const u = state.currentUser;
  if (!u) return;

  document.querySelectorAll('.my-avatar').forEach(el => {
    if (u.customPhotoUri && u.customPhotoUri.trim() !== '') {
      el.style.backgroundColor = 'transparent';
      el.innerHTML = `<img src="${u.customPhotoUri}" alt="${escapeHtml(u.fullName)}" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover; display: block;">`;
    } else {
      el.style.backgroundColor = getHexColor(u.avatarColor);
      el.innerHTML = `<i class="fa-solid ${getAvatarIcon(u.avatarIcon)}"></i>`;
    }
  });

  const nameEl = document.getElementById('topbar-user-name');
  if (nameEl) nameEl.textContent = u.fullName;

  // Show or hide teacher admin badges
  const adminBadge = document.getElementById('admin-actions-bar');
  if (adminBadge) {
    adminBadge.style.display = u.role === 'WALI_KELAS' ? 'flex' : 'none';
  }
}

function initNavigation() {
  document.querySelectorAll('.nav-tab-item').forEach(tab => {
    tab.addEventListener('click', (e) => {
      e.preventDefault();
      const tabName = tab.dataset.tab;
      switchTab(tabName);
    });
  });
}

function switchTab(tabName) {
  state.activeTab = tabName;
  document.querySelectorAll('.nav-tab-item').forEach(t => {
    t.classList.toggle('active', t.dataset.tab === tabName);
  });

  document.querySelectorAll('.tab-view-container').forEach(view => {
    view.style.display = 'none';
  });

  const activeView = document.getElementById(`view-${tabName}`);
  if (activeView) activeView.style.display = 'block';

  if (tabName === 'beranda') fetchPosts();
  if (tabName === 'teman') renderFriendsView();
  if (tabName === 'pesan') renderMessagesView();
  if (tabName === 'notifikasi') renderNotificationsView();
  if (tabName === 'profil') renderProfileView();
}

function initPolling() {
  setInterval(() => {
    fetchNotifications(true);
    if (state.activeTab === 'pesan' && state.activeChatPartner) {
      loadChatWith(state.activeChatPartner.id, true);
    }
  }, 5000);
}

// ----------------------------------------------------
// POSTS & FEED
// ----------------------------------------------------
async function fetchPosts() {
  const res = await apiRequest('posts.php?action=get_posts');
  if (res.status) {
    state.posts = res.data.posts;
    renderPosts();
  }
}

// Global state for comment attachments (postId -> { file, type, name })
const commentAttachments = {};

// Helper: Extract YouTube embed URL from text or URL
function extractYoutubeEmbed(urlOrText) {
  if (!urlOrText) return null;
  const regExp = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/(?:watch\?v=|embed\/|shorts\/)|youtu\.be\/)([a-zA-Z0-9_-]{11})/i;
  const match = String(urlOrText).match(regExp);
  return match ? `https://www.youtube.com/embed/${match[1]}` : null;
}

// Media handlers for Post Modal
function handlePostDescInput(text) {
  const embedUrl = extractYoutubeEmbed(text);
  const detectBox = document.getElementById('post-yt-detect-box');
  const iframe = document.getElementById('post-yt-iframe');
  if (embedUrl) {
    if (detectBox) detectBox.style.display = 'block';
    if (iframe && iframe.src !== embedUrl) iframe.src = embedUrl;
  }
}

function cancelYtEmbed() {
  const detectBox = document.getElementById('post-yt-detect-box');
  const iframe = document.getElementById('post-yt-iframe');
  const embedInput = document.getElementById('post-embed-url');
  if (detectBox) detectBox.style.display = 'none';
  if (iframe) iframe.src = '';
  if (embedInput) embedInput.value = '';
}

function toggleEmbedLinkInput() {
  const box = document.getElementById('post-embed-input-box');
  if (box) {
    box.style.display = box.style.display === 'none' ? 'block' : 'none';
    if (box.style.display === 'block') {
      const inp = document.getElementById('post-embed-url');
      if (inp) inp.focus();
    }
  }
}

function handleEmbedUrlInput(url) {
  const embedUrl = extractYoutubeEmbed(url);
  const detectBox = document.getElementById('post-yt-detect-box');
  const iframe = document.getElementById('post-yt-iframe');
  if (embedUrl) {
    if (detectBox) detectBox.style.display = 'block';
    if (iframe) iframe.src = embedUrl;
  } else {
    if (detectBox) detectBox.style.display = 'none';
    if (iframe) iframe.src = '';
  }
}

function triggerPostFileInput(type) {
  const fileInp = document.getElementById('post-media-file-input');
  if (!fileInp) return;
  if (type === 'image') fileInp.accept = 'image/*';
  else if (type === 'video') fileInp.accept = 'video/*, .mp4, .webm, .mov, .m4v';
  else if (type === 'audio') fileInp.accept = 'audio/*, .mp3, .m4a, .wav, .ogg, .aac, .flac';
  fileInp.click();
}

function handlePostMediaSelected(input) {
  if (!input.files || !input.files[0]) return;
  const file = input.files[0];
  const previewArea = document.getElementById('post-media-preview-area');
  const previewImg = document.getElementById('post-preview-img');
  const previewVideo = document.getElementById('post-preview-video');
  const previewAudioCard = document.getElementById('post-preview-audio-card');
  const previewAudio = document.getElementById('post-preview-audio');
  const previewAudioName = document.getElementById('post-preview-audio-name');
  const label = document.getElementById('active-media-label');

  // Reset previews
  previewImg.style.display = 'none';
  previewVideo.style.display = 'none';
  previewAudioCard.style.display = 'none';
  previewArea.style.display = 'block';

  const ext = file.name.split('.').pop().toLowerCase();
  const fileUrl = URL.createObjectURL(file);

  if (file.type.startsWith('image/')) {
    previewImg.src = fileUrl;
    previewImg.style.display = 'block';
    if (label) label.textContent = `Foto: ${file.name}`;
  } else if (file.type.startsWith('video/') || ['mp4', 'webm', 'mov', 'm4v', '3gp'].includes(ext)) {
    previewVideo.src = fileUrl;
    previewVideo.style.display = 'block';
    if (label) label.textContent = `Video: ${file.name}`;
  } else if (file.type.startsWith('audio/') || ['mp3', 'm4a', 'wav', 'ogg', 'aac', 'flac'].includes(ext)) {
    previewAudio.src = fileUrl;
    if (previewAudioName) previewAudioName.textContent = file.name;
    previewAudioCard.style.display = 'block';
    if (label) label.textContent = `Audio: ${file.name}`;
  }
}

function clearPostMedia() {
  const fileInp = document.getElementById('post-media-file-input');
  if (fileInp) fileInp.value = '';
  const previewArea = document.getElementById('post-media-preview-area');
  if (previewArea) previewArea.style.display = 'none';
  const label = document.getElementById('active-media-label');
  if (label) label.textContent = '';
}

// Media handlers for comments
function triggerCommentFile(postId, type) {
  const inp = document.getElementById(`comment-file-${type}-${postId}`);
  if (inp) inp.click();
}

function handleCommentFileSelected(postId, input, type) {
  if (!input.files || !input.files[0]) return;
  const file = input.files[0];
  commentAttachments[postId] = { file, type, name: file.name };

  const previewBar = document.getElementById(`comment-preview-bar-${postId}`);
  const previewName = document.getElementById(`comment-preview-name-${postId}`);
  const previewIcon = document.getElementById(`comment-preview-icon-${postId}`);

  if (previewBar && previewName && previewIcon) {
    previewName.textContent = `${file.name} (${type})`;
    if (type === 'PHOTO') previewIcon.className = 'fa-solid fa-image';
    else if (type === 'VIDEO') previewIcon.className = 'fa-solid fa-video';
    else if (type === 'AUDIO') previewIcon.className = 'fa-solid fa-music';
    previewBar.style.display = 'flex';
  }
}

function clearCommentAttachment(postId) {
  delete commentAttachments[postId];
  const previewBar = document.getElementById(`comment-preview-bar-${postId}`);
  if (previewBar) previewBar.style.display = 'none';
  ['image', 'video', 'audio'].forEach(t => {
    const inp = document.getElementById(`comment-file-${t}-${postId}`);
    if (inp) inp.value = '';
  });
}

function renderPosts() {
  const container = document.getElementById('posts-feed-container');
  if (!container) return;

  let filtered = state.posts;
  if (state.filterCategory !== 'SEMUA') {
    filtered = filtered.filter(p => p.category === state.filterCategory);
  }

  if (filtered.length === 0) {
    container.innerHTML = `
      <div style="text-align: center; padding: 40px; background: #fff; border-radius: 16px;">
        <i class="fa-regular fa-newspaper" style="font-size: 40px; color: #1877F2; margin-bottom: 12px;"></i>
        <h4 style="font-weight: 700;">Belum Ada Postingan</h4>
        <p style="color: #65676B; font-size: 13px;">Jadilah yang pertama membagikan kegiatan atau karya di Fourbook!</p>
      </div>
    `;
    return;
  }

  container.innerHTML = filtered.map(p => {
    const isLiked = p.reactions && p.reactions['LIKE'] && p.reactions['LIKE'].includes(state.currentUser?.id);
    const timeFormatted = formatTimestamp(p.timestamp);

    // Auto-detect YouTube embed if not explicitly saved
    let embedUrl = p.embedUrl || '';
    if (!embedUrl) {
      embedUrl = extractYoutubeEmbed(p.description) || extractYoutubeEmbed(p.title) || '';
    }

    const commentsList = (p.comments || []).map(c => {
      let commentMediaHtml = '';
      if (c.mediaUri) {
        const isAudio = c.mediaType === 'AUDIO' || !!c.mediaUri.match(/\.(mp3|m4a|wav|ogg|aac|flac)$/i);
        const isVideo = c.mediaType === 'VIDEO' || !!c.mediaUri.match(/\.(mp4|webm|mov|m4v|3gp|mkv)$/i);
        if (isAudio) {
          commentMediaHtml = `
            <div style="margin-top: 6px; background: #EFF6FF; border: 1.5px solid #BFDBFE; border-radius: 8px; padding: 6px 10px; max-width: 290px;">
              <div style="font-size: 11px; font-weight: 700; color: #1E40AF; margin-bottom: 4px;">
                <i class="fa-solid fa-music"></i> Lampiran Audio
              </div>
              <audio controls src="${escapeHtml(c.mediaUri)}" style="width: 100%; height: 32px; outline: none;"></audio>
            </div>
          `;
        } else if (isVideo) {
          commentMediaHtml = `
            <div style="margin-top: 6px; border-radius: 8px; overflow: hidden; max-width: 320px; background: #000;">
              <video controls src="${escapeHtml(c.mediaUri)}" style="width: 100%; max-height: 200px; display: block;"></video>
            </div>
          `;
        } else {
          commentMediaHtml = `
            <div style="margin-top: 6px;">
              <img src="${escapeHtml(c.mediaUri)}" alt="Lampiran komentar" style="max-width: 100%; max-height: 180px; border-radius: 8px; cursor: pointer; object-fit: cover; border: 1px solid #E4E6EB;" onclick="window.open('${escapeHtml(c.mediaUri)}', '_blank')" onerror="this.style.display='none'">
            </div>
          `;
        }
      }

      return `
        <div class="comment-item">
          <div onclick="showUserProfileModal(${c.userId})" style="cursor: pointer;" title="Lihat profil / tambah teman">
            ${renderAvatarHtml(c.userPhotoUri, c.userAvatarColor, c.userAvatarIcon, 32, 13)}
          </div>
          <div class="comment-bubble">
            <div class="comment-author" onclick="showUserProfileModal(${c.userId})" style="cursor: pointer;" title="Lihat profil / tambah teman">
              ${escapeHtml(c.userName)} <span class="role-tag ${c.userRole}">${getRoleLabel(c.userRole)}</span>
            </div>
            ${c.commentText ? `<div class="comment-text">${escapeHtml(c.commentText)}</div>` : ''}
            ${commentMediaHtml}
          </div>
        </div>
      `;
    }).join('');

    // Post Media Rendering
    let postMediaHtml = '';
    if (embedUrl) {
      postMediaHtml = `
        <div class="post-media-container" style="margin-top: 10px; border-radius: 12px; overflow: hidden; position: relative; padding-bottom: 56.25%; height: 0; background: #000;">
          <iframe src="${escapeHtml(embedUrl)}" style="position: absolute; top:0; left:0; width:100%; height:100%; border:0;" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
        </div>
      `;
    } else if (p.mediaType === 'AUDIO' || (p.mediaUri && p.mediaUri.match(/\.(mp3|m4a|wav|ogg|aac|flac)$/i)) || p.postType === 'AUDIO') {
      postMediaHtml = `
        <div class="post-audio-player-card" style="background: linear-gradient(135deg, #EFF6FF, #DBEAFE); border: 1.5px solid #BFDBFE; border-radius: 14px; padding: 14px; margin-top: 10px;">
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 8px;">
            <div style="width: 44px; height: 44px; border-radius: 50%; background: #1877F2; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 18px; box-shadow: 0 4px 10px rgba(24, 119, 242, 0.3);">
              <i class="fa-solid fa-music"></i>
            </div>
            <div style="flex: 1; min-width: 0;">
              <div style="font-weight: 700; font-size: 14px; color: #1E3A8A; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                ${escapeHtml(p.audioTitle || p.title || 'Audio / Musik SDN 4')}
              </div>
              <div style="font-size: 12px; color: #3B82F6;">
                <i class="fa-solid fa-microphone-lines"></i> ${escapeHtml(p.audioArtist || p.uploaderName || 'SDN 4 Putrajawa')}
              </div>
            </div>
          </div>
          <audio controls src="${escapeHtml(p.mediaUri)}" style="width: 100%; height: 38px; border-radius: 8px; outline: none;"></audio>
        </div>
      `;
    } else if (p.mediaType === 'VIDEO' || (p.mediaUri && p.mediaUri.match(/\.(mp4|webm|mov|m4v|3gp|mkv)$/i)) || p.postType === 'VIDEO') {
      postMediaHtml = `
        <div class="post-media-container" style="margin-top: 10px; border-radius: 12px; overflow: hidden; background: #000;">
          <video controls src="${escapeHtml(p.mediaUri)}" style="width: 100%; max-height: 420px; display: block;"></video>
        </div>
      `;
    } else if (p.mediaUri) {
      postMediaHtml = `
        <div class="post-media-container" style="margin-top: 10px; border-radius: 12px; overflow: hidden;">
          <img src="${escapeHtml(p.mediaUri)}" alt="Media Post" onerror="this.style.display='none'" style="width: 100%; max-height: 450px; object-fit: cover; cursor: pointer;" onclick="window.open('${escapeHtml(p.mediaUri)}', '_blank')">
        </div>
      `;
    }

    return `
      <div class="fb-post-card" id="post-card-${p.id}">
        <div class="post-header">
          <div class="post-user-info" onclick="showUserProfileModal(${p.uploaderId})" style="cursor: pointer;" title="Lihat profil / tambah teman">
            ${renderAvatarHtml(p.uploaderPhotoUri, p.uploaderAvatarColor, p.uploaderAvatarIcon, 40, 16)}
            <div>
              <div class="post-author-name">
                ${escapeHtml(p.uploaderName)}
                <span class="role-tag ${p.uploaderRole}">${getRoleLabel(p.uploaderRole)}</span>
              </div>
              <div class="post-time">${timeFormatted} &bull; SDN 4 Putrajawa</div>
            </div>
          </div>
          ${(p.uploaderId === state.currentUser?.id || state.currentUser?.role === 'WALI_KELAS') ? `
            <button class="fb-icon-btn" onclick="deletePost(${p.id})" title="Hapus Postingan">
              <i class="fa-solid fa-trash-can" style="font-size: 13px; color: #FA3E3E;"></i>
            </button>
          ` : ''}
        </div>

        <div class="post-body">
          ${p.title ? `<div class="post-title">${escapeHtml(p.title)}</div>` : ''}
          <div class="post-text">${escapeHtml(p.description)}</div>
          ${p.category ? `<div class="post-category-tag"><i class="fa-solid fa-tag"></i> ${escapeHtml(p.category)}</div>` : ''}
        </div>

        ${postMediaHtml}

        <div class="post-stats">
          <div><i class="fa-solid fa-thumbs-up" style="color: #1877F2;"></i> <span id="like-count-${p.id}">${p.likeCount || 0}</span> Suka</div>
          <div>${(p.comments || []).length} Komentar</div>
        </div>

        <div class="post-footer-actions">
          <button class="post-action-btn ${isLiked ? 'active-like' : ''}" onclick="toggleLikePost(${p.id})">
            <i class="${isLiked ? 'fa-solid' : 'fa-regular'} fa-thumbs-up"></i> Suka
          </button>
          <button class="post-action-btn" onclick="focusCommentInput(${p.id})">
            <i class="fa-regular fa-comment"></i> Komentar
          </button>
          <button class="post-action-btn" onclick="sharePost(${p.id})">
            <i class="fa-solid fa-share"></i> Bagikan
          </button>
        </div>

        <div class="post-comments-wrapper">
          <div id="comments-list-${p.id}">${commentsList}</div>
          
          <div class="comment-input-area" id="comment-area-${p.id}" style="margin-top: 8px;">
            <!-- Attachment Preview Chip (hidden until attached) -->
            <div id="comment-preview-bar-${p.id}" class="comment-attachment-preview" style="display: none; align-items: center; justify-content: space-between; background: #EFF6FF; border: 1.5px dashed #3B82F6; border-radius: 8px; padding: 6px 10px; margin-bottom: 6px; font-size: 12px; color: #1D4ED8;">
              <div style="display: flex; align-items: center; gap: 8px; overflow: hidden;">
                <i id="comment-preview-icon-${p.id}" class="fa-solid fa-paperclip"></i>
                <span id="comment-preview-name-${p.id}" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 220px;">Lampiran</span>
              </div>
              <button type="button" onclick="clearCommentAttachment(${p.id})" style="background: none; border: none; color: #DC2626; cursor: pointer; font-size: 14px; font-weight: bold;" title="Hapus lampiran"><i class="fa-solid fa-xmark"></i></button>
            </div>

            <div class="comment-input-row" style="display: flex; align-items: center; gap: 6px;">
              <div class="avatar-circle my-avatar" style="width: 32px; height: 32px; font-size: 13px; background-color: ${getHexColor(state.currentUser?.avatarColor)}">
                <i class="fa-solid ${getAvatarIcon(state.currentUser?.avatarIcon)}"></i>
              </div>
              <input type="text" class="comment-input-field" id="comment-input-${p.id}" placeholder="Tulis komentar atau lampirkan media..." onkeypress="handleCommentKeyPress(event, ${p.id})" style="flex: 1;">
              
              <!-- Media attachment buttons in comment -->
              <button type="button" class="fb-icon-btn" onclick="triggerCommentFile(${p.id}, 'image')" title="Lampirkan Foto" style="width: 32px; height: 32px; color: #10B981;">
                <i class="fa-solid fa-camera"></i>
              </button>
              <button type="button" class="fb-icon-btn" onclick="triggerCommentFile(${p.id}, 'video')" title="Lampirkan Video" style="width: 32px; height: 32px; color: #EF4444;">
                <i class="fa-solid fa-video"></i>
              </button>
              <button type="button" class="fb-icon-btn" onclick="triggerCommentFile(${p.id}, 'audio')" title="Lampirkan Audio / Rekaman MP3" style="width: 32px; height: 32px; color: #8B5CF6;">
                <i class="fa-solid fa-microphone-lines"></i>
              </button>

              <!-- Hidden file inputs for comments -->
              <input type="file" id="comment-file-image-${p.id}" accept="image/*" style="display: none;" onchange="handleCommentFileSelected(${p.id}, this, 'PHOTO')">
              <input type="file" id="comment-file-video-${p.id}" accept="video/*, .mp4, .webm, .mov, .m4v" style="display: none;" onchange="handleCommentFileSelected(${p.id}, this, 'VIDEO')">
              <input type="file" id="comment-file-audio-${p.id}" accept="audio/*, .mp3, .m4a, .wav, .ogg, .aac, .flac" style="display: none;" onchange="handleCommentFileSelected(${p.id}, this, 'AUDIO')">

              <button class="comment-send-btn" onclick="sendComment(${p.id})" title="Kirim Komentar"><i class="fa-solid fa-paper-plane"></i></button>
            </div>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

async function createNewPost(e) {
  e.preventDefault();
  const form = document.getElementById('create-post-form');
  const formData = new FormData(form);

  const res = await apiRequest('posts.php?action=create_post', {
    method: 'POST',
    body: formData
  });

  if (res.status) {
    closeModal('createPostModal');
    form.reset();
    clearPostMedia();
    cancelYtEmbed();
    fetchPosts();
    fetchNotifications();
  } else {
    alert(res.message);
  }
}

async function toggleLikePost(postId) {
  const formData = new FormData();
  formData.append('postId', postId);
  formData.append('reaction', 'LIKE');

  const res = await apiRequest('posts.php?action=toggle_reaction', {
    method: 'POST',
    body: formData
  });

  if (res.status) {
    fetchPosts();
  }
}

async function sendComment(postId) {
  const input = document.getElementById(`comment-input-${postId}`);
  const text = input ? input.value.trim() : '';
  const attachment = commentAttachments[postId];

  if (!text && !attachment) return;

  const formData = new FormData();
  formData.append('postId', postId);
  formData.append('commentText', text);

  if (attachment) {
    formData.append('commentMediaFile', attachment.file);
    formData.append('mediaType', attachment.type);
  }

  const res = await apiRequest('posts.php?action=add_comment', {
    method: 'POST',
    body: formData
  });

  if (res.status) {
    if (input) input.value = '';
    clearCommentAttachment(postId);
    fetchPosts();
  } else {
    alert(res.message || 'Gagal mengirim komentar');
  }
}

function handleCommentKeyPress(e, postId) {
  if (e.key === 'Enter') {
    sendComment(postId);
  }
}

async function deletePost(postId) {
  if (!confirm('Yakin ingin menghapus postingan ini?')) return;
  const formData = new FormData();
  formData.append('postId', postId);

  const res = await apiRequest('posts.php?action=delete_post', {
    method: 'POST',
    body: formData
  });

  if (res.status) {
    fetchPosts();
  } else {
    alert(res.message);
  }
}

function focusCommentInput(postId) {
  const input = document.getElementById(`comment-input-${postId}`);
  if (input) input.focus();
}

function sharePost(postId) {
  alert('Tautan postingan SDN 4 Putrajawa telah disalin!');
}

// ----------------------------------------------------
// FRIENDS & COMMUNITY
// ----------------------------------------------------
async function fetchFriendships() {
  const res = await apiRequest('friends.php?action=get_friendships');
  if (res.status) {
    state.friendships = res.data.friendships;
  }
}

function renderFriendsView() {
  const container = document.getElementById('members-list-container');
  if (!container) return;

  const myId = state.currentUser?.id || 0;
  let list = state.allUsers;

  if (state.friendsTabFilter === 'TEMAN') {
    const friendIds = state.friendships
      .filter(f => f.status === 'ACCEPTED')
      .map(f => f.senderId === myId ? f.receiverId : f.senderId);
    list = list.filter(u => friendIds.includes(u.id));
  } else if (state.friendsTabFilter === 'PERMINTAAN') {
    const requesterIds = state.friendships
      .filter(f => f.receiverId === myId && f.status === 'PENDING')
      .map(f => f.senderId);
    list = list.filter(u => requesterIds.includes(u.id));
  }

  if (state.searchQuery.trim()) {
    const q = state.searchQuery.toLowerCase();
    list = list.filter(u => u.fullName.toLowerCase().includes(q) || u.studentNumber.toLowerCase().includes(q));
  }

  if (list.length === 0) {
    container.innerHTML = `
      <div style="text-align: center; padding: 40px; background: #fff; border-radius: 16px;">
        <i class="fa-solid fa-user-group" style="font-size: 40px; color: #1877F2; margin-bottom: 12px;"></i>
        <h4 style="font-weight: 700;">Tidak Ada Data</h4>
        <p style="color: #65676B; font-size: 13px;">Belum ada anggota atau permintaan pertemanan yang cocok.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = list.map(u => {
    const isMe = u.id === myId;
    const friendship = state.friendships.find(f => 
      (f.senderId === myId && f.receiverId === u.id) ||
      (f.senderId === u.id && f.receiverId === myId)
    );

    let actionBtnHtml = '';
    if (!isMe) {
      if (!friendship) {
        actionBtnHtml = `<button class="btn-fb-primary" onclick="sendFriendRequest(${u.id})"><i class="fa-solid fa-user-plus"></i> Tambah Jadi Teman</button>`;
      } else if (friendship.status === 'ACCEPTED') {
        actionBtnHtml = `<button class="btn-fb-outlined" onclick="removeFriendship(${u.id})"><i class="fa-solid fa-check"></i> Berteman</button>`;
      } else if (friendship.status === 'PENDING' && friendship.senderId === myId) {
        actionBtnHtml = `<button class="btn-fb-secondary" onclick="removeFriendship(${u.id})"><i class="fa-solid fa-clock"></i> Batal Permintaan</button>`;
      } else if (friendship.status === 'PENDING' && friendship.receiverId === myId) {
        actionBtnHtml = `<button class="btn-fb-primary" onclick="acceptFriendRequest(${u.id})"><i class="fa-solid fa-check"></i> Konfirmasi</button>`;
      }
    }

    return `
      <div class="member-card">
        <div class="member-card-top" onclick="showUserProfileModal(${u.id})" style="cursor: pointer;" title="Lihat profil / detail">
          ${renderAvatarHtml(u.customPhotoUri, u.avatarColor, u.avatarIcon, 50, 20)}
          <div style="flex: 1;">
            <div style="font-weight: 700; font-size: 15px;">
              ${escapeHtml(u.fullName)}
              ${isMe ? '<span style="background: #1877F2; color: #fff; font-size: 9px; padding: 2px 6px; border-radius: 4px; margin-left: 6px;">Saya</span>' : ''}
            </div>
            <div style="margin-top: 3px;">
              <span class="role-tag ${u.role}">${getRoleLabel(u.role)}</span>
              <span style="font-size: 11px; color: #65676B; margin-left: 6px;">${escapeHtml(u.studentNumber)}</span>
            </div>
            ${u.bio ? `<div style="font-size: 12px; color: #65676B; margin-top: 4px;">"${escapeHtml(u.bio)}"</div>` : ''}
          </div>
        </div>
        ${!isMe ? `
          <div class="member-card-actions">
            ${actionBtnHtml}
            <button class="btn-fb-secondary" onclick="openChatWithUser(${u.id})">
              <i class="fa-solid fa-comment-dots" style="color: #1877F2;"></i> Pesan
            </button>
          </div>
        ` : ''}
      </div>
    `;
  }).join('');
}

async function sendFriendRequest(targetId) {
  const formData = new FormData();
  formData.append('receiverId', targetId);
  const res = await apiRequest('friends.php?action=send_request', { method: 'POST', body: formData });
  if (res.status) {
    await fetchFriendships();
    renderFriendsView();
  }
}

async function acceptFriendRequest(requesterId) {
  const formData = new FormData();
  formData.append('requesterId', requesterId);
  const res = await apiRequest('friends.php?action=accept_request', { method: 'POST', body: formData });
  if (res.status) {
    await fetchFriendships();
    renderFriendsView();
  }
}

async function removeFriendship(targetUserId) {
  const formData = new FormData();
  formData.append('targetUserId', targetUserId);
  const res = await apiRequest('friends.php?action=remove_friendship', { method: 'POST', body: formData });
  if (res.status) {
    await fetchFriendships();
    renderFriendsView();
  }
}

function setFriendsFilter(filter) {
  state.friendsTabFilter = filter;
  document.querySelectorAll('.friends-filter-chip').forEach(c => {
    c.classList.toggle('active', c.dataset.filter === filter);
  });
  renderFriendsView();
}

function handleFriendsSearch(query) {
  state.searchQuery = query;
  renderFriendsView();
}

function showUserProfileModal(userId) {
  const user = state.allUsers.find(u => u.id === Number(userId));
  if (!user) return;

  const myId = state.currentUser?.id || 0;
  const isMe = user.id === myId;
  const friendship = state.friendships.find(f => 
    (f.senderId === myId && f.receiverId === user.id) ||
    (f.senderId === user.id && f.receiverId === myId)
  );

  const modalTitle = document.getElementById('upm-title');
  if (modalTitle) {
    modalTitle.innerHTML = `<i class="fa-solid fa-address-card" style="color: #1877F2;"></i> Profil ${escapeHtml(user.fullName)}`;
  }

  const container = document.getElementById('upm-content');
  if (!container) return;

  let friendActionBtn = '';
  if (!isMe) {
    if (!friendship) {
      friendActionBtn = `
        <button class="btn-fb-primary" style="flex: 1; padding: 10px 14px; font-weight: 700; font-size: 13px;" onclick="sendFriendRequestFromModal(${user.id})">
          <i class="fa-solid fa-user-plus"></i> Tambah Teman
        </button>
      `;
    } else if (friendship.status === 'ACCEPTED') {
      friendActionBtn = `
        <button class="btn-fb-outlined" style="flex: 1; padding: 10px 14px; font-weight: 700; font-size: 13px; color: #10B981; border-color: #10B981;" onclick="removeFriendshipFromModal(${user.id})">
          <i class="fa-solid fa-user-check"></i> Berteman (Hapus)
        </button>
      `;
    } else if (friendship.status === 'PENDING' && friendship.senderId === myId) {
      friendActionBtn = `
        <button class="btn-fb-secondary" style="flex: 1; padding: 10px 14px; font-weight: 700; font-size: 13px;" onclick="removeFriendshipFromModal(${user.id})">
          <i class="fa-solid fa-clock"></i> Batal Permintaan
        </button>
      `;
    } else if (friendship.status === 'PENDING' && friendship.receiverId === myId) {
      friendActionBtn = `
        <button class="btn-fb-primary" style="flex: 1; padding: 10px 14px; font-weight: 700; font-size: 13px;" onclick="acceptFriendRequestFromModal(${user.id})">
          <i class="fa-solid fa-check"></i> Terima Permintaan
        </button>
      `;
    }
  }

  // Count user's posts
  const userPosts = state.posts.filter(p => p.uploaderId === user.id);

  // Friends and Mutual Friends
  const userFriendIds = (state.friendships || [])
    .filter(f => f.status === 'ACCEPTED' && (f.senderId === user.id || f.receiverId === user.id))
    .map(f => f.senderId === user.id ? f.receiverId : f.senderId);
  
  const myFriendIds = (state.friendships || [])
    .filter(f => f.status === 'ACCEPTED' && (f.senderId === myId || f.receiverId === myId))
    .map(f => f.senderId === myId ? f.receiverId : f.senderId);

  const mutualFriendIds = userFriendIds.filter(id => myFriendIds.includes(id));
  const mutualFriends = state.allUsers.filter(u => mutualFriendIds.includes(u.id));
  const userFriends = state.allUsers.filter(u => userFriendIds.includes(u.id));

  container.innerHTML = `
    <div style="text-align: center; padding: 6px 0 12px;">
      <!-- Profile Header Banner Background -->
      <div style="background: linear-gradient(135deg, #1877F2 0%, #0052CC 100%); height: 80px; border-radius: 12px; position: relative; margin-bottom: 42px;">
        <div style="position: absolute; bottom: -36px; left: 50%; transform: translateX(-50%);">
          <div style="border: 3.5px solid #fff; border-radius: 50%; display: inline-block; box-shadow: 0 4px 10px rgba(0,0,0,0.12);">
            ${renderAvatarHtml(user.customPhotoUri, user.avatarColor, user.avatarIcon, 74, 30)}
          </div>
        </div>
      </div>

      <h3 style="font-size: 18px; font-weight: 800; color: #050505; margin: 0 0 4px;">
        ${escapeHtml(user.fullName)}
        ${isMe ? '<span style="background: #1877F2; color: #fff; font-size: 10px; padding: 2px 8px; border-radius: 6px; margin-left: 6px; vertical-align: middle;">Saya</span>' : ''}
      </h3>
      <div style="display: flex; justify-content: center; gap: 8px; align-items: center; margin-bottom: 6px;">
        <span class="role-tag ${user.role}">${getRoleLabel(user.role)}</span>
        <span style="font-size: 12px; color: #65676B; background: #F0F2F5; padding: 2px 8px; border-radius: 6px;">${escapeHtml(user.studentNumber || 'SDN 4 Putrajawa')}</span>
      </div>
      ${user.bio ? `
        <p style="font-size: 12.5px; color: #4B5563; background: #F8FAFC; padding: 8px 14px; border-radius: 10px; border: 1px solid #E2E8F0; margin: 8px 0 12px; font-style: italic;">
          "${escapeHtml(user.bio)}"
        </p>
      ` : ''}

      <!-- Primary Action Buttons -->
      <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px;">
        ${friendActionBtn}
        ${!isMe ? `
          <button class="btn-fb-secondary" style="flex: 1; min-width: 120px; padding: 9px 12px; font-weight: 700; font-size: 12.5px;" onclick="closeModal('userProfileModal'); openChatWithUser(${user.id});">
            <i class="fa-solid fa-comment-dots" style="color: #1877F2;"></i> Kirim Pesan
          </button>
          <button class="btn-fb-secondary" style="padding: 9px 14px; font-weight: 700; font-size: 12.5px; background: #FEF3C7; color: #B45309; border: 1px solid #FCD34D;" onclick="sendWaveMessageToUser(${user.id})">
            👋 Sapa
          </button>
        ` : `
          <button class="btn-fb-secondary" style="flex: 1; padding: 9px 14px; font-weight: 700; font-size: 12.5px;" onclick="closeModal('userProfileModal'); switchTab('profil');">
            <i class="fa-solid fa-user-pen" style="color: #1877F2;"></i> Buka Edit Profil Saya
          </button>
        `}
      </div>

      <!-- Quick Action Pills -->
      <div style="display: flex; gap: 8px; margin-top: 8px;">
        <button class="btn-fb-secondary" style="flex: 1; font-size: 11.5px; padding: 6px 10px;" onclick="filterFeedByAuthor(${user.id})">
          <i class="fa-solid fa-newspaper" style="color: #1877F2;"></i> Lihat di Beranda
        </button>
        <button class="btn-fb-secondary" style="flex: 1; font-size: 11.5px; padding: 6px 10px;" onclick="copyUserProfileDetails(${user.id})">
          <i class="fa-regular fa-copy"></i> Salin Info
        </button>
      </div>

      <!-- Segmented Tabs Navigation -->
      <div style="display: flex; border-bottom: 2px solid #E4E6EB; margin-top: 16px;">
        <button id="upm-tab-btn-karya" onclick="switchUpmTab('karya')" style="flex: 1; padding: 8px; font-weight: 700; font-size: 12.5px; border: none; background: none; border-bottom: 2px solid #1877F2; color: #1877F2; cursor: pointer;">
          Karya (${userPosts.length})
        </button>
        <button id="upm-tab-btn-info" onclick="switchUpmTab('info')" style="flex: 1; padding: 8px; font-weight: 600; font-size: 12.5px; border: none; background: none; color: #65676B; cursor: pointer;">
          Info Lengkap
        </button>
        <button id="upm-tab-btn-teman" onclick="switchUpmTab('teman')" style="flex: 1; padding: 8px; font-weight: 600; font-size: 12.5px; border: none; background: none; color: #65676B; cursor: pointer;">
          Teman (${userFriendIds.length})
        </button>
      </div>

      <!-- TAB CONTENT: KARYA -->
      <div id="upm-tab-content-karya" style="margin-top: 14px; text-align: left;">
        <div style="display: flex; justify-content: space-around; background: #F8FAFC; padding: 10px; border-radius: 10px; margin-bottom: 12px; border: 1px solid #E2E8F0; text-align: center;">
          <div>
            <div style="font-weight: 800; font-size: 16px; color: #1877F2;">${userPosts.length}</div>
            <div style="font-size: 10.5px; color: #65676B;">Postingan</div>
          </div>
          <div>
            <div style="font-weight: 800; font-size: 16px; color: #E11D48;">${userPosts.reduce((acc, p) => acc + (p.likesCount || 0), 0)}</div>
            <div style="font-size: 10.5px; color: #65676B;">Suka Diterima</div>
          </div>
          <div>
            <div style="font-weight: 800; font-size: 16px; color: #10B981;">${userPosts.reduce((acc, p) => acc + (p.viewsCount || 0), 0)}</div>
            <div style="font-size: 10.5px; color: #65676B;">Tayangan</div>
          </div>
        </div>

        ${userPosts.length === 0 ? `
          <p style="font-size: 12px; color: #8A8D91; font-style: italic; text-align: center; padding: 12px 0;">Belum ada postingan dari ${escapeHtml(user.fullName)}.</p>
        ` : `
          <div style="display: flex; flex-direction: column; gap: 6px; max-height: 160px; overflow-y: auto;">
            ${userPosts.map(p => `
              <div style="padding: 8px 10px; background: #F0F2F5; border-radius: 8px; font-size: 12px; display: flex; justify-content: space-between; align-items: center; cursor: pointer;" onclick="closeModal('userProfileModal'); switchTab('beranda'); const el = document.getElementById('post-card-${p.id}'); if (el) el.scrollIntoView({behavior: 'smooth'});">
                <span style="font-weight: 600; color: #050505; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 250px;">
                  <i class="fa-solid fa-image" style="color: #1877F2; margin-right: 4px;"></i>
                  ${escapeHtml(p.title || p.description || 'Foto Kegiatan')}
                </span>
                <span style="font-size: 10px; color: #65676B;">${formatTimestamp(p.timestamp)}</span>
              </div>
            `).join('')}
          </div>
        `}
      </div>

      <!-- TAB CONTENT: INFO LENGKAP -->
      <div id="upm-tab-content-info" style="margin-top: 14px; text-align: left; display: none;">
        <div style="background: #F8FAFC; border-radius: 10px; padding: 12px; border: 1px solid #E2E8F0; font-size: 12.5px; display: flex; flex-direction: column; gap: 8px;">
          <div><span style="color: #65676B; font-size: 11px; display: block;">Nama Lengkap</span><strong>${escapeHtml(user.fullName)}</strong></div>
          <div><span style="color: #65676B; font-size: 11px; display: block;">NISN / NIP</span><strong>${escapeHtml(user.studentNumber || 'Belum diisi')}</strong></div>
          <div><span style="color: #65676B; font-size: 11px; display: block;">Peran</span><strong>${getRoleLabel(user.role)}</strong></div>
          <div><span style="color: #65676B; font-size: 11px; display: block;">Satuan Pendidikan</span><strong>SDN 4 Putrajawa - Kelas IV</strong></div>
          <div><span style="color: #65676B; font-size: 11px; display: block;">Status Akun</span><strong style="color: #10B981;"><i class="fa-solid fa-circle-check"></i> Aktif Terdaftar</strong></div>
        </div>
      </div>

      <!-- TAB CONTENT: TEMAN -->
      <div id="upm-tab-content-teman" style="margin-top: 14px; text-align: left; display: none;">
        ${!isMe && mutualFriends.length > 0 ? `
          <div style="background: #EFF6FF; border: 1px solid #BFDBFE; border-radius: 8px; padding: 8px 12px; margin-bottom: 10px; font-size: 12px; color: #1E40AF;">
            <i class="fa-solid fa-user-group" style="margin-right: 4px;"></i>
            <strong>${mutualFriends.length} Teman Bersama:</strong> ${mutualFriends.map(f => escapeHtml(f.fullName.split(' ')[0])).join(', ')}
          </div>
        ` : ''}

        <div style="font-weight: 700; font-size: 12px; color: #050505; margin-bottom: 6px;">
          Daftar Teman (${userFriends.length}):
        </div>
        ${userFriends.length === 0 ? `
          <p style="font-size: 12px; color: #8A8D91; font-style: italic;">Belum ada pertemanan yang disetujui.</p>
        ` : `
          <div style="display: flex; flex-direction: column; gap: 6px; max-height: 160px; overflow-y: auto;">
            ${userFriends.map(f => `
              <div style="display: flex; align-items: center; gap: 8px; padding: 6px 8px; background: #F8FAFC; border-radius: 8px; cursor: pointer;" onclick="showUserProfileModal(${f.id})">
                ${renderAvatarHtml(f.customPhotoUri, f.avatarColor, f.avatarIcon, 30, 12)}
                <div style="flex: 1;">
                  <div style="font-weight: 600; font-size: 12px;">${escapeHtml(f.fullName)}</div>
                  <div style="font-size: 10px; color: #65676B;">${getRoleLabel(f.role)}</div>
                </div>
              </div>
            `).join('')}
          </div>
        `}
      </div>
    </div>
  `;

  openModal('userProfileModal');
}

async function sendFriendRequestFromModal(targetId) {
  await sendFriendRequest(targetId);
  showUserProfileModal(targetId);
}

async function acceptFriendRequestFromModal(targetId) {
  await acceptFriendRequest(targetId);
  showUserProfileModal(targetId);
}

async function removeFriendshipFromModal(targetId) {
  await removeFriendship(targetId);
  showUserProfileModal(targetId);
}

function switchUpmTab(tabName) {
  const tabs = ['karya', 'info', 'teman'];
  tabs.forEach(t => {
    const btn = document.getElementById(`upm-tab-btn-${t}`);
    const content = document.getElementById(`upm-tab-content-${t}`);
    if (t === tabName) {
      if (btn) {
        btn.style.borderBottom = '2px solid #1877F2';
        btn.style.color = '#1877F2';
        btn.style.fontWeight = '700';
      }
      if (content) content.style.display = 'block';
    } else {
      if (btn) {
        btn.style.borderBottom = 'none';
        btn.style.color = '#65676B';
        btn.style.fontWeight = '600';
      }
      if (content) content.style.display = 'none';
    }
  });
}

async function sendWaveMessageToUser(targetId) {
  const user = state.allUsers.find(u => u.id === Number(targetId));
  if (!user) return;
  const formData = new FormData();
  formData.append('recipientId', targetId);
  formData.append('messageText', `👋 Halo ${user.fullName.split(' ')[0]}! Saya menyapa kamu di SDN 4 Putrajawa.`);

  const res = await apiRequest('messages.php?action=send_message', { method: 'POST', body: formData });
  if (res.status) {
    alert(`👋 Sapaan terkirim ke ${user.fullName.split(' ')[0]}! Pesan percakapan telah dibuat.`);
    showUserProfileModal(targetId);
  }
}

function filterFeedByAuthor(userId) {
  closeModal('userProfileModal');
  switchTab('beranda');
  const user = state.allUsers.find(u => u.id === Number(userId));
  if (!user) return;

  const container = document.getElementById('feed-container');
  if (!container) return;

  const userPosts = state.posts.filter(p => p.uploaderId === Number(userId));
  if (userPosts.length === 0) {
    container.innerHTML = `
      <div style="background: #fff; border-radius: 10px; padding: 24px; text-align: center; border: 1px solid #E4E6EB; margin-top: 12px;">
        <i class="fa-solid fa-folder-open" style="font-size: 36px; color: #1877F2; margin-bottom: 10px;"></i>
        <h4 style="font-weight: 700; color: #050505;">Belum Ada Karya</h4>
        <p style="font-size: 13px; color: #65676B;">${escapeHtml(user.fullName)} belum membagikan karya foto di beranda.</p>
        <button class="btn-fb-secondary" style="margin-top: 12px;" onclick="renderFeed()">Kembali ke Semua Postingan</button>
      </div>
    `;
    return;
  }

  container.innerHTML = `
    <div style="background: #EFF6FF; border: 1px solid #BFDBFE; border-radius: 10px; padding: 12px 16px; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-size: 13px; color: #1E40AF; font-weight: 600;">
        <i class="fa-solid fa-filter"></i> Menampilkan ${userPosts.length} postingan dari <strong>${escapeHtml(user.fullName)}</strong>
      </div>
      <button class="btn-fb-secondary" style="padding: 4px 10px; font-size: 11px;" onclick="renderFeed()">
        Reset Filter
      </button>
    </div>
  ` + userPosts.map(p => renderSinglePost(p)).join('');
}

function copyUserProfileDetails(userId) {
  const user = state.allUsers.find(u => u.id === Number(userId));
  if (!user) return;
  const text = `Profil SDN 4 Putrajawa:\nNama: ${user.fullName}\nPeran: ${user.role}\nNISN/NIP: ${user.studentNumber || '-'}\nBio: ${user.bio || '-'}`;
  navigator.clipboard.writeText(text).then(() => {
    alert(`Profil ${user.fullName} disalin ke papan klip!`);
  }).catch(() => {
    alert(text);
  });
}

// ----------------------------------------------------
// NOTIFICATIONS
// ----------------------------------------------------
async function fetchNotifications(silent = false) {
  const res = await apiRequest('notifications.php?action=get_notifications');
  if (res.status) {
    state.notifications = res.data.notifications;
    state.unreadNotifsCount = res.data.unread_count;

    const badge = document.getElementById('nav-notif-badge');
    if (badge) {
      if (state.unreadNotifsCount > 0) {
        badge.textContent = state.unreadNotifsCount;
        badge.style.display = 'block';
      } else {
        badge.style.display = 'none';
      }
    }

    if (!silent && state.activeTab === 'notifikasi') {
      renderNotificationsView();
    }
  }
}

function renderNotificationsView() {
  const container = document.getElementById('notifs-list-container');
  if (!container) return;

  if (state.notifications.length === 0) {
    container.innerHTML = `
      <div style="text-align: center; padding: 40px; background: #fff; border-radius: 16px;">
        <i class="fa-solid fa-bell-slash" style="font-size: 40px; color: #1877F2; margin-bottom: 12px;"></i>
        <h4 style="font-weight: 700;">Belum Ada Notifikasi</h4>
        <p style="color: #65676B; font-size: 13px;">Notifikasi postingan, pertemanan, dan obrolan akan muncul di sini.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = state.notifications.map(n => {
    const timeFormatted = formatTimestamp(n.createdAt);
    const [iconClass, iconBg] = getNotifIconAndBg(n.type);

    return `
      <div class="notif-item-card ${!n.isRead ? 'unread' : ''}" onclick="handleNotifClick(${n.id}, '${n.type}', ${n.targetId}, ${n.senderUserId})">
        <div style="position: relative;">
          ${renderAvatarHtml(n.senderPhotoUri, n.senderAvatarColor, n.senderAvatarIcon, 44, 16)}
          <div class="notif-badge-icon" style="background-color: ${iconBg};">
            <i class="fa-solid ${iconClass}"></i>
          </div>
        </div>
        <div style="flex: 1;">
          <div class="notif-title">${escapeHtml(n.title)}</div>
          <div class="notif-msg">${escapeHtml(n.message)}</div>
          <div class="notif-time">${timeFormatted}</div>
        </div>
        <div>
          <button class="fb-icon-btn" style="width: 28px; height: 28px; font-size: 11px;" onclick="event.stopPropagation(); deleteNotification(${n.id})">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>
      </div>
    `;
  }).join('');
}

function getNotifIconAndBg(type) {
  switch (type) {
    case 'LIKE': return ['fa-thumbs-up', '#1877F2'];
    case 'COMMENT': return ['fa-comment', '#10B981'];
    case 'FRIEND_REQUEST': return ['fa-user-plus', '#1877F2'];
    case 'FRIEND_ACCEPTED': return ['fa-circle-check', '#10B981'];
    case 'MESSAGE': return ['fa-envelope', '#8B5CF6'];
    default: return ['fa-bullhorn', '#F59E0B'];
  }
}

async function handleNotifClick(notifId, type, targetId, senderUserId) {
  const formData = new FormData();
  formData.append('id', notifId);
  await apiRequest('notifications.php?action=mark_read', { method: 'POST', body: formData });
  fetchNotifications();

  if (type === 'NEW_POST' || type === 'LIKE' || type === 'COMMENT') {
    switchTab('beranda');
  } else if (type === 'MESSAGE' || type === 'FRIEND_REQUEST' || type === 'FRIEND_ACCEPTED') {
    openChatWithUser(senderUserId);
  }
}

async function markAllNotificationsRead() {
  await apiRequest('notifications.php?action=mark_all_read', { method: 'POST' });
  fetchNotifications();
}

async function clearAllNotifications() {
  if (!confirm('Bersihkan semua notifikasi?')) return;
  await apiRequest('notifications.php?action=clear_all', { method: 'POST' });
  fetchNotifications();
}

async function deleteNotification(id) {
  const formData = new FormData();
  formData.append('id', id);
  await apiRequest('notifications.php?action=delete', { method: 'POST', body: formData });
  fetchNotifications();
}

// ----------------------------------------------------
// CHAT MESSENGER
// ----------------------------------------------------
async function renderMessagesView() {
  const container = document.getElementById('messages-view-container');
  if (!container) return;

  if (state.activeChatPartner) {
    await loadChatWith(state.activeChatPartner.id);
  } else {
    // Show conversations list
    const res = await apiRequest('messages.php?action=get_conversations');
    const convs = res.data?.conversations || [];

    let html = `
      <div class="members-header-box" style="margin-bottom: 12px;">
        <h4 style="font-weight: 800; font-size: 18px; color: #050505;"><i class="fa-solid fa-comments" style="color: #1877F2;"></i> Pesan & Obrolan</h4>
        <p style="font-size: 12px; color: #65676B;">Kirim pesan langsung kepada Bapak/Ibu Guru dan teman sekelas SDN 4 Putrajawa.</p>
      </div>
    `;

    if (convs.length === 0) {
      html += `
        <div style="text-align: center; padding: 40px; background: #fff; border-radius: 16px;">
          <i class="fa-solid fa-comment-dots" style="font-size: 40px; color: #1877F2; margin-bottom: 12px;"></i>
          <h4 style="font-weight: 700;">Belum Ada Obrolan</h4>
          <p style="color: #65676B; font-size: 13px;">Buka menu Teman dan klik tombol Pesan untuk memulai obrolan baru.</p>
        </div>
      `;
    } else {
      html += convs.map(c => `
        <div class="member-card" style="cursor: pointer;" onclick="openChatWithUser(${c.user.id})">
          <div class="member-card-top">
            ${renderAvatarHtml(c.user.customPhotoUri, c.user.avatarColor, c.user.avatarIcon, 46, 18)}
            <div style="flex: 1;">
              <div style="font-weight: 700; font-size: 14px;">${escapeHtml(c.user.fullName)}</div>
              <div style="font-size: 12px; color: #65676B; margin-top: 2px;">${escapeHtml(c.lastMessage)}</div>
            </div>
            <div style="text-align: right;">
              <div style="font-size: 10px; color: #65676B;">${formatTimestamp(c.timestamp)}</div>
              ${c.unreadCount > 0 ? `<span style="background: #FA3E3E; color: #fff; font-size: 10px; font-weight: bold; padding: 2px 6px; border-radius: 10px;">${c.unreadCount}</span>` : ''}
            </div>
          </div>
        </div>
      `).join('');
    }
    container.innerHTML = html;
  }
}

async function openChatWithUser(userId) {
  const user = state.allUsers.find(u => u.id === userId);
  if (!user) return;
  state.activeChatPartner = user;
  switchTab('pesan');
  await loadChatWith(userId);
}

async function loadChatWith(partnerId, silent = false) {
  const res = await apiRequest(`messages.php?action=get_chat&partnerId=${partnerId}`);
  if (res.status) {
    state.chatMessages = res.data.messages;
    renderChatWindow(silent);
  }
}

function renderChatWindow(silent = false) {
  const container = document.getElementById('messages-view-container');
  if (!container || !state.activeChatPartner) return;

  const partner = state.activeChatPartner;
  const myId = state.currentUser?.id || 0;

  const messagesHtml = state.chatMessages.map(m => {
    const isMe = m.senderId === myId;
    return `
      <div class="chat-bubble-row ${isMe ? 'me' : 'other'}">
        <div class="chat-bubble">${escapeHtml(m.messageText)}</div>
      </div>
    `;
  }).join('');

  if (silent) {
    const chatBody = document.getElementById('chat-body-scroll');
    if (chatBody) {
      const wasAtBottom = chatBody.scrollHeight - chatBody.scrollTop <= chatBody.clientHeight + 60;
      chatBody.innerHTML = messagesHtml;
      if (wasAtBottom) chatBody.scrollTop = chatBody.scrollHeight;
      return;
    }
  }

  container.innerHTML = `
    <div class="chat-window">
      <div class="chat-header">
        <div style="display: flex; align-items: center; gap: 10px;">
          <button class="fb-icon-btn" style="width: 32px; height: 32px;" onclick="closeChatWindow()">
            <i class="fa-solid fa-arrow-left"></i>
          </button>
          <div onclick="showUserProfileModal(${partner.id})" style="display: flex; align-items: center; gap: 10px; cursor: pointer;" title="Lihat profil ${escapeHtml(partner.fullName)}">
            ${renderAvatarHtml(partner.customPhotoUri, partner.avatarColor, partner.avatarIcon, 38, 14)}
            <div>
              <div style="font-weight: 700; font-size: 14px;">${escapeHtml(partner.fullName)}</div>
              <div style="font-size: 10px; color: #10B981;"><i class="fa-solid fa-circle" style="font-size: 8px;"></i> ${getRoleLabel(partner.role)}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-body" id="chat-body-scroll">
        ${messagesHtml}
      </div>

      <div class="chat-footer">
        <input type="text" class="form-input" id="chat-input-text" placeholder="Ketik pesan..." style="flex: 1; border-radius: 20px;" onkeypress="handleChatKeyPress(event)">
        <button class="btn-fb-primary" style="border-radius: 20px;" onclick="sendChatMessage()"><i class="fa-solid fa-paper-plane"></i></button>
      </div>
    </div>
  `;

  const chatScroll = document.getElementById('chat-body-scroll');
  if (chatScroll) chatScroll.scrollTop = chatScroll.scrollHeight;
}

function closeChatWindow() {
  state.activeChatPartner = null;
  renderMessagesView();
}

async function sendChatMessage() {
  const input = document.getElementById('chat-input-text');
  const text = input.value.trim();
  if (!text || !state.activeChatPartner) return;

  const formData = new FormData();
  formData.append('recipientId', state.activeChatPartner.id);
  formData.append('messageText', text);

  const res = await apiRequest('messages.php?action=send_message', { method: 'POST', body: formData });
  if (res.status) {
    input.value = '';
    await loadChatWith(state.activeChatPartner.id);
  }
}

function handleChatKeyPress(e) {
  if (e.key === 'Enter') sendChatMessage();
}

// ----------------------------------------------------
// QUIZZES & JOURNALS
// ----------------------------------------------------
async function openQuizModal() {
  const res = await apiRequest('quizzes.php?action=get_quizzes');
  if (res.status) {
    state.quizzes = res.data.quizzes;
    const container = document.getElementById('quizzes-modal-list');
    
    container.innerHTML = state.quizzes.map(q => `
      <div class="member-card">
        <div style="display: flex; justify-content: space-between; align-items: flex-start;">
          <div>
            <h4 style="font-weight: 700; color: #1877F2;">${escapeHtml(q.title)}</h4>
            <div style="font-size: 11px; color: #65676B; margin: 2px 0;">Mata Pelajaran: <b>${escapeHtml(q.subject)}</b> &bull; ${q.questions.length} Soal</div>
            <p style="font-size: 12px; color: #050505; margin-top: 6px;">${escapeHtml(q.description)}</p>
          </div>
          ${q.mySubmission ? `
            <div style="background: #DCFCE7; color: #16A34A; font-weight: 800; font-size: 14px; padding: 6px 10px; border-radius: 8px;">
              Nilai: ${q.mySubmission.score}
            </div>
          ` : ''}
        </div>
        <div style="margin-top: 10px;">
          ${q.mySubmission ? `
            <button class="btn-fb-secondary" disabled><i class="fa-solid fa-check-double"></i> Sudah Dikerjakan</button>
          ` : `
            <button class="btn-fb-primary" onclick="startQuiz(${q.id})"><i class="fa-solid fa-play"></i> Kerjakan Kuis Sekarang</button>
          `}
        </div>
      </div>
    `).join('');

    openModal('quizzesModal');
  }
}

function startQuiz(quizId) {
  const quiz = state.quizzes.find(q => q.id === quizId);
  if (!quiz) return;

  const content = document.getElementById('quiz-taking-container');
  content.innerHTML = `
    <h3 style="font-weight: 800; color: #1877F2; margin-bottom: 8px;">${escapeHtml(quiz.title)}</h3>
    <p style="font-size: 12px; color: #65676B; margin-bottom: 16px;">Jawab seluruh pertanyaan dengan teliti.</p>

    <form id="quiz-taking-form" onsubmit="submitQuizAnswers(event, ${quiz.id})">
      ${quiz.questions.map((q, idx) => `
        <div style="background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 12px; padding: 14px; margin-bottom: 12px;">
          <div style="font-weight: 700; font-size: 14px; margin-bottom: 10px;">${idx + 1}. ${escapeHtml(q.questionText)}</div>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            ${q.options.map((opt, optIdx) => `
              <label style="display: flex; align-items: center; gap: 8px; font-size: 13px; cursor: pointer; padding: 6px 10px; background: #fff; border-radius: 8px; border: 1px solid #E2E8F0;">
                <input type="radio" name="q_${idx}" value="${optIdx}" required>
                ${escapeHtml(opt)}
              </label>
            `).join('')}
          </div>
        </div>
      `).join('')}
      <button type="submit" class="btn-fb-primary" style="width: 100%; height: 44px; font-size: 14px;"><i class="fa-solid fa-paper-plane"></i> Kumpulkan Jawaban Kuis</button>
    </form>
  `;

  closeModal('quizzesModal');
  openModal('quizTakingModal');
}

async function submitQuizAnswers(e, quizId) {
  e.preventDefault();
  const quiz = state.quizzes.find(q => q.id === quizId);
  if (!quiz) return;

  const answers = [];
  quiz.questions.forEach((q, idx) => {
    const selected = document.querySelector(`input[name="q_${idx}"]:checked`);
    answers.push(selected ? parseInt(selected.value) : -1);
  });

  const formData = new FormData();
  formData.append('quizId', quizId);
  formData.append('answers', JSON.stringify(answers));

  const res = await apiRequest('quizzes.php?action=submit_quiz', { method: 'POST', body: formData });
  if (res.status) {
    alert(res.message);
    closeModal('quizTakingModal');
    openQuizModal();
  }
}

async function openJournalModal() {
  const res = await apiRequest('journal.php?action=get_journals');
  if (res.status) {
    state.journals = res.data.journals;
    const container = document.getElementById('journals-modal-list');
    
    container.innerHTML = state.journals.map(j => `
      <div class="member-card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h4 style="font-weight: 700; color: #1877F2;">${escapeHtml(j.subject)}</h4>
          <span style="font-size: 11px; background: #E7F3FF; color: #1877F2; padding: 2px 8px; border-radius: 4px; font-weight: bold;">${j.dateString}</span>
        </div>
        <div style="font-size: 13px; font-weight: 600; margin-top: 4px;">${escapeHtml(j.topic)}</div>
        <div style="font-size: 12px; color: #4B5563; margin-top: 4px;">${escapeHtml(j.activity)}</div>
        <div style="margin-top: 8px; padding-top: 8px; border-top: 1px solid #E4E6EB; font-size: 11px; color: #10B981;">
          <i class="fa-solid fa-users"></i> ${escapeHtml(j.attendanceSummary || 'Hadir Lengkap')}
        </div>
      </div>
    `).join('');

    openModal('journalsModal');
  }
}

// ----------------------------------------------------
// PROFILE & ACCOUNT SWITCHING
// ----------------------------------------------------
function renderProfileView() {
  const container = document.getElementById('profile-view-container');
  if (!container || !state.currentUser) return;

  const u = state.currentUser;
  const myPosts = state.posts.filter(p => p.uploaderId === u.id);
  const myFriends = state.friendships.filter(f => f.status === 'ACCEPTED');

  container.innerHTML = `
    <div style="background: linear-gradient(135deg, #1877F2 0%, #0052CC 100%); border-radius: 16px 16px 0 0; height: 110px; position: relative;">
      <div style="position: absolute; bottom: -35px; left: 20px;">
        ${renderAvatarHtml(u.customPhotoUri, u.avatarColor, u.avatarIcon, 70, 28, 'border: 3px solid #fff; box-shadow: 0 4px 12px rgba(0,0,0,0.15);')}
      </div>
    </div>
    <div class="fb-post-card" style="border-radius: 0 0 16px 16px; padding: 45px 20px 20px 20px;">
      <h2 style="font-weight: 800; font-size: 20px;">${escapeHtml(u.fullName)}</h2>
      <div style="margin-top: 4px;">
        <span class="role-tag ${u.role}">${getRoleLabel(u.role)}</span>
        <span style="font-size: 12px; color: #65676B; margin-left: 8px;">${escapeHtml(u.studentNumber)}</span>
      </div>
      ${u.bio ? `<p style="font-size: 13px; color: #4B5563; margin-top: 10px;">"${escapeHtml(u.bio)}"</p>` : ''}

      <div style="display: flex; gap: 16px; margin-top: 16px; padding-top: 14px; border-top: 1px solid #E4E6EB;">
        <div><b>${myPosts.length}</b> <span style="color: #65676B; font-size: 12px;">Postingan</span></div>
        <div><b>${myFriends.length}</b> <span style="color: #65676B; font-size: 12px;">Teman</span></div>
      </div>

      <div style="display: flex; gap: 8px; margin-top: 16px;">
        <button class="btn-fb-primary" style="flex: 1;" onclick="prepareEditProfileModal()"><i class="fa-solid fa-pen"></i> Edit Profil & Foto</button>
        <button class="btn-fb-secondary" style="color: #FA3E3E; border-color: #FECACA; background: #FEF2F2;" onclick="handleLogout()"><i class="fa-solid fa-arrow-right-from-bracket"></i> Keluar</button>
      </div>
    </div>
  `;
}

// ----------------------------------------------------
// HELPERS
// ----------------------------------------------------
function renderAvatarHtml(photoUri, color, icon, size = 40, fontSize = 16, extraStyle = '') {
  if (photoUri && photoUri.trim() !== '') {
    return `<img src="${photoUri}" alt="Avatar" class="avatar-circle" style="width: ${size}px; height: ${size}px; border-radius: 50%; object-fit: cover; flex-shrink: 0; display: inline-block; ${extraStyle}">`;
  }
  return `<div class="avatar-circle" style="width: ${size}px; height: ${size}px; font-size: ${fontSize}px; background-color: ${getHexColor(color)}; flex-shrink: 0; ${extraStyle}"><i class="fa-solid ${getAvatarIcon(icon)}"></i></div>`;
}
function getHexColor(colorStr) {
  if (!colorStr) return '#1877F2';
  if (colorStr.startsWith('0xFF')) return '#' + colorStr.substring(4);
  if (colorStr.startsWith('#')) return colorStr;
  return '#1877F2';
}

function getAvatarIcon(iconStr) {
  switch (iconStr) {
    case 'teacher': return 'fa-chalkboard-user';
    case 'star': return 'fa-star';
    case 'palette': return 'fa-palette';
    case 'sports': return 'fa-futbol';
    case 'music': return 'fa-music';
    default: return 'fa-user-graduate';
  }
}

function getRoleLabel(role) {
  switch (role) {
    case 'WALI_KELAS': return 'Guru';
    default: return 'Siswa';
  }
}

function formatTimestamp(ts) {
  if (!ts) return 'Baru saja';
  const diff = Date.now() - ts;
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return 'Baru saja';
  if (mins < 60) return `${mins} mnt lalu`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours} jam lalu`;
  const days = Math.floor(hours / 24);
  if (days < 7) return `${days} hr lalu`;
  return new Date(ts).toLocaleDateString('id-ID');
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function openModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.add('active');
}

function closeModal(id) {
  const m = document.getElementById(id);
  if (m) m.classList.remove('active');
}

// Close modal when clicking overlay background
document.addEventListener('click', (e) => {
  if (e.target && e.target.classList && e.target.classList.contains('fb-modal-overlay')) {
    e.target.classList.remove('active');
  }
});
