<?php
require_once __DIR__ . '/helper.php';

$action = $_GET['action'] ?? $_POST['action'] ?? '';
$users = get_json_data('users.json');

if ($action === 'get_current') {
    $user = get_current_user_session();
    json_response(true, ['user' => $user, 'all_users' => $users]);
}

if ($action === 'switch_user') {
    $userId = (int)($_POST['userId'] ?? 0);
    foreach ($users as $u) {
        if ($u['id'] === $userId) {
            $_SESSION['user'] = $u;
            json_response(true, ['user' => $u], 'Berhasil beralih akun ke ' . $u['fullName']);
        }
    }
    json_response(false, [], 'User tidak ditemukan');
}

if ($action === 'login') {
    $username = trim($_POST['username'] ?? '');
    $password = trim($_POST['password'] ?? '');
    foreach ($users as $u) {
        if (strtolower($u['username']) === strtolower($username) && $u['password'] === $password) {
            $_SESSION['user'] = $u;
            json_response(true, ['user' => $u], 'Selamat datang, ' . $u['fullName']);
        }
    }
    json_response(false, [], 'Username atau kata sandi salah');
}

if ($action === 'logout') {
    unset($_SESSION['user']);
    session_destroy();
    json_response(true, [], 'Berhasil keluar');
}

if ($action === 'register') {
    $fullName = trim($_POST['fullName'] ?? '');
    $username = trim($_POST['username'] ?? '');
    $password = trim($_POST['password'] ?? '');
    $studentNumber = trim($_POST['studentNumber'] ?? '');
    $role = $_POST['role'] ?? 'MURID';
    $bio = trim($_POST['bio'] ?? '');

    if (empty($fullName) || empty($username) || empty($password)) {
        json_response(false, [], 'Nama lengkap, username, dan password wajib diisi');
    }

    foreach ($users as $u) {
        if (strtolower($u['username']) === strtolower($username)) {
            json_response(false, [], 'Username sudah digunakan, pilih username lain');
        }
    }

    $colors = ['0xFF1877F2', '0xFFD97706', '0xFFEC4899', '0xFF3B82F6', '0xFF10B981', '0xFF8B5CF6'];
    $randomColor = $colors[array_rand($colors)];

    $newId = empty($users) ? 1 : max(array_column($users, 'id')) + 1;
    $newUser = [
        'id' => $newId,
        'username' => $username,
        'password' => $password,
        'fullName' => $fullName,
        'role' => $role,
        'studentNumber' => $studentNumber,
        'avatarColor' => $randomColor,
        'avatarIcon' => 'student',
        'bio' => $bio,
        'customPhotoUri' => ''
    ];

    $users[] = $newUser;
    save_json_data('users.json', $users);
    $_SESSION['user'] = $newUser;

    // Send welcome notification
    $notifications = get_json_data('notifications.json');
    $notifications[] = [
        'id' => empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1,
        'recipientUserId' => $newId,
        'senderUserId' => 1,
        'senderName' => 'Teten Kurniawan, S.Pd.',
        'senderRole' => 'WALI_KELAS',
        'senderAvatarColor' => '0xFF1877F2',
        'senderAvatarIcon' => 'teacher',
        'type' => 'NEW_POST',
        'title' => 'Selamat Datang di Fourbook!',
        'message' => 'Selamat bergabung di media sosial resmi SDN 4 Putrajawa. Bagikan karya dan prestasimu!',
        'targetId' => 0,
        'createdAt' => round(microtime(true) * 1000),
        'isRead' => false
    ];
    save_json_data('notifications.json', $notifications);

    json_response(true, ['user' => $newUser], 'Pendaftaran akun berhasil!');
}

if ($action === 'update_profile') {
    $currentUser = get_current_user_session();
    if (!$currentUser) json_response(false, [], 'Belum login');

    $fullName = trim($_POST['fullName'] ?? $currentUser['fullName']);
    $studentNumber = trim($_POST['studentNumber'] ?? $currentUser['studentNumber']);
    $bio = trim($_POST['bio'] ?? $currentUser['bio']);
    $avatarColor = $_POST['avatarColor'] ?? $currentUser['avatarColor'];
    $avatarIcon = $_POST['avatarIcon'] ?? $currentUser['avatarIcon'];
    $customPhotoUri = $_POST['customPhotoUri'] ?? $currentUser['customPhotoUri'];

    foreach ($users as &$u) {
        if ($u['id'] === $currentUser['id']) {
            $u['fullName'] = $fullName;
            $u['studentNumber'] = $studentNumber;
            $u['bio'] = $bio;
            $u['avatarColor'] = $avatarColor;
            $u['avatarIcon'] = $avatarIcon;
            if (!empty($customPhotoUri)) $u['customPhotoUri'] = $customPhotoUri;
            $_SESSION['user'] = $u;
            $currentUser = $u;
            break;
        }
    }
    save_json_data('users.json', $users);
    json_response(true, ['user' => $currentUser], 'Profil berhasil diperbarui');
}

json_response(false, [], 'Aksi tidak dikenal');
