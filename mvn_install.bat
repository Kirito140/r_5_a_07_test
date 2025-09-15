@echo off
setlocal

:: Check if Maven is already installed
where mvn >nul 2>&1
if %ERRORLEVEL%==0 (
    echo Maven already installed.
    exit /b
)

:: Variables
set "MAVEN_VERSION=3.9.11"
set "MAVEN_DIR=%USERPROFILE%\maven"
set "MAVEN_ZIP=apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION:~0,1%/apache-maven-%MAVEN_VERSION%-bin.zip"

:: Create installation directory
if not exist "%MAVEN_DIR%" mkdir "%MAVEN_DIR%"

:: Download Maven
powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%TEMP%\%MAVEN_ZIP%'"

:: Extract
powershell -Command "Expand-Archive -Path '%TEMP%\%MAVEN_ZIP%' -DestinationPath '%MAVEN_DIR%' -Force"

:: Clean ZIP
del "%TEMP%\%MAVEN_ZIP%"

:: Persist environment variables
setx M2_HOME "%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%"
setx PATH "%PATH%;%M2_HOME%\bin"

echo.
echo Maven %MAVEN_VERSION% installed in %MAVEN_DIR%.
echo Please restart your terminal to apply PATH changes.
