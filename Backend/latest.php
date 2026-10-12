<?php

declare(strict_types=1);

/**
 * Punto de entrada HTTP para consultar
 * las mediciones más recientes.
 *
 * Responsabilidades de comunicación:
 * - comprobar el método HTTP;
 * - solicitar los datos a la lógica de negocio;
 * - devolver una respuesta JSON.
 *
 * Este archivo no contiene SQL
 * ni reglas de validación de mediciones.
 */

require_once __DIR__ . '/config.php';

require_once
    __DIR__ . '/communication/JsonResponse.php';

require_once
    __DIR__ . '/database/Database.php';

require_once
    __DIR__ . '/database/MeasurementRepository.php';

require_once
    __DIR__ . '/business/MeasurementService.php';

/*
 * Este punto de entrada solamente acepta GET.
 */
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    JsonResponse::send(
        [
            'ok' => false,
            'error' =>
                'Only GET requests are allowed'
        ],
        405
    );
}

try {
    /*
     * Construcción de las dependencias.
     */
    $database =
        new Database();

    $repository =
        new MeasurementRepository(
            $database->getConnection()
        );

    $service =
        new MeasurementService(
            $repository
        );

    /*
     * La comunicación no conoce SQL.
     * Solicita las mediciones a la lógica de negocio.
     */
    $measurements =
        $service->getRecentMeasurements(50);

    JsonResponse::send(
        [
            'ok' => true,
            'measurements' => $measurements
        ]
    );

} catch (Throwable $exception) {
    error_log(
        $exception->getMessage()
    );

    JsonResponse::send(
        [
            'ok' => false,
            'error' =>
                'Internal server error'
        ],
        500
    );
}