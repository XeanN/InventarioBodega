# Inventario Bodega — Guía para el equipo

## ¿Cómo se conecta todo?

```
Frontend React (puerto 5173)
        │  HTTP /api/*
        ▼
Backend Java — servidor.ServidorApi (puerto 8080)
        │  JDBC
        ▼
PostgreSQL / Supabase  ←── tu compañero configura esto
```

**El frontend SÍ habla con el backend Java.**  
React no va directo a la base de datos: llama al API REST que expone el backend Java (`servidor/ServidorApi.java`), y ese API usa repositorios → servicios → controladores (la arquitectura del proyecto).

---

## Rol de cada compañero

### Compañero (Backend + Base de datos)
1. Crear la base en PostgreSQL/Supabase
2. Ejecutar `database/schema.sql` y `database/seed.sql`
3. Copiar `src/inventariobodega/config.properties.example` → `config.properties`
4. Poner URL, usuario y contraseña reales de Supabase
5. Implementar/mejorar lógica en `servicio/`, `repositorio/`, patrones (Observer, Cola)
6. Correr el servidor API Java

### Tú (Frontend)
1. `cd frontend && npm install && npm run dev`
2. Abrir http://localhost:5173
3. El frontend ya apunta al backend Java en puerto 8080

---

## Cómo correr el proyecto (paso a paso)

### 1. Base de datos (lo hace tu compañero una vez)

En Supabase o PostgreSQL local, ejecutar:
```bash
psql -U postgres -d inventario_bodega -f database/schema.sql
psql -U postgres -d inventario_bodega -f database/seed.sql
```

### 2. Configurar conexión (tu compañero)

```bash
copy src\inventariobodega\config.properties.example src\inventariobodega\config.properties
```

Editar `config.properties`:
```properties
db.url=jdbc:postgresql://TU_HOST:5432/TU_BD
db.usuario=TU_USUARIO
db.password=TU_CONTRASEÑA
api.puerto=8080
```

### 3. Iniciar backend Java (API REST)

**Opción A — NetBeans:**  
Abrir el proyecto → Run (clase principal: `servidor.ServidorApi`)

**Opción B — Terminal:**
```bash
cd c:\Users\n1cko\InventarioBodega
javac -cp "lib/postgresql-42.7.4.jar" -d build/classes -sourcepath src/inventariobodega src/inventariobodega/servidor/ServidorApi.java src/inventariobodega/**/*.java
java -cp "build/classes;lib/postgresql-42.7.4.jar" servidor.ServidorApi
```

Debe aparecer: `Backend Java API → http://localhost:8080/api`

### 4. Iniciar frontend (tú)

```bash
cd frontend
npm install
npm run dev
```

Abrir: **http://localhost:5173**

---

## Verificar que funciona

1. Backend: http://localhost:8080/api/health → debe decir `"mode":"java-backend"`
2. Frontend: Dashboard debe mostrar productos, clientes, etc. de la base real

---

## Carpeta `api/` (Node) — solo respaldo

La carpeta `api/` es un **respaldo temporal** para probar sin Java.  
**En producción usen el backend Java.** Si quieren usar Node como puente:
- Copiar `api/.env.example` → `api/.env` con `DATABASE_URL=...`
- `cd api && npm run dev` (puerto 3001)
- Cambiar en `frontend/vite.config.ts` el proxy a `3001`

---

## Estructura del backend Java

| Capa | Carpeta | Función |
|------|---------|---------|
| Modelo | `modelo/` | Datos (Producto, Cliente, etc.) |
| Repositorio | `repositorio/` | SQL / JDBC |
| Servicio | `servicio/` | Lógica de negocio |
| Controlador | `controlador/` | Coordina servicios |
| API REST | `servidor/` | HTTP para el frontend web |
| Vista Swing | `vista/` | App de escritorio (opcional) |
