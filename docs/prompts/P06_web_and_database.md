# P06 - Almacenamiento y visualización de CO2 y temperatura

## Herramienta

Microsoft Copilot.

## Idioma original

Chino.

## Objetivo

Guardar CO2 y temperatura como campos separados en MariaDB
y mostrarlos correctamente en una página web.

## Prompt

Android recibe correctamente una medida de CO2 y una medida
de temperatura, pero el backend solamente guarda raw_data.

Necesito modificar la cadena completa para que:

1. Android envíe co2_value y temperature_value;
2. PHP valide los dos campos;
3. MariaDB almacene los dos valores;
4. latest.php devuelva los valores;
5. la página web muestre dos tarjetas separadas;
6. los valores negativos de temperatura se conserven;
7. la página siga mostrando dirección BLE, RSSI, fecha y raw_data.

## Requisitos relacionados

- Comunicación Android-servidor.
- Almacenamiento en base de datos.
- Página web de demostración.
- Visualización de la medida ficticia definida en Arduino.

## Archivos relacionados

- MainActivity.java
- PleskClient.java
- Backend/upload.php
- Backend/latest.php
- MeasurementService.php
- MeasurementRepository.php
- Database/bxiang*sprint0_2*\*.sql
- Web/index.php

## Propuesta de la IA

Se añadieron los campos:

- co2_value
- temperature_value

El JSON Android contiene:

- JSON
  {
  "device_name": "XBW01",
  "device_address": "dirección BLE",
  "rssi": -60,
  "co2_value": 456,
  "temperature_value": -24,
  "raw_data": "datos BLE"
  }

La página web muestra:

- Última medida de CO2
- 456 ppm

y:

- Última medida de temperatura
- -24 °C

## Revisión humana

Se realizó una prueba dinámica.

Los valores iniciales:

- CO2 = 123
- temperatura = -12

fueron sustituidos en Arduino por:

- CO2 = 456
- temperatura = -24

## Evidencia de validación

Después de cargar el nuevo firmware:

- Android recibió 456 y -24;
- PHP devolvió HTTP 201;
- MariaDB almacenó ambos valores;
- latest.php devolvió ambos campos;
- la página web cambió automáticamente a 456 ppm y -24 °C.

## Resultado final

La página no contiene valores fijos. Los datos mostrados proceden de Arduino y recorren la cadena completa:

Arduino
→ BLE
→ Android
→ HTTPS
→ PHP
→ MariaDB
→ página web
