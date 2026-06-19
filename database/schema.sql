-- Esquema Inventario Bodega (PostgreSQL / Supabase)
-- Alineado con el ERD del proyecto Java

CREATE TABLE IF NOT EXISTS producto (
    codigo VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    precio_venta DECIMAL(12, 2) NOT NULL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    documento VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS proveedor (
    id_proveedor SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    contacto VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS caja (
    id_caja SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'CERRADA' CHECK (estado IN ('ABIERTA', 'CERRADA'))
);

CREATE TABLE IF NOT EXISTS venta (
    id_venta SERIAL PRIMARY KEY,
    id_cliente INTEGER NOT NULL REFERENCES cliente(id_cliente),
    id_caja INTEGER NOT NULL REFERENCES caja(id_caja),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE', 'RESERVADO', 'PAGADO', 'ENTREGADO')),
    fecha TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    id_detalle SERIAL PRIMARY KEY,
    id_venta INTEGER NOT NULL REFERENCES venta(id_venta) ON DELETE CASCADE,
    codigo_producto VARCHAR(50) NOT NULL REFERENCES producto(codigo),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    subtotal DECIMAL(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reserva_stock (
    id_reserva SERIAL PRIMARY KEY,
    codigo_producto VARCHAR(50) NOT NULL REFERENCES producto(codigo),
    id_venta INTEGER NOT NULL REFERENCES venta(id_venta) ON DELETE CASCADE,
    cantidad_reservada INTEGER NOT NULL CHECK (cantidad_reservada > 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA'
        CHECK (estado IN ('ACTIVA', 'CONFIRMADA', 'CANCELADA'))
);

CREATE TABLE IF NOT EXISTS compra (
    id_compra SERIAL PRIMARY KEY,
    id_proveedor INTEGER NOT NULL REFERENCES proveedor(id_proveedor),
    fecha TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS detalle_compra (
    id_detalle SERIAL PRIMARY KEY,
    id_compra INTEGER NOT NULL REFERENCES compra(id_compra) ON DELETE CASCADE,
    codigo_producto VARCHAR(50) NOT NULL REFERENCES producto(codigo),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    costo_unitario DECIMAL(12, 2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha);
CREATE INDEX IF NOT EXISTS idx_compra_fecha ON compra(fecha);
CREATE INDEX IF NOT EXISTS idx_detalle_venta_venta ON detalle_venta(id_venta);
CREATE INDEX IF NOT EXISTS idx_detalle_compra_compra ON detalle_compra(id_compra);
