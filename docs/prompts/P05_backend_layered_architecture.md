# P05 - Arquitectura por capas del backend PHP

## Herramienta

Microsoft Copilot.

## Idioma original

Chino.

## Objetivo

Separar completamente la comunicación, la lógica de negocio
y el acceso a la base de datos en el backend PHP.

## Prompt

El archivo upload.php actual realiza demasiadas tareas:

- comprueba el método HTTP;
- valida la API Key;
- lee JSON;
- valida campos;
- conecta con MariaDB;
- ejecuta INSERT;
- devuelve JSON.

El profesor exige tres diseños y capas separadas:

1. comunicación;
2. lógica de negocio;
3. base de datos.

La comunicación y la lógica de negocio deben estar separadas
completamente.

Refactoriza el backend sin cambiar la URL utilizada por la
aplicación Android.

## Requisitos relacionados

- Tres diseños separados en Backend.
- Separación completa de comunicación y negocio.
- Separación del acceso a base de datos.
- Mantenimiento de la URL /api/upload.php.

## Archivos relacionados

- Backend/upload.php
- Backend/latest.php
- Backend/communication/JsonResponse.php
- Backend/business/MeasurementService.php
- Backend/database/Database.php
- Backend/database/MeasurementRepository.php
- Backend/config.example.php
- Web/index.php
- docs/design/backend-layered-architecture.md

## Propuesta de la IA

Se propuso la siguiente separación:

### Comunicación

- upload.php;
- latest.php;
- index.php;
- JsonResponse.php.

### Lógica de negocio

- MeasurementService.php.
- Base de datos
- Database.php;
- MeasurementRepository.php.

## Reglas de negocio implementadas

- nombre del dispositivo obligatorio;
- dispositivo esperado XBW01;
- RSSI entero y dentro de un rango aceptable;
- CO2 entero y dentro de un rango aceptable;
- temperatura entera, incluyendo valores negativos;
- datos BLE con formato hexadecimal;
- normalización de los datos antes de guardarlos.

## Problemas encontrados

El servidor Plesk utiliza un sistema de archivos que distingue mayúsculas y minúsculas.

El archivo fue subido inicialmente como:

- database.php

pero el código requería:

- Database.php

Esto produjo un error HTTP 500.

## Modificaciones posteriores

- corrección del nombre a Database.php;
- migración gradual de upload.php;
- migración de latest.php;
- migración de index.php;
- eliminación final de funciones temporales de compatibilidad.

## Revisión humana

Se verificaron:

- respuesta HTTP 201;
- registros MariaDB;
- consulta JSON;
- página web;
- eliminación de SQL en los puntos de entrada;
- funcionamiento después de eliminar las funciones antiguas.

## Resultado final

La comunicación HTTP, la lógica de negocio y el acceso a MariaDB se encuentran en clases y archivos independientes.
