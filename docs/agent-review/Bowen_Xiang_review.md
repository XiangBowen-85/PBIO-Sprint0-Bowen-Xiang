# Informe de auditoría — Bowen Xiang

- Fecha de revisión: 2026-10-14
- Repositorio: `C:\Users\xiangbw\Documents\GitHub\PBIO-Sprint0-Bowen-Xiang`
- Rama revisada: `agent-v2-review`
- Alcance: inspección estática de archivos en modo de solo lectura. No se ejecutaron pruebas ni se modificó el repositorio revisado.
- Recursos del agente: `skills/` no está disponible en el espacio de trabajo, por lo que la auditoría se realizó con `AGENTS.md` y ambas especificaciones de `context`.

## Resultados

### 1. Autor
✅ CUMPLE — `author.md` existe en la raíz e identifica a Bowen Xiang.

### 2. Estructura de `doc/`
❌ NO CUMPLE — Motivo: falta el documento de diseño frontend requerido y los diseños Android aparecen fuera de `doc/`; archivo: `doc/` (falta `frontend_business_logic_design.md`; Android está descrito en `docs/design/android-layered-architecture.md`); corrección mínima: añadir el diseño proxy frontend a `doc/` y documentar allí los componentes que implementa; prioridad: alta.

### 3. Estructura de `src/`
✅ CUMPLE — Existen `src/communication/`, `src/business_logic/`, `src/database/`, `src/frontend_business_logic/` y `src/gui/`.

### 4. Formato de todos los diseños
❌ NO CUMPLE — Motivo: los documentos no siguen íntegramente la estructura/secciones prescritas ni presentan consistentemente el diseño lógico formal de sus operaciones; archivo: `doc/*_design.md`; corrección mínima: organizar cada diseño en las secciones requeridas e incluir operaciones con tipos abstractos y diseños lógicos; prioridad: alta.

### 5. Secciones obligatorias
❌ NO CUMPLE — Motivo: no aparece la sección `Design Clarifications` y `Component Design` se sustituye por encabezados variables como `Componente` u `Objetivo`; archivo: `doc/business_logic_design.md`, `doc/communication_design.md`, `doc/database_design.md`; corrección mínima: añadir las secciones diferenciadas requeridas y usar una lista de aclaraciones cuando proceda; prioridad: media.

### 6. Diseños separados por capa
❌ NO CUMPLE — Motivo: hay diseños backend de comunicación, negocio y datos, pero falta el diseño frontend de lógica proxy; archivo: `doc/` (falta `frontend_business_logic_design.md`); corrección mínima: especificar la interfaz cliente y sus responsabilidades de proxy; prioridad: alta.

### 7. Independencia entre comunicación y lógica de negocio
✅ CUMPLE — El diseño y `Backend/business/MeasurementService.php` no implementan transporte; el servicio delega persistencia en `MeasurementRepository`. Las menciones a HTTP/JSON en comentarios explican explícitamente su exclusión.

### 8. Flujo Communication → Business Logic → Database
✅ CUMPLE — `Backend/upload.php`, `Backend/latest.php` y `Web/index.php` llaman a `MeasurementService`, que usa `MeasurementRepository` y `Database`.

### 9. Ausencia de SQL en comunicación
❌ NO CUMPLE — Motivo: el endpoint de diagnóstico ejecuta un `SELECT` directamente; archivo: `Backend/db_test.php`; corrección mínima: mover la consulta a un método de repositorio y hacer que el endpoint lo invoque; prioridad: alta.

### 10. Ausencia de HTTP, cabeceras, códigos de estado y JSON en lógica de negocio
✅ CUMPLE — `Backend/business/MeasurementService.php` no importa ni invoca transporte, cabeceras, estado HTTP o serialización JSON.

### 11. Correspondencia entre diseños e implementación
❌ NO CUMPLE — Motivo: los README de `src/` mapean los componentes a `Backend/`, `Android/` y `Web/`, fuera de `src/`, y el mapeo frontend apunta a un diseño inexistente; archivo: `src/*/README.md`, `doc/` y rutas de implementación; corrección mínima: alinear los diseños, mapeos y ubicación efectiva de los componentes, incluyendo el diseño frontend ausente; prioridad: alta.

### 12. Diseño relacional de `measurements`
❌ NO CUMPLE — Motivo: el diseño no define la tabla ni sus columnas, clave primaria, claves foráneas y restricciones con el formato obligatorio; archivo: `doc/database_design.md`; corrección mínima: documentar `measurements` con todos los campos del formato de `Database_Design_Spec.md` y reflejar el esquema SQL; prioridad: alta.

### 13. Separación entre `MainActivity` y lógica Android
✅ CUMPLE — `MainActivity` delega el análisis y estado a `MeasurementController`/`IBeaconMeasurementParser` y la subida a `PleskClient`; no construye JSON ni abre la conexión HTTPS.

### 14. Encapsulación HTTPS en `PleskClient`
✅ CUMPLE — `PleskClient.java` encapsula `HttpsURLConnection`, configuración de cabeceras, envío y lectura de la respuesta.

### 15. Diseño e implementación del frontend business logic proxy
❌ NO CUMPLE — Motivo: falta el diseño requerido y `PleskClient` expone una operación de subida, pero no una interfaz de dominio con paridad de firma respecto al backend; archivo: falta `doc/frontend_business_logic_design.md` y `Android/.../PleskClient.java`; corrección mínima: documentar e implementar el proxy de dominio con operaciones y tipos alineados con el backend; prioridad: alta.

### 16. Pruebas automáticas
❌ NO CUMPLE — Motivo: hay pruebas Android para parser/controlador, pero no se identifican pruebas automatizadas para el servicio, repositorio ni proxy HTTP, que son métodos críticos; archivo: `Backend/business/MeasurementService.php`, `Backend/database/MeasurementRepository.php`, `Android/.../PleskClient.java`; corrección mínima: añadir pruebas unitarias/de integración para esas operaciones críticas; prioridad: alta.

### 17. Ausencia de credenciales o secretos reales
❌ NO CUMPLE — Motivo: el cliente contiene una clave API literal enviada en cada solicitud; archivo: `Android/Prueba2025/app/src/main/java/org/jordi/prueba2025/PleskClient.java`; corrección mínima: revocar/rotar la clave expuesta y retirarla del repositorio, cargándola desde configuración no versionada; prioridad: crítica.

### 18. Correspondencia de `src/` con componentes documentados
❌ NO CUMPLE — Motivo: `src/` contiene solo README de mapeo y el código real vive en árboles paralelos; además, el mapeo frontal referencia un diseño inexistente; archivo: `src/communication/README.md`, `src/business_logic/README.md`, `src/database/README.md`, `src/frontend_business_logic/README.md`, `src/gui/README.md`; corrección mínima: hacer coincidir los componentes fuente con sus diseños y corregir el mapeo frontend; prioridad: alta.

## Observaciones de alcance

- El esquema SQL existente sí crea la tabla `measurements` y una clave primaria, pero eso no sustituye su especificación obligatoria en `doc/database_design.md`.
- `Backend/config.example.php` usa valores de marcador (`YOUR_*`); la clave incrustada en `PleskClient.java` es el hallazgo de secreto expuesto.
- No se evaluó la corrección funcional en ejecución; las pruebas existentes se identificaron por inspección, sin ejecutarlas.
