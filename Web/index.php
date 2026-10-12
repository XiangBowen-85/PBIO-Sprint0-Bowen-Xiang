<?php

declare(strict_types=1);

require_once __DIR__ . '/config.php';

/**
 * 从 Android 保存的十六进制 BLE 字符串中提取 iBeacon minor。
 *
 * Android 的 TramaIBeacon 使用：
 * - minor[0] = byte 27
 * - minor[1] = byte 28
 *
 * @param string $rawData BLE 数据，例如 02:01:...
 * @return int|null 无法解析时返回 null
 */
function extractMinorValue(string $rawData): ?int
{
    $parts = array_values(
        array_filter(
            explode(':', $rawData),
            static fn(string $value): bool => $value !== ''
        )
    );

    if (count($parts) < 29) {
        return null;
    }

    if (
        !ctype_xdigit($parts[27]) ||
        !ctype_xdigit($parts[28])
    ) {
        return null;
    }

    $highByte = hexdec($parts[27]);
    $lowByte = hexdec($parts[28]);

    $value = ($highByte << 8) | $lowByte;

    /*
     * Conversión a entero con signo de 16 bits.
     * Para CO2 normalmente será un valor positivo.
     */
    if ($value >= 32768) {
        $value -= 65536;
    }

    return $value;
}

/**
 * Escapa texto antes de mostrarlo en HTML.
 */
function escapeHtml(?string $value): string
{
    return htmlspecialchars(
        $value ?? '',
        ENT_QUOTES | ENT_SUBSTITUTE,
        'UTF-8'
    );
}

$measurement = null;
$errorMessage = null;
$minorValue = null;

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
         LIMIT 1'
    );

    $result = $statement->fetch();

    if (is_array($result)) {
        $measurement = $result;
        $minorValue = extractMinorValue(
            (string) $measurement['raw_data']
        );
    }

} catch (Throwable $exception) {
    error_log($exception->getMessage());

    $errorMessage =
        'No se ha podido consultar la última medición.';
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1"
    >

    <meta http-equiv="refresh" content="10">

    <title>PBIO Sprint 0 - Última medición</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 32px 16px;
            background: #f3f6f9;
            color: #263238;
            font-family: Arial, Helvetica, sans-serif;
        }

        .container {
            width: 100%;
            max-width: 760px;
            margin: 0 auto;
        }

        h1 {
            margin-bottom: 8px;
            color: #145a8d;
        }

        .subtitle {
            margin-top: 0;
            margin-bottom: 24px;
            color: #607d8b;
        }

        .status {
            padding: 12px 16px;
            margin-bottom: 20px;
            border-radius: 8px;
            background: #dff4e4;
            color: #1b5e20;
        }

        .error {
            padding: 12px 16px;
            border-radius: 8px;
            background: #fde0e0;
            color: #8b1a1a;
        }

        .card {
            overflow: hidden;
            border-radius: 12px;
            background: white;
            box-shadow: 0 4px 18px rgba(0, 0, 0, 0.10);
        }

        .measurement {
            padding: 28px;
            text-align: center;
            background: #145a8d;
            color: white;
        }

        .measurement-label {
            margin-bottom: 8px;
            font-size: 18px;
        }

        .measurement-value {
            font-size: 56px;
            font-weight: bold;
        }

        .measurement-unit {
            font-size: 22px;
        }

        .details {
            padding: 24px;
        }

        .row {
            display: grid;
            grid-template-columns: 180px 1fr;
            gap: 16px;
            padding: 12px 0;
            border-bottom: 1px solid #eceff1;
        }

        .row:last-child {
            border-bottom: none;
        }

        .label {
            font-weight: bold;
            color: #455a64;
        }

        .raw-data {
            overflow-wrap: anywhere;
            font-family: Consolas, monospace;
            font-size: 13px;
        }

        .footer {
            margin-top: 18px;
            color: #78909c;
            font-size: 14px;
            text-align: center;
        }

        @media (max-width: 600px) {
            .row {
                grid-template-columns: 1fr;
                gap: 5px;
            }

            .measurement-value {
                font-size: 44px;
            }
        }
    </style>
</head>

<body>
<div class="container">

    <h1>PBIO - Sprint 0</h1>

    <p class="subtitle">
        Última medición recibida desde el nodo Arduino mediante BLE y Android
    </p>

    <?php if ($errorMessage !== null): ?>

        <div class="error">
            <?= escapeHtml($errorMessage) ?>
        </div>

    <?php elseif ($measurement === null): ?>

        <div class="error">
            Todavía no hay mediciones almacenadas.
        </div>

    <?php else: ?>

        <div class="status">
            Comunicación completada correctamente:
            Arduino → BLE → Android → PHP → MariaDB
        </div>

        <div class="card">

            <div class="measurement">
                <div class="measurement-label">
                    Última medida de CO₂
                </div>

                <div>
                    <span class="measurement-value">
                        <?= $minorValue === null
                            ? 'N/D'
                            : escapeHtml((string) $minorValue) ?>
                    </span>

                    <?php if ($minorValue !== null): ?>
                        <span class="measurement-unit">ppm</span>
                    <?php endif; ?>
                </div>
            </div>

            <div class="details">

                <div class="row">
                    <div class="label">ID</div>
                    <div>
                        <?= escapeHtml(
                            (string) $measurement['id']
                        ) ?>
                    </div>
                </div>

                <div class="row">
                    <div class="label">Dispositivo</div>
                    <div>
                        <?= escapeHtml(
                            (string) $measurement['device_name']
                        ) ?>
                    </div>
                </div>

                <div class="row">
                    <div class="label">Dirección BLE</div>
                    <div>
                        <?= escapeHtml(
                            (string) $measurement['device_address']
                        ) ?>
                    </div>
                </div>

                <div class="row">
                    <div class="label">RSSI</div>
                    <div>
                        <?= escapeHtml(
                            (string) $measurement['rssi']
                        ) ?> dBm
                    </div>
                </div>

                <div class="row">
                    <div class="label">Fecha de recepción</div>
                    <div>
                        <?= escapeHtml(
                            (string) $measurement['received_at']
                        ) ?>
                    </div>
                </div>

                <div class="row">
                    <div class="label">Datos BLE completos</div>
                    <div class="raw-data">
                        <?= escapeHtml(
                            (string) $measurement['raw_data']
                        ) ?>
                    </div>
                </div>

            </div>
        </div>

        <div class="footer">
            La página se actualiza automáticamente cada 10 segundos.
        </div>

    <?php endif; ?>

</div>
</body>
</html>