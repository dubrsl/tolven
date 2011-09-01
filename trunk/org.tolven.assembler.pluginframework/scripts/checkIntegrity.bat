@echo off
tpf -plugin org.tolven.assembler.pluginframework -noop %*
if %ERRORLEVEL% NEQ 0 pause