# P03 - Recepción y decodificación de iBeacon en Android

## Herramienta

Microsoft Copilot.

## Idioma original

Chino.

## Objetivo

Recibir en Android los anuncios iBeacon del nodo Arduino, interpretar correctamente Major y Minor y evitar mezclar medidas de otros dispositivos.

## Prompt

La aplicación Android detecta anuncios BLE, pero la interpretación actual utiliza posiciones fijas de los bytes completos del ScanRecord.

La página ha mostrado el valor 22594 como CO2, aunque Arduino estaba enviando otro valor.

Necesito corregir la interpretación de la trama.

La solución debe:

1. leer Manufacturer Specific Data con identificador 0x004C;
2. comprobar el formato iBeacon 02:15;
3. comprobar el UUID del proyecto;
4. extraer Major y Minor;
5. interpretar Minor como entero de 16 bits con signo;
6. distinguir el tipo 11 como CO2;
7. distinguir el tipo 12 como temperatura;
8. rechazar tipos desconocidos;
9. no depender del nombre BLE ni de una dirección MAC fija.

## Requisitos relacionados

- Recepción del beacon BLE.
- Interpretación fiable de medidas.
- Separación de la lógica BLE y la interfaz.
- Evitar datos pertenecientes a otros nodos.

## Archivos relacionados

- MainActivity.java
- IBeaconMeasurementParser.java
- ParsedMeasurement.java
- MeasurementData.java
- MeasurementController.java

## Propuesta de la IA

La IA propuso utilizar:

- getManufacturerSpecificData(0x004c)

y trabajar con la estructura:

- 0-1 tipo iBeacon 02:15
- 2-17 UUID
- 18-19 Major
- 20-21 Minor
- 22 TxPower

## Problemas encontrados

- Interpretación incorrecta 22594

- La primera página interpretó: 58:42

- como un entero: 0x5842 = 22594

- Sin embargo: 0x58 = X; 0x42 = B

- Los bytes pertenecían al nombre XBW, no a la medida.

## Interferencia de otros nodos

En el laboratorio, otros estudiantes utilizaban el mismo UUID original de la asignatura.

Android llegó a recibir:

- CO2 = 1234

desde otro nodo y:

- temperatura = -24

desde el nodo XBW01.

## Modificaciones posteriores

Se asignó el UUID único:

BOWEN-XBW01-2026

El nuevo UUID se configuró en:

Arduino;
el filtro de escaneo Android;
IBeaconMeasurementParser.

## Revisión humana

Se inspeccionaron las tramas recibidas en Logcat y se comprobaron:

CO2:

- 01:C8 = 456

 
Temperatura:

- FF:E8 = -24

## Evidencia de validación

- recepción estable de 456 ppm;
- recepción estable de -24 °C;
- rechazo de tipos desconocidos;
- rechazo de UUID diferentes;
- eliminación de la interferencia de otros nodos;
- pruebas automáticas superadas.

## Resultado final

Android identifica exclusivamente el nodo XBW01 mediante su UUID único y decodifica correctamente las dos medidas.
