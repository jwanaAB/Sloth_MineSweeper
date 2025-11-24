@echo off
taskkill /F /IM java.exe
taskkill /F /IM javaw.exe
echo All Java processes have been terminated.
pause
