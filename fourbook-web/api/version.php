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
        'versionCode' => 3,
        'versionName' => '1.0.3',
        'apkUrl' => 'https://balallica.my.id/fourbook.apk',
        'apkSizeMb' => '24 MB',
        'releaseNotes' => 'Pembaruan SDN 4 Putrajawa: Pemutar video YouTube langsung aktif dengan suara dan durasi penuh.',
        'releaseDate' => '2026-10-14',
        'forceUpdate' => false
    ];
    @file_put_contents($dataFile, json_encode($defaultData, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES));
}

$content = @file_get_contents($dataFile);
$versionData = json_decode($content, true);

if (!$versionData) {
    $versionData = [
        'versionCode' => 3,
        'versionName' => '1.0.3',
        'apkUrl' => 'https://balallica.my.id/fourbook.apk',
        'apkSizeMb' => '24 MB',
        'releaseNotes' => 'Pembaruan SDN 4 Putrajawa',
        'releaseDate' => '2026-10-14',
        'forceUpdate' => false
    ];
}

echo json_encode([
    'status' => true,
    'success' => true,
    'data' => $versionData
], JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT);
