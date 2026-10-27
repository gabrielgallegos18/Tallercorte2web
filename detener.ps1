$ErrorActionPreference = 'Stop'
$pidFile = Join-Path $PSScriptRoot '.local\api.pid'
if (Test-Path -LiteralPath $pidFile) {
    $apiProcessId = [int](Get-Content -LiteralPath $pidFile)
    $process = Get-CimInstance Win32_Process -Filter "ProcessId = $apiProcessId"
    if ($process -and $process.Name -eq 'java.exe' -and
        $process.CommandLine -like '*target/gestion-productos-1.0.0.jar*') {
        Stop-Process -Id $apiProcessId
    }
    Remove-Item -LiteralPath $pidFile
}
$data = Join-Path $PSScriptRoot '.local\postgres-data'
if (Test-Path -LiteralPath $data) {
    & 'C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe' -D $data status *> $null
    if ($LASTEXITCODE -eq 0) {
        & 'C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe' -D $data -m fast -w stop
        if ($LASTEXITCODE -ne 0) { throw 'No se pudo detener PostgreSQL' }
    }
}
