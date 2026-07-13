$ErrorActionPreference = "Stop"

$javaHome = "C:\Program Files\Java\jdk-17"
if (-not (Test-Path (Join-Path $javaHome "bin\java.exe"))) {
    throw "No se encontro Java 17 en $javaHome"
}

$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"

# Credenciales del directorio institucional (data.cresio.com).
# Viven en .env.local.data (ignorado por git) - NUNCA hardcodearlas aqui
# ni en application.properties.
$envFile = Join-Path $PSScriptRoot ".env.local.data"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $linea = $_.Trim()
        if ($linea -and -not $linea.StartsWith("#") -and $linea.Contains("=")) {
            $partes = $linea.Split("=", 2)
            [Environment]::SetEnvironmentVariable($partes[0].Trim(), $partes[1].Trim(), "Process")
        }
    }
    Write-Host "Variables cargadas desde .env.local.data" -ForegroundColor Green
} else {
    Write-Host "AVISO: no existe .env.local.data - el directorio data.cresio quedara desactivado." -ForegroundColor Yellow
    Write-Host "       Crea .env.local.data con EMPLEADOS_SYNC_URL/USUARIO/CONTRASENA." -ForegroundColor Yellow
}

if (-not $env:EMPLEADOS_SYNC_URL) {
    Write-Host "AVISO: EMPLEADOS_SYNC_URL vacia - busqueda/sincronizacion de custodios sin efecto." -ForegroundColor Yellow
}

$apps = @(
    @{ Name = "CRESIO API"; Path = Join-Path $PSScriptRoot "gestionactivosapi" },
    @{ Name = "CRESIO Web"; Path = Join-Path $PSScriptRoot "consumogestionactivosapi" }
)

foreach ($app in $apps) {
    Start-Process powershell.exe `
        -WorkingDirectory $app.Path `
        -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-Command", ".\mvnw.cmd spring-boot:run"

    Write-Host "Iniciado $($app.Name)" -ForegroundColor Green
}
