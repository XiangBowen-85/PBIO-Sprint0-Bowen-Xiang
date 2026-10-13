package org.jordi.prueba2025;

/**
 * Resultado inmutable de una petición
 * de subida al backend.
 *
 * Esta clase no conoce Activity, Toast
 * ni componentes de la interfaz gráfica.
 */
public final class UploadResult {

    private final int statusCode;
    private final String responseBody;

    public UploadResult(
            int statusCode,
            String responseBody
    ) {
        this.statusCode = statusCode;
        this.responseBody =
                responseBody == null
                        ? ""
                        : responseBody;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public boolean isSuccessful() {
        return
                this.statusCode >= 200 &&
                        this.statusCode < 300;
    }
}