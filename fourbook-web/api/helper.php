<?php
// fourbook-web/api/helper.php
header('Content-Type: application/json; charset=utf-8');
session_start();

function get_json_data($filename) {
    $filepath = __DIR__ . '/../data/' . $filename;
    if (!file_exists($filepath)) {
        return [];
    }
    $content = file_get_contents($filepath);
    $data = json_decode($content, true);
    return is_array($data) ? $data : [];
}

function save_json_data($filename, $data) {
    $filepath = __DIR__ . '/../data/' . $filename;
    $json = json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    return file_put_contents($filepath, $json, LOCK_EX);
}

function json_response($status, $data = [], $message = '') {
    echo json_encode([
        'status' => $status,
        'message' => $message,
        'data' => $data
    ]);
    exit;
}

function get_current_user_session() {
    if (isset($_SESSION['user'])) {
        return $_SESSION['user'];
    }
    return null;
}
