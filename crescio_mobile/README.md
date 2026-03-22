# CRESIO Mobile

Base inicial de la aplicacion Flutter Android para tecnicos de soporte CRESIO.

## Estado actual

Se dejo implementada la base del proyecto:

- Configuracion Android inicial con `AndroidManifest.xml` y `network_security_config.xml`
- `AppConfig` con IP y puerto configurables
- Tema visual corporativo
- `ApiClient` con JWT, timeout y manejo base de errores
- `SecureStorageService` y `LocalDatabase`
- Flujo inicial `server config -> login -> dashboard`
- Shell principal con navegacion inferior y placeholders de features

## Limitacion de este entorno

En este workspace no esta instalado `flutter` en PATH, por lo que no fue posible:

- ejecutar `flutter create`
- resolver dependencias con `flutter pub get`
- compilar APK
- generar wrappers completos de Android/Gradle

## Siguiente paso recomendado

1. Instalar Flutter estable 3.x y agregarlo a PATH.
2. Desde `crescio_mobile/`, ejecutar:

```powershell
flutter create .
flutter pub get
```

3. Revisar y fusionar cuidadosamente los archivos generados por Flutter con:

- `lib/`
- `android/app/src/main/AndroidManifest.xml`
- `android/app/src/main/res/xml/network_security_config.xml`
- `key.properties`

4. Configurar firma release en `android/app/build.gradle` usando:

```gradle
def keystoreProperties = new Properties()
def keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
        }
    }
}
```

5. Generar keystore:

```powershell
keytool -genkey -v -keystore cresio-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias cresio
```

6. Generar APK release:

```powershell
flutter build apk --release --split-per-abi
```

## Estructura

```text
lib/
  core/
  features/
  shared/
```

## Pendiente de implementar sobre esta base

- Repositories online/offline por feature
- Equipos lista/detalle real y cache local
- QR scanner
- Flujo completo de mantenimientos
- Sincronizacion offline -> online
- Tickets y notificaciones con polling
- Integracion con endpoints adicionales del backend
