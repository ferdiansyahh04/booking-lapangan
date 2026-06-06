@echo off
setlocal enabledelayedexpansion

set "LIB_DIR=D:\sportbooking\lib"
set "BASE_URL=https://repo1.maven.org/maven2"

echo ================================================
echo  Download JasperReports Libraries
echo ================================================
echo.

if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

REM Delete old/corrupt files
if exist "%LIB_DIR%\commons-collections4-4.4.jar" del /f "%LIB_DIR%\commons-collections4-4.4.jar"
if exist "%LIB_DIR%\commons-logging-1.3.1.jar" del /f "%LIB_DIR%\commons-logging-1.3.1.jar"

echo Downloading JasperReports 6.21.0...
echo - jasperreports-6.21.0.jar
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/net/sf/jasperreports/jasperreports/6.21.0/jasperreports-6.21.0.jar' -OutFile '%LIB_DIR%\jasperreports-6.21.0.jar'"

echo - jasperreports-fonts-6.21.0.jar
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/net/sf/jasperreports/jasperreports-fonts/6.21.0/jasperreports-fonts-6.21.0.jar' -OutFile '%LIB_DIR%\jasperreports-fonts-6.21.0.jar'"

echo.
echo Downloading iText PDF 5.5.13.3...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar' -OutFile '%LIB_DIR%\itextpdf-5.5.13.3.jar'"

echo.
echo Downloading Commons dependencies...
echo - commons-digester-2.1
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/commons-digester/commons-digester/2.1/commons-digester-2.1.jar' -OutFile '%LIB_DIR%\commons-digester-2.1.jar'"

echo - commons-logging-1.3.1
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/commons-logging/commons-logging/1.3.1/commons-logging-1.3.1.jar' -OutFile '%LIB_DIR%\commons-logging-1.3.1.jar'"

echo - commons-collections4-4.4
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar' -OutFile '%LIB_DIR%\commons-collections4-4.4.jar'"

echo - commons-beanutils-1.9.4
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar' -OutFile '%LIB_DIR%\commons-beanutils-1.9.4.jar'"

echo.
echo ================================================
echo  Verifying downloads...
echo ================================================
echo.

set "HAS_ERROR=0"

for %%F in (
    "jasperreports-6.21.0.jar"
    "jasperreports-fonts-6.21.0.jar"
    "itextpdf-5.5.13.3.jar"
    "commons-digester-2.1.jar"
    "commons-logging-1.3.1.jar"
    "commons-collections4-4.4.jar"
    "commons-beanutils-1.9.4.jar"
) do (
    set "JAR_FILE=%%~nxF"
    if not exist "%LIB_DIR%\!JAR_FILE!" (
        echo [MISSING] !JAR_FILE!
        set "HAS_ERROR=1"
    ) else (
        for %%A in ("%LIB_DIR%\!JAR_FILE!") do (
            if %%~zA LSS 1000 (
                echo [CORRUPT] !JAR_FILE! (%%~zA bytes)
                set "HAS_ERROR=1"
            ) else (
                echo [OK] !JAR_FILE! (%%~zA bytes)
            )
        )
    )
)

echo.
if "%HAS_ERROR%"=="1" (
    echo ================================================
    echo  WARNING: Some files failed to download!
    echo  Please check your internet connection.
    echo ================================================
) else (
    echo ================================================
    echo  All files downloaded successfully!
    echo ================================================
)

echo.
echo Files in lib folder:
dir /b "%LIB_DIR%\*.jar"
echo.
pause