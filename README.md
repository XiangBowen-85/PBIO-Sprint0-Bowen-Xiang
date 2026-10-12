# PBIO - Sprint 0

## Autor

Bowen Xiang

## Asignatura

Proyecto Aplicaciones de Biometría y Medio Ambiente

## Objetivo

El objetivo del Sprint 0 es construir un prototipo funcional
que comunique una placa Arduino con una aplicación Android
mediante Bluetooth Low Energy y que envíe las medidas a un
servidor remoto con PHP y MariaDB.

## Flujo del sistema

1. Arduino genera medidas simuladas de CO2 y temperatura.
2. Las medidas se codifican en anuncios iBeacon BLE.
3. La aplicación Android recibe y decodifica los anuncios.
4. Android envía los datos al backend mediante HTTPS y JSON.
5. El backend valida los datos y los almacena en MariaDB.
6. Una página web consulta y muestra la última medición.

## Estado del prototipo

En la versión inicial almacenada en este repositorio se ha
verificado el siguiente flujo:

- firmware Arduino cargado correctamente;
- nodo BLE identificado como `XBW01`;
- UUID del proyecto: `EPSG-GTI-PROY-3A`;
- recepción de anuncios iBeacon en Android;
- medida simulada de CO2: `123 ppm`;
- medida simulada de temperatura: `-12 °C`;
- envío HTTPS desde Android al backend PHP;
- almacenamiento en la tabla `measurements`;
- consulta de la última medición mediante una página web.

La página web de esta versión todavía interpreta únicamente
el valor `minor` de la última trama almacenada. La separación
definitiva de CO2 y temperatura se realizará en una versión
posterior.

## Estructura del repositorio

- `Arduino/`: firmware y clases de emisión BLE.
- `Android/`: aplicación móvil Android.
- `Backend/`: API PHP y configuración de ejemplo.
- `Database/`: exportación SQL de la base de datos.
- `Web/`: página de consulta de la última medición.
- `docs/design/`: diseños de ingeniería inversa y arquitectura.
- `docs/prompts/`: prompts utilizados durante el desarrollo.
- `docs/acceptance/`: criterios de aceptación.
- `docs/evidence/`: capturas y evidencias de funcionamiento.
- `docs/assignments/`: entregas previas del Sprint 0.
- `docs/agent-review/`: instrucciones del agente revisor.

## Protocolo BLE

El UUID utilizado por el nodo es:

- EPSG-GTI-PROY-3A

El campo major contiene:

- byte alto 11: medida de CO2;
- byte alto 12: medida de temperatura;
- byte bajo: contador de emisión.

El campo minor contiene el valor de la medida:

- CO2: 123;
- temperatura: -12.
- Seguridad

## Seguridad

Las credenciales reales y la API Key no se incluyen en el repositorio. El archivo config.example.php contiene solamente valores de ejemplo.

## Autoría

Parte del código inicial fue proporcionada por el profesorado. Las adaptaciones, integraciones, documentación y pruebas del Sprint 0 han sido realizadas por Bowen Xiang con apoyo de herramientas de inteligencia artificial.

## Historial Git

El repositorio Git se creó durante la fase final de integración y documentación del Sprint 0. El trabajo anterior se realizó mediante copias locales y entregas parciales.

## 