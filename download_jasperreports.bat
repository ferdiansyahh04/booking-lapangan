@echo off
echo ================================================
echo  Download JasperReports Libraries
echo ================================================
echo.

set JASPER_VERSION=6.21.0
set ITEXT_VERSION=5.5.13.3
set COMMONS_DIGESTER=commons-digester-2.1
set COMMONS_LOGGING=commons-logging-1.3.1
set COMMONS_COLLECTIONS=commons-collections4-4.4

set LIB_DIR=lib
set BASE_URL=https://repo1.maven.org/maven2/net/sf/jasperreports

echo Creating lib directory if not exists...
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

echo.
echo Downloading JasperReports Core...
echo.

REM Download JasperReports
curl -L -o "%LIB_DIR%\jasperreports-%JASPER_VERSION%.jar" "%BASE_URL%/jasperreports/%JASPER_VERSION%/jasperreports-%JASPER_VERSION%.jar" --progress-bar

REM Download JasperReports Fonts
curl -L -o "%LIB_DIR%\jasperreports-fonts-%JASPER_VERSION%.jar" "%BASE_URL%/jasperreports-fonts/%JASPER_VERSION%/jasperreports-fonts-%JASPER_VERSION%.jar" --progress-bar

echo.
echo Downloading iText (for PDF export)...
echo.

REM Download iText
curl -L -o "%LIB_DIR%\itextpdf-%ITEXT_VERSION%.jar" "https://repo1.maven.org/maven2/com/itextpdf/itextpdf/%ITEXT_VERSION%/itextpdf-%ITEXT_VERSION%.jar" --progress-bar

echo.
echo Downloading Commons dependencies...
echo.

REM Download Commons Digester
curl -L -o "%LIB_DIR%\%COMMONS_DIGESTER%.jar" "https://repo1.maven.org/maven2/commons-digester/commons-digester/2.1/%COMMONS_DIGESTER%.jar" --progress-bar

REM Download Commons Logging
curl -L -o "%LIB_DIR%\%COMMONS_LOGGING%.jar" "https://repo1.maven.org/maven2/commons-logging/commons-logging/%COMMONS_LOGGING%/%COMMONS_LOGGING%.jar" --progress-bar

REM Download Commons Collections 4
curl -L -o "%LIB_DIR%\%COMMONS_COLLECTIONS%.jar" "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/%COMMONS_COLLECTIONS%/%COMMONS_COLLECTIONS%.jar" --progress-bar

REM Download Commons BeanUtils
curl -L -o "%LIB_DIR%\commons-beanutils-1.9.4.jar" "https://repo1.maven.org/maven2/commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar" --progress-bar

echo.
echo ================================================
echo  Download Complete!
echo ================================================
echo.
echo JAR files saved to: %LIB_DIR%
echo.
echo Next steps:
echo 1. Open project in NetBeans
echo 2. Right-click project - Properties - Libraries
echo 3. Add all JAR files from lib folder
echo 4. Build and run the project
echo.
echo Files downloaded:
dir /b "%LIB_DIR%\*.jar"
echo.
pause