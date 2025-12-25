@echo off
echo Starting Sloth Minesweeper...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH.
    echo Please install Java and try again.
    pause
    exit /b 1
)

REM Check if JAR file exists
if not exist "SlothMinesweeper.jar" (
    echo ERROR: SlothMinesweeper.jar not found!
    echo Please ensure the JAR file is in the same directory as this batch file.
    pause
    exit /b 1
)

REM Run the application
java -jar SlothMinesweeper.jar

REM Check if there was an error
if %errorlevel% neq 0 (
    echo.
    echo Application exited with an error.
    pause
    exit /b %errorlevel%
)

pause

