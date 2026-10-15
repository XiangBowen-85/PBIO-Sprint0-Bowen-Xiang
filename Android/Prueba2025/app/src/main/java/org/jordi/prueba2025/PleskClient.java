package org.jordi.prueba2025;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Cliente de comunicación con el backend PHP.
 *
 * Responsabilidades:
 *
 * - construir el JSON de una medición;
 * - realizar la petición HTTPS;
 * - enviar la API Key;
 * - leer el código HTTP;
 * - leer la respuesta del servidor;
 * - informar del resultado mediante un callback.
 *
 * Esta clase no conoce Activity, Toast,
 * botones ni otros elementos de la interfaz gráfica.
 */
public final class PleskClient {

    private static final String UPLOAD_URL =
            "https://bxiang.upv.edu.es/api/upload.php";

    /*
     * Valor utilizado por el prototipo Sprint 0.
     *
     * En una versión posterior debería almacenarse
     * mediante un mecanismo de configuración seguro.
     */

    private static final int TIMEOUT_MILLISECONDS =
            10000;

    /**
     * Callback para comunicar el resultado
     * sin depender de MainActivity.
     */
    public interface UploadCallback {

        void onSuccess(
                UploadResult result
        );

        void onError(
                Exception exception
        );
    }

    /**
     * Envía una medición completa al backend.
     *
     * La petición se ejecuta en un hilo de trabajo
     * para no bloquear la interfaz de Android.
     */
    public void uploadMeasurement(
            MeasurementData measurement,
            UploadCallback callback
    ) {
        /*
         * Se crea una copia de los valores necesarios
         * antes de iniciar el hilo.
         */
        String deviceName =
                measurement.getDeviceName();

        String deviceAddress =
                measurement.getDeviceAddress();

        int rssi =
                measurement.getRssi();

        Integer co2Value =
                measurement.getCo2Value();

        Integer temperatureValue =
                measurement.getTemperatureValue();

        String rawData =
                measurement.getRawData();

        new Thread(() -> {
            HttpsURLConnection connection = null;

            try {
                JSONObject json =
                        createJson(
                                deviceName,
                                deviceAddress,
                                rssi,
                                co2Value,
                                temperatureValue,
                                rawData
                        );

                URL url =
                        new URL(UPLOAD_URL);

                connection =
                        (HttpsURLConnection)
                                url.openConnection();

                configureConnection(
                        connection
                );

                byte[] body =
                        json.toString()
                                .getBytes("UTF-8");

                connection.setFixedLengthStreamingMode(
                        body.length
                );

                try (
                        OutputStream output =
                                connection
                                        .getOutputStream()
                ) {
                    output.write(body);
                    output.flush();
                }

                int statusCode =
                        connection.getResponseCode();

                InputStream input;

                if (
                        statusCode >= 200 &&
                                statusCode < 300
                ) {
                    input =
                            connection.getInputStream();

                } else {
                    input =
                            connection.getErrorStream();
                }

                String responseBody =
                        readResponse(input);

                UploadResult result =
                        new UploadResult(
                                statusCode,
                                responseBody
                        );

                callback.onSuccess(result);

            } catch (Exception exception) {
                callback.onError(exception);

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    /**
     * Construye el JSON esperado por upload.php.
     */
    private JSONObject createJson(
            String deviceName,
            String deviceAddress,
            int rssi,
            Integer co2Value,
            Integer temperatureValue,
            String rawData
    ) throws Exception {
        JSONObject json =
                new JSONObject();

        json.put(
                "device_name",
                deviceName
        );

        json.put(
                "device_address",
                deviceAddress == null
                        ? ""
                        : deviceAddress
        );

        json.put(
                "rssi",
                rssi
        );

        json.put(
                "co2_value",
                co2Value
        );

        json.put(
                "temperature_value",
                temperatureValue
        );

        json.put(
                "raw_data",
                rawData == null
                        ? ""
                        : rawData
        );

        return json;
    }

    /**
     * Configura los datos comunes de la petición HTTPS.
     */
    private void configureConnection(
            HttpsURLConnection connection
    ) throws Exception {
        connection.setRequestMethod("POST");

        connection.setConnectTimeout(
                TIMEOUT_MILLISECONDS
        );

        connection.setReadTimeout(
                TIMEOUT_MILLISECONDS
        );

        connection.setDoOutput(true);

        connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        connection.setRequestProperty(
                "Accept",
                "application/json"
        );

        connection.setRequestProperty(
                "X-API-Key",
                ApiConfig.API_KEY
        );
    }

    /**
     * Lee completamente la respuesta HTTP.
     */
    private String readResponse(
            InputStream input
    ) throws Exception {
        if (input == null) {
            return "";
        }

        StringBuilder response =
                new StringBuilder();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        input,
                                        "UTF-8"
                                )
                        )
        ) {
            String line;

            while (
                    (line = reader.readLine()) != null
            ) {
                response.append(line);
            }
        }

        return response.toString();
    }
}