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
        'versionCode' => 4,
        'versionName' => '1.0.4',
        'apkUrl' => 'https://balallica.my.id/fourbook.apk',
        'apkSizeMb' => '24 MB',
        'releaseNotes' => "1. Pemutaran video rekaman/lokal HP kini bersuara dan gambar asli dengan durasi penuh (bukan animasi 45 detik).\n2. Pemutar video link YouTube aktif langsung di aplikasi Android dengan suara jernih.\n3. Posting link YouTube langsung di aplikasi Android (tombol 'Tempel Link' instan & live preview).",
        'releaseDate' => '2026-10-14',
        'forceUpdate' => false
    ];
    @file_put_contents($dataFile, json_encode($defaultData, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES));
}

$content = @file_get_contents($dataFile);
$versionData = json_decode($content, true);

if (!$versionData) {
    $versionData = [
        'versionCode' => 4,
        'versionName' => '1.0.4',
        'apkUrl' => 'https://balallica.my.id/fourbook.apk',
        'apkSizeMb' => '24 MB',
        'releaseNotes' => 'Pembaruan SDN 4 Putrajawa v1.0.4',
        'releaseDate' => '2026-10-14',
        'forceUpdate' => false
    ];
}

echo json_encode([
    'status' => true,
    'success' => true,
    'data' => $versionData
], JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT);
