<?php
require_once __DIR__ . '/helper.php';

$action = $_GET['action'] ?? $_POST['action'] ?? '';
$messages = get_json_data('messages.json');
$currentUser = get_current_user_session();

if ($action === 'get_conversations') {
    if (!$currentUser) json_response(true, ['conversations' => []]);
    $myId = $currentUser['id'];

    // Group messages by partner ID
    $partners = [];
    foreach ($messages as $m) {
        if ($m['senderId'] === $myId || $m['recipientId'] === $myId) {
            $partnerId = ($m['senderId'] === $myId) ? $m['recipientId'] : $m['senderId'];
            if (!isset($partners[$partnerId]) || $m['timestamp'] > $partners[$partnerId]['timestamp']) {
                $partners[$partnerId] = $m;
            }
        }
    }

    $users = get_json_data('users.json');
    $userMap = [];
    foreach ($users as $u) { $userMap[$u['id']] = $u; }

    $convList = [];
    foreach ($partners as $partnerId => $lastMsg) {
        if (isset($userMap[$partnerId])) {
            $u = $userMap[$partnerId];
            $unreadCount = count(array_filter($messages, function($msg) use ($myId, $partnerId) {
                return $msg['senderId'] === $partnerId && $msg['recipientId'] === $myId && !($msg['isRead'] ?? false);
            }));

            $convList[] = [
                'user' => $u,
                'lastMessage' => $lastMsg['messageText'],
                'timestamp' => $lastMsg['timestamp'],
                'unreadCount' => $unreadCount
            ];
        }
    }

    usort($convList, function($a, $b) {
        return ($b['timestamp'] ?? 0) - ($a['timestamp'] ?? 0);
    });

    json_response(true, ['conversations' => $convList]);
}

if ($action === 'get_chat') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $partnerId = (int)($_GET['partnerId'] ?? $_POST['partnerId'] ?? 0);
    $myId = $currentUser['id'];

    $chat = array_filter($messages, function($m) use ($myId, $partnerId) {
        return ($m['senderId'] === $myId && $m['recipientId'] === $partnerId) ||
               ($m['senderId'] === $partnerId && $m['recipientId'] === $myId);
    });

    // Mark messages from partner as read
    $updated = false;
    foreach ($messages as &$m) {
        if ($m['senderId'] === $partnerId && $m['recipientId'] === $myId && !($m['isRead'] ?? false)) {
            $m['isRead'] = true;
            $updated = true;
        }
    }
    if ($updated) save_json_data('messages.json', $messages);

    usort($chat, function($a, $b) {
        return ($a['timestamp'] ?? 0) - ($b['timestamp'] ?? 0);
    });

    json_response(true, ['messages' => array_values($chat)]);
}

if ($action === 'send_message') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $recipientId = (int)($_POST['recipientId'] ?? 0);
    $text = trim($_POST['messageText'] ?? '');

    if (empty($text)) json_response(false, [], 'Pesan tidak boleh kosong');

    $users = get_json_data('users.json');
    $recipient = null;
    foreach ($users as $u) {
        if ($u['id'] === $recipientId) {
            $recipient = $u;
            break;
        }
    }

    if (!$recipient) json_response(false, [], 'Penerima tidak ditemukan');

    $newId = empty($messages) ? 1 : max(array_column($messages, 'id')) + 1;
    $newMsg = [
        'id' => $newId,
        'senderId' => $currentUser['id'],
        'senderName' => $currentUser['fullName'],
        'senderRole' => $currentUser['role'],
        'senderAvatarColor' => $currentUser['avatarColor'],
        'senderAvatarIcon' => $currentUser['avatarIcon'],
        'senderPhotoUri' => $currentUser['customPhotoUri'] ?? '',
        'recipientId' => $recipient['id'],
        'recipientName' => $recipient['fullName'],
        'recipientPhotoUri' => $recipient['customPhotoUri'] ?? '',
        'messageText' => $text,
        'timestamp' => round(microtime(true) * 1000),
        'isRead' => false
    ];

    $messages[] = $newMsg;
    save_json_data('messages.json', $messages);

    // Send notification to recipient
    $notifications = get_json_data('notifications.json');
    $notifId = empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1;
    $notifications[] = [
        'id' => $notifId,
        'recipientUserId' => $recipient['id'],
        'senderUserId' => $currentUser['id'],
        'senderName' => $currentUser['fullName'],
        'senderRole' => $currentUser['role'],
        'senderAvatarColor' => $currentUser['avatarColor'],
        'senderAvatarIcon' => $currentUser['avatarIcon'],
        'senderPhotoUri' => $currentUser['customPhotoUri'] ?? '',
        'type' => 'MESSAGE',
        'title' => 'Pesan Baru dari ' . $currentUser['fullName'],
        'message' => mb_substr($text, 0, 50),
        'targetId' => $currentUser['id'],
        'createdAt' => round(microtime(true) * 1000),
        'isRead' => false
    ];
    save_json_data('notifications.json', $notifications);

    json_response(true, ['message' => $newMsg], 'Pesan terkirim');
}

json_response(false, [], 'Aksi tidak dikenal');
