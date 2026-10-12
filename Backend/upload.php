<?php

declare(strict_types=1);

require_once __DIR__ . '/config.php';

/*
 * 只允许使用 POST 请求上传数据。
 */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJson(
        [
            'ok' => false,
            'error' => 'Only POST requests are allowed'
        ],
        405
    );
}

/*
 * 从 HTTP 请求头读取 Android 发来的 API Key。
 */
$receivedApiKey =
    $_SERVER['HTTP_X_API_KEY'] ?? '';

if (
    $receivedApiKey === '' ||
    !hash_equals(API_KEY, $receivedApiKey)
) {
    sendJson(
        [
            'ok' => false,
            'error' => 'Unauthorized'
        ],
        401
    );
}

/*
 * 读取 Android 发送的 JSON。
 */
$rawBody = file_get_contents('php://input');

$data = json_decode(
    $rawBody,
    true
);

if (!is_array($data)) {
    sendJson(
        [
            'ok' => false,
            'error' => 'Invalid JSON body'
        ],
        400
    );
}

/*
 * 提取字段。
 */
$deviceName =
    trim(
        (string) (
            $data['device_name'] ?? ''
        )
    );

$deviceAddress =
    trim(
        (string) (
            $data['device_address'] ?? ''
        )
    );

$rawData =
    trim(
        (string) (
            $data['raw_data'] ?? ''
        )
    );

$rssi = filter_var(
    $data['rssi'] ?? null,
    FILTER_VALIDATE_INT
);

/*
 * Medida de CO2 ya decodificada por Android.
 */
$co2Value = filter_var(
    $data['co2_value'] ?? null,
    FILTER_VALIDATE_INT
);

/*
 * Temperatura ya decodificada por Android.
 */
$temperatureValue = filter_var(
    $data['temperature_value'] ?? null,
    FILTER_VALIDATE_INT
);

/*
 * El valor de CO2 debe ser un número entero.
 */
if ($co2Value === false) {
    sendJson(
        [
            'ok' => false,
            'error' =>
                'co2_value must be an integer'
        ],
        400
    );
}

/*
 * La temperatura debe ser un número entero.
 * Se permiten valores negativos.
 */
if ($temperatureValue === false) {
    sendJson(
        [
            'ok' => false,
            'error' =>
                'temperature_value must be an integer'
        ],
        400
    );
}

/*
 * 检查设备名称。
 */
if ($deviceName === '') {
    sendJson(
        [
            'ok' => false,
            'error' => 'device_name is required'
        ],
        400
    );
}

/*
 * 检查 RSSI。
 */
if ($rssi === false) {
    sendJson(
        [
            'ok' => false,
            'error' => 'rssi must be an integer'
        ],
        400
    );
}

/*
 * 检查原始 BLE 数据。
 * 允许冒号，因为 Android 当前生成的数据包含冒号。
 */
if (
    $rawData === '' ||
    !preg_match(
        '/^[0-9A-Fa-f:]+$/',
        $rawData
    )
) {
    sendJson(
        [
            'ok' => false,
            'error' =>
                'raw_data must contain hexadecimal data'
        ],
        400
    );
}

try {
    $database = getDatabase();

    $statement = $database->prepare(
    'INSERT INTO measurements (
        device_name,
        device_address,
        rssi,
        co2_value,
        temperature_value,
        raw_data
     ) VALUES (
        :device_name,
        :device_address,
        :rssi,
        :co2_value,
        :temperature_value,
        :raw_data
     )'
);

    $statement->execute([
    ':device_name' =>
        $deviceName,

    ':device_address' =>
        $deviceAddress === ''
            ? null
            : $deviceAddress,

    ':rssi' =>
        $rssi,

    ':co2_value' =>
        $co2Value,

    ':temperature_value' =>
        $temperatureValue,

    ':raw_data' =>
        strtoupper($rawData)
]);

    sendJson([
        'ok' => true,
        'id' =>
            (int) $database->lastInsertId(),
        'message' =>
            'Measurement stored successfully'
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