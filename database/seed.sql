-- Datos de ejemplo para desarrollo
INSERT INTO caja (nombre, estado) VALUES
    ('Caja 1', 'ABIERTA'),
    ('Caja 2', 'CERRADA')
ON CONFLICT DO NOTHING;

INSERT INTO producto (codigo, nombre, precio_venta, stock) VALUES
    ('P001', 'Arroz 1kg', 2.50, 120),
    ('P002', 'Aceite 1L', 4.80, 85),
    ('P003', 'Azúcar 1kg', 1.90, 200),
    ('P004', 'Leche 1L', 1.20, 60),
    ('P005', 'Fideos 500g', 1.50, 150)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO cliente (nombre, documento) VALUES
    ('Juan Pérez', '12345678'),
    ('María García', '87654321'),
    ('Carlos López', '11223344')
ON CONFLICT (documento) DO NOTHING;

INSERT INTO proveedor (nombre, contacto) VALUES
    ('Distribuidora Norte', 'contacto@norte.com'),
    ('Alimentos del Sur', 'ventas@suralimentos.com')
ON CONFLICT DO NOTHING;
