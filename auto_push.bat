@echo off
cd /d %~dp0

REM Stage all tracked and untracked files, ignore build/dist/ thanks to .gitignore
git add -A

REM Commit changes (allow empty)
git commit -m "auto update" --allow-empty

REM Push to GitHub
git push

pause
