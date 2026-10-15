# UD01-LAB02 — Radiografía de una aplicación móvil Android

## Escenario

Te entregan una pequeña aplicación móvil Android desarrollada con Kotlin.

La aplicación muestra información básica del entorno, mantiene una lista local de avisos de seguridad y permite añadir un nuevo aviso.

Tu objetivo no es aprender Android en profundidad ni modificar la aplicación. Debes aprender a orientarte dentro de un proyecto móvil desconocido y obtener evidencias concretas sobre:

- lenguaje y plataforma;
- estructura del proyecto;
- dependencias;
- configuración;
- punto de entrada;
- build;
- artefacto generado;
- runtime;
- pruebas;
- configuración de seguridad;
- tratamiento de configuración y secretos.

## Reglas

- Trabaja sobre la versión entregada del repositorio.
- No modifiques archivos antes de responder las preguntas que soliciten números de línea.
- Puedes utilizar terminal, Android Studio y las herramientas del SDK de Android.
- Las respuestas deben proceder de una evidencia observable: archivo, línea, salida de un comando, APK generado o ejecución en un emulador/dispositivo.
- No es necesario entregar memoria. La evidencia del laboratorio será el cuestionario Moodle.

## Requisitos

La forma más sencilla de trabajar es utilizar Android Studio con:

- JDK 17 o superior;
- Android SDK 35;
- un emulador Android o un dispositivo con depuración USB para las preguntas de ejecución.

## Configuración local

Android Studio suele crear automáticamente un archivo `local.properties` con la ubicación de tu SDK.

Puedes añadir en ese mismo archivo:

```properties
APP_ENV=development
API_BASE_URL=https://api.example.test
DEMO_API_KEY=replace-me
```

También se proporciona `local.properties.example` como referencia.

`local.properties` está excluido de Git. No debes subir valores reales de configuración o credenciales al repositorio.

> Importante: incluso aunque una clave se introduzca desde `local.properties`, si termina incorporada a `BuildConfig` quedará empaquetada dentro de la aplicación. Por tanto, una API key de larga duración no puede considerarse secreta por el mero hecho de no estar en Git. Los secretos reales deben mantenerse en un backend o sustituirse por credenciales temporales adecuadas.

## Construcción

Desde Android Studio puedes utilizar **Build > Make Project** o ejecutar:

```bash
./gradlew assembleDebug
```

El APK de depuración se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Pruebas unitarias

Ejecuta:

```bash
./gradlew testDebugUnitTest
```

Los resultados XML quedan en:

```text
app/build/test-results/testDebugUnitTest/
```

## Instalación en un emulador o dispositivo

Con un dispositivo visible en `adb devices`:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Puedes arrancar la actividad principal con:

```bash
adb shell am start -n org.iesalixar.pps.radiografia/.MainActivity
```

Y comprobar que el paquete está instalado con:

```bash
adb shell pm list packages | grep org.iesalixar.pps.radiografia
```

## Qué debes observar

No memorices Android. Sigue el proyecto:

```text
Repositorio
   ↓
Gradle
   ↓
AndroidManifest.xml
   ↓
MainActivity
   ↓
Repository / Validator
   ↓
Recursos XML
   ↓
Tests
   ↓
APK
   ↓
Android Runtime (ART)
```
