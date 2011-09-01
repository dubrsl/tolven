@echo off
tpf -plugin org.tolven.deploy.plugincopy %*
if %ERRORLEVEL% NEQ 0 pause