<?php

declare(strict_types=1);

/**
 * Punto de entrada HTTP para almacenar una medición.
 *
 * Responsabilidades:
 * - comprobar el método HTTP;
 * - comprobar la API Key;
 * - leer y decodificar JSON;
 * - llamar a la lógica de negocio;
 * - convertir el resultado en una respuesta HTTP.
 *
 * Este archivo no contiene SQL ni reglas de negocio.
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
 * La comunicación solamente acepta POST.
 */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    JsonResponse::send(
        [
            'ok' => false,
            'error' =>
                'Only POST requests are allowed'
        ],
        405
    );
}

/*
 * Autenticación de la petición HTTP.
 */
$receivedApiKey =
    $_SERVER['HTTP_X_API_KEY'] ?? '';

if (
    $receivedApiKey === '' ||
    !hash_equals(
        API_KEY,
        $receivedApiKey
    )
) {
    JsonResponse::send(
        [
            'ok' => false,
            'error' => 'Unauthorized'
        ],
        401
    );
}

/*
 * Lectura del cuerpo HTTP.
 */
$rawBody =
    file_get_contents('php://input');

$data = json_decode(
    $rawBody,
    true
);

if (!is_array($data)) {
    JsonResponse::send(
        [
            'ok' => false,
            'error' => 'Invalid JSON body'
        ],
        400
    );
}

try {
    /*
     * Construcción de dependencias.
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
     * Delegación completa de la validación
     * y almacenamiento a la lógica de negocio.
     */
    $result =
        $service->createMeasurement($data);

    JsonResponse::send(
        [
            'ok' => true,
            'id' => $result['id'],
            'message' =>
                'Measurement stored successfully'
        ],
        201
    );

} catch (InvalidArgumentException $exception) {
    JsonResponse::send(
        [
            'ok' => false,
            'error' =>
                $exception->getMessage()
        ],
        400
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