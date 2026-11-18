$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
$projectRoot = $PSScriptRoot
$jar = Join-Path $projectRoot 'gradle\wrapper\gradle-wrapper.jar'
if (Test-Path $jar) { Write-Host 'Gradle Wrapper déjà présent.'; exit 0 }

$version = '8.13'
$cache = Join-Path $env:USERPROFILE '.gradle\trail-log-bootstrap'
$archive = Join-Path $cache "gradle-$version-bin.zip"
$gradleExecutable = Join-Path $cache "gradle-$version\bin\gradle.bat"
New-Item -ItemType Directory -Force -Path $cache | Out-Null

if (-not (Test-Path $gradleExecutable)) {
    if (-not (Test-Path $archive)) {
        $uri = "https://services.gradle.org/distributions/gradle-$version-bin.zip"
        Write-Host "Téléchargement de la distribution officielle Gradle $version..."
        Invoke-WebRequest -Uri $uri -OutFile $archive
    }
    Write-Host 'Extraction de Gradle...'
    Expand-Archive -Path $archive -DestinationPath $cache -Force
}
if (-not (Test-Path $gradleExecutable)) { throw 'Distribution Gradle introuvable après extraction.' }

$tempProject = Join-Path $cache 'wrapper-generator'
New-Item -ItemType Directory -Force -Path $tempProject | Out-Null
Set-Content -Path (Join-Path $tempProject 'settings.gradle') -Value "rootProject.name = 'wrapper-generator'" -Encoding ASCII
Set-Content -Path (Join-Path $tempProject 'build.gradle') -Value "" -Encoding ASCII
Write-Host 'Génération du Gradle Wrapper...'
& $gradleExecutable -p $tempProject wrapper --gradle-version $version --distribution-type bin
if ($LASTEXITCODE -ne 0) { throw "La génération du wrapper a échoué (code $LASTEXITCODE)." }
$generatedJar = Join-Path $tempProject 'gradle\wrapper\gradle-wrapper.jar'
if (-not (Test-Path $generatedJar)) { throw 'Gradle n’a pas généré le Wrapper JAR.' }
New-Item -ItemType Directory -Force -Path (Split-Path $jar) | Out-Null
Copy-Item $generatedJar $jar -Force
Write-Host 'Gradle Wrapper prêt. Exécutez .\gradlew.bat assembleDebug'
