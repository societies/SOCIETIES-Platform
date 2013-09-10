@echo off
echo #############################
echo Shutdown {{Parameter.userName}} node
echo #############################
set SCRIPT_PATH=%~dp0
%SCRIPT_PATH%..\bin\shutdown.bat  -configDir %SCRIPT_PATH%..\{{Parameter.configFolderNameShort}} -jmxport {{Parameter.openfirePortNumber}}
