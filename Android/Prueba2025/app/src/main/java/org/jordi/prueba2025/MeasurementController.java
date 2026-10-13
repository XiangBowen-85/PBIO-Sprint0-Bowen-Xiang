package org.jordi.prueba2025;

/**
 * Coordinador de la lógica de negocio
 * de las mediciones recibidas por Android.
 *
 * Esta clase no conoce Activity, botones,
 * Toast, Bluetooth ni comunicación HTTP.
 */
public final class MeasurementController {

    private final IBeaconMeasurementParser parser;

    private final MeasurementData measurementData;

    public MeasurementController(
            String deviceName
    ) {
        this.parser =
                new IBeaconMeasurementParser();

        this.measurementData =
                new MeasurementData(
                        deviceName
                );
    }

    /**
     * Reinicia el estado de la sesión de medición.
     */
    public void reset() {
        this.measurementData.reset();
    }

    /**
     * Decodifica un anuncio iBeacon
     * y actualiza el modelo de medición.
     */
    public ParsedMeasurement processManufacturerData(
            byte[] manufacturerData
    ) {
        ParsedMeasurement parsedMeasurement =
                this.parser.parse(
                        manufacturerData
                );

        this.measurementData.applyMeasurement(
                parsedMeasurement
        );

        return parsedMeasurement;
    }

    /**
     * Devuelve el estado actual de las mediciones.
     */
    public MeasurementData getMeasurementData() {
        return this.measurementData;
    }

    /**
     * Indica si ya se han recibido CO2
     * y temperatura.
     */
    public boolean isReadyToUpload() {
        return
                this.measurementData
                        .hasCompleteMeasurement();
    }
}