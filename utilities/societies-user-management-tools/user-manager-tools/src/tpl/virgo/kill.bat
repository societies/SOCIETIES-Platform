@ECHO OFF
SET PORT_NUMBER={{Parameter.openfirePortNumber}}
FOR /F "tokens=1-5* " %%I IN ('netstat -o -n -a -b ^| findstr 0.0:%PORT_NUMBER%') DO (
	TASKKILL /F /PID %%M >log-kill
	IF NOT ERRORLEVEL 1 (
		echo Container {{Parameter.number}} stopped
		rm log-kill
	) ELSE (
		echo Can't kill container {{Parameter.number}}. See log file.
	)
	GOTO :eof
)
echo Container {{Parameter.number}} already stopped
