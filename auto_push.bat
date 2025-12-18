@echo off
cd /d %~dp0

git add -A
git commit -m "auto update" --allow-empty
git push

pause
