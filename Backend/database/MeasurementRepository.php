<?php

declare(strict_types=1);

/**
 * Repositorio de mediciones.
 *
 * Esta clase contiene exclusivamente operaciones SQL
 * relacionadas con la tabla measurements.
 */
final class MeasurementRepository
{
    private PDO $database;

    public function __construct(PDO $database)
    {
        $this->database = $database;
    }

    /**
     * Guarda una medición y devuelve su identificador.
     *
     * @param array<string, mixed> $measurement
     */
    public function insert(array $measurement): int
    {
        $statement = $this->database->prepare(
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
                $measurement['device_name'],

            ':device_address' =>
                $measurement['device_address'],

            ':rssi' =>
                $measurement['rssi'],

            ':co2_value' =>
                $measurement['co2_value'],

            ':temperature_value' =>
                $measurement['temperature_value'],

            ':raw_data' =>
                $measurement['raw_data']
        ]);

        return (int) $this->database->lastInsertId();
    }

    /**
     * Devuelve la última medición almacenada.
     *
     * @return array<string, mixed>|null
     */
    public function findLatest(): ?array
    {
        $statement = $this->database->query(
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
             LIMIT 1'
        );

        $measurement = $statement->fetch();

        if (!is_array($measurement)) {
            return null;
        }

        return $measurement;
    }

    /**
     * Devuelve las últimas mediciones.
     *
     * @return array<int, array<string, mixed>>
     */
    public function findRecent(int $limit = 50): array
    {
        /*
         * LIMIT no se recibe directamente desde HTTP.
         * Aun así, se restringe a un intervalo seguro.
         */
        $safeLimit = max(
            1,
            min($limit, 100)
        );

        $statement = $this->database->query(
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
             LIMIT ' . $safeLimit
        );

        return $statement->fetchAll();
    }
}