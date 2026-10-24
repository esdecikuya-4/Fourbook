<?php
// fourbook-web/api/version.php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

$dataFile = __DIR__ . '/../data/app_version.json';

// Return current version metadata
if (!file_exists($dataFile)) {
    $defaultData = [
        'versionCode' => 6,
        'versionName' => '1.0.6',
        'apkUrl' => 'https://balallica.my.id/fourbook.apk',
        'apkSizeMb' => '25 MB',
        'releaseNotes' => "1. Keamanan Website: Pengunjung web balallica.my.id wajib login/daftar terlebih dahulu sebelum masuk (tidak lagi otomatis masuk sebagai admin).\n2. Foto Profil Langsung Muncul: Foto asli pengguna langsung terlihat di postingan feed dan komentar tanpa harus diklik.\n3. Avatar kartun hanya digunakan untuk akun yang belum mengunggah foto profil asli.\n4. Sinkronisasi foto profil real-time di semua postingan.",
        'releaseDate' => '2026-10-21',
        'forceUpdate' => true
    ];
    @file_put_contents($dataFile, json_encode($defaultData, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES));
}

$content = @file_get_contents($dataFile);
$versionData = json_decode($content, true);

if (!$versionData) {
    $versionData = [
        'versionCode' => 6,
        'versionName' => '1.0.6',
        'apkUrl' => 'https://balallica.my.id/fourbook.apk',
        'apkSizeMb' => '24 MB',
        'releaseNotes' => 'Pembaruan SDN 4 Putrajawa v1.0.5',
        'releaseDate' => '2026-10-14',
        'forceUpdate' => true
    ];
}

echo json_encode([
    'status' => true,
    'success' => true,
    'data' => $versionData
], JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT);
