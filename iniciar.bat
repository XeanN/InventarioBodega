@echo off
echo ============================================
echo  INVENTARIO BODEGA - Iniciar todo
echo ============================================
echo.
echo 1) Asegurate que config.properties tenga los datos de Supabase
echo 2) Se abrira el backend Java en puerto 8080
echo 3) Se abrira el frontend en puerto 5173
echo.

start "Backend Java API" cmd /k "cd /d %~dp0 && java -cp build\classes;lib\postgresql-42.7.4.jar servidor.ServidorApi"
timeout /t 3 /nobreak > nul
start "Frontend React" cmd /k "cd /d %~dp0frontend && npm run dev"
echo.
echo Abre http://localhost:5173 en tu navegador
pause
