@ECHO OFF

REM First param is the action. Rest are parameters - shift done in subroutines
set ACTION=%~1
set SCRIPT_PATH=%~dp0user-manager-tools
set HELP_PARAM_NAMES=
set CURRENT_PATH=
FOR /F %%P IN ('cd') DO set CURRENT_PATH=%%P

REM Launch the relevant action
if "%ACTION%" == "create" (
	call :doPhpCommand %*
	exit /B 0
)
if "%ACTION%" == "config-users" (
	call :doPhpCommand %*
	exit /B 0
)
if "%ACTION%" == "create-containers" (
	call :doPhpCommand %*
	exit /B 0
)
if "%ACTION%" == "help" (
	call :doPhpCommand %*
	exit /B 0
)
if "%ACTION%" == "list" (
	call :doPhpCommand %*
	exit /B 0
)
if "%ACTION%" == "deploy" (
	SHIFT 
	set HELP_PARAM_NAMES=["container numbers"] ["deploy path"]
	call :doDeployCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" NEQ ""  (
	echo Unknown action: %ACTION%
	echo.
)
echo ### SOCIETIES User Management Tools
echo # Action list
echo - create             Generates Virgo containers, create databases if required and
echo                      configure SOCIETIES users (create them if necessary)
echo - deploy             Deploy Virgo containers to the Virgo installation folder
echo - config-users       Configures several SOCIETIES users, and create them if necessary
echo - create-containers  Generates several Virgo containers, and their MySQL database
echo - help               Displays help for a command
echo - list               Lists commands
echo.
echo "container-numbers" may be a number or an interval.
echo E.g. "create 0" will create and configure the first container and its user account.
echo E.g. "create 1-2" will create and configure containers 1 and 2 and their user account.
echo E.g. "deploy 0-2" will deploy containers 0, 1 and 2.

if "%OS%" == "Windows_NT" endlocal
exit /B 1

:doPhpCommand
cd %SCRIPT_PATH%
../../tools/php/php app/console %*
cd %CURRENT_PATH%
exit /B 0

:doDeployCommand
echo #############################
echo Deploy Virgo containers
echo Usage: %ACTION% %HELP_PARAM_NAMES%
	echo.
	echo - "container numbers" is optional. It may be a number or an number interval. 
	echo E.g. "deploy 0" will deploy container 0.
	echo E.g. "deploy 1-2" will deploy containers 1 and 2.
	echo.
	echo - "deploy path" is optional. It is the path to the Virgo directory.
echo #############################
REM Parse Param
REM Container numbers
set NODE_NUMBERS=%~2
set FIRST=0
set LAST=100
IF "%~2" NEQ "" (
	for /f "tokens=1,2* delims=- " %%i IN ('echo %NODE_NUMBERS%') DO (
		set FIRST=%%i
		set LAST=%%j
		IF "%%j" == "" (
			set LAST=%%i
		)
	)
)
REM Deploy path
set DEPLOY_PATH=%SCRIPT_PATH%\..\..\virgo
IF "%~3" NEQ "" (
	set DEPLOY_PATH=%~3
)
set OPTIONS=-f

REM Launch action on container(s)
FOR /L %%N IN (%FIRST%,1,%LAST%) DO (
	REM Deploy
	cp -r %SCRIPT_PATH%\gen\config%%N %DEPLOY_PATH% %OPTIONS% 2>null
	IF NOT ERRORLEVEL 1 (
		REM Create pickup if necessary
		IF not exist %DEPLOY_PATH%\pickup%%N (
			cp -r %DEPLOY_PATH%\pickup1 %DEPLOY_PATH%\pickup%%N 2>null
		)
		echo Container %%N deployed
	) ELSE (
		goto :exitLoop
	)
)
exit /B 0

:exitLoop
del null
exit /B 0

