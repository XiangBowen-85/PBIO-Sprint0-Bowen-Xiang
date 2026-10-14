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

# General Rules

- Programming Language: PHP 8 with strict types.

- Function/Method Headers: Every function and method must include its logical design in a dedicated comment block bounded by dashed lines.

- Code Readability: The implementation must use clear and self-explanatory names and should require minimal additional inline comments.

- Automated Testing: Unit or integration tests must be generated for all critical functions and methods.

- Responsibility Segregation: Communication, business logic and database access must remain in separate components.

- Security: Real credentials and API keys must not be stored in the public repository.
