<?php

declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');

echo json_encode(
    [
        'ok' => true,
        'message' => 'PHP API is working',
        'server_time' => date('Y-m-d H:i:s')
    ],
    JSON_UNESCAPED_UNICODE |
    JSON_UNESCAPED_SLASHES
);