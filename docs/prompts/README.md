# Registro de prompts y uso de inteligencia artificial

## Herramienta utilizada

Durante el desarrollo del Sprint 0 se utilizó principalmente
Microsoft Copilot como asistente conversacional.

La interacción con la herramienta se realizó mayoritariamente
en chino, porque este idioma facilitó la comprensión de los
problemas técnicos y de las instrucciones de la asignatura.

## Naturaleza de este registro

Los documentos de esta carpeta contienen una reconstrucción
fiel en español de los prompts y objetivos reales utilizados
durante el desarrollo.

Las conversaciones originales fueron interactivas y estuvieron
formadas por múltiples preguntas, respuestas, pruebas, mensajes
de error y correcciones. Por esta razón, los documentos no son
una transcripción literal completa, sino una organización de
los prompts más importantes por funcionalidad.

## Forma de utilización de la IA

La IA se utilizó para:

- comprender los requisitos del Sprint 0;
- analizar el código inicial proporcionado por el profesorado;
- diagnosticar errores de Arduino y Android;
- interpretar anuncios BLE e iBeacon;
- implementar la comunicación HTTPS con el backend;
- organizar el backend en capas;
- crear la página web de visualización;
- diseñar pruebas automáticas;
- organizar el repositorio Git;
- redactar documentación técnica.

## Revisión humana

Las propuestas de la IA no fueron aceptadas automáticamente.

Cada modificación importante fue revisada y comprobada mediante:

- compilación del firmware Arduino;
- carga del firmware en la placa;
- inspección del puerto serie;
- ejecución de la aplicación Android;
- revisión de Logcat;
- inspección de las tramas BLE;
- revisión de peticiones y respuestas HTTP;
- comprobación de registros en MariaDB;
- consulta de la API JSON;
- comprobación de la página web;
- ejecución de pruebas automáticas;
- commits y versiones estables en GitHub.

Durante el proceso, algunas propuestas iniciales de la IA
produjeron errores o no se adaptaron correctamente al proyecto.
Esas propuestas fueron corregidas después de analizar los
resultados reales.

## Relación entre prompts, diseños y código

Cada documento de esta carpeta indica:

- el objetivo del prompt;
- los requisitos relacionados;
- los archivos afectados;
- la solución propuesta;
- los problemas encontrados;
- las modificaciones realizadas;
- la evidencia utilizada para validar el resultado.

## Agente de revisión

El agente proporcionado por la asignatura no fue la herramienta
principal de generación del código.

El desarrollo se realizó principalmente mediante interacción
conversacional con Microsoft Copilot.

El agente de la asignatura se utiliza posteriormente para
revisar el repositorio completo siguiendo las instrucciones
contenidas en `AGENTS.md`.