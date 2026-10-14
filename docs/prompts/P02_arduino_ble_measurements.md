# P02 - Emisión BLE de CO2 y temperatura desde Arduino

## Herramienta

Microsoft Copilot.

## Idioma original

Chino.

## Objetivo

Adaptar el código Arduino para generar medidas simuladas de CO2 y temperatura y transmitirlas mediante anuncios iBeacon.

## Prompt

Necesito adaptar el código Arduino proporcionado por el profesor.

La clase Medidor debe producir dos valores simulados:

- una medida de CO2;
- una medida de temperatura.

La clase Publicador debe transmitir las dos medidas mediante iBeacon sin mezclar la obtención de la medida con la comunicación BLE.

Utiliza el campo Major para indicar el tipo de medida y un contador, y utiliza Minor para transportar el valor.

Los tipos de medida deben ser:

- 11 para CO2;
- 12 para temperatura.

La aplicación Android debe poder distinguir posteriormente los dos anuncios.
Mantén separadas las responsabilidades de Medidor, Publicador y EmisoraBLE.

## Requisitos relacionados

- Medición simulada en Arduino.
- Comunicación mediante beacon BLE.
- Separación entre medición, publicación y comunicación.
- Demostración de una medida ficticia.

## Archivos relacionados

- Arduino/HolaMundoIBeacon/Medidor.h
- Arduino/HolaMundoIBeacon/Publicador.h
- Arduino/HolaMundoIBeacon/EmisoraBLE.h
- Arduino/HolaMundoIBeacon/HolaMundoIBeacon.ino

## Propuesta de la IA

Se utilizó el siguiente protocolo:

Major, byte alto:

- 11 = CO2
- 12 = temperatura

 
Major, byte bajo:

- contador de emisión

 
Minor:

- valor de la medida como entero de 16 bits

Ejemplos finales:

- CO2 = 456
- Hexadecimal = 01:C8

- Temperatura = -24
- Complemento a dos = FF:E8

## Revisión humana

Se verificó que el programa Arduino:

- se compilaba correctamente;
- se cargaba mediante DFU;
- mostraba el identificador de firmware por el puerto serie;
- ejecutaba continuamente el método loop;
- alternaba anuncios de CO2 y temperatura.

## Problemas encontrados

Durante el desarrollo se detectaron varios problemas:

- existían varias copias locales del proyecto;
- el nombre BLE anterior no coincidía con el nombre buscado desde Android;
- permanecía activo un anuncio de prueba con el texto MolaMolaMolaMolaMolaM;
- el firmware esperaba indefinidamente la conexión serie;
- el anuncio original no se interpretaba correctamente desde Android;
- se utilizaba inicialmente un UUID compartido con otros estudiantes.

## Modificaciones posteriores

Se realizaron las siguientes correcciones:

- nombre del nodo: XBW01;
- UUID único: BOWEN-XBW01-2026;
- eliminación del anuncio de prueba;
- espera limitada del puerto serie;
- construcción explícita de los datos de fabricante iBeacon;
- aumento temporal del intervalo de emisión para facilitar las pruebas.

## Evidencia de validación

- firmware cargado correctamente;
- mensaje de puerto serie: FIRMWARE XBW01 - 2026-10-12;
- recepción de CO2 456 ppm;
- recepción de temperatura -24 °C;
- visualización de ambos valores en la página web.

## Resultado final

Arduino emite dos anuncios iBeacon diferenciados y mantiene separadas las responsabilidades de medición, publicación y comunicación BLE.
