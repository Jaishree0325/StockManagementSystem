@echo off
title Stock Management System Launcher
echo Launching your Stock Management System...
java -cp "build/classes;lib/mysql-connector-j.jar" com.stockms.Main
pause
