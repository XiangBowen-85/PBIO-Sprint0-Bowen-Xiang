# PBIO - Sprint 0

## Autor

Bowen Xiang

## Asignatura

Proyecto Aplicaciones de Biometría y Medio Ambiente

## Objetivo

El objetivo del Sprint 0 es construir un prototipo funcional
que comunique una placa Arduino con una aplicación Android
mediante Bluetooth Low Energy y que envíe las medidas a un
servidor remoto con PHP y MariaDB.

## Flujo del sistema

1. Arduino genera medidas simuladas de CO2 y temperatura.
2. Las medidas se codifican en anuncios iBeacon BLE.
3. La aplicación Android recibe y decodifica los anuncios.
4. Android envía los datos al backend mediante HTTPS y JSON.
5. El backend valida los datos y los almacena en MariaDB.
6. Una página web consulta y muestra la última medición.

## Estado del prototipo

En la versión inicial almacenada en este repositorio se ha
verificado el siguiente flujo:

- firmware Arduino cargado correctamente;
- nodo BLE identificado como `XBW01`;
- UUID del proyecto: `EPSG-GTI-PROY-3A`;
- recepción de anuncios iBeacon en Android;
- medida simulada de CO2: `123 ppm`;
- medida simulada de temperatura: `-12 °C`;
- envío HTTPS desde Android al backend PHP;
- almacenamiento en la tabla `measurements`;
- consulta de la última medición mediante una página web.

La página web de esta versión todavía interpreta únicamente
el valor `minor` de la última trama almacenada. La separación
definitiva de CO2 y temperatura se realizará en una versión
posterior.

## Estructura del repositorio

- `Arduino/`: firmware y clases de emisión BLE.
- `Android/`: aplicación móvil Android.
- `Backend/`: API PHP y configuración de ejemplo.
- `Database/`: exportación SQL de la base de datos.
- `Web/`: página de consulta de la última medición.
- `docs/design/`: diseños de ingeniería inversa y arquitectura.
- `docs/prompts/`: prompts utilizados durante el desarrollo.
- `docs/acceptance/`: criterios de aceptación.
- `docs/evidence/`: capturas y evidencias de funcionamiento.
- `docs/assignments/`: entregas previas del Sprint 0.
- `docs/agent-review/`: instrucciones del agente revisor.

## Protocolo BLE

El UUID utilizado por el nodo es:

- EPSG-GTI-PROY-3A

El campo major contiene:

- byte alto 11: medida de CO2;
- byte alto 12: medida de temperatura;
- byte bajo: contador de emisión.

El campo minor contiene el valor de la medida:

- CO2: 123;
- temperatura: -12.
- Seguridad

## Seguridad

Las credenciales reales y la API Key no se incluyen en el repositorio. El archivo config.example.php contiene solamente valores de ejemplo.

## Autoría

Parte del código inicial fue proporcionada por el profesorado. Las adaptaciones, integraciones, documentación y pruebas del Sprint 0 han sido realizadas por Bowen Xiang con apoyo de herramientas de inteligencia artificial.

## Historial Git

El repositorio Git se creó durante la fase final de integración y documentación del Sprint 0. El trabajo anterior se realizó mediante copias locales y entregas parciales.

## Estado final del Sprint 0

 
La versión disponible en la rama `develop` constituye la
versión funcional preparada para la demostración del Sprint 0.

 

### Flujo verificado

 
Arduino XBW01
|
| BLE iBeacon
v
Aplicación Android
|
| HTTPS + JSON
v
Backend PHP
|
v
MariaDB
|
v
Página web

## Funcionalidades verificadas

- Arduino genera medidas simuladas de CO2 y temperatura.
- El nodo utiliza el nombre BLE XBW01.
- El UUID único utilizado es BOWEN-XBW01-2026.
- Android recibe y decodifica los anuncios iBeacon.
- Android distingue correctamente:
  tipo 11: CO2;
  tipo 12: temperatura.
- Android evita mezclar datos de otros nodos del laboratorio.
- La aplicación almacena ambas medidas en su modelo de negocio.
- PleskClient encapsula la comunicación HTTPS.
- El backend devuelve HTTP 201 después de una subida correcta.
- El backend está separado en:
  comunicación;
  lógica de negocio;
  acceso a base de datos.
- MariaDB almacena CO2, temperatura, RSSI, dirección BLE, datos originales y fecha de recepción.
- La página web muestra la última lectura almacenada.
- La API latest.php devuelve las mediciones recientes en JSON.

## Valores de la demostración

- CO2: 456 ppm
- Temperatura: -24 °C

Los valores fueron modificados desde una versión anterior con 123 ppm y -12 °C. La actualización correcta en Android, MariaDB y la página web demuestra que los valores no están escritos de forma fija en la interfaz web.

## Arquitectura Android

MainActivity
|
+--> MeasurementController
| |
| +--> IBeaconMeasurementParser
| +--> MeasurementData
|
+--> PleskClient

### Responsabilidades principales:

- MainActivity: interfaz, permisos, botones y control de escaneo.
- MeasurementController: coordinación del estado de las medidas.
- IBeaconMeasurementParser: validación y decodificación iBeacon.
- MeasurementData: modelo de la medición actual.
- PleskClient: JSON, HTTPS y respuesta del servidor.
- UploadResult: resultado de la operación remota.

## Arquitectura backend

upload.php / latest.php / index.php
|
v
MeasurementService
|
v
MeasurementRepository
|
v
Database
|
v
MariaDB

### Responsabilidades principales:

- comunicación HTTP y presentación;
- validación y normalización de negocio;
- consultas y persistencia;
- conexión PDO.

## Pruebas automáticas

Se añadieron siete pruebas automáticas para la lógica Android:

- cinco pruebas de IBeaconMeasurementParser;
- dos pruebas de MeasurementController.

Las pruebas cubren:

- CO2 positivo;
- temperatura negativa;
- UUID incorrecto;
- tipo de medida desconocido;
- trama demasiado corta;
- estado incompleto y completo;
- reinicio del estado.

## Revisión automática

El repositorio fue revisado mediante el agente Sprint Reviewer v2.

El primer informe está almacenado en:

- docs/agent-review/Bowen_Xiang_review.md

La revisión automática se utilizó como autoevaluación complementaria. La versión funcional se mantuvo estable y no se realizaron cambios estructurales de riesgo antes de la demostración.

## Estado de la demostración

La siguiente secuencia ha sido comprobada:

- cargar el firmware en Arduino;
- iniciar el escaneo desde Android;
- recibir 456 ppm y -24 °C;
- pulsar el botón de subida;
- recibir HTTP 201;
- comprobar la nueva fila en MariaDB;
- actualizar la página web;
- comprobar la última lectura.

## Página web

https://bxiang.upv.edu.es/api/

## Rama de demostración

develop

Esta rama contiene la versión utilizada para la demostración funcional del Sprint 0.
