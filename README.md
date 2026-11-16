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

- JDK 17;
- Android SDK;
- Android SDK Platform 35;
- Android SDK Build Tools 35.0.0;
- Android SDK Platform Tools;
- Android Command-line Tools 13.0;
- Android Emulator;
- imagen de sistema Android 35;
- `adb`;
- opcionalmente Android Studio.

En Ubuntu instala inicialmente:

```bash
sudo apt update

sudo apt install openjdk-17-jdk
sudo apt install android-sdk
sudo apt install google-android-cmdline-tools-13.0-installer
```

Estas herramientas iniciales se utilizarán para preparar un SDK Android completo dentro del directorio personal del usuario.

Opcionalmente puedes instalar Android Studio:

```bash
sudo snap install android-studio --classic
```

Comprueba inicialmente:

```bash
java -version
adb version
sdkmanager --version
avdmanager --version
```

También puedes consultar su ubicación:

```bash
which java
which adb
which sdkmanager
which avdmanager
```

> En este momento es posible que `sdkmanager` y `avdmanager` pertenezcan al SDK instalado por Ubuntu en `/usr/lib/android-sdk`. Lo utilizaremos únicamente para preparar el SDK personal.

---

# 2. Crear un SDK Android en el directorio personal

En algunos sistemas Ubuntu, el SDK instalado mediante paquetes se encuentra en:

```text
/usr/lib/android-sdk
```

Esta ubicación pertenece al sistema y puede provocar problemas de permisos o conflictos entre herramientas instaladas mediante paquetes y componentes descargados mediante `sdkmanager`.

Para evitarlo utilizaremos:

```text
$HOME/Android/Sdk
```

Crea el directorio:

```bash
mkdir -p "$HOME/Android/Sdk"
```

Configura las variables de entorno:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
```

Comprueba:

```bash
echo "$ANDROID_HOME"
echo "$ANDROID_SDK_ROOT"
```

El resultado debe ser similar a:

```text
/home/usuario/Android/Sdk
```

---

# 3. Instalar Command-line Tools dentro del SDK personal

Este paso es importante.

No utilizaremos permanentemente el `avdmanager` situado en `/usr/lib/android-sdk`.

Utiliza el `sdkmanager` disponible inicialmente para instalar las **Command-line Tools 13.0 dentro del SDK personal**:

```bash
sdkmanager \
  --sdk_root="$ANDROID_HOME" \
  "cmdline-tools;13.0"
```

Comprueba que se han instalado:

```bash
ls "$ANDROID_HOME/cmdline-tools/13.0/bin"
```

Deberías encontrar herramientas como:

```text
sdkmanager
avdmanager
apkanalyzer
lint
```

A partir de ahora utilizaremos las herramientas instaladas en:

```text
$HOME/Android/Sdk/cmdline-tools/13.0/bin
```

---

# 4. Configurar el PATH

Añade al `PATH` las herramientas del SDK personal:

```bash
export PATH="$ANDROID_HOME/cmdline-tools/13.0/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
```

Limpia la caché de comandos de la shell:

```bash
hash -r
```

Comprueba:

```bash
which sdkmanager
which avdmanager
```

Ahora deberían apuntar a rutas similares a:

```text
/home/usuario/Android/Sdk/cmdline-tools/13.0/bin/sdkmanager
/home/usuario/Android/Sdk/cmdline-tools/13.0/bin/avdmanager
```

No deberían apuntar a:

```text
/usr/lib/android-sdk
```

---

## Hacer permanente la configuración

Añade al final de:

```text
~/.bashrc
```

lo siguiente:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"

export PATH="$ANDROID_HOME/cmdline-tools/13.0/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
```

Después recarga:

```bash
source ~/.bashrc
```

Comprueba:

```bash
echo "$ANDROID_HOME"
which sdkmanager
which avdmanager
```

---

# 5. Instalar Android SDK 35

Utiliza ahora el `sdkmanager` del SDK personal:

```bash
"$ANDROID_HOME/cmdline-tools/13.0/bin/sdkmanager" \
  --sdk_root="$ANDROID_HOME" \
  "platform-tools" \
  "platforms;android-35" \
  "build-tools;35.0.0" \
  "emulator" \
  "system-images;android-35;google_apis;x86_64"
```

Se instalarán:

```text
platform-tools
      ↓
adb y herramientas de comunicación

platforms;android-35
      ↓
API Android 35 utilizada para compilar

build-tools;35.0.0
      ↓
herramientas utilizadas para construir el APK

emulator
      ↓
emulador Android

system-images;android-35;google_apis;x86_64
      ↓
sistema Android utilizado por el dispositivo virtual
```

Acepta las licencias:

```bash
yes | "$ANDROID_HOME/cmdline-tools/13.0/bin/sdkmanager" \
  --sdk_root="$ANDROID_HOME" \
  --licenses
```

---

# 6. Comprobar la instalación

Comprueba la plataforma:

```bash
ls "$ANDROID_HOME/platforms"
```

Resultado esperado:

```text
android-35
```

Comprueba los Build Tools:

```bash
ls "$ANDROID_HOME/build-tools"
```

Resultado esperado:

```text
35.0.0
```

Comprueba algunas de sus herramientas:

```bash
"$ANDROID_HOME/build-tools/35.0.0/aapt2" version
```

y:

```bash
"$ANDROID_HOME/build-tools/35.0.0/apksigner" version
```

Comprueba la imagen del sistema:

```bash
find "$ANDROID_HOME/system-images" \
  -maxdepth 4 \
  -type d
```

Deberías localizar:

```text
/home/usuario/Android/Sdk/system-images/android-35/google_apis/x86_64
```

También puedes comprobar que `sdkmanager` reconoce los componentes:

```bash
"$ANDROID_HOME/cmdline-tools/13.0/bin/sdkmanager" \
  --list_installed
```

---

# 7. Crear un emulador Android

Una aplicación Android necesita un dispositivo físico o un emulador para ejecutarse.

Crearemos un dispositivo virtual llamado:

```text
PPS_API_35
```

Es importante utilizar el `avdmanager` instalado dentro del SDK personal:

```bash
"$ANDROID_HOME/cmdline-tools/13.0/bin/avdmanager" create avd \
  -n PPS_API_35 \
  -k "system-images;android-35;google_apis;x86_64"
```

Si aparece:

```text
Do you wish to create a custom hardware profile? [no]
```

responde:

```text
no
```

Comprueba los dispositivos virtuales:

```bash
emulator -list-avds
```

Debería aparecer:

```text
PPS_API_35
```

---

# 8. Arrancar el emulador

Ejecuta:

```bash
emulator -avd PPS_API_35
```

Espera hasta que Android termine completamente de iniciar.

En otra terminal comprueba:

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
```

ni:

```text
unauthorized
```

ni una lista vacía.

---

# 9. Configuración local del proyecto

El proyecto utiliza un archivo:

```text
local.properties
```

Android Studio puede crearlo automáticamente.

También puedes crearlo manualmente:

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

`local.properties` está excluido de Git.

> Importante: que una clave se encuentre en `local.properties` no implica que sea un secreto seguro. Si posteriormente se incorpora mediante `BuildConfig`, el valor terminará incluido dentro del APK.

Por tanto, secretos reales como contraseñas, tokens o claves privadas no deben protegerse únicamente mediante `local.properties`.

---

# 10. Compatibilidad Java / Kotlin

El proyecto debe compilar Java y Kotlin utilizando versiones compatibles.

En:

```text
app/build.gradle.kts
```

debe aparecer:

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

Comprueba:

```bash
java -version
```

Para este laboratorio utilizaremos:

```text
JDK 17
```

---

# 11. Construir la aplicación

Comprueba primero:

```bash
echo "$ANDROID_HOME"
```

Después:

```bash
ls "$ANDROID_HOME/platforms"
ls "$ANDROID_HOME/build-tools"
```

Debes encontrar:

```text
android-35
35.0.0
```

Desde la raíz del proyecto limpia compilaciones anteriores:

```bash
./gradlew clean
```

Construye la versión de depuración:

```bash
./gradlew assembleDebug
```

Si todo funciona correctamente aparecerá:

```text
BUILD SUCCESSFUL
```

El APK se generará en:

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

# 12. Ejecutar las pruebas unitarias

Ejecuta:

```bash
./gradlew testDebugUnitTest
```

Los resultados quedan almacenados en:

```text
app/build/test-results/testDebugUnitTest/
```

Comprueba:

```bash
ls app/build/test-results/testDebugUnitTest/
```

También puedes consultar los informes HTML:

```text
app/build/reports/tests/testDebugUnitTest/
```

---

# 13. Instalar la aplicación

Antes de instalarla comprueba:

```bash
adb devices
```

Debe aparecer:

```text
emulator-5554    device
```

Instala el APK:

```bash
adb install -r \
  app/build/outputs/apk/debug/app-debug.apk
```

Resultado esperado:

```text
Performing Streamed Install
Success
```

La opción:

```text
-r
```

permite reinstalar o actualizar una aplicación ya instalada.

---

# 14. Comprobar que la aplicación está instalada

Ejecuta:

```bash
adb shell pm list packages \
  | grep org.iesalixar.pps.radiografia
```

Resultado esperado:

```text
package:org.iesalixar.pps.radiografia
```

También puedes comprobarlo mediante:

```bash
adb shell pm list packages \
  | grep -c '^package:org.iesalixar.pps.radiografia$'
```

Resultado esperado:

```text
1
```

---

# 15. Arrancar la aplicación desde terminal

Ejecuta:

```bash
adb shell am start \
  -n org.iesalixar.pps.radiografia/.MainActivity
```

La aplicación debería aparecer en el emulador.

---

# 16. Comprobar el proceso

Con la aplicación arrancada:

```bash
adb shell pidof \
  org.iesalixar.pps.radiografia
```

Deberías obtener su PID.

También puedes comprobar automáticamente que existe un proceso:

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

# 17. Observar el aislamiento de Android

Cada aplicación Android se ejecuta normalmente con un usuario propio.

Ejecuta:

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

Esto permite observar uno de los mecanismos básicos de aislamiento de Android:

```text
Aplicación
    ↓
UID propio
    ↓
sandbox independiente
```

La aplicación no se ejecuta como `root`.

---

# 18. Qué debes observar

No necesitas memorizar Android.

Debes comprender el recorrido general:

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
- ¿Qué plataforma utiliza?
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

Consulta los AVD disponibles:

```bash
emulator -list-avds
```

Si aparece:

```text
PPS_API_35
```

arráncalo:

```bash
emulator -avd PPS_API_35
```

Espera a que Android inicie y vuelve a ejecutar:

```bash
adb devices
```

---

## `Error: Package path is not valid`

Si al ejecutar:

```bash
avdmanager create avd ...
```

aparece algo similar a:

```text
Warning: Observed package id ...
in '/usr/lib/android-sdk/...'

Error: Package path is not valid.
Valid system image paths are:
null
```

significa que estás utilizando herramientas pertenecientes al SDK del sistema:

```text
/usr/lib/android-sdk
```

mientras las imágenes están instaladas en:

```text
$HOME/Android/Sdk
```

Comprueba:

```bash
which sdkmanager
which avdmanager
```

Deben apuntar a:

```text
$HOME/Android/Sdk/cmdline-tools/13.0/bin/
```

Si no es así:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"

export PATH="$ANDROID_HOME/cmdline-tools/13.0/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

hash -r
```

Después crea el AVD utilizando explícitamente:

```bash
"$ANDROID_HOME/cmdline-tools/13.0/bin/avdmanager" create avd \
  -n PPS_API_35 \
  -k "system-images;android-35;google_apis;x86_64"
```

---

## `emulator: command not found`

Comprueba:

```bash
ls "$ANDROID_HOME/emulator"
```

Si no existe:

```bash
"$ANDROID_HOME/cmdline-tools/13.0/bin/sdkmanager" \
  --sdk_root="$ANDROID_HOME" \
  "emulator"
```

Añade:

```bash
export PATH="$ANDROID_HOME/emulator:$PATH"
```

y ejecuta:

```bash
hash -r
```

Después:

```bash
emulator -version
```

---

## `PANIC: Cannot find AVD system path`

Comprueba:

```bash
echo "$ANDROID_HOME"
echo "$ANDROID_SDK_ROOT"
```

Ambos deben apuntar a:

```text
$HOME/Android/Sdk
```

Comprueba la imagen:

```bash
find "$ANDROID_HOME/system-images" \
  -maxdepth 4 \
  -type d
```

Debe existir:

```text
$ANDROID_HOME/system-images/android-35/google_apis/x86_64
```

---

## Android SDK Build Tools no encontrado

Comprueba:

```bash
ls "$ANDROID_HOME/build-tools"
```

Debe aparecer:

```text
35.0.0
```

Si no aparece:

```bash
"$ANDROID_HOME/cmdline-tools/13.0/bin/sdkmanager" \
  --sdk_root="$ANDROID_HOME" \
  "build-tools;35.0.0"
```

Comprueba después:

```bash
"$ANDROID_HOME/build-tools/35.0.0/aapt2" version
```

---

## Android Platform 35 no encontrada

Comprueba:

```bash
ls "$ANDROID_HOME/platforms"
```

Debe aparecer:

```text
android-35
```

Si no aparece:

```bash
"$ANDROID_HOME/cmdline-tools/13.0/bin/sdkmanager" \
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

Antes de comenzar puedes ejecutar:

```bash
java -version

echo "ANDROID_HOME=$ANDROID_HOME"
echo "ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"

which sdkmanager
which avdmanager
which emulator
which adb

sdkmanager --version
avdmanager --version
emulator -version
adb version

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

El entorno está correctamente preparado cuando se cumple:

```text
JDK 17
   ✓

ANDROID_HOME=$HOME/Android/Sdk
   ✓

Command-line Tools 13.0
   ✓

sdkmanager → ~/Android/Sdk/cmdline-tools/13.0/bin
   ✓

avdmanager → ~/Android/Sdk/cmdline-tools/13.0/bin
   ✓

Platform android-35
   ✓

Build Tools 35.0.0
   ✓

Platform Tools / adb
   ✓

Android Emulator
   ✓

system-images/android-35/google_apis/x86_64
   ✓

PPS_API_35
   ✓

adb devices
   ↓
emulator-5554    device
```

Una vez completadas estas comprobaciones puedes comenzar la construcción, pruebas e instalación de la aplicación.
