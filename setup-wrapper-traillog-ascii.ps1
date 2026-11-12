$ErrorActionPreference = 'Stop'
$gradleVersion = '8.13'
$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectDir

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$wrapperJar = Join-Path $projectDir 'gradle\wrapper\gradle-wrapper.jar'

if (Test-Path $wrapperJar) {
    Write-Host 'Gradle Wrapper already exists.'
    exit 0
}

$tempDir = Join-Path $env:TEMP 'traillog-gradle-wrapper-8.13'
$zipPath = Join-Path $tempDir 'gradle-8.13-bin.zip'
$gradleBat = Join-Path $tempDir 'gradle-8.13\bin\gradle.bat'

New-Item -ItemType Directory -Force -Path $tempDir | Out-Null

if (-not (Test-Path $gradleBat)) {
    if (-not (Test-Path $zipPath)) {
        Write-Host 'Downloading Gradle 8.13...'
        Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.13-bin.zip' -OutFile $zipPath -UseBasicParsing
    }
    Expand-Archive -LiteralPath $zipPath -DestinationPath $tempDir -Force
}

& $gradleBat wrapper --gradle-version $gradleVersion --distribution-type bin

if ($LASTEXITCODE -ne 0) {
    throw 'Gradle Wrapper generation failed.'
}

Write-Host 'Gradle Wrapper ready.'
