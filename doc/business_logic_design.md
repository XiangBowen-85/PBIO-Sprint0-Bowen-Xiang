# Diseño de lógica de negocio del backend

## Objetivo

Validar, normalizar y coordinar las mediciones sin depender
de HTTP, JSON o MariaDB.

## Componente

### `MeasurementService.php`

Responsabilidades:

- comprobar el nombre del dispositivo;
- validar RSSI;
- validar CO2;
- validar temperatura;
- validar el formato hexadecimal de `raw_data`;
- normalizar la información;
- solicitar operaciones al repositorio.

## Operaciones principales

- createMeasurement(input)
- validateAndNormalize(input)
- getLatestMeasurement()
- getRecentMeasurements(limit)

## Reglas de negocio

- el dispositivo esperado es XBW01;
- RSSI debe ser un entero dentro del rango aceptado;
- CO2 debe ser un entero no negativo dentro del rango aceptado;
- temperatura debe ser un entero dentro del rango aceptado;
- raw_data debe contener datos hexadecimales;
- la dirección BLE puede ser nula;
- los datos BLE se guardan normalizados en mayúsculas.

## Flujo

Capa de comunicación
|
v
MeasurementService
|
+--> validación
|
+--> normalización
|
v
MeasurementRepository

## Separación de responsabilidades

MeasurementService:

- no lee php://input;
- no lee cabeceras HTTP;
- no devuelve códigos HTTP;
- no codifica JSON;
- no contiene SQL;
- no crea conexiones PDO.

# General Rules

 

- Programming Language: PHP 8 with strict types.

- Function/Method Headers: Every function and method must include its logical design in a dedicated comment block bounded by dashed lines.

- Code Readability: The implementation must use clear and self-explanatory names and should require minimal additional inline comments.

- Automated Testing: Unit or integration tests must be generated for all critical functions and methods.

- Responsibility Segregation: Communication, business logic and database access must remain in separate components.

- Security: Real credentials and API keys must not be stored in the public repository.
