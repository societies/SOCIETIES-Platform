@ECHO OFF

REM First param is the action. Rest are parameters - shift done in subroutines
set ACTION=%~1
set SCRIPT_PATH=%~dp0..\virgo
set HELP_PARAM_NAMES=
set COMMAND=
set COMMAND_EXTENSION=bat

REM Launch the relevant action
if "%ACTION%" == "init" (
	set COMMAND=init
	set HELP_PARAM_NAMES=["Openfire path"] ["Mysql path"]
	call :doToolsCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "close" (
	set COMMAND=close
	set HELP_PARAM_NAMES=
	call :doToolsCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "xmpp-start" (
	set COMMAND=xmpp-start
	set HELP_PARAM_NAMES="Openfire path"]
	call :doToolsCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "xmpp-kill" (
	set COMMAND=xmpp-kill
	set HELP_PARAM_NAMES=
	call :doToolsCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "mysql-start" (
	set COMMAND=mysql-start
	set HELP_PARAM_NAMES=["MySQL path"]
	call :doToolsCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "mysql-stop" (
	set COMMAND=mysql-stop
	set HELP_PARAM_NAMES=
	call :doToolsCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "mysql-kill" (
	set COMMAND=mysql-kill
	set HELP_PARAM_NAMES=
	call :doToolsCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "isstarted" (
	set COMMAND=isstarted
	set HELP_PARAM_NAMES="container numbers" ["first port number"]
	call :doCallCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "start" (
	set COMMAND=startup
	set HELP_PARAM_NAMES="container numbers" ["first port number"]
	call :doCallCommand %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "stop"  (
	set COMMAND=shutdown
	set HELP_PARAM_NAMES="container numbers" ["first port number"]
	call :doCallCommand  %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" == "kill"  (
	set COMMAND=kill
	set HELP_PARAM_NAMES="container numbers" ["first port number"]
	call :doCallCommand  %*
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)
if "%ACTION%" NEQ ""  (
	echo Unknown action: %ACTION%
	echo.
)
echo ### Societies Launcher Tools
echo # Action list
echo - init            To initialize SOCIETIES: Openfire and MySQL will be started.
echo - close           To close SOCIETIES: Openfire and MySQL will be stopped.
echo                   All containers still have to be closed.
echo -- mysql-start    MySQL server will be launched.
echo -- mysql-stop     MySQL server will be stopped.
echo                   All containers still have to be closed.
echo -- mysql-kill     MySQL server will be killed.
echo                   All containers still have to be closed.
echo -- xmpp-start     Openfire XMPP server will be launched.
echo -- xmpp-kill      Openfire XMPP server will be stopped.
echo                   All containers still have to be closed.
echo - isstarted       To know if a container is started or not.
echo - start           To start a container.
echo - stop            To stop one or several containers. A kill is often necessary.
echo - kill            To stop abruptely one or several containers.
echo - "command" help  To display help message of a specific command

if "%OS%" == "Windows_NT" endlocal
exit /B 1


REM --- Subroutines
:doCallCommand
REM Param not available
IF "%~2" == "help" (
	echo Usage: %ACTION% %HELP_PARAM_NAMES%
	echo.
	echo "container numbers" may be a number or an interval.
	echo "first port number" Openfire port number of the first container. It will be incremented for the other containers.
	echo E.g. "start 0" will start the first container.
	echo E.g. "start 0-1" will start container 0 and 1.
	echo E.g. "kill 0-2" will stop containers 0, 1 and 2.
	if "%OS%" == "Windows_NT" endlocal
	exit /B 0
)

REM Parse Param
if "%OS%" == "Windows_NT" setlocal ENABLEDELAYEDEXPANSION
REM Node numbers
set FIRST=0
set LAST=
REM Do for all
IF "%~2" == "" (
	FOR /F "usebackq tokens=1-2 delims=g " %%I IN (`DIR /B %SCRIPT_PATH%\config* /O:-N`) DO (
		set LAST=%%J
		goto :endOfNodeNumbers
	)
	REM set lsConfig=`ls %SCRIPT_PATH% ^| findstr config ^| sort -rh`
	REM FOR /F "usebackq tokens=1-2 delims=g " %%I IN (%lsConfig%) DO (
		REM echo there %%J
		REM set LAST=%%J
		REM goto :endOfNodeNumbers
	REM )
	REM Windows command
	REM if "!LAST!" == "" (
		
	REM )
) else (
	set NODE_NUMBERS=%~2
	for /f "tokens=1,2* delims=- " %%i IN ("!NODE_NUMBERS!") DO (
		set FIRST=%%i
		set LAST=%%j
		IF "%%j" == "" (
			set LAST=%%i
		)
	)
)
:endOfNodeNumbers
REM Port number
set FIRST_PORT_NUMBER=%~3
if "%FIRST_PORT_NUMBER%" EQU "" (
	set /A FIRST_PORT_NUMBER=60000+%FIRST%
)

REM Launch action on container(s)
set COUNTER=0
set PORT_NUMBER=0
FOR /L %%N IN (%FIRST%,1,!LAST!) DO (
	REM No port number: use an existing script (never used because we prefilled the port number
	if "%FIRST_PORT_NUMBER%" EQU "" (
		if "%ACTION%" == "start" (
			START /B %SCRIPT_PATH%\config%%N\%COMMAND%%%N.%COMMAND_EXTENSION% ^&
		) else (
			CALL %SCRIPT_PATH%\config%%N\%COMMAND%%%N.%COMMAND_EXTENSION%
		)
	) else (
		REM Port number available: use the following code
		set /A PORT_NUMBER=%FIRST_PORT_NUMBER%+!COUNTER!
		if "%COMMAND%" == "isstarted" (
			set STARTED=0
			FOR /F "tokens=1-5* " %%I IN ('netstat -o -n -a -b ^| findstr 0.0:!PORT_NUMBER!') DO (
				IF "%%M" NEQ "" (
					set STARTED=1
				)
			)
			echo !STARTED!
		)
		if "%COMMAND%" == "startup" (
			START /B %SCRIPT_PATH%\bin\startup.bat -clean -configDir %SCRIPT_PATH%\config%%N -jmxport !PORT_NUMBER! ^&
		)
		if "%COMMAND%" == "shutdown"  (
			CALL %SCRIPT_PATH%\bin\shutdown.bat -configDir %SCRIPT_PATH%\config%%N -jmxport !PORT_NUMBER! ^&
			echo Container %%N stopped
		)
		if "%COMMAND%" == "kill"  (
			set KILLED=Container %%N already stopped
			FOR /F "tokens=1-5 " %%I IN ('netstat -o -n -a -b ^| findstr 0.0:!PORT_NUMBER!') DO (
				TASKKILL /F /PID %%M > log-kill 2>&1
				IF NOT ERRORLEVEL 1 (
					set KILLED=Container %%N stopped
					del log-kill
				) ELSE (
					set KILLED=Can't kill container %%N. See log file.
				)
			)
			echo !KILLED!
		)
	)
	set /A COUNTER+=1
	
	REM if "%%N" NEQ "%LAST%" (
		REM echo Continue with next container?
		REM pause
	REM )
)
if "%OS%" == "Windows_NT" endlocal
exit /B 0

:doToolsCommand
if "%OS%" == "Windows_NT" setlocal ENABLEDELAYEDEXPANSION
if "%COMMAND%" == "init" (
	set COMMAND=xmpp-start
	call :doToolsCommand %*
	set COMMAND=mysql-start
)
if "%COMMAND%" == "close" (
	set COMMAND=xmpp-kill
	call :doToolsCommand %*
	set COMMAND=mysql-kill
)
if "%COMMAND%" == "xmpp-start" (
	set OPENFIRE_PATH=%SCRIPT_PATH%\..\tools\openfire\bin
	IF "%~2" NEQ "" (
		set OPENFIRE_PATH=%~2
	)
	START /B !OPENFIRE_PATH!\openfired.exe ^&
	echo Starting Openfire...
)
if "%COMMAND%" == "xmpp-kill" (
	set KILLED=Openfire already stopped
	FOR /F "tokens=1-2 " %%I IN ('TASKLIST ^| findstr openfire') DO (
		TASKKILL /F /PID %%J > log-openfire 2>&1
		IF NOT ERRORLEVEL 1 (
			set KILLED=Openfire stopped
			del log-openfire
		) ELSE (
			set KILLED=Can't stop Openfire. See log file.
			exit /B 1
		)
	)
	echo !KILLED!
)
if "%COMMAND%" == "mysql-start" (
	set MYSQL_PATH=%SCRIPT_PATH%\..\tools\mysql\bin
	IF "%~2" NEQ "" (
		set MYSQL_PATH=%~2
	)
	START /B !MYSQL_PATH!\mysqld.exe --console
	echo Starting MySQL...
)
if "%COMMAND%" == "mysql-stop" (
	set MYSQL_PATH=%SCRIPT_PATH%\..\tools\mysql\bin
	IF "%~2" NEQ "" (
		set MYSQL_PATH=%~2
	)
	START /B !MYSQL_PATH!\mysqladmin.exe -u root shutdown ^&
	echo Stopping MySQL...
)
if "%COMMAND%" == "mysql-kill" (
	set APP_NAME=MySQL
	set PROCESS_NAME=mysql
	set KILLED=!APP_NAME! already stopped
	FOR /F "tokens=1-2 " %%I IN ('TASKLIST ^| findstr !PROCESS_NAME!') DO (
		TASKKILL /F /PID %%J > log-!PROCESS_NAME! 2>&1
		IF NOT ERRORLEVEL 1 (
			set KILLED=!APP_NAME! stopped
			del log-!PROCESS_NAME!
		) ELSE (
			set KILLED=Can't stop !APP_NAME!. See log file log-!PROCESS_NAME!.
			exit /B 1
		)
	)
	echo !KILLED!
)
if "%OS%" == "Windows_NT" endlocal
exit /B 0

