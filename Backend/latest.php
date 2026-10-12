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
            co2_value,
            temperature_value,
            raw_data,
            received_at
         FROM measurements
         ORDER BY id DESC
         LIMIT 50'
    );

    sendJson([
        'ok' => true,
        'measurements' =>
            $statement->fetchAll()
    ]);

} catch (Throwable $exception) {
    error_log($exception->getMessage());

    sendJson(
        [
            'ok' => false,
            'error' =>
                'Database operation failed'
        ],
        500
    );
}