# P07 - Pruebas automáticas de la lógica Android

## Estado del registro

Reconstrucción fiel en español de una conversación real.

## Herramienta

Microsoft Copilot.

## Idioma original

Chino.

## Objetivo

Crear pruebas automáticas y repetibles para la lógica Android
separada de la interfaz gráfica.

## Prompt

La interpretación iBeacon y el control del estado ya están
separados de MainActivity.

Necesito pruebas automáticas que demuestren que estas clases
funcionan sin depender de los botones de la aplicación.

Las pruebas deben cubrir:

- CO2 positivo;
- temperatura negativa;
- UUID incorrecto;
- tipo de medida desconocido;
- trama demasiado corta;
- estado incompleto con una sola medida;
- estado completo con las dos medidas;
- reinicio del estado.

## Archivos relacionados

- IBeaconMeasurementParserTest.java
- MeasurementControllerTest.java
- IBeaconMeasurementParser.java
- MeasurementController.java
- MeasurementData.java

## Pruebas del parser

Se añadieron cinco pruebas:

- parsesCo2Value456;
- parsesNegativeTemperatureMinus24;
- rejectsUnexpectedUuid;
- rejectsUnknownMeasurementType;
- rejectsShortManufacturerData.

## Pruebas del controlador

Se añadieron dos pruebas:

- becomesReadyAfterBothMeasurements;
- resetRemovesPreviousMeasurements.

## Revisión humana

Las pruebas fueron ejecutadas desde Android Studio en un dispositivo Android conectado.

## Evidencia de validación

Resultado del parser:

- 5 tests passed
- BUILD SUCCESSFUL

Resultado del controlador:

- 2 tests passed
- BUILD SUCCESSFUL

Resultado total:

- 7 tests passed
- Relación con errores reales

La prueba rejectsUnknownMeasurementType reproduce el caso detectado en el laboratorio, donde un nodo cercano emitía el tipo 14.

La prueba del UUID demuestra que el parser rechaza anuncios que no pertenecen al nodo BOWEN-XBW01-2026.

## Resultado final

La lógica crítica de interpretación y coordinación dispone de pruebas automáticas y repetibles.
