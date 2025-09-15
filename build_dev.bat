@echo off
setlocal enabledelayedexpansion

REM Nom du conteneur
set DB_CONTAINER=demo-mysql

REM Vérifie si le conteneur existe déjà
docker ps -q -f name=%DB_CONTAINER% >nul
if %ERRORLEVEL%==0 (
    echo ✅ Conteneur %DB_CONTAINER% deja lance
) else (
    echo 🚀 Demarrage de la base de donnees...
    docker compose up -d db
)

REM Attente que la BDD soit prête
echo ⏳ Attente que MySQL soit pret...
:waitloop
docker exec %DB_CONTAINER% mysqladmin ping -h localhost -u%DB_USERNAME% -p%DB_PASSWORD% --silent >nul 2>&1
if ERRORLEVEL 1 (
    timeout /t 2 >nul
    goto waitloop
)

REM Exportation des variables d'environnement depuis .env
for /f "tokens=1,2 delims==" %%A in (.env) do (
    set %%A=%%B
)

echo ✅ Base prête, compilation de l'application...
mvn clean package
echo ✅Application compilée, lancement de l'application dans un conteneur
docker build -t mon-app .
docker run -d -p 2005:2005 --name demo-app mon-app
