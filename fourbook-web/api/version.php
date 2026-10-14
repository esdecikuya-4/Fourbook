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
        'versionCode' => 5,
        'versionName' => '1.0.5',
        'apkUrl' => 'https://balallica.my.id/fourbook.apk',
        'apkSizeMb' => '24 MB',
        'releaseNotes' => "1. Pemutaran video asli: Video rekaman HP dan YouTube berputar dengan gambar asli & suara jernih penuh tanpa animasi 45 detik.\n2. Posting video lokal HP tersimpan dan tersinkronisasi otomatis ke server.\n3. Tombol Tempel Link YouTube di Android berfungsi instan & live preview.\n4. Pembaruan otomatis versi aplikasi Fourbook Android v1.0.5.",
        'releaseDate' => '2026-10-14',
        'forceUpdate' => true
    ];
    @file_put_contents($dataFile, json_encode($defaultData, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES));
}

$content = @file_get_contents($dataFile);
$versionData = json_decode($content, true);

if (!$versionData) {
    $versionData = [
        'versionCode' => 5,
        'versionName' => '1.0.5',
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
