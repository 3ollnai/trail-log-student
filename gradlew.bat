@echo off
setlocal
cd /d "%~dp0"
if not exist "gradle\wrapper\gradle-wrapper.jar" (
  echo Gradle wrapper JAR manquant. Lancez: powershell -ExecutionPolicy Bypass -File .\setup-wrapper.ps1
  exit /b 1
)
java -cp "gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%
