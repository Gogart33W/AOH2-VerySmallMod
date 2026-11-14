@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0restore.ps1"
if "%~1"=="" pause
