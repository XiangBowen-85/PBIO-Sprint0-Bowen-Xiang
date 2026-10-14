# P01 - Ingeniería inversa del código proporcionado

## Herramienta

Microsoft Copilot.

## Idioma original

Chino.

## Objetivo

Comprender la estructura y las responsabilidades del código
Arduino y Android proporcionado por el profesorado antes de
realizar modificaciones.

## Prompt

Analiza el código proporcionado por el profesor para Arduino
y Android.

Necesito comprender qué responsabilidad tiene cada clase,
qué funciones se llaman entre sí y cómo circulan los datos
desde la medición hasta el anuncio BLE y desde el anuncio BLE
hasta la aplicación Android.

Organiza el resultado de forma que pueda utilizarlo para crear
un diseño lógico siguiendo la notación de la asignatura.

No generes todavía una nueva implementación. Primero identifica:

1. clases y módulos;
2. responsabilidades;
3. funciones públicas y privadas;
4. relaciones y llamadas;
5. datos de entrada y salida;
6. dependencias entre comunicación y lógica;
7. posibles problemas de separación de responsabilidades.

## Requisitos relacionados

- Ingeniería inversa del código proporcionado.
- Diseño lógico de Arduino.
- Diseño lógico de Android.
- Uso de la notación indicada por la asignatura.

## Archivos relacionados

- docs/design/Diseno_logico_ingenieria_inversa.pdf
- código Arduino original;
- código Android original;
- TramaIBeacon.java;
- Utilidades.java;
- clases originales de emisión BLE.

## Propuesta de la IA

La IA propuso identificar los siguientes componentes principales:

## Arduino

- Medidor: obtención de medidas simuladas;
- Publicador: codificación y publicación de medidas;
- EmisoraBLE: comunicación Bluetooth Low Energy;
- programa principal: inicialización y coordinación;
- PuertoSerie: comunicación de depuración;
- LED: indicación visual.

## Android

- MainActivity: interfaz, permisos y control del escaneo;
- TramaIBeacon: interpretación de la trama original;
- Utilidades: conversiones entre bytes, números y texto.

## Revisión humana

El análisis se comparó manualmente con los archivos originales y con el comportamiento observado durante la ejecución.

Los diagramas se dibujaron manualmente siguiendo las orientaciones obtenidas durante la conversación.

## Problemas encontrados

La primera documentación se centraba principalmente en la estructura original de Arduino y Android y no incluía todavía:

- el backend PHP;
- la base de datos;
- la página web;
- las nuevas clases creadas posteriormente;
- la arquitectura final por capas.

## Resultado final

Se elaboró el documento: Diseno_logico_ingenieria_inversa.pdf

El documento constituye el punto de partida del diseño, pero los diseños correspondientes al código nuevo se documentaron posteriormente en archivos separados.
