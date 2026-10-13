package org.jordi.prueba2025;

/**
 * Resultado inmutable de la decodificación
 * de un anuncio iBeacon del proyecto.
 *
 * Esta clase no conoce Android, Bluetooth,
 * la interfaz gráfica ni el servidor.
 */
public final class ParsedMeasurement {

    /**
     * Tipos de medida definidos por el protocolo Arduino.
     */
    public static final int TYPE_CO2 = 11;
    public static final int TYPE_TEMPERATURE = 12;

    private final int type;
    private final int counter;
    private final int value;

    public ParsedMeasurement(
            int type,
            int counter,
            int value
    ) {
        this.type = type;
        this.counter = counter;
        this.value = value;
    }

    public int getType() {
        return this.type;
    }

    public int getCounter() {
        return this.counter;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isCo2() {
        return this.type == TYPE_CO2;
    }

    public boolean isTemperature() {
        return this.type == TYPE_TEMPERATURE;
    }
}