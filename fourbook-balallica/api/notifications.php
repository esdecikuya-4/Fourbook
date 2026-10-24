<?php
require_once __DIR__ . '/helper.php';

$action = $_GET['action'] ?? $_POST['action'] ?? '';
$notifications = get_json_data('notifications.json');
$currentUser = get_current_user_session();

if ($action === 'get_notifications') {
    if (!$currentUser) json_response(true, ['notifications' => [], 'unread_count' => 0]);
    $myId = $currentUser['id'];
    $users = get_json_data('users.json');
    $userMap = [];
    foreach ($users as $u) {
        $userMap[$u['id']] = $u;
    }

    // Filter notifications for this user (recipientId == myId OR recipientId == 0)
    $myNotifs = array_filter($notifications, function($n) use ($myId) {
        if ($n['senderUserId'] === $myId) return false; // don't notify self
        return $n['recipientUserId'] === $myId || $n['recipientUserId'] === 0;
    });

    foreach ($myNotifs as &$n) {
        if (isset($userMap[$n['senderUserId']])) {
            $n['senderPhotoUri'] = $userMap[$n['senderUserId']]['customPhotoUri'] ?? '';
            $n['senderAvatarColor'] = $userMap[$n['senderUserId']]['avatarColor'] ?? $n['senderAvatarColor'];
            $n['senderAvatarIcon'] = $userMap[$n['senderUserId']]['avatarIcon'] ?? $n['senderAvatarIcon'];
        }
    }

    // Sort newest first
    usort($myNotifs, function($a, $b) {
        return ($b['createdAt'] ?? 0) - ($a['createdAt'] ?? 0);
    });

    $unread = count(array_filter($myNotifs, function($n) { return !$n['isRead']; }));

    json_response(true, [
        'notifications' => array_values($myNotifs),
        'unread_count' => $unread
    ]);
}

if ($action === 'mark_read') {
    $notifId = (int)($_POST['id'] ?? 0);
    foreach ($notifications as &$n) {
        if ($n['id'] === $notifId) {
            $n['isRead'] = true;
            break;
        }
    }
    save_json_data('notifications.json', $notifications);
    json_response(true, [], 'Ditandai telah dibaca');
}

if ($action === 'mark_all_read') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $myId = $currentUser['id'];
    foreach ($notifications as &$n) {
        if ($n['recipientUserId'] === $myId || $n['recipientUserId'] === 0) {
            $n['isRead'] = true;
        }
    }
    save_json_data('notifications.json', $notifications);
    json_response(true, [], 'Semua notifikasi ditandai telah dibaca');
}

if ($action === 'delete') {
    $notifId = (int)($_POST['id'] ?? 0);
    $filtered = array_filter($notifications, function($n) use ($notifId) {
        return $n['id'] !== $notifId;
    });
    save_json_data('notifications.json', array_values($filtered));
    json_response(true, [], 'Notifikasi dihapus');
}

if ($action === 'clear_all') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $myId = $currentUser['id'];
    $filtered = array_filter($notifications, function($n) use ($myId) {
        return !($n['recipientUserId'] === $myId || $n['recipientUserId'] === 0);
    });
    save_json_data('notifications.json', array_values($filtered));
    json_response(true, [], 'Semua notifikasi dibersihkan');
}

json_response(false, [], 'Aksi tidak dikenal');
