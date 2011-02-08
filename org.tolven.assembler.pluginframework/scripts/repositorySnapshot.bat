@echo off
tpf -repositorySnapshot %*
if %ERRORLEVEL% NEQ 0 pause