param([switch]$SoloBase)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
$pgBin = 'C:\Program Files\PostgreSQL\18\bin'
$local = Join-Path $PSScriptRoot '.local'
$data = Join-Path $local 'postgres-data'
$passwordFile = Join-Path $local 'postgres-password.txt'
if (!(Test-Path -LiteralPath $local)) { New-Item -ItemType Directory -Path $local | Out-Null }
if (!(Test-Path -LiteralPath $passwordFile)) {
    $bytes = New-Object byte[] 32
    [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
    [System.IO.File]::WriteAllText($passwordFile, [Convert]::ToBase64String($bytes))
}
$env:DB_PASSWORD = [System.IO.File]::ReadAllText($passwordFile).Trim()
$env:PGPASSWORD = $env:DB_PASSWORD
if (!(Test-Path -LiteralPath (Join-Path $data 'PG_VERSION'))) {
    & "$pgBin\initdb.exe" -D $data -U gestion_productos --pwfile=$passwordFile --auth=scram-sha-256 --encoding=UTF8 --locale=C
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo inicializar PostgreSQL' }
}
& "$pgBin\pg_ctl.exe" -D $data status *> $null
if ($LASTEXITCODE -ne 0) {
    & "$pgBin\pg_ctl.exe" -D $data -l (Join-Path $local 'postgres.log') -o '-p 5433 -h 127.0.0.1' -w start
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo iniciar PostgreSQL en el puerto 5433' }
}
$exists = & "$pgBin\psql.exe" -h 127.0.0.1 -p 5433 -U gestion_productos -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'gestion_productos'"
if ($LASTEXITCODE -ne 0) { throw 'No se pudo conectar a PostgreSQL' }
if ($exists -ne '1') {
    & "$pgBin\createdb.exe" -h 127.0.0.1 -p 5433 -U gestion_productos gestion_productos
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear la base de datos' }
}
Remove-Item Env:PGPASSWORD
if (!$SoloBase) {
    $listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($listener) {
        Write-Host 'El puerto 8080 ya esta ocupado. Si la API esta iniciada, puedes probarla en Postman.'
        return
    }
    & mvn -B -ntp package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw 'La compilación falló' }
    & java -jar target/gestion-productos-1.0.0.jar
}
