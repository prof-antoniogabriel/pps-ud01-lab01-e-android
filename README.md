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

- **JDK 17**.
- **Android SDK**.
- **Android SDK Platform 35**.
- **Android SDK Build Tools 35.0.0**.
- **Android SDK Platform Tools**.
- **Android Command-line Tools**.
- **Android Emulator**.
- una **imagen de sistema Android 35**;
- `adb`;
- opcionalmente **Android Studio**.

En Ubuntu puedes instalar inicialmente:

```bash
sudo apt update

sudo apt install android-sdk
sudo apt install google-android-cmdline-tools-13.0-installer
```

El emulador se instalará posteriormente mediante `sdkmanager` dentro del SDK personal del usuario.

Opcionalmente puedes instalar Android Studio:

```bash
sudo snap install android-studio --classic
```

Comprueba inicialmente las herramientas disponibles:

```bash
java -version
adb version
sdkmanager --version
avdmanager --version
```

También puedes comprobar su ubicación:

```bash
which java
which adb
which sdkmanager
which avdmanager
```

---

# 2. Configurar el Android SDK

En algunos sistemas Ubuntu, el SDK instalado mediante paquetes se encuentra en:

```text
/usr/lib/android-sdk
```

Sin embargo, esa ubicación pertenece al sistema y puede generar problemas de permisos al instalar nuevas plataformas, imágenes o componentes.

Para este laboratorio utilizaremos un SDK dentro del directorio personal del usuario:

```bash
mkdir -p "$HOME/Android/Sdk"
```

Configura las variables de entorno:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
```

`ANDROID_HOME` será la ubicación principal del SDK.

Se mantiene también `ANDROID_SDK_ROOT` apuntando al mismo directorio para evitar problemas de compatibilidad con determinadas herramientas.

Añade al `PATH` las herramientas que se instalarán dentro del SDK:

```bash
export PATH="$ANDROID_HOME/platform-tools:$PATH"
export PATH="$ANDROID_HOME/emulator:$PATH"
```

Comprueba:

```bash
echo $ANDROID_HOME
echo $ANDROID_SDK_ROOT
```

Deberías obtener una ruta similar a:

```text
/home/usuario/Android/Sdk
```

Para hacer permanente esta configuración, añade al final de:

```text
~/.bashrc
```

lo siguiente:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"

export PATH="$ANDROID_HOME/platform-tools:$PATH"
export PATH="$ANDROID_HOME/emulator:$PATH"
```

Después recarga la configuración:

```bash
source ~/.bashrc
```

Comprueba de nuevo:

```bash
echo $ANDROID_HOME
echo $ANDROID_SDK_ROOT
```

---

# 3. Instalar Android SDK 35

Instala todos los componentes necesarios para construir y ejecutar la aplicación:

```bash
sdkmanager \
  --sdk_root="$ANDROID_HOME" \
  "platform-tools" \
  "platforms;android-35" \
  "build-tools;35.0.0" \
  "emulator" \
  "system-images;android-35;google_apis;x86_64"
```

Con este comando se instalan:

```text
platform-tools
      ↓
adb y herramientas de comunicación con dispositivos

platforms;android-35
      ↓
API Android 35 utilizada para compilar

build-tools;35.0.0
      ↓
herramientas utilizadas para construir el APK

emulator
      ↓
emulador Android

system-images
      ↓
sistema Android que ejecutará el dispositivo virtual
```

Acepta las licencias:

```bash
yes | sdkmanager \
  --sdk_root="$ANDROID_HOME" \
  --licenses
```

---

## Comprobar la instalación

Comprueba las plataformas instaladas:

```bash
ls "$ANDROID_HOME/platforms"
```

Debería aparecer:

```text
android-35
```

Comprueba los Build Tools:

```bash
ls "$ANDROID_HOME/build-tools"
```

Debería aparecer:

```text
35.0.0
```

Puedes comprobar directamente algunas de las herramientas instaladas:

```bash
"$ANDROID_HOME/build-tools/35.0.0/aapt2" version
```

y:

```bash
"$ANDROID_HOME/build-tools/35.0.0/apksigner" version
```

Comprueba también la imagen del sistema:

```bash
find "$ANDROID_HOME/system-images" \
  -maxdepth 4 \
  -type d
```

Deberías localizar una ruta similar a:

```text
/home/usuario/Android/Sdk/system-images/android-35/google_apis/x86_64
```

Finalmente comprueba el emulador:

```bash
emulator -version
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

Si pregunta si deseas crear un perfil de hardware personalizado puedes responder:

```text
no
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

Espera hasta que Android termine completamente de iniciar.

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

No debe aparecer:

```text
offline
unauthorized
```

ni una lista vacía.

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

> **Importante:** aunque una clave se introduzca desde `local.properties`, si posteriormente se incorpora a `BuildConfig`, quedará empaquetada dentro de la aplicación. Por tanto, una API key no puede considerarse secreta únicamente porque no esté almacenada en Git.

Los secretos reales deberían mantenerse en servicios backend o sustituirse por credenciales temporales y mecanismos adecuados de autenticación.

---

# 7. Compatibilidad Java / Kotlin

El proyecto debe compilar Java y Kotlin utilizando versiones compatibles.

En:

```text
app/build.gradle.kts
```

debe aparecer una configuración similar a:

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

Comprueba también la versión de Java:

```bash
java -version
```

Para este laboratorio se utilizará:

```text
JDK 17
```

---

# 8. Construcción de la aplicación

Antes de construir comprueba que el SDK está disponible:

```bash
echo $ANDROID_HOME
```

y que existen los componentes requeridos:

```bash
ls "$ANDROID_HOME/platforms"
ls "$ANDROID_HOME/build-tools"
```

Deberías encontrar:

```text
android-35
35.0.0
```

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

Deberías encontrar:

```text
app-debug.apk
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

También puedes consultar los informes HTML generados por Gradle en:

```text
app/build/reports/tests/testDebugUnitTest/
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

permite reinstalar o actualizar una aplicación que ya se encuentre instalada.

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

También puedes contar automáticamente las coincidencias:

```bash
adb shell pm list packages \
  | grep -c '^package:org.iesalixar.pps.radiografia$'
```

Resultado esperado:

```text
1
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

Deberías obtener un número correspondiente al PID del proceso.

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

Obtendrás información similar a:

```text
uid=10xxx(...)
gid=10xxx(...)
```

También puedes consultar el UID asignado:

```bash
adb shell dumpsys package \
  org.iesalixar.pps.radiografia \
  | grep userId
```

Esto permite observar uno de los mecanismos básicos de aislamiento de aplicaciones Android.

La aplicación:

```text
no se ejecuta como root
        ↓
Android le asigna un UID
        ↓
ese UID forma parte del sandbox de la aplicación
```

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
- ¿Qué Android SDK utiliza?
- ¿Qué Build Tools necesita?
- ¿Cómo se instala?
- ¿Cómo se identifica su proceso?
- ¿Qué datos se introducen en `BuildConfig`?
- ¿Qué configuraciones de seguridad aparecen en `AndroidManifest.xml`?
- ¿Con qué usuario se ejecuta la aplicación?

---

# Problemas frecuentes

## `adb: no devices/emulators found`

Comprueba:

```bash
adb devices
```

Si no aparece ningún dispositivo, comprueba primero los AVD disponibles:

```bash
emulator -list-avds
```

Después arranca el emulador:

```bash
emulator -avd PPS_API_35
```

Espera a que Android haya arrancado y vuelve a ejecutar:

```bash
adb devices
```

---

## `emulator: command not found`

Comprueba:

```bash
echo $ANDROID_HOME
```

y:

```bash
ls "$ANDROID_HOME/emulator"
```

Asegúrate también de tener en el `PATH`:

```bash
export PATH="$ANDROID_HOME/emulator:$PATH"
```

Recarga:

```bash
source ~/.bashrc
```

Comprueba:

```bash
which emulator
emulator -version
```

---

## `PANIC: Cannot find AVD system path`

Comprueba:

```bash
echo $ANDROID_HOME
echo $ANDROID_SDK_ROOT
```

Ambos deben apuntar a:

```text
$HOME/Android/Sdk
```

Comprueba también que exista la imagen:

```bash
find "$ANDROID_HOME/system-images" \
  -maxdepth 4 \
  -type d
```

Debería existir una ruta similar a:

```text
$HOME/Android/Sdk/system-images/android-35/google_apis/x86_64
```

---

## `sdkmanager: Failed to read or create install properties file`

No instales las nuevas plataformas, imágenes y Build Tools dentro de:

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
  --sdk_root="$ANDROID_HOME" \
  ...
```

---

## Error: Android SDK Build Tools no encontrado

Comprueba:

```bash
ls "$ANDROID_HOME/build-tools"
```

Debería aparecer:

```text
35.0.0
```

Si no existe, instala los Build Tools:

```bash
sdkmanager \
  --sdk_root="$ANDROID_HOME" \
  "build-tools;35.0.0"
```

Comprueba después:

```bash
"$ANDROID_HOME/build-tools/35.0.0/aapt2" version
```

---

## Error: Android Platform 35 no encontrada

Comprueba:

```bash
ls "$ANDROID_HOME/platforms"
```

Debería aparecer:

```text
android-35
```

Si no aparece:

```bash
sdkmanager \
  --sdk_root="$ANDROID_HOME" \
  "platforms;android-35"
```

---

## Error de versión Java / Kotlin

Si aparece:

```text
compileDebugJavaWithJavac (1.8)
compileDebugKotlin (21)
```

comprueba en:

```text
app/build.gradle.kts
```

que exista:

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

# Comprobación rápida del entorno

Antes de comenzar el laboratorio puedes ejecutar:

```bash
java -version

echo "ANDROID_HOME=$ANDROID_HOME"
echo "ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"

adb version
sdkmanager --version
avdmanager --version
emulator -version

echo "=== Android Platforms ==="
ls "$ANDROID_HOME/platforms"

echo "=== Android Build Tools ==="
ls "$ANDROID_HOME/build-tools"

echo "=== System Images ==="
find "$ANDROID_HOME/system-images" \
  -maxdepth 4 \
  -type d

echo "=== AVD disponibles ==="
emulator -list-avds

echo "=== Dispositivos detectados ==="
adb devices
```

El entorno estará correctamente preparado cuando puedas comprobar:

```text
JDK 17
      ✓

Android SDK
      ✓

Platform android-35
      ✓

Build Tools 35.0.0
      ✓

Platform Tools / adb
      ✓

Android Emulator
      ✓

Imagen android-35/google_apis/x86_64
      ✓

AVD PPS_API_35
      ✓

adb devices
      ↓
emulator-5554    device
```

Una vez completadas estas comprobaciones puedes comenzar con la construcción, pruebas e instalación de la aplicación.
