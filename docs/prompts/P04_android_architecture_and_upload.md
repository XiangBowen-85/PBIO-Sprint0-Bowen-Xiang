# P04 - Separación de la interfaz, lógica y comunicación Android

## Herramienta

Microsoft Copilot.

## Idioma original

Chino.

## Objetivo

Separar `MainActivity` de la lógica de negocio de las
mediciones y de la comunicación HTTPS con Plesk.

## Prompt

Actualmente MainActivity realiza demasiadas tareas:

- solicita permisos;
- inicia el escaneo BLE;
- interpreta iBeacon;
- almacena CO2 y temperatura;
- decide si la medición está completa;
- construye JSON;
- realiza la petición HTTPS;
- procesa la respuesta;
- muestra Toast.

El profesor exige que la interfaz gráfica esté separada de la
lógica de negocio fake.

Ayúdame a reorganizar el código en clases independientes,
manteniendo el mismo comportamiento y modificando el proyecto
en pequeñas etapas verificables.

## Requisitos relacionados

- Separación de interfaz gráfica y lógica de negocio.
- Segregación de responsabilidades.
- Correspondencia entre diseño y código.
- Comunicación Android-servidor.

## Archivos relacionados

- MainActivity.java
- MeasurementController.java
- MeasurementData.java
- ParsedMeasurement.java
- IBeaconMeasurementParser.java
- PleskClient.java
- UploadResult.java

## Propuesta de la IA

La reorganización se dividió en tres etapas.

### Etapa A: parser

Se crearon:

- IBeaconMeasurementParser;
- ParsedMeasurement;
- MeasurementData.

### Etapa B: controlador

Se creó:

- MeasurementController.

Responsabilidades:

- coordinar la interpretación;
- actualizar el modelo;
- reiniciar una sesión;
- indicar si la medición está completa.

### Etapa C: comunicación

Se crearon:

- PleskClient;
- UploadResult.

Responsabilidades:

- construir JSON;
- realizar HTTPS POST;
- enviar API Key;
- leer código HTTP;
- leer respuesta;
- devolver el resultado mediante callback.

## Revisión humana

Cada etapa fue compilada y probada antes de continuar.

Después de cada refactorización se comprobó:

- recepción de ambas medidas;
- estado completo del controlador;
- respuesta HTTP 201;
- nueva fila en MariaDB;
- actualización de la página web.

## Problemas encontrados

Durante la etapa B se pulsó manualmente el botón de detener antes de recibir la segunda medida.

El controlador rechazó correctamente la subida con el mensaje:

- Todavía no se han recibido las medidas de CO2 y temperatura.

La prueba se repitió sin detener manualmente el escaneo y se recibieron correctamente ambas medidas.

## Resultado final

La estructura final es:

MainActivity

- interfaz, permisos y botones

 
MeasurementController

- coordinación de la lógica de negocio

 
IBeaconMeasurementParser

- interpretación del protocolo BLE

 
MeasurementData

- estado de la medición

 
PleskClient

- comunicación HTTPS

 
UploadResult

- resultado de comunicación

MainActivity ya no interpreta directamente Major y Minor y tampoco contiene la implementación de la conexión HTTPS.
