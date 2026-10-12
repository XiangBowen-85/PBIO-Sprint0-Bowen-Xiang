<?php

declare(strict_types=1);

/*
 * Archivo de configuración de ejemplo.
 *
 * Las credenciales reales no se incluyen
 * en el repositorio.
 */

const DB_HOST = 'localhost';
const DB_NAME = 'YOUR_DATABASE_NAME';
const DB_USER = 'YOUR_DATABASE_USER';
const DB_PASSWORD = 'YOUR_DATABASE_PASSWORD';

const API_KEY = 'YOUR_PRIVATE_API_KEY';

function getDatabase(): PDO
{
    $dsn =
        'mysql:host=' . DB_HOST .
        ';dbname=' . DB_NAME .
        ';charset=utf8mb4';

    return new PDO(
        $dsn,
        DB_USER,
        DB_PASSWORD,
        [
            PDO::ATTR_ERRMODE =>
                PDO::ERRMODE_EXCEPTION,

            PDO::ATTR_DEFAULT_FETCH_MODE =>
                PDO::FETCH_ASSOC,

            PDO::ATTR_EMULATE_PREPARES =>
                false
        ]
    );
}

function sendJson(
    array $data,
    int $statusCode = 200
): void {
    http_response_code($statusCode);

    header(
        'Content-Type: application/json; charset=utf-8'
    );

    echo json_encode(
        $data,
        JSON_UNESCAPED_UNICODE |
        JSON_UNESCAPED_SLASHES
    );

    exit;
}