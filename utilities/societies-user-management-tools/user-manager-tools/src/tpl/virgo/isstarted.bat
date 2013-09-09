@ECHO OFF
set PORT_NUMBER={{Parameter.openfirePortNumber}}
FOR /F "tokens=1-5* " %%I IN ('netstat -o -n -a -b ^| findstr 0.0:%PORT_NUMBER%') DO (
	IF "%%M" NEQ "" (
		echo 1
		GOTO :eof
	)
)
echo 0
