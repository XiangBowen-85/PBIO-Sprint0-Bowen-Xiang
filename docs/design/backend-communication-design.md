# Diseño de comunicación del backend

## Objetivo

Gestionar la comunicación entre los clientes y el backend
sin contener reglas de negocio ni consultas SQL.

## Componentes

### `upload.php`

Responsabilidades:

- aceptar peticiones HTTP POST;
- leer la cabecera `X-API-Key`;
- leer y decodificar el cuerpo JSON;
- llamar a `MeasurementService`;
- transformar el resultado en una respuesta HTTP.

No contiene consultas SQL ni valida directamente los valores
de CO2, temperatura o RSSI.

### `latest.php`

Responsabilidades:

- aceptar peticiones HTTP GET;
- solicitar las mediciones recientes a `MeasurementService`;
- devolver una respuesta JSON.

No accede directamente a MariaDB.

### `index.php`

Responsabilidades:

- solicitar la última medición;
- presentar los datos mediante HTML;
- escapar los valores antes de mostrarlos.

No contiene consultas SQL.

### `JsonResponse.php`

Responsabilidades:

- establecer el código HTTP;
- establecer el tipo de contenido;
- codificar la respuesta en JSON;
- finalizar la respuesta.

## Flujo

Cliente
|
v
Punto de entrada HTTP
|
v
MeasurementService
|
v
JsonResponse o interfaz HTML

## Separación de responsabilidades

La capa de comunicación no ejecuta SQL y no implementa las reglas de validación de las mediciones.
