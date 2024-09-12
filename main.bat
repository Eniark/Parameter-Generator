@echo off
setlocal enabledelayedexpansion

set CONFIG_FILE="configurations.txt"


@rem MOVE java configs into configurations file. Read all output of java in STDOUT
echo.
echo ====Current Settings====    
for /f "usebackq tokens=1,2 delims==" %%A in (%CONFIG_FILE%) do (
    set %%A=%%B
    echo %%A: %%B
)
echo ===============================
echo.


set inputFileName="Development Tests.csv"
set outputFileName="Development Tests_Combinations.csv"

set "jarStatusCode="
set "generatedParametersPath="

echo Running java application...
for /f "tokens=*" %%i in ('java -jar "../Parameter Generator/ParameterGenerator.jar" %inputFileName% %outputFileName%') do (
    set "jarStatusCode=!generatedParametersPath!"
    set "generatedParametersPath=%%i"
)

echo.
echo Status Code: !jarStatusCode!
echo.
echo The output file has been created here:
echo !generatedParametersPath!
if !ERRORLEVEL! == 0 (
    echo Java application finished sucessfully

    set testPlanPath="../Test Plans/!JMETER_TEST_NAME!"
    set resultsPath="../Results/!JMETER_RESULTS_FILE_NAME!"

    cd ../apache-jmeter-5.6.2/bin
    if exist "!resultsPath!" (
        echo WARNING: File !resultsPath! already exists. Aborting the program.
        pause
        exit /b 1
    )

    jmeter -n -t "!testPlanPath!" -l "!resultsPath!" -Jthreads=!JMETER_NUM_THREADS! ^
        -JnumUsers=!JMETER_NUM_USERS! -JnumIterations=!JMETER_NUM_ITERATIONS! ^
        -JdataFileAbsolutePath="!generatedParametersPath!"

    cmd /k
) else (
    echo Java program failed with error code !ERRORLEVEL!.
)

endlocal
set /p=Press ENTER to continue...
