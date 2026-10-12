package org.jordi.prueba2025;

import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;

import android.os.Bundle;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


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

                ultimosDatosXBW01 =
                        resultado
                                .getScanRecord()
                                .getBytes();

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
                        ultimoCo2 != null &&
                                ultimaTemperatura != null
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

                (byte) 'E',
                (byte) 'P',
                (byte) 'S',
                (byte) 'G',
                (byte) '-',
                (byte) 'G',
                (byte) 'T',
                (byte) 'I',
                (byte) '-',
                (byte) 'P',
                (byte) 'R',
                (byte) 'O',
                (byte) 'Y',
                (byte) '-',
                (byte) '3',
                (byte) 'A'
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

    private void procesarAnuncioIBeacon(
            ScanResult resultado
    ) {
        if (
                resultado == null ||
                        resultado.getScanRecord() == null
        ) {
            return;
        }

        byte[] datosFabricante =
                resultado
                        .getScanRecord()
                        .getManufacturerSpecificData(0x004c);

        if (
                datosFabricante == null ||
                        datosFabricante.length < 23
        ) {
            Log.d(
                    ETIQUETA_LOG,
                    "Anuncio sin datos iBeacon válidos"
            );

            return;
        }

        Log.d(
                ETIQUETA_LOG,
                "Longitud manufacturer data = " +
                        datosFabricante.length
        );

        /*
         * Formato esperado:
         *
         * 02 15
         * UUID: 16 bytes
         * major: 2 bytes
         * minor: 2 bytes
         * txPower: 1 byte
         */
        if (
                (datosFabricante[0] & 0xFF) != 0x02 ||
                        (datosFabricante[1] & 0xFF) != 0x15
        ) {
            Log.d(
                    ETIQUETA_LOG,
                    "Los datos del fabricante no tienen formato iBeacon"
            );

            return;
        }

        int indiceMajor = 18;
        int indiceMinor = 20;

        int major =
                ((datosFabricante[indiceMajor] & 0xFF) << 8) |
                        (datosFabricante[indiceMajor + 1] & 0xFF);

        int minorSinSigno =
                ((datosFabricante[indiceMinor] & 0xFF) << 8) |
                        (datosFabricante[indiceMinor + 1] & 0xFF);

        /*
         * Conversión a entero de 16 bits con signo.
         *
         * Es necesaria porque la temperatura puede ser negativa,
         * por ejemplo -12.
         */
        short valorMedida =
                (short) minorSinSigno;

        int tipoMedida =
                (major >> 8) & 0xFF;

        int contador =
                major & 0xFF;

        Log.d(
                ETIQUETA_LOG,
                "Tipo de medida = " + tipoMedida
        );

        Log.d(
                ETIQUETA_LOG,
                "Contador = " + contador
        );

        Log.d(
                ETIQUETA_LOG,
                "Valor decodificado = " + valorMedida
        );

        if (tipoMedida == 11) {
            ultimoCo2 =
                    (int) valorMedida;

            Log.d(
                    ETIQUETA_LOG,
                    "CO2 recibido = " +
                            ultimoCo2 +
                            " ppm"
            );

        } else if (tipoMedida == 12) {
            ultimaTemperatura =
                    (int) valorMedida;

            Log.d(
                    ETIQUETA_LOG,
                    "Temperatura recibida = " +
                            ultimaTemperatura +
                            " grados"
            );

        } else {
            Log.d(
                    ETIQUETA_LOG,
                    "Tipo de medida desconocido = " +
                            tipoMedida
            );
        }

        /*
         * Cuando ya hemos recibido las dos medidas,
         * podemos informar al usuario.
         */
        if (
                ultimoCo2 != null &&
                        ultimaTemperatura != null
        ) {
            Toast.makeText(
                    MainActivity.this,
                    "Medidas recibidas: CO2 = " +
                            ultimoCo2 +
                            " ppm, temperatura = " +
                            ultimaTemperatura +
                            " °C",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLLE(): empieza ");

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, "                                                     ");
        Log.d(ETIQUETA_LOG, "                                                     ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLLE(): no tengo el permiso BLUETOOTH_CONNECT ");
            return;
        }
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        if (this.elEscanner == null) {

            Toast.makeText(
                    this,
                    "El escáner BLE no está preparado",
                    Toast.LENGTH_LONG
            ).show();

            comprobarPermisosBluetooth();
            return;
        }

        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): buscando " + dispositivoBuscado);

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                String nombre = null;

                if (resultado.getScanRecord() != null) {
                    nombre = resultado.getScanRecord().getDeviceName();
                }

                if (
                        nombre == null &&
                                ActivityCompat.checkSelfPermission(
                                        MainActivity.this,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                ) == PackageManager.PERMISSION_GRANTED
                ) {
                    nombre = resultado.getDevice().getName();
                }

                Log.d(
                        ETIQUETA_LOG,
                        "Dispositivo encontrado: " +
                                nombre +
                                " RSSI: " +
                                resultado.getRssi()
                );

                if ("XBW01".equals(nombre)) {

                    Toast.makeText(
                            MainActivity.this,
                            "Encontrado XBW01, RSSI: " + resultado.getRssi(),
                            Toast.LENGTH_LONG
                    ).show();

                    //mostrarInformacionDispositivoBTLE(resultado);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
//                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);

                Log.e(
                        ETIQUETA_LOG,
                        "buscarTodosLosDispositivosBTLE(): " +
                                "onScanFailed(), codigo=" +
                                errorCode
                );

                Toast.makeText(
                        MainActivity.this,
                        "Error de escaneo BLE: " + errorCode,
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

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

        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieztar scan");

        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        ArrayList<ScanFilter> filtros = new ArrayList<ScanFilter>();
        filtros.add( sf );


        // ANTES this.elEscanner.startScan( this.callbackDelEscaneo);

        // AHORA (2025)
        this.elEscanner.startScan(
                filtros,
                settings,
                this.callbackDelEscaneo
        );

        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): termina");
    } // ()

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

    public void botonSubirDatosPulsado(View view) {

        if (ultimosDatosXBW01 == null) {
            Toast.makeText(
                    this,
                    "Primero debes detectar el nodo XBW01",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String datosHex =
                Utilidades.bytesToHexString(
                        ultimosDatosXBW01
                );

        subirDatosAPlesk(
                "XBW01",
                direccionXBW01,
                ultimoRssi,
                datosHex
        );
    }

    private void subirDatosAPlesk(
            String nombre,
            String direccion,
            int rssi,
            String datosHex
    ) {

        Toast.makeText(
                this,
                "Subiendo datos a Plesk...",
                Toast.LENGTH_SHORT
        ).show();

        /*
         * Android 不允许在主界面线程中执行网络请求，
         * 所以使用一个新的工作线程。
         */
        new Thread(() -> {

            HttpsURLConnection conexion = null;

            try {
                /*
                 * Dirección del punto de entrada PHP.
                 */
                URL url = new URL(
                        "https://bxiang.upv.edu.es/api/upload.php"
                );

                conexion =
                        (HttpsURLConnection) url.openConnection();

                /*
                 * Configuración de la petición HTTP.
                 */
                conexion.setRequestMethod("POST");
                conexion.setConnectTimeout(10000);
                conexion.setReadTimeout(10000);
                conexion.setDoOutput(true);

                conexion.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

                conexion.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                /*
                 * IMPORTANTE:
                 * Sustituye el texto por la misma API Key
                 * configurada en config.php.
                 */
                conexion.setRequestProperty(
                        "X-API-Key",
                        "XBW01_SPRINT0"
                );

                /*
                 * Construcción del JSON que se enviará al servidor.
                 */
                JSONObject json = new JSONObject();

                json.put(
                        "device_name",
                        nombre
                );

                json.put(
                        "device_address",
                        direccion == null
                                ? ""
                                : direccion
                );

                json.put(
                        "rssi",
                        rssi
                );

                json.put(
                        "raw_data",
                        datosHex
                );

                String jsonTexto =
                        json.toString();

                Log.d(
                        ETIQUETA_LOG,
                        "JSON enviado a Plesk: " +
                                jsonTexto
                );

                /*
                 * Escritura del cuerpo JSON.
                 */
                byte[] cuerpo =
                        jsonTexto.getBytes("UTF-8");

                conexion.setFixedLengthStreamingMode(
                        cuerpo.length
                );

                try (
                        OutputStream salida =
                                conexion.getOutputStream()
                ) {
                    salida.write(cuerpo);
                    salida.flush();
                }

                /*
                 * Lectura del código HTTP.
                 */
                int codigoRespuesta =
                        conexion.getResponseCode();

                /*
                 * Para respuestas 2xx se utiliza inputStream.
                 * Para errores 4xx o 5xx se utiliza errorStream.
                 */
                InputStream entrada;

                if (
                        codigoRespuesta >= 200 &&
                                codigoRespuesta < 300
                ) {
                    entrada =
                            conexion.getInputStream();
                } else {
                    entrada =
                            conexion.getErrorStream();
                }

                String respuesta =
                        leerRespuestaServidor(entrada);

                Log.d(
                        ETIQUETA_LOG,
                        "Código HTTP: " +
                                codigoRespuesta
                );

                Log.d(
                        ETIQUETA_LOG,
                        "Respuesta Plesk: " +
                                respuesta
                );

                /*
                 * Volvemos al hilo de la interfaz para mostrar
                 * el resultado mediante Toast.
                 */
                int codigoFinal = codigoRespuesta;
                String respuestaFinal = respuesta;

                runOnUiThread(() -> {

                    if (
                            codigoFinal >= 200 &&
                                    codigoFinal < 300
                    ) {
                        Toast.makeText(
                                MainActivity.this,
                                "Datos guardados en Plesk",
                                Toast.LENGTH_LONG
                        ).show();

                        Log.d(
                                ETIQUETA_LOG,
                                "SUBIDA CORRECTA: " +
                                        respuestaFinal
                        );

                    } else {
                        Toast.makeText(
                                MainActivity.this,
                                "Error del servidor: " +
                                        codigoFinal,
                                Toast.LENGTH_LONG
                        ).show();

                        Log.e(
                                ETIQUETA_LOG,
                                "ERROR DEL SERVIDOR: " +
                                        respuestaFinal
                        );
                    }
                });

            } catch (Exception excepcion) {

                Log.e(
                        ETIQUETA_LOG,
                        "Error al subir datos a Plesk",
                        excepcion
                );

                String mensajeError =
                        excepcion.getClass()
                                .getSimpleName() +
                                ": " +
                                excepcion.getMessage();

                runOnUiThread(() ->
                        Toast.makeText(
                                MainActivity.this,
                                "Error de conexión: " +
                                        mensajeError,
                                Toast.LENGTH_LONG
                        ).show()
                );

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }
    private String leerRespuestaServidor(
            InputStream entrada
    ) throws Exception {

        if (entrada == null) {
            return "";
        }

        StringBuilder respuesta =
                new StringBuilder();

        try (
                BufferedReader lector =
                        new BufferedReader(
                                new InputStreamReader(
                                        entrada,
                                        "UTF-8"
                                )
                        )
        ) {
            String linea;

            while (
                    (linea = lector.readLine()) != null
            ) {
                respuesta.append(linea);
            }
        }

        return respuesta.toString();
    }



} // class