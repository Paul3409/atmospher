@echo off
setlocal
cd /d "%~dp0"
call "C:\Gradle\gradle-8.14.6\bin\gradle.bat" clean build --no-daemon --stacktrace
if errorlevel 1 exit /b 1
echo.
echo Fertig: build\libs\quellcode-1.0.0.jar
endlocal
