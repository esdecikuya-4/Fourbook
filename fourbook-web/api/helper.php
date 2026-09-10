<?php
// fourbook-web/api/helper.php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

session_start();

// Automatically parse JSON request body if sent from Android Retrofit/REST client
$rawInput = file_get_contents('php://input');
if (!empty($rawInput)) {
    $jsonData = json_decode($rawInput, true);
    if (is_array($jsonData)) {
        foreach ($jsonData as $key => $value) {
            if (!isset($_POST[$key])) {
                $_POST[$key] = $value;
            }
        }
    }
}

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
    $response = [
        'status' => (bool)$status,
        'success' => (bool)$status,
        'message' => $message,
        'data' => $data
    ];
    if (is_array($data)) {
        if (isset($data['user'])) {
            $response['user'] = $data['user'];
        }
        if (isset($data['posts'])) {
            $response['posts'] = $data['posts'];
        }
    }
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
    exit;
}

function get_current_user_session() {
    if (isset($_SESSION['user'])) {
        return $_SESSION['user'];
    }
    return null;
}
