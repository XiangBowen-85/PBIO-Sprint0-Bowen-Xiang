<?php

declare(strict_types=1);

/**
 * Utilidad de la capa de comunicación HTTP.
 *
 * Convierte una respuesta PHP en una respuesta JSON.
 */
final class JsonResponse
{
    /**
     * Envía una respuesta JSON y termina la ejecución.
     *
     * @param array<string, mixed> $data
     */
    public static function send(
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
}