$ErrorActionPreference = "Stop"

$javaHome = "C:\Program Files\Java\jdk-17"
if (-not (Test-Path (Join-Path $javaHome "bin\java.exe"))) {
    throw "No se encontro Java 17 en $javaHome"
}

$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"

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