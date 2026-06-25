# InvTICS

CRESIO se ejecuta localmente con PostgreSQL instalado en la máquina, sin Docker.

## Puertos locales

- API backend: `http://localhost:8084`
- Portal web: `http://localhost:8081`
- PostgreSQL: `localhost:5432`, base `cresio4`

## Arranque local

```powershell
.\run-local.ps1
```

El script usa Java 17 desde `C:\Program Files\Java\jdk-17` y abre API + portal web.
