package org.jordi.prueba2025;

/**
 * Modelo que contiene el estado actual
 * de las mediciones del nodo sensor.
 *
 * Esta clase no conoce la interfaz gráfica,
 * Bluetooth ni la comunicación HTTP.
 */
public final class MeasurementData {

    private final String deviceName;

    private String deviceAddress;
    private int rssi;

    private Integer co2Value;
    private Integer temperatureValue;

    private String rawData;

    public MeasurementData(
            String deviceName
    ) {
        this.deviceName = deviceName;
    }

    /**
     * Elimina las medidas de una sesión anterior.
     */
    public void reset() {
        this.deviceAddress = null;
        this.rssi = 0;
        this.co2Value = null;
        this.temperatureValue = null;
        this.rawData = null;
    }

    /**
     * Actualiza el modelo con una medida decodificada.
     */
    public void applyMeasurement(
            ParsedMeasurement measurement
    ) {
        if (measurement.isCo2()) {
            this.co2Value =
                    measurement.getValue();

        } else if (measurement.isTemperature()) {
            this.temperatureValue =
                    measurement.getValue();
        }
    }

    public boolean hasCompleteMeasurement() {
        return
                this.co2Value != null &&
                        this.temperatureValue != null;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getDeviceAddress() {
        return this.deviceAddress;
    }

    public void setDeviceAddress(
            String deviceAddress
    ) {
        this.deviceAddress = deviceAddress;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public Integer getCo2Value() {
        return this.co2Value;
    }

    public Integer getTemperatureValue() {
        return this.temperatureValue;
    }

    public String getRawData() {
        return this.rawData;
    }

    public void setRawData(
            String rawData
    ) {
        this.rawData = rawData;
    }
}