@echo off
tpf -genMetadata %*
if %ERRORLEVEL% NEQ 0 pause