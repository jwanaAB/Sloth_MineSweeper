@echo off
echo ========================================
echo Downloading JUnit 5 Libraries
echo ========================================
echo.

if not exist lib mkdir lib
cd lib

echo Downloading JUnit Jupiter API...
curl -L -o junit-jupiter-api-5.10.0.jar https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar

echo Downloading JUnit Jupiter Engine...
curl -L -o junit-jupiter-engine-5.10.0.jar https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar

echo Downloading JUnit Jupiter Params...
curl -L -o junit-jupiter-params-5.10.0.jar https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-params/5.10.0/junit-jupiter-params-5.10.0.jar

echo Downloading JUnit Platform Commons...
curl -L -o junit-platform-commons-1.10.0.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-commons/1.10.0/junit-platform-commons-1.10.0.jar

echo Downloading JUnit Platform Engine...
curl -L -o junit-platform-engine-1.10.0.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-engine/1.10.0/junit-platform-engine-1.10.0.jar

echo Downloading API Guardian...
curl -L -o apiguardian-api-1.1.2.jar https://repo1.maven.org/maven2/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar

echo Downloading OpenTest4J...
curl -L -o opentest4j-1.3.0.jar https://repo1.maven.org/maven2/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar

cd ..
echo.
echo ========================================
echo Download Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Refresh your Eclipse project (F5)
echo 2. Clean and rebuild (Project -^> Clean)
echo 3. Run the tests (Right-click test file -^> Run As -^> JUnit Test)
echo.
pause



