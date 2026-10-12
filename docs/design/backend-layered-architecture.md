# Arquitectura por capas del backend

## Objetivo

Separar completamente la comunicación HTTP, la lógica de negocio
y el acceso a la base de datos del Sprint 0.

## Flujo general

Android
|
| HTTPS + JSON
v
upload.php
|
v
MeasurementService.php
|
v
MeasurementRepository.php
|
v
Database.php
|
v
MariaDB

## Capa de comunicación

- upload.php

Responsabilidades:

comprobar que la petición utiliza POST;
leer la cabecera X-API-Key;
leer el cuerpo JSON;
llamar a la lógica de negocio;
devolver códigos de estado HTTP.

Este archivo no contiene consultas SQL ni reglas de validación de las mediciones.

- latest.php

Responsabilidades:

aceptar peticiones GET;
solicitar las mediciones recientes al servicio;
devolver la información en JSON.

Este archivo no consulta directamente la base de datos.

- index.php

Responsabilidades:

solicitar la última medición al servicio;
mostrar los datos mediante HTML;
escapar los datos antes de mostrarlos.

La página no contiene consultas SQL.

- communication/JsonResponse.php

Responsabilidades:

establecer el código de estado HTTP;
establecer el tipo de contenido JSON;
codificar y devolver la respuesta.

## Capa de lógica de negocio

- business/MeasurementService.php

Responsabilidades:

validar el nombre del dispositivo;
validar RSSI;
validar CO2;
validar temperatura;
validar datos BLE en hexadecimal;
normalizar los valores;
coordinar el acceso al repositorio.

Esta clase no lee cabeceras HTTP, no procesa JSON y no ejecuta SQL.

## Capa de base de datos

- database/Database.php

Responsabilidades:

crear la conexión PDO;
configurar la conexión MariaDB;
reutilizar la conexión.

- database/MeasurementRepository.php

Responsabilidades:

insertar mediciones;
consultar la última medición;
consultar las mediciones recientes.

Esta clase no conoce HTTP, JSON, Android ni la interfaz web.

## Configuración privada

El archivo real config.php contiene:

- dirección de la base de datos;
- nombre de la base de datos;
- usuario;
- contraseña;
- API Key.

El archivo real no se incluye en Git. El repositorio contiene únicamente config.example.php.

## Prueba realizada

La arquitectura refactorizada fue verificada con:

- dispositivo: XBW01;
- CO2: 456 ppm;
- temperatura: -24 °C;
- respuesta de subida: HTTP 201;
- última fila verificada: ID 15;
- consulta JSON mediante latest.php;
- visualización correcta en la página web.

## Resultado

La comunicación, la lógica de negocio y el acceso a datos se encuentran separados en archivos y clases independientes.
