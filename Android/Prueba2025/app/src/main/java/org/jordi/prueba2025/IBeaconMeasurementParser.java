package org.jordi.prueba2025;

import java.nio.charset.StandardCharsets;

/**
 * Lógica de negocio encargada de interpretar
 * los datos iBeacon emitidos por Arduino.
 *
 * Formato esperado de manufacturerData:
 *
 * 0-1     Tipo iBeacon: 02 15
 * 2-17    UUID de 16 bytes
 * 18-19   Major
 * 20-21   Minor
 * 22      TxPower
 *
 * El byte alto de Major identifica la medida:
 *
 * 11 = CO2
 * 12 = temperatura
 *
 * Minor contiene el valor de la medida
 * como entero de 16 bits con signo.
 */
public final class IBeaconMeasurementParser {

    private static final int MANUFACTURER_DATA_LENGTH = 23;

    private static final int IBEACON_TYPE = 0x02;
    private static final int IBEACON_LENGTH = 0x15;

    private static final int UUID_START_INDEX = 2;
    private static final int UUID_LENGTH = 16;

    private static final int MAJOR_INDEX = 18;
    private static final int MINOR_INDEX = 20;

    private static final byte[] EXPECTED_UUID =
            "BOWEN-XBW01-2026"
                    .getBytes(StandardCharsets.UTF_8);

    /**
     * Decodifica un anuncio iBeacon del proyecto.
     *
     * @param manufacturerData datos del fabricante 0x004C
     *
     * @return medida decodificada
     *
     * @throws IllegalArgumentException si la trama
     *                                  no pertenece al proyecto
     */
    public ParsedMeasurement parse(
            byte[] manufacturerData
    ) {
        validateManufacturerData(
                manufacturerData
        );

        int major =
                readUnsignedInt16(
                        manufacturerData,
                        MAJOR_INDEX
                );

        int minorUnsigned =
                readUnsignedInt16(
                        manufacturerData,
                        MINOR_INDEX
                );

        /*
         * Conversión a entero de 16 bits con signo.
         * Permite interpretar correctamente -24.
         */
        short measurementValue =
                (short) minorUnsigned;

        int measurementType =
                (major >> 8) & 0xFF;

        int counter =
                major & 0xFF;

        if (
                measurementType !=
                        ParsedMeasurement.TYPE_CO2 &&
                        measurementType !=
                                ParsedMeasurement.TYPE_TEMPERATURE
        ) {
            throw new IllegalArgumentException(
                    "Unknown measurement type: " +
                            measurementType
            );
        }

        return new ParsedMeasurement(
                measurementType,
                counter,
                (int) measurementValue
        );
    }

    /**
     * Comprueba la longitud, el formato iBeacon
     * y el UUID utilizado por el proyecto.
     */
    private void validateManufacturerData(
            byte[] manufacturerData
    ) {
        if (manufacturerData == null) {
            throw new IllegalArgumentException(
                    "Manufacturer data is null"
            );
        }

        if (
                manufacturerData.length <
                        MANUFACTURER_DATA_LENGTH
        ) {
            throw new IllegalArgumentException(
                    "Manufacturer data is too short"
            );
        }

        if (
                (manufacturerData[0] & 0xFF) !=
                        IBEACON_TYPE ||
                        (manufacturerData[1] & 0xFF) !=
                                IBEACON_LENGTH
        ) {
            throw new IllegalArgumentException(
                    "The advertisement is not an iBeacon"
            );
        }

        for (int i = 0; i < UUID_LENGTH; i++) {
            if (
                    manufacturerData[
                            UUID_START_INDEX + i
                            ] != EXPECTED_UUID[i]
            ) {
                throw new IllegalArgumentException(
                        "Unexpected iBeacon UUID"
                );
            }
        }
    }

    /**
     * Lee un entero de 16 bits en orden big endian.
     */
    private int readUnsignedInt16(
            byte[] data,
            int index
    ) {
        return
                ((data[index] & 0xFF) << 8) |
                        (data[index + 1] & 0xFF);
    }
}