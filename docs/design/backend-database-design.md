# Diseño de acceso a base de datos

## Objetivo

Encapsular completamente la conexión MariaDB y las consultas
relacionadas con las mediciones.

## Componentes

### `Database.php`

Responsabilidades:

- construir el DSN;
- crear la conexión PDO;
- configurar el modo de errores;
- configurar el formato de los resultados;
- reutilizar la conexión.

### `MeasurementRepository.php`

Responsabilidades:

- insertar una medición;
- obtener la última medición;
- obtener las mediciones recientes.

## Operaciones principales

- Database.getConnection()
- MeasurementRepository.insert(measurement)
- MeasurementRepository.findLatest()
- MeasurementRepository.findRecent(limit)

## Flujo

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

## Separación de responsabilidades

La capa de base de datos:

- no procesa JSON;
- no lee peticiones HTTP;
- no comprueba API Keys;
- no devuelve códigos HTTP;
- no conoce Android;
- no genera HTML.
