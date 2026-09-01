<?php
require_once __DIR__ . '/helper.php';

$action = $_GET['action'] ?? $_POST['action'] ?? '';
$quizzes = get_json_data('quizzes.json');
$submissions = get_json_data('quiz_submissions.json');
$currentUser = get_current_user_session();

if ($action === 'get_quizzes') {
    $myId = $currentUser ? $currentUser['id'] : 0;
    
    // Attach my submission if any
    $list = [];
    foreach ($quizzes as $q) {
        $mySub = null;
        foreach ($submissions as $s) {
            if ($s['quizId'] === $q['id'] && $s['studentId'] === $myId) {
                $mySub = $s;
                break;
            }
        }
        $subCount = count(array_filter($submissions, function($s) use ($q) { return $s['quizId'] === $q['id']; }));
        
        $item = $q;
        $item['mySubmission'] = $mySub;
        $item['totalSubmissions'] = $subCount;
        $list[] = $item;
    }

    json_response(true, ['quizzes' => $list]);
}

if ($action === 'get_submissions') {
    if (!$currentUser || $currentUser['role'] !== 'WALI_KELAS') {
        json_response(false, [], 'Akses khusus Wali Kelas');
    }
    $quizId = (int)($_GET['quizId'] ?? 0);
    $subs = array_filter($submissions, function($s) use ($quizId) {
        return $quizId === 0 || $s['quizId'] === $quizId;
    });
    json_response(true, ['submissions' => array_values($subs)]);
}

if ($action === 'create_quiz') {
    if (!$currentUser || $currentUser['role'] !== 'WALI_KELAS') {
        json_response(false, [], 'Hanya Wali Kelas yang dapat membuat kuis');
    }

    $title = trim($_POST['title'] ?? '');
    $subject = trim($_POST['subject'] ?? 'Umum');
    $description = trim($_POST['description'] ?? '');
    $timeLimit = (int)($_POST['timeLimitMinutes'] ?? 15);
    $questionsJson = $_POST['questions'] ?? '[]';
    $questions = json_decode($questionsJson, true) ?: [];

    if (empty($title) || empty($questions)) {
        json_response(false, [], 'Judul dan pertanyaan kuis wajib diisi');
    }

    $newId = empty($quizzes) ? 1 : max(array_column($quizzes, 'id')) + 1;
    $newQuiz = [
        'id' => $newId,
        'title' => $title,
        'subject' => $subject,
        'description' => $description,
        'timeLimitMinutes' => $timeLimit,
        'createdByName' => $currentUser['fullName'],
        'createdAt' => round(microtime(true) * 1000),
        'questions' => $questions
    ];

    $quizzes[] = $newQuiz;
    save_json_data('quizzes.json', $quizzes);

    // Notify all students
    $notifications = get_json_data('notifications.json');
    $notifId = empty($notifications) ? 1 : max(array_column($notifications, 'id')) + 1;
    $notifications[] = [
        'id' => $notifId,
        'recipientUserId' => 0,
        'senderUserId' => $currentUser['id'],
        'senderName' => $currentUser['fullName'],
        'senderRole' => $currentUser['role'],
        'senderAvatarColor' => $currentUser['avatarColor'],
        'senderAvatarIcon' => $currentUser['avatarIcon'],
        'type' => 'NEW_POST',
        'title' => 'Kuis / Ulangan Harian Baru!',
        'message' => 'Pak Guru membuat kuis baru: "' . $title . '" (' . $subject . ')',
        'targetId' => $newId,
        'createdAt' => round(microtime(true) * 1000),
        'isRead' => false
    ];
    save_json_data('notifications.json', $notifications);

    json_response(true, ['quiz' => $newQuiz], 'Kuis berhasil dibuat');
}

if ($action === 'submit_quiz') {
    if (!$currentUser) json_response(false, [], 'Harus login');
    $quizId = (int)($_POST['quizId'] ?? 0);
    $answers = json_decode($_POST['answers'] ?? '[]', true) ?: [];

    $targetQuiz = null;
    foreach ($quizzes as $q) {
        if ($q['id'] === $quizId) {
            $targetQuiz = $q;
            break;
        }
    }
    if (!$targetQuiz) json_response(false, [], 'Kuis tidak ditemukan');

    // Calculate score
    $correct = 0;
    $total = count($targetQuiz['questions']);
    foreach ($targetQuiz['questions'] as $idx => $quest) {
        $userAns = $answers[$idx] ?? -1;
        if ($userAns === $quest['correctIndex']) {
            $correct++;
        }
    }
    $score = $total > 0 ? round(($correct / $total) * 100) : 0;

    $subId = empty($submissions) ? 1 : max(array_column($submissions, 'id')) + 1;
    $submission = [
        'id' => $subId,
        'quizId' => $quizId,
        'studentId' => $currentUser['id'],
        'studentName' => $currentUser['fullName'],
        'studentNumber' => $currentUser['studentNumber'],
        'score' => $score,
        'totalQuestions' => $total,
        'correctAnswers' => $correct,
        'submittedAt' => round(microtime(true) * 1000)
    ];

    $submissions[] = $submission;
    save_json_data('quiz_submissions.json', $submissions);

    json_response(true, ['submission' => $submission], 'Kuis berhasil diselesaikan! Skor Anda: ' . $score);
}

json_response(false, [], 'Aksi tidak dikenal');
