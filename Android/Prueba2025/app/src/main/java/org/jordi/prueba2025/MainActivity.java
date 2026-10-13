package org.jordi.prueba2025;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 1001;

    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    private byte[] ultimosDatosXBW01 = null;

    private int ultimoRssi = 0;

    private String direccionXBW01 = null;

    private Integer ultimoCo2 = null;

    private Integer ultimaTemperatura = null;

    /*
     * Servicios y modelo separados de la interfaz gráfica.
     */
    /**
     * Controlador de la lógica de negocio
     * de las mediciones.
     */
    private final MeasurementController
            measurementController =
            new MeasurementController("XBW01");

    /**
     * Cliente de comunicación separado
     * de la interfaz gráfica.
     */
    private final PleskClient
            pleskClient =
            new PleskClient();
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");
        /*
         * Si existe un escaneo anterior, se detiene antes
         * de crear un nuevo callback.
         */
        if (
                callbackDelEscaneo != null &&
                        elEscanner != null &&
                        ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.BLUETOOTH_SCAN
                        ) == PackageManager.PERMISSION_GRANTED
        ) {
            elEscanner.stopScan(callbackDelEscaneo);
            callbackDelEscaneo = null;
        }

        direccionXBW01 = null;
        ultimosDatosXBW01 = null;
        ultimoRssi = 0;
        ultimoCo2 = null;
        ultimaTemperatura = null;

        measurementController.reset();

        if (this.elEscanner == null) {

            Log.d(
                    ETIQUETA_LOG,
                    " El escáner BLE todavía no está inicializado"
            );

            Toast.makeText(
                    this,
                    "El escáner BLE no está preparado",
                    Toast.LENGTH_LONG
            ).show();

            comprobarPermisosBluetooth();
            return;
        }

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {

            @Override
            public void onScanResult(
                    int callbackType,
                    ScanResult resultado
            ) {
                super.onScanResult(
                        callbackType,
                        resultado
                );

                if (
                        resultado == null ||
                                resultado.getScanRecord() == null
                ) {
                    return;
                }

                byte[] datosFabricante =
                        resultado
                                .getScanRecord()
                                .getManufacturerSpecificData(
                                        0x004c
                                );

                if (
                        datosFabricante == null ||
                                datosFabricante.length < 23
                ) {
                    return;
                }

                Log.d(
                        ETIQUETA_LOG,
                        "IBEACON XBW01 RECIBIDO = " +
                                Utilidades.bytesToHexString(
                                        datosFabricante
                                )
                );

                /*
                 * Guardamos la información del anuncio.
                 */
                ultimoRssi =
                        resultado.getRssi();

                String rawDataHex =
                        Utilidades.bytesToHexString(
                                resultado
                                        .getScanRecord()
                                        .getBytes()
                        );

                ultimosDatosXBW01 =
                        resultado
                                .getScanRecord()
                                .getBytes();

                /*
                 * El modelo guarda los datos necesarios,
                 * independientemente de la interfaz.
                 */
                MeasurementData measurementData =
                        measurementController
                                .getMeasurementData();

                measurementData.setRssi(
                        ultimoRssi
                );

                measurementData.setRawData(
                        rawDataHex
                );

                if (
                        ActivityCompat.checkSelfPermission(
                                MainActivity.this,
                                Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                ) {
                    direccionXBW01 =
                            resultado
                                    .getDevice()
                                    .getAddress();

                    measurementController
                            .getMeasurementData()
                            .setDeviceAddress(
                                    direccionXBW01
                            );
                }

                /*
                 * Extraemos major, minor y tipo de medida.
                 */
                procesarAnuncioIBeacon(
                        resultado
                );

                Log.d(
                        ETIQUETA_LOG,
                        "ESTADO XBW01: CO2 = " +
                                ultimoCo2 +
                                ", temperatura = " +
                                ultimaTemperatura
                );

                /*
                 * Arduino transmite ambas medidas por separado.
                 * Detenemos el escaneo cuando ya tenemos las dos.
                 */
                if (
                        measurementController
                                .isReadyToUpload()
                ) {
                    if (
                            ActivityCompat.checkSelfPermission(
                                    MainActivity.this,
                                    Manifest.permission.BLUETOOTH_SCAN
                            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        elEscanner.stopScan(
                                callbackDelEscaneo
                        );

                        callbackDelEscaneo = null;
                    }

                    Toast.makeText(
                            MainActivity.this,
                            "CO2 = " +
                                    ultimoCo2 +
                                    " ppm\nTemperatura = " +
                                    ultimaTemperatura +
                                    " °C",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): vuelvo a pedir permiso para escanear por si no lo tuviea de antes");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): startScan ");

        ScanSettings settings =
                new ScanSettings.Builder()
                        .setScanMode(
                                ScanSettings.SCAN_MODE_LOW_LATENCY
                        )
                        .setReportDelay(0)
                        .build();

        /*
         * Filtro del iBeacon utilizado por el proyecto.
         *
         * Android elimina el identificador 0x004C de los datos
         * devueltos por getManufacturerSpecificData().
         *
         * Por tanto, el patrón comienza por:
         * 02 15 + UUID "EPSG-GTI-PROY-3A".
         */
        byte[] patronIBeacon = new byte[] {
                (byte) 0x02,
                (byte) 0x15,

                (byte) 'B',
                (byte) 'O',
                (byte) 'W',
                (byte) 'E',
                (byte) 'N',
                (byte) '-',

                (byte) 'X',
                (byte) 'B',
                (byte) 'W',
                (byte) '0',
                (byte) '1',
                (byte) '-',

                (byte) '2',
                (byte) '0',
                (byte) '2',
                (byte) '6'
        };

        /*
         * 0xFF significa que todos los bits del patrón
         * deben coincidir.
         */
        byte[] mascaraIBeacon = new byte[
                patronIBeacon.length
                ];

        for (int i = 0; i < mascaraIBeacon.length; i++) {
            mascaraIBeacon[i] =
                    (byte) 0xFF;
        }

        ScanFilter filtroIBeacon =
                new ScanFilter.Builder()
                        .setManufacturerData(
                                0x004c,
                                patronIBeacon,
                                mascaraIBeacon
                        )
                        .build();

        ArrayList<ScanFilter> filtros =
                new ArrayList<>();

        filtros.add(filtroIBeacon);

        this.elEscanner.startScan(
                filtros,
                settings,
                this.callbackDelEscaneo
        );

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): termina ");
    } // ()

    /**
     * Delega la interpretación de la trama
     * en IBeaconMeasurementParser.
     *
     * MainActivity solamente coordina el resultado
     * con el modelo y la interfaz gráfica.
     */
    private void procesarAnuncioIBeacon(
            ScanResult resultado
    ) {
        if (
                resultado == null ||
                        resultado.getScanRecord() == null
        ) {
            return;
        }

        byte[] manufacturerData =
                resultado
                        .getScanRecord()
                        .getManufacturerSpecificData(
                                0x004c
                        );

        try {
            ParsedMeasurement parsedMeasurement =
                    this.measurementController
                            .processManufacturerData(
                                    manufacturerData
                            );

            MeasurementData measurementData =
                    this.measurementController
                            .getMeasurementData();

            /*
             * Estos dos atributos se mantienen temporalmente
             * para no modificar todavía la subida a Plesk.
             */
            this.ultimoCo2 =
                    measurementData.getCo2Value();

            this.ultimaTemperatura =
                    measurementData.getTemperatureValue();

            Log.d(
                    ETIQUETA_LOG,
                    "Tipo de medida = " +
                            parsedMeasurement.getType()
            );

            Log.d(
                    ETIQUETA_LOG,
                    "Contador = " +
                            parsedMeasurement.getCounter()
            );

            Log.d(
                    ETIQUETA_LOG,
                    "Valor decodificado = " +
                            parsedMeasurement.getValue()
            );

            if (parsedMeasurement.isCo2()) {
                Log.d(
                        ETIQUETA_LOG,
                        "CO2 recibido = " +
                                parsedMeasurement.getValue() +
                                " ppm"
                );

            } else if (
                    parsedMeasurement.isTemperature()
            ) {
                Log.d(
                        ETIQUETA_LOG,
                        "Temperatura recibida = " +
                                parsedMeasurement.getValue() +
                                " grados"
                );
            }

        } catch (IllegalArgumentException exception) {
            Log.d(
                    ETIQUETA_LOG,
                    "Anuncio iBeacon ignorado: " +
                            exception.getMessage()
            );
        }
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    private void detenerBusquedaDispositivosBTLE() {

        Log.d(ETIQUETA_LOG, " detenerBusquedaDispositivosBTLE(): empieza");
        if (this.callbackDelEscaneo == null) {
            Log.d(ETIQUETA_LOG, " detenerBusquedaDispositivosBTLE(): termina");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Log.d(ETIQUETA_LOG, " detenerBusquedaDispositivosBTLE(): parando scan");
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

        Log.d(ETIQUETA_LOG, " detenerBusquedaDispositivosBTLE(): termina");
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        Log.d(
                ETIQUETA_LOG,
                "Botón buscar nodo XBW01 pulsado"
        );

        /*
         * Se utiliza el escaneo general porque el nombre
         * y los datos iBeacon pueden recibirse en anuncios distintos.
         */
        buscarTodosLosDispositivosBTLE();
    }// ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    private void inicializarBlueTooth() {

        Log.d(
                ETIQUETA_LOG,
                " inicializarBlueTooth(): empieza"
        );

        BluetoothAdapter bta =
                BluetoothAdapter.getDefaultAdapter();

        if (bta == null) {

            Log.d(
                    ETIQUETA_LOG,
                    " El dispositivo no tiene Bluetooth"
            );

            Toast.makeText(
                    this,
                    "Este teléfono no dispone de Bluetooth",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (
                    ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
            ) {

                Log.d(
                        ETIQUETA_LOG,
                        " Falta BLUETOOTH_CONNECT"
                );

                comprobarPermisosBluetooth();
                return;
            }
        }

        if (!bta.isEnabled()) {

            Log.d(
                    ETIQUETA_LOG,
                    " El Bluetooth del teléfono está apagado"
            );

            Toast.makeText(
                    this,
                    "Activa el Bluetooth del teléfono",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        this.elEscanner =
                bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {

            Log.d(
                    ETIQUETA_LOG,
                    " No se ha podido obtener BluetoothLeScanner"
            );

            Toast.makeText(
                    this,
                    "No se pudo iniciar el escáner BLE",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Log.d(
                ETIQUETA_LOG,
                " BluetoothLeScanner obtenido correctamente"
        );

        Toast.makeText(
                this,
                "Bluetooth preparado",
                Toast.LENGTH_SHORT
        ).show();

        Log.d(
                ETIQUETA_LOG,
                " inicializarBlueTooth(): termina"
        );
    }


    private void comprobarPermisosBluetooth() {

        Log.d(
                ETIQUETA_LOG,
                " comprobarPermisosBluetooth(): empieza"
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            boolean permisoScan =
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED;

            boolean permisoConnect =
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED;

            boolean permisoLocalizacion =
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;

            if (
                    !permisoScan ||
                            !permisoConnect ||
                            !permisoLocalizacion
            ) {

                Log.d(
                        ETIQUETA_LOG,
                        " Pedimos permisos BLUETOOTH_SCAN y BLUETOOTH_CONNECT"
                );

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        CODIGO_PETICION_PERMISOS
                );

            } else {

                Log.d(
                        ETIQUETA_LOG,
                        " Los permisos Bluetooth ya estaban concedidos"
                );

                inicializarBlueTooth();
            }

        } else {

            boolean permisoLocalizacion =
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;

            if (!permisoLocalizacion) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        CODIGO_PETICION_PERMISOS
                );

            } else {

                inicializarBlueTooth();
            }
        }
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        comprobarPermisosBluetooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        Log.d(
                ETIQUETA_LOG,
                " onRequestPermissionsResult(): EMPIEZA"
        );

        if (requestCode != CODIGO_PETICION_PERMISOS) {
            return;
        }

        boolean todosConcedidos = grantResults.length > 0;

        for (int resultado : grantResults) {
            if (resultado != PackageManager.PERMISSION_GRANTED) {
                todosConcedidos = false;
                break;
            }
        }

        if (todosConcedidos) {

            Log.d(
                    ETIQUETA_LOG,
                    " Permisos Bluetooth concedidos"
            );

            Toast.makeText(
                    this,
                    "Permisos Bluetooth concedidos",
                    Toast.LENGTH_LONG
            ).show();

            inicializarBlueTooth();

        } else {

            Log.d(
                    ETIQUETA_LOG,
                    " Permisos Bluetooth NO concedidos"
            );

            Toast.makeText(
                    this,
                    "Debes permitir el acceso a dispositivos cercanos",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Gestiona la pulsación del botón de subida.
     *
     * MainActivity solamente:
     *
     * - comprueba el estado mediante el controlador;
     * - llama al cliente de comunicación;
     * - muestra el resultado en la interfaz.
     */
    public void botonSubirDatosPulsado(
            View view
    ) {
        if (
                !measurementController
                        .isReadyToUpload()
        ) {
            Toast.makeText(
                    this,
                    "Todavía no se han recibido las medidas " +
                            "de CO2 y temperatura",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        MeasurementData measurementData =
                measurementController
                        .getMeasurementData();

        Toast.makeText(
                this,
                "Subiendo datos a Plesk...",
                Toast.LENGTH_SHORT
        ).show();

        pleskClient.uploadMeasurement(
                measurementData,
                new PleskClient.UploadCallback() {

                    @Override
                    public void onSuccess(
                            UploadResult result
                    ) {
                        /*
                         * El callback se ejecuta en un hilo
                         * de trabajo. Volvemos al hilo de UI.
                         */
                        runOnUiThread(() -> {
                            Log.d(
                                    ETIQUETA_LOG,
                                    "Código HTTP: " +
                                            result.getStatusCode()
                            );

                            Log.d(
                                    ETIQUETA_LOG,
                                    "Respuesta Plesk: " +
                                            result.getResponseBody()
                            );

                            if (result.isSuccessful()) {
                                Toast.makeText(
                                        MainActivity.this,
                                        "Datos guardados en Plesk",
                                        Toast.LENGTH_LONG
                                ).show();

                                Log.d(
                                        ETIQUETA_LOG,
                                        "SUBIDA CORRECTA: " +
                                                result.getResponseBody()
                                );

                            } else {
                                Toast.makeText(
                                        MainActivity.this,
                                        "Error del servidor: " +
                                                result.getStatusCode(),
                                        Toast.LENGTH_LONG
                                ).show();

                                Log.e(
                                        ETIQUETA_LOG,
                                        "ERROR DEL SERVIDOR: " +
                                                result.getResponseBody()
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(
                            Exception exception
                    ) {
                        runOnUiThread(() -> {
                            String errorMessage =
                                    exception
                                            .getClass()
                                            .getSimpleName() +
                                            ": " +
                                            exception.getMessage();

                            Toast.makeText(
                                    MainActivity.this,
                                    "Error de conexión: " +
                                            errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();

                            Log.e(
                                    ETIQUETA_LOG,
                                    "Error al subir datos a Plesk",
                                    exception
                            );
                        });
                    }
                }
        );
    }

} // class