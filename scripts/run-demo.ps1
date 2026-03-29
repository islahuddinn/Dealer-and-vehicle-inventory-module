# Starts the inventory API for local demos (requires JDK 17+).
# Usage: from repo root:  .\scripts\run-demo.ps1
$ErrorActionPreference = "Stop"
$root = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $root
if (-not (Test-Path ".\pom.xml")) {
    Write-Host "Run this script from the inventory repo (pom.xml missing in $root)" -ForegroundColor Red
    exit 1
}

if (Test-Path ".\mvnw.cmd") {
    Write-Host "Starting Spring Boot (Maven Wrapper)..." -ForegroundColor Cyan
    & .\mvnw.cmd spring-boot:run
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "Starting Spring Boot (system Maven)..." -ForegroundColor Cyan
    mvn spring-boot:run
} else {
    Write-Host "Install JDK 17 and run from project root: .\mvnw.cmd spring-boot:run" -ForegroundColor Red
    exit 1
}
