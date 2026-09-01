<?php
require_once __DIR__ . '/helper.php';

$action = $_GET['action'] ?? $_POST['action'] ?? '';
$friendships = get_json_data('friendships.json');
$currentUser = get_current_user_session();

if ($action === 'get_friendships') {
    if (!$currentUser) json_response(true, ['friendships' => []]);
    $myId = $currentUser['id'];
    $myList = array_filter($friendships, function($f) use ($myId) {
        return $f['senderId'] === $myId || $f['receiverId'] === $myId;
    });
    json_response(true, ['friendships' => array_values($myList)]);
}

if ($action === 'send_request') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $receiverId = (int)($_POST['receiverId'] ?? 0);
    if ($receiverId === $currentUser['id']) json_response(false, [], 'Tidak dapat menambahkan diri sendiri');

    // Check existing
    foreach ($friendships as $f) {
        if (($f['senderId'] === $currentUser['id'] && $f['receiverId'] === $receiverId) ||
            ($f['senderId'] === $receiverId && $f['receiverId'] === $currentUser['id'])) {
            json_response(false, [], 'Permintaan pertemanan sudah ada');
        }
    }

    $newId = empty($friendships) ? 1 : max(array_column($friendships, 'id')) + 1;
    $newFriendship = [
        'id' => $newId,
        'senderId' => $currentUser['id'],
        'receiverId' => $receiverId,
        'status' => 'PENDING',
        'createdAt' => round(microtime(true) * 1000)
    ];
    $friendships[] = $newFriendship;
    save_json_data('friendships.json', $friendships);

    // Send notification
    $notifications = get_json_data('notifications.json');
    $notifId = empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1;
    $notifications[] = [
        'id' => $notifId,
        'recipientUserId' => $receiverId,
        'senderUserId' => $currentUser['id'],
        'senderName' => $currentUser['fullName'],
        'senderRole' => $currentUser['role'],
        'senderAvatarColor' => $currentUser['avatarColor'],
        'senderAvatarIcon' => $currentUser['avatarIcon'],
        'type' => 'FRIEND_REQUEST',
        'title' => 'Permintaan Pertemanan Baru',
        'message' => $currentUser['fullName'] . ' mengirimkan permintaan pertemanan kepada Anda.',
        'targetId' => $currentUser['id'],
        'createdAt' => round(microtime(true) * 1000),
        'isRead' => false
    ];
    save_json_data('notifications.json', $notifications);

    json_response(true, ['friendship' => $newFriendship], 'Permintaan pertemanan dikirim');
}

if ($action === 'accept_request') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $requesterId = (int)($_POST['requesterId'] ?? 0);

    foreach ($friendships as &$f) {
        if ($f['senderId'] === $requesterId && $f['receiverId'] === $currentUser['id']) {
            $f['status'] = 'ACCEPTED';
            save_json_data('friendships.json', $friendships);

            // Send notification to requester
            $notifications = get_json_data('notifications.json');
            $notifId = empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1;
            $notifications[] = [
                'id' => $notifId,
                'recipientUserId' => $requesterId,
                'senderUserId' => $currentUser['id'],
                'senderName' => $currentUser['fullName'],
                'senderRole' => $currentUser['role'],
                'senderAvatarColor' => $currentUser['avatarColor'],
                'senderAvatarIcon' => $currentUser['avatarIcon'],
                'type' => 'FRIEND_ACCEPTED',
                'title' => 'Permintaan Diterima',
                'message' => $currentUser['fullName'] . ' telah menerima permintaan pertemanan Anda.',
                'targetId' => $currentUser['id'],
                'createdAt' => round(microtime(true) * 1000),
                'isRead' => false
            ];
            save_json_data('notifications.json', $notifications);

            json_response(true, ['friendship' => $f], 'Permintaan pertemanan diterima');
        }
    }
    json_response(false, [], 'Permintaan tidak ditemukan');
}

if ($action === 'remove_friendship') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $targetUserId = (int)($_POST['targetUserId'] ?? 0);

    $filtered = array_filter($friendships, function($f) use ($currentUser, $targetUserId) {
        $isRelated = ($f['senderId'] === $currentUser['id'] && $f['receiverId'] === $targetUserId) ||
                     ($f['senderId'] === $targetUserId && $f['receiverId'] === $currentUser['id']);
        return !$isRelated;
    });

    save_json_data('friendships.json', array_values($filtered));
    json_response(true, [], 'Pertemanan berhasil dihapus/dibatalkan');
}

json_response(false, [], 'Aksi tidak dikenal');
