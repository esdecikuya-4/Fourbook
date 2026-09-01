<?php
require_once __DIR__ . '/helper.php';

$action = $_GET['action'] ?? $_POST['action'] ?? '';
$journals = get_json_data('journals.json');
$currentUser = get_current_user_session();

if ($action === 'get_journals') {
    usort($journals, function($a, $b) {
        return ($b['createdAt'] ?? 0) - ($a['createdAt'] ?? 0);
    });
    json_response(true, ['journals' => $journals]);
}

if ($action === 'create_journal') {
    if (!$currentUser || $currentUser['role'] !== 'WALI_KELAS') {
        json_response(false, [], 'Hanya Wali Kelas yang dapat mengisi Jurnal Pembelajaran');
    }

    $dateString = trim($_POST['dateString'] ?? date('Y-m-d'));
    $subject = trim($_POST['subject'] ?? 'Pelajaran');
    $topic = trim($_POST['topic'] ?? '');
    $activity = trim($_POST['activity'] ?? '');
    $attendance = trim($_POST['attendanceSummary'] ?? '');
    $notes = trim($_POST['notes'] ?? '');

    if (empty($topic) || empty($activity)) {
        json_response(false, [], 'Materi pokok dan uraian kegiatan wajib diisi');
    }

    $newId = empty($journals) ? 1 : max(array_column($journals, 'id')) + 1;
    $newJournal = [
        'id' => $newId,
        'teacherId' => $currentUser['id'],
        'teacherName' => $currentUser['fullName'],
        'dateString' => $dateString,
        'subject' => $subject,
        'topic' => $topic,
        'activity' => $activity,
        'attendanceSummary' => $attendance,
        'notes' => $notes,
        'createdAt' => round(microtime(true) * 1000)
    ];

    $journals[] = $newJournal;
    save_json_data('journals.json', $journals);

    json_response(true, ['journal' => $newJournal], 'Jurnal pembelajaran berhasil disimpan');
}

json_response(false, [], 'Aksi tidak dikenal');
