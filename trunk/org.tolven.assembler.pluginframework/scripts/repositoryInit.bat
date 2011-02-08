@echo off
tpf -getPlugins %*
if %ERRORLEVEL% NEQ 0 pause