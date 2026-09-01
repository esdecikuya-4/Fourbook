<?php
require_once __DIR__ . '/helper.php';

$action = $_GET['action'] ?? $_POST['action'] ?? '';
$posts = get_json_data('posts.json');
$currentUser = get_current_user_session();

if ($action === 'get_posts') {
    // Sort descending by timestamp
    usort($posts, function($a, $b) {
        return ($b['timestamp'] ?? 0) - ($a['timestamp'] ?? 0);
    });
    json_response(true, ['posts' => $posts]);
}

if ($action === 'create_post') {
    if (!$currentUser) json_response(false, [], 'Harus login terlebih dahulu');

    $title = trim($_POST['title'] ?? '');
    $description = trim($_POST['description'] ?? '');
    $category = $_POST['category'] ?? 'Umum';
    $postType = $_POST['postType'] ?? 'TEXT_STATUS';
    $mediaUri = trim($_POST['mediaUri'] ?? '');

    // Handle file upload if any
    if (isset($_FILES['mediaFile']) && $_FILES['mediaFile']['error'] === UPLOAD_ERR_OK) {
        $uploadsDir = __DIR__ . '/../uploads';
        if (!file_exists($uploadsDir)) {
            mkdir($uploadsDir, 0755, true);
        }
        $fileName = time() . '_' . preg_replace('/[^a-zA-Z0-9._-]/', '', $_FILES['mediaFile']['name']);
        $targetPath = $uploadsDir . '/' . $fileName;
        if (move_uploaded_file($_FILES['mediaFile']['tmp_name'], $targetPath)) {
            $mediaUri = 'uploads/' . $fileName;
            $postType = 'PHOTO';
        }
    }

    if (empty($title) && empty($description) && empty($mediaUri)) {
        json_response(false, [], 'Konten postingan tidak boleh kosong');
    }

    $newId = empty($posts) ? 1 : max(array_column($posts, 'id')) + 1;
    $newPost = [
        'id' => $newId,
        'uploaderId' => $currentUser['id'],
        'uploaderName' => $currentUser['fullName'],
        'uploaderRole' => $currentUser['role'],
        'uploaderAvatarColor' => $currentUser['avatarColor'],
        'uploaderAvatarIcon' => $currentUser['avatarIcon'],
        'title' => $title,
        'description' => $description,
        'mediaUri' => $mediaUri,
        'mediaType' => !empty($mediaUri) ? 'PHOTO' : 'NONE',
        'postType' => $postType,
        'category' => $category,
        'likeCount' => 0,
        'timestamp' => round(microtime(true) * 1000),
        'reactions' => [],
        'comments' => []
    ];

    array_unshift($posts, $newPost);
    save_json_data('posts.json', $posts);

    // Broadcast notification to other users
    $notifications = get_json_data('notifications.json');
    $notifId = empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1;
    $notifications[] = [
        'id' => $notifId,
        'recipientUserId' => 0, // 0 means all users
        'senderUserId' => $currentUser['id'],
        'senderName' => $currentUser['fullName'],
        'senderRole' => $currentUser['role'],
        'senderAvatarColor' => $currentUser['avatarColor'],
        'senderAvatarIcon' => $currentUser['avatarIcon'],
        'type' => 'NEW_POST',
        'title' => 'Postingan Baru dari ' . ($currentUser['role'] === 'WALI_KELAS' ? 'Wali Kelas' : $currentUser['fullName']),
        'message' => $currentUser['fullName'] . ' memposting: "' . mb_substr($title ?: $description, 0, 50) . '..."',
        'targetId' => $newId,
        'createdAt' => round(microtime(true) * 1000),
        'isRead' => false
    ];
    save_json_data('notifications.json', $notifications);

    json_response(true, ['post' => $newPost], 'Postingan berhasil dibagikan');
}

if ($action === 'toggle_reaction') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $postId = (int)($_POST['postId'] ?? 0);
    $reactionType = $_POST['reaction'] ?? 'LIKE'; // LIKE, LOVE, AWESOME, IDEA, FIRE, LAUGH

    foreach ($posts as &$p) {
        if ($p['id'] === $postId) {
            if (!isset($p['reactions'])) $p['reactions'] = [];
            
            // Check if already reacted with this type
            $userList = $p['reactions'][$reactionType] ?? [];
            if (in_array($currentUser['id'], $userList)) {
                // Remove reaction
                $p['reactions'][$reactionType] = array_values(array_diff($userList, [$currentUser['id']]));
            } else {
                // Remove from other types first
                foreach ($p['reactions'] as $type => $uIds) {
                    $p['reactions'][$type] = array_values(array_diff($uIds, [$currentUser['id']]));
                }
                // Add to selected type
                $p['reactions'][$reactionType][] = $currentUser['id'];

                // Send notification to post owner if not self
                if ($p['uploaderId'] !== $currentUser['id']) {
                    $notifications = get_json_data('notifications.json');
                    $notifId = empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1;
                    $notifications[] = [
                        'id' => $notifId,
                        'recipientUserId' => $p['uploaderId'],
                        'senderUserId' => $currentUser['id'],
                        'senderName' => $currentUser['fullName'],
                        'senderRole' => $currentUser['role'],
                        'senderAvatarColor' => $currentUser['avatarColor'],
                        'senderAvatarIcon' => $currentUser['avatarIcon'],
                        'type' => 'LIKE',
                        'title' => 'Reaksi Baru',
                        'message' => $currentUser['fullName'] . ' menyukai postingan Anda: "' . mb_substr($p['title'] ?: $p['description'], 0, 40) . '"',
                        'targetId' => $postId,
                        'createdAt' => round(microtime(true) * 1000),
                        'isRead' => false
                    ];
                    save_json_data('notifications.json', $notifications);
                }
            }

            // Recalculate total likes
            $total = 0;
            foreach ($p['reactions'] as $uIds) {
                $total += count($uIds);
            }
            $p['likeCount'] = $total;

            save_json_data('posts.json', $posts);
            json_response(true, ['post' => $p]);
        }
    }
    json_response(false, [], 'Postingan tidak ditemukan');
}

if ($action === 'add_comment') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $postId = (int)($_POST['postId'] ?? 0);
    $commentText = trim($_POST['commentText'] ?? '');

    if (empty($commentText)) json_response(false, [], 'Komentar tidak boleh kosong');

    foreach ($posts as &$p) {
        if ($p['id'] === $postId) {
            if (!isset($p['comments'])) $p['comments'] = [];
            $commentId = empty($p['comments']) ? 1 : max(array_column($p['comments'], 'id')) + 1;
            $newComment = [
                'id' => $commentId,
                'userId' => $currentUser['id'],
                'userName' => $currentUser['fullName'],
                'userRole' => $currentUser['role'],
                'userAvatarColor' => $currentUser['avatarColor'],
                'userAvatarIcon' => $currentUser['avatarIcon'],
                'commentText' => $commentText,
                'timestamp' => round(microtime(true) * 1000)
            ];
            $p['comments'][] = $newComment;
            save_json_data('posts.json', $posts);

            // Send notification to post owner if not self
            if ($p['uploaderId'] !== $currentUser['id']) {
                $notifications = get_json_data('notifications.json');
                $notifId = empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1;
                $notifications[] = [
                    'id' => $notifId,
                    'recipientUserId' => $p['uploaderId'],
                    'senderUserId' => $currentUser['id'],
                    'senderName' => $currentUser['fullName'],
                    'senderRole' => $currentUser['role'],
                    'senderAvatarColor' => $currentUser['avatarColor'],
                    'senderAvatarIcon' => $currentUser['avatarIcon'],
                    'type' => 'COMMENT',
                    'title' => 'Komentar Baru',
                    'message' => $currentUser['fullName'] . ' mengomentari: "' . mb_substr($commentText, 0, 40) . '"',
                    'targetId' => $postId,
                    'createdAt' => round(microtime(true) * 1000),
                    'isRead' => false
                ];
                save_json_data('notifications.json', $notifications);
            }

            json_response(true, ['comments' => $p['comments'], 'new_comment' => $newComment]);
        }
    }
    json_response(false, [], 'Postingan tidak ditemukan');
}

if ($action === 'delete_post') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $postId = (int)($_POST['postId'] ?? 0);

    $filtered = [];
    $found = false;
    foreach ($posts as $p) {
        if ($p['id'] === $postId) {
            // Only uploader or Wali Kelas can delete
            if ($p['uploaderId'] === $currentUser['id'] || $currentUser['role'] === 'WALI_KELAS') {
                $found = true;
                continue;
            } else {
                json_response(false, [], 'Anda tidak memiliki hak untuk menghapus postingan ini');
            }
        }
        $filtered[] = $p;
    }

    if ($found) {
        save_json_data('posts.json', $filtered);
        json_response(true, [], 'Postingan berhasil dihapus');
    }
    json_response(false, [], 'Postingan tidak ditemukan');
}

json_response(false, [], 'Aksi tidak dikenal');
