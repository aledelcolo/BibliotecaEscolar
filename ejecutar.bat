@echo off
cd /d "%~dp0"
if not exist biblioteca-escolar.jar (
    echo No se encontro biblioteca-escolar.jar en esta carpeta.
    echo Genera el ejecutable en IntelliJ: panel Maven ^> Lifecycle ^> package
    echo y copia target\biblioteca-escolar.jar dentro de la carpeta "ejecutable".
    pause
    exit /b 1
)
java -jar biblioteca-escolar.jar
if errorlevel 1 pause
