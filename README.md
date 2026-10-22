# UD01-LAB02 — Radiografía de una aplicación móvil Android

## Escenario

Te entregan una pequeña aplicación móvil Android desarrollada con **Kotlin**.

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

---

## Reglas

- Trabaja sobre la versión entregada del repositorio.
- No modifiques archivos antes de responder las preguntas que soliciten números de línea.
- Puedes utilizar terminal, Android Studio y las herramientas del SDK de Android.
- Las respuestas deben proceder de una evidencia observable: archivo, línea, salida de un comando, APK generado o ejecución en un emulador/dispositivo.
- No es necesario entregar memoria. La evidencia del laboratorio será el cuestionario Moodle.

---

# 1. Requisitos

Para realizar el laboratorio necesitas:

- JDK 17 o superior;
- Android SDK;
- Android SDK Platform 35;
- Android Command-line Tools;
- Android Emulator;
- una imagen de sistema Android;
- `adb`;
- opcionalmente Android Studio.

En Ubuntu puedes instalar inicialmente:

```bash
sudo apt update

sudo apt install android-sdk
sudo apt install google-android-emulator-installer
sudo apt install google-android-cmdline-tools-13.0-installer
```

Instalar Android Studio:

```bash
sudo snap install android-studio --classic
```

Comprueba las herramientas:

```bash
java -version
adb version
emulator -version
sdkmanager --version
avdmanager --version
```

También puedes comprobar su ubicación:

```bash
which adb
which emulator
which sdkmanager
which avdmanager
```

---

# 2. Configurar el Android SDK

En algunos sistemas Ubuntu, el SDK instalado mediante paquetes se encuentra en:

```text
/usr/lib/android-sdk
```

Sin embargo, esa ubicación pertenece al sistema y puede generar problemas de permisos al instalar nuevas imágenes.

Para este laboratorio utilizaremos un SDK dentro del directorio personal del usuario:

```bash
mkdir -p "$HOME/Android/Sdk"
```

Configura las variables de entorno:

```bash
export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
export ANDROID_HOME="$HOME/Android/Sdk"
```

Añade las herramientas al `PATH`:

```bash
export PATH="$ANDROID_SDK_ROOT/platform-tools:$PATH"
```

Comprueba:

```bash
echo $ANDROID_SDK_ROOT
echo $ANDROID_HOME
```

Deberías obtener una ruta similar a:

```text
/home/usuario/Android/Sdk
```

Para hacer permanente esta configuración puedes añadir al final de `~/.bashrc`:

```bash
export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_SDK_ROOT/platform-tools:$PATH"
```

Después recarga:

```bash
source ~/.bashrc
```

---

# 3. Instalar Android SDK 35

Instala los componentes necesarios:

```bash
sdkmanager \
  --sdk_root="$HOME/Android/Sdk" \
  "platform-tools" \
  "platforms;android-35" \
  "system-images;android-35;google_apis;x86_64"
```

Acepta las licencias:

```bash
yes | sdkmanager \
  --sdk_root="$HOME/Android/Sdk" \
  --licenses
```

Comprueba que la imagen de sistema se ha instalado:

```bash
find "$HOME/Android/Sdk/system-images" \
  -maxdepth 4 \
  -type d
```

Deberías localizar una ruta similar a:

```text
/home/usuario/Android/Sdk/system-images/android-35/google_apis/x86_64
```

---

# 4. Crear un emulador Android

Una aplicación Android necesita un dispositivo físico o un emulador para poder ejecutarse.

Crea un dispositivo virtual llamado:

```text
PPS_API_35
```

Ejecuta:

```bash
avdmanager create avd \
  -n PPS_API_35 \
  -k "system-images;android-35;google_apis;x86_64"
```

Comprueba los dispositivos virtuales disponibles:

```bash
emulator -list-avds
```

Debería aparecer:

```text
PPS_API_35
```

---

# 5. Arrancar el emulador

Arranca el dispositivo virtual:

```bash
emulator -avd PPS_API_35
```

Espera hasta que Android termine de iniciar.

En otra terminal comprueba que `adb` detecta el emulador:

```bash
adb devices
```

Deberías obtener algo similar a:

```text
List of devices attached
emulator-5554    device
```

Antes de continuar debe aparecer al menos un dispositivo con estado:

```text
device
```

No debe aparecer vacío, `offline` o `unauthorized`.

---

# 6. Configuración local del proyecto

El proyecto puede utilizar un archivo:

```text
local.properties
```

Android Studio puede crearlo automáticamente indicando dónde se encuentra el SDK.

En este entorno puedes definirlo manualmente:

```properties
sdk.dir=/home/usuario/Android/Sdk
```

Sustituye `usuario` por tu nombre de usuario.

También puedes añadir:

```properties
APP_ENV=development
API_BASE_URL=https://api.example.test
DEMO_API_KEY=replace-me
```

El proyecto incluye:

```text
local.properties.example
```

como referencia.

`local.properties` está excluido de Git y no debe contener credenciales reales que se suban al repositorio.

> Importante: aunque una clave se introduzca desde `local.properties`, si posteriormente se incorpora a `BuildConfig`, quedará empaquetada dentro de la aplicación. Por tanto, una API key no puede considerarse secreta únicamente porque no esté almacenada en Git.

Los secretos reales deberían mantenerse en servicios backend o sustituirse por credenciales temporales y mecanismos adecuados de autenticación.

---

# 7. Compatibilidad Java / Kotlin

El proyecto debe compilar Java y Kotlin utilizando una versión compatible.

En `app/build.gradle.kts` debe aparecer una configuración similar a:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = "17"
}
```

Esto evita errores como:

```text
compileDebugJavaWithJavac -> 1.8
compileDebugKotlin       -> 21
```

Java y Kotlin deben generar bytecode compatible.

Comprueba también:

```bash
java -version
```

---

# 8. Construcción de la aplicación

Desde la raíz del proyecto limpia primero compilaciones anteriores:

```bash
./gradlew clean
```

Después construye la versión de depuración:

```bash
./gradlew assembleDebug
```

Si el proceso finaliza correctamente aparecerá:

```text
BUILD SUCCESSFUL
```

El APK generado se encuentra en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Compruébalo:

```bash
ls -lh app/build/outputs/apk/debug/
```

---

# 9. Pruebas unitarias

Ejecuta:

```bash
./gradlew testDebugUnitTest
```

Los resultados quedan almacenados en:

```text
app/build/test-results/testDebugUnitTest/
```

Puedes comprobar los ficheros generados con:

```bash
ls app/build/test-results/testDebugUnitTest/
```

---

# 10. Instalar la aplicación

Antes de instalar comprueba:

```bash
adb devices
```

Debe aparecer el emulador:

```text
emulator-5554    device
```

Instala el APK:

```bash
adb install -r \
  app/build/outputs/apk/debug/app-debug.apk
```

Si todo funciona correctamente deberías obtener:

```text
Performing Streamed Install
Success
```

La opción:

```text
-r
```

permite reinstalar o actualizar una aplicación ya existente.

---

# 11. Comprobar que la aplicación está instalada

Ejecuta:

```bash
adb shell pm list packages \
  | grep org.iesalixar.pps.radiografia
```

Resultado esperado:

```text
package:org.iesalixar.pps.radiografia
```

---

# 12. Arrancar la aplicación desde terminal

Puedes abrir directamente la actividad principal:

```bash
adb shell am start \
  -n org.iesalixar.pps.radiografia/.MainActivity
```

La aplicación debería aparecer en el emulador.

---

# 13. Comprobar el proceso

Con la aplicación arrancada:

```bash
adb shell pidof \
  org.iesalixar.pps.radiografia
```

Si quieres comprobar automáticamente que existe un proceso:

```bash
adb shell pidof \
  org.iesalixar.pps.radiografia \
  | wc -w
```

Resultado esperado:

```text
1
```

---

# 14. Observar el aislamiento de Android

Cada aplicación Android se ejecuta normalmente con un usuario propio.

Comprueba:

```bash
adb shell run-as \
  org.iesalixar.pps.radiografia \
  id
```

También puedes consultar el UID asignado:

```bash
adb shell dumpsys package \
  org.iesalixar.pps.radiografia \
  | grep userId
```

Esto permite observar uno de los mecanismos básicos de aislamiento de aplicaciones Android.

---

# 15. Qué debes observar

No necesitas memorizar Android.

Debes ser capaz de seguir el recorrido del proyecto:

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
Build
   ↓
APK
   ↓
Instalación
   ↓
Android Runtime (ART)
```

Durante el laboratorio debes ser capaz de responder preguntas como:

- ¿Qué lenguaje utiliza la aplicación?
- ¿Qué versión mínima de Android admite?
- ¿Dónde se declaran las dependencias?
- ¿Qué fichero configura la aplicación?
- ¿Cuál es la actividad principal?
- ¿Cómo se genera el APK?
- ¿Dónde queda almacenado?
- ¿Cómo se instala?
- ¿Cómo se identifica su proceso?
- ¿Qué datos se introducen en `BuildConfig`?
- ¿Qué configuraciones de seguridad aparecen en `AndroidManifest.xml`?

---

# Problemas frecuentes

## `adb: no devices/emulators found`

Comprueba:

```bash
adb devices
```

Si no aparece ningún dispositivo, arranca el emulador:

```bash
emulator -avd PPS_API_35
```

---

## `PANIC: Cannot find AVD system path`

Comprueba:

```bash
echo $ANDROID_SDK_ROOT
```

Debe apuntar a:

```text
$HOME/Android/Sdk
```

Comprueba también que exista:

```bash
find "$HOME/Android/Sdk/system-images" \
  -maxdepth 4 \
  -type d
```

---

## `sdkmanager: Failed to read or create install properties file`

No instales las nuevas imágenes dentro de:

```text
/usr/lib/android-sdk
```

Utiliza:

```text
$HOME/Android/Sdk
```

y ejecuta:

```bash
sdkmanager \
  --sdk_root="$HOME/Android/Sdk" \
  ...
```

---

## Error de versión Java / Kotlin

Si aparece:

```text
compileDebugJavaWithJavac (1.8)
compileDebugKotlin (21)
```

comprueba en `app/build.gradle.kts`:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = "17"
}
```

Después:

```bash
./gradlew clean
./gradlew assembleDebug
```

---

## Comprobación rápida del entorno

Antes de comenzar puedes ejecutar:

```bash
java -version
adb version
sdkmanager --version
avdmanager --version
emulator -version
echo $ANDROID_SDK_ROOT
emulator -list-avds
adb devices
```

Si todas las herramientas funcionan y `adb devices` muestra un dispositivo, el entorno está preparado para realizar el laboratorio.
