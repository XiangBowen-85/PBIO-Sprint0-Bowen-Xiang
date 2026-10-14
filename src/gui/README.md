# GUI source mapping

The Android graphical interface implementation is located at:

- `Android/Prueba2025/app/src/main/java/org/jordi/prueba2025/MainActivity.java`
- `Android/Prueba2025/app/src/main/res/layout/activity_main.xml`

The GUI manages buttons, permissions, scanning control and
user feedback.

The GUI delegates measurement logic to `MeasurementController`
and HTTPS communication to `PleskClient`.
