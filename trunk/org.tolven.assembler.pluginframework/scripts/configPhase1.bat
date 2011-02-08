@echo off
tpf -plugin org.tolven.assembler %*
if %ERRORLEVEL% NEQ 0 pause