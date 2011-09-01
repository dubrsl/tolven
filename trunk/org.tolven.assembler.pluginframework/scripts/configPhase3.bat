@echo off
tpf -plugin org.tolven.component.application %*
if %ERRORLEVEL% NEQ 0 pause