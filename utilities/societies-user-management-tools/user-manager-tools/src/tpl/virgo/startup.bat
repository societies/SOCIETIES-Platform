@echo off
echo #############################
echo Start {{Parameter.userName}} node
echo #############################
set SCRIPT_PATH=%~dp0
%SCRIPT_PATH%..\bin\startup.bat -clean -configDir %SCRIPT_PATH%..\{{Parameter.configFolderNameShort}} -jmxport {{Parameter.openfirePortNumber}}
