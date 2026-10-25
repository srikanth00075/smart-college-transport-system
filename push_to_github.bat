@echo off
title Push Smart College Transport to GitHub
cd /d "%~dp0"
echo =======================================================
echo Pushing Smart College Transport System to GitHub...
echo Repository: https://github.com/srikanth00075/yes-smart-college-transport-system
echo =======================================================
echo.

set "GIT_EXE=C:\Users\HP\.gemini\antigravity\scratch\tools\git\cmd\git.exe"

"%GIT_EXE%" remote remove origin >nul 2>&1
"%GIT_EXE%" remote add origin https://github.com/srikanth00075/yes-smart-college-transport-system.git
"%GIT_EXE%" branch -M main

echo If prompted, please authenticate with your GitHub account in the browser/terminal.
echo Force-pushing to replace existing files as requested...
"%GIT_EXE%" push -u origin main --force

echo.
if %ERRORLEVEL% EQU 0 (
    echo =======================================================
    echo SUCCESS! Project successfully pushed to:
    echo https://github.com/srikanth00075/yes-smart-college-transport-system
    echo =======================================================
) else (
    echo [Notice] If authentication failed, please generate a Personal Access Token on GitHub:
    echo https://github.com/settings/tokens (Select 'repo' scope)
    echo Then run: git push https://YOUR_TOKEN@github.com/srikanth00075/yes-smart-college-transport-system.git main --force
)

pause
