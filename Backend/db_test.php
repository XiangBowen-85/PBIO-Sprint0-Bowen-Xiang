<?php

declare(strict_types=1);

require_once __DIR__ . '/config.php';

try {
    $database = getDatabase();

    $statement = $database->query(
        'SELECT
            id,
            device_name,
            device_address,
            rssi,
            raw_data,
            received_at
         FROM measurements
         ORDER BY id DESC
         LIMIT 10'
    );

    $measurements = $statement->fetchAll();

    sendJson([
        'ok' => true,
        'message' => 'Database connection is working',
        'count' => count($measurements),
        'measurements' => $measurements
    ]);

} catch (Throwable $exception) {

    /*
     * 详细错误只写入服务器日志，
     * 不把数据库信息暴露给外部访问者。
     */
    error_log($exception->getMessage());

    sendJson(
        [
            'ok' => false,
            'error' => 'Database connection failed'
        ],
        500
    );
}