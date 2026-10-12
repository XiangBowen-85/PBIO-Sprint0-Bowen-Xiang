<?php

declare(strict_types=1);

/**
 * Lógica de negocio de las mediciones.
 *
 * Esta clase valida y normaliza los datos recibidos.
 * No conoce HTTP, JSON, cabeceras ni códigos de estado.
 */
final class MeasurementService
{
    private MeasurementRepository $repository;

    public function __construct(
        MeasurementRepository $repository
    ) {
        $this->repository = $repository;
    }

    /**
     * Valida, normaliza y almacena una medición.
     *
     * @param array<string, mixed> $input
     *
     * @return array<string, mixed>
     *
     * @throws InvalidArgumentException
     */
    public function createMeasurement(array $input): array
    {
        $measurement =
            $this->validateAndNormalize($input);

        $id =
            $this->repository->insert($measurement);

        return [
            'id' => $id,
            'measurement' => $measurement
        ];
    }

    /**
     * Devuelve la última medición.
     *
     * @return array<string, mixed>|null
     */
    public function getLatestMeasurement(): ?array
    {
        return $this->repository->findLatest();
    }

    /**
     * Devuelve las últimas mediciones.
     *
     * @return array<int, array<string, mixed>>
     */
    public function getRecentMeasurements(
        int $limit = 50
    ): array {
        return $this->repository->findRecent($limit);
    }

    /**
     * Aplica las reglas de negocio de una medición.
     *
     * @param array<string, mixed> $input
     *
     * @return array<string, mixed>
     *
     * @throws InvalidArgumentException
     */
    private function validateAndNormalize(
        array $input
    ): array {
        $deviceName = trim(
            (string) (
                $input['device_name'] ?? ''
            )
        );

        $deviceAddress = trim(
            (string) (
                $input['device_address'] ?? ''
            )
        );

        $rawData = trim(
            (string) (
                $input['raw_data'] ?? ''
            )
        );

        $rssi = filter_var(
            $input['rssi'] ?? null,
            FILTER_VALIDATE_INT
        );

        $co2Value = filter_var(
            $input['co2_value'] ?? null,
            FILTER_VALIDATE_INT
        );

        $temperatureValue = filter_var(
            $input['temperature_value'] ?? null,
            FILTER_VALIDATE_INT
        );

        if ($deviceName === '') {
            throw new InvalidArgumentException(
                'device_name is required'
            );
        }

        if ($deviceName !== 'XBW01') {
            throw new InvalidArgumentException(
                'Unknown device_name'
            );
        }

        if ($rssi === false) {
            throw new InvalidArgumentException(
                'rssi must be an integer'
            );
        }

        if (
            $rssi < -150 ||
            $rssi > 20
        ) {
            throw new InvalidArgumentException(
                'rssi is outside the accepted range'
            );
        }

        if ($co2Value === false) {
            throw new InvalidArgumentException(
                'co2_value must be an integer'
            );
        }

        if (
            $co2Value < 0 ||
            $co2Value > 100000
        ) {
            throw new InvalidArgumentException(
                'co2_value is outside the accepted range'
            );
        }

        if ($temperatureValue === false) {
            throw new InvalidArgumentException(
                'temperature_value must be an integer'
            );
        }

        if (
            $temperatureValue < -100 ||
            $temperatureValue > 150
        ) {
            throw new InvalidArgumentException(
                'temperature_value is outside the accepted range'
            );
        }

        if (
            $rawData === '' ||
            !preg_match(
                '/^[0-9A-Fa-f:]+$/',
                $rawData
            )
        ) {
            throw new InvalidArgumentException(
                'raw_data must contain hexadecimal data'
            );
        }

        return [
            'device_name' =>
                $deviceName,

            'device_address' =>
                $deviceAddress === ''
                    ? null
                    : $deviceAddress,

            'rssi' =>
                $rssi,

            'co2_value' =>
                $co2Value,

            'temperature_value' =>
                $temperatureValue,

            'raw_data' =>
                strtoupper($rawData)
        ];
    }
}