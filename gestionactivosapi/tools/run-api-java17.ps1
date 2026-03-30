$ErrorActionPreference = "Stop"

function Resolve-Java17Home {
    if ($env:JAVA17_HOME -and (Test-Path $env:JAVA17_HOME)) {
        return $env:JAVA17_HOME
    }

    $candidates = @(
        "C:\Program Files\Java\jdk-17",
        "C:\Program Files\Java\jdk-17.0.12",
        "C:\Program Files\Java\jdk-17.0.13",
        "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot",
        "C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot",
        "C:\Program Files\Microsoft\jdk-17",
        "C:\Program Files\Amazon Corretto\jdk17.0.12_7"
    )

    foreach ($candidate in $candidates) {
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    return $null
}

$javaHome17 = Resolve-Java17Home

if (-not $javaHome17) {
    Write-Host "No se encontro un JDK 17 en el equipo." -ForegroundColor Red
    Write-Host "Configura JAVA17_HOME o instala Java 17 LTS y vuelve a ejecutar este script." -ForegroundColor Yellow
    exit 1
}

$env:JAVA_HOME = $javaHome17
$env:Path = "$javaHome17\bin;$env:Path"

Write-Host "Usando JAVA_HOME=$javaHome17" -ForegroundColor Green
& ".\mvnw.cmd" spring-boot:run
