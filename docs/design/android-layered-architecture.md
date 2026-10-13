# Arquitectura por capas de la aplicación Android

## Objetivo

Separar la interfaz gráfica, la lógica de negocio de las
mediciones, la interpretación del protocolo BLE y la
comunicación con el servidor.

## Flujo general

Usuario
|
v
MainActivity
|
+--> BluetoothLeScanner
|
+--> MeasurementController
| |
| +--> IBeaconMeasurementParser
| |
| +--> MeasurementData
|
+--> PleskClient
|
v
Backend HTTPS

## Capa de interfaz gráfica

- MainActivity.java

Responsabilidades:

solicitar permisos Bluetooth;
iniciar y detener el escaneo BLE;
gestionar las pulsaciones de los botones;
entregar los datos de fabricante al controlador;
mostrar mensajes mediante Toast;
mostrar información de depuración en Logcat.

MainActivity no interpreta directamente los campos Major y Minor del protocolo iBeacon.

MainActivity no construye el JSON y no ejecuta directamente la petición HTTPS.

## Lógica de negocio

- MeasurementController.java

Responsabilidades:

coordinar la interpretación de las tramas;
actualizar el estado de las mediciones;
reiniciar una sesión de medición;
determinar si la información está completa;
proporcionar el modelo actual a la interfaz.

Esta clase no conoce Activity, Toast, botones ni HTTP.

- MeasurementData.java

Responsabilidades:

almacenar el nombre del dispositivo;
almacenar la dirección BLE;
almacenar el RSSI;
almacenar la medida de CO2;
almacenar la medida de temperatura;
almacenar los datos BLE originales;
indicar si una medición está completa.

- ParsedMeasurement.java

Representa el resultado inmutable de una trama:

tipo de medida;
contador;
valor decodificado.
Interpretación del protocolo BLE
IBeaconMeasurementParser.java

Responsabilidades:

comprobar el formato 02:15;
comprobar el UUID único BOWEN-XBW01-2026;
extraer Major;
extraer Minor;
interpretar números negativos;
distinguir CO2 y temperatura.

Tipos del protocolo:

11: CO2;
12: temperatura.

Esta clase no utiliza Activity, Toast, BluetoothLeScanner, JSON ni HTTP.

## Capa de comunicación

- PleskClient.java

Responsabilidades:

construir el cuerpo JSON;
añadir la API Key;
abrir una conexión HTTPS;
enviar la petición POST;
leer el código HTTP;
leer la respuesta del backend;
comunicar el resultado mediante un callback.

Esta clase no muestra Toast ni accede a elementos de la interfaz gráfica.

- UploadResult.java

Representa el resultado de una petición:

código HTTP;
cuerpo de la respuesta;
indicador de éxito.

## UUID único

El nodo utiliza el UUID:

Plain Text: BOWEN-XBW01-2026

El UUID anterior era compartido por otros nodos de la asignatura y podía provocar que Android combinara medidas de diferentes placas.

El UUID único permite que el filtro BLE acepte exclusivamente las tramas del nodo XBW01.

## Prueba realizada

Valores simulados:

CO2: 456 ppm;
temperatura: -24 °C.

Resultado:

Android recibe ambas medidas;
no se mezclan medidas de otros nodos;
PleskClient envía la medición;
el backend devuelve HTTP 201;
MariaDB almacena los valores;
la página web muestra la última medición.

## Resultado de la separación

La interfaz gráfica, la lógica de negocio, la interpretación BLE y la comunicación remota están implementadas en clases independientes.
