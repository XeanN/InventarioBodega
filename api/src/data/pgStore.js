import { query, withTransaction } from '../db.js';

export function createPgStore() {
  return {
    listProductos: async () => {
      const { rows } = await query(
        'SELECT codigo, nombre, precio_venta AS "precioVenta", stock FROM producto ORDER BY nombre'
      );
      return rows;
    },
    getProducto: async (codigo) => {
      const { rows } = await query(
        'SELECT codigo, nombre, precio_venta AS "precioVenta", stock FROM producto WHERE codigo = $1',
        [codigo]
      );
      return rows[0] || null;
    },
    createProducto: async (data) => {
      const { rows } = await query(
        `INSERT INTO producto (codigo, nombre, precio_venta, stock) VALUES ($1,$2,$3,$4)
         RETURNING codigo, nombre, precio_venta AS "precioVenta", stock`,
        [data.codigo, data.nombre, data.precioVenta, data.stock]
      );
      return rows[0];
    },
    updateProducto: async (codigo, data) => {
      const { rows } = await query(
        `UPDATE producto SET nombre=COALESCE($2,nombre), precio_venta=COALESCE($3,precio_venta), stock=COALESCE($4,stock)
         WHERE codigo=$1 RETURNING codigo, nombre, precio_venta AS "precioVenta", stock`,
        [codigo, data.nombre, data.precioVenta, data.stock]
      );
      return rows[0] || null;
    },
    deleteProducto: async (codigo) => {
      const { rowCount } = await query('DELETE FROM producto WHERE codigo=$1', [codigo]);
      return rowCount > 0;
    },

    listClientes: async () => {
      const { rows } = await query('SELECT id_cliente AS "idCliente", nombre, documento FROM cliente ORDER BY nombre');
      return rows;
    },
    createCliente: async (data) => {
      const { rows } = await query(
        `INSERT INTO cliente (nombre, documento) VALUES ($1,$2)
         RETURNING id_cliente AS "idCliente", nombre, documento`,
        [data.nombre, data.documento]
      );
      return rows[0];
    },
    updateCliente: async (id, data) => {
      const { rows } = await query(
        `UPDATE cliente SET nombre=COALESCE($2,nombre), documento=COALESCE($3,documento)
         WHERE id_cliente=$1 RETURNING id_cliente AS "idCliente", nombre, documento`,
        [id, data.nombre, data.documento]
      );
      return rows[0] || null;
    },
    deleteCliente: async (id) => {
      const { rowCount } = await query('DELETE FROM cliente WHERE id_cliente=$1', [id]);
      return rowCount > 0;
    },

    listProveedores: async () => {
      const { rows } = await query('SELECT id_proveedor AS "idProveedor", nombre, contacto FROM proveedor ORDER BY nombre');
      return rows;
    },
    createProveedor: async (data) => {
      const { rows } = await query(
        `INSERT INTO proveedor (nombre, contacto) VALUES ($1,$2)
         RETURNING id_proveedor AS "idProveedor", nombre, contacto`,
        [data.nombre, data.contacto]
      );
      return rows[0];
    },
    updateProveedor: async (id, data) => {
      const { rows } = await query(
        `UPDATE proveedor SET nombre=COALESCE($2,nombre), contacto=COALESCE($3,contacto)
         WHERE id_proveedor=$1 RETURNING id_proveedor AS "idProveedor", nombre, contacto`,
        [id, data.nombre, data.contacto]
      );
      return rows[0] || null;
    },
    deleteProveedor: async (id) => {
      const { rowCount } = await query('DELETE FROM proveedor WHERE id_proveedor=$1', [id]);
      return rowCount > 0;
    },

    listCajas: async () => {
      const { rows } = await query('SELECT id_caja AS "idCaja", nombre, estado FROM caja ORDER BY id_caja');
      return rows;
    },
    setCajaEstado: async (id, estado) => {
      const { rows } = await query(
        `UPDATE caja SET estado=$2 WHERE id_caja=$1 RETURNING id_caja AS "idCaja", nombre, estado`,
        [id, estado]
      );
      return rows[0] || null;
    },

    listVentas: async () => {
      const { rows } = await query(`
        SELECT v.id_venta AS "idVenta", v.estado, v.fecha, c.id_cliente AS "idCliente",
               c.nombre AS "clienteNombre", ca.id_caja AS "idCaja", ca.nombre AS "cajaNombre",
               COALESCE(SUM(dv.subtotal),0) AS total
        FROM venta v JOIN cliente c ON c.id_cliente=v.id_cliente
        JOIN caja ca ON ca.id_caja=v.id_caja
        LEFT JOIN detalle_venta dv ON dv.id_venta=v.id_venta
        GROUP BY v.id_venta, c.id_cliente, ca.id_caja ORDER BY v.fecha DESC`);
      return rows;
    },
    getVenta: async (id) => {
      const venta = await query(`
        SELECT v.id_venta AS "idVenta", v.estado, v.fecha, c.nombre AS "clienteNombre",
               ca.nombre AS "cajaNombre" FROM venta v
        JOIN cliente c ON c.id_cliente=v.id_cliente JOIN caja ca ON ca.id_caja=v.id_caja
        WHERE v.id_venta=$1`, [id]);
      if (!venta.rows[0]) return null;
      const detalles = await query(`
        SELECT dv.codigo_producto AS "codigoProducto", p.nombre AS "productoNombre",
               dv.cantidad, dv.subtotal FROM detalle_venta dv
        JOIN producto p ON p.codigo=dv.codigo_producto WHERE dv.id_venta=$1`, [id]);
      const total = detalles.rows.reduce((s, d) => s + Number(d.subtotal), 0);
      return { ...venta.rows[0], detalles: detalles.rows, total };
    },
    createVenta: async ({ idCliente, idCaja, detalles }) => {
      return withTransaction(async (client) => {
        const caja = await client.query('SELECT estado FROM caja WHERE id_caja=$1', [idCaja]);
        if (!caja.rows[0] || caja.rows[0].estado !== 'ABIERTA') {
          throw Object.assign(new Error('La caja debe estar ABIERTA'), { status: 400 });
        }
        const venta = await client.query(
          `INSERT INTO venta (id_cliente, id_caja, estado) VALUES ($1,$2,'PAGADO') RETURNING id_venta`,
          [idCliente, idCaja]
        );
        const idVenta = venta.rows[0].id_venta;
        for (const item of detalles) {
          const prod = await client.query('SELECT precio_venta, stock, nombre FROM producto WHERE codigo=$1 FOR UPDATE', [item.codigoProducto]);
          if (!prod.rows[0]) throw Object.assign(new Error('Producto no encontrado'), { status: 404 });
          if (prod.rows[0].stock < item.cantidad) {
            throw Object.assign(new Error(`Stock insuficiente para ${prod.rows[0].nombre}`), { status: 400 });
          }
          const subtotal = prod.rows[0].precio_venta * item.cantidad;
          await client.query(
            'INSERT INTO detalle_venta (id_venta, codigo_producto, cantidad, subtotal) VALUES ($1,$2,$3,$4)',
            [idVenta, item.codigoProducto, item.cantidad, subtotal]
          );
          await client.query('UPDATE producto SET stock=stock-$1 WHERE codigo=$2', [item.cantidad, item.codigoProducto]);
        }
        return { idVenta, estado: 'PAGADO' };
      });
    },

    listCompras: async () => {
      const { rows } = await query(`
        SELECT co.id_compra AS "idCompra", co.fecha, p.nombre AS "proveedorNombre",
               COALESCE(SUM(dc.cantidad*dc.costo_unitario),0) AS total
        FROM compra co JOIN proveedor p ON p.id_proveedor=co.id_proveedor
        LEFT JOIN detalle_compra dc ON dc.id_compra=co.id_compra
        GROUP BY co.id_compra, p.nombre ORDER BY co.fecha DESC`);
      return rows;
    },
    getCompra: async (id) => {
      const compra = await query(`
        SELECT co.id_compra AS "idCompra", co.fecha, p.nombre AS "proveedorNombre"
        FROM compra co JOIN proveedor p ON p.id_proveedor=co.id_proveedor WHERE co.id_compra=$1`, [id]);
      if (!compra.rows[0]) return null;
      const detalles = await query(`
        SELECT dc.codigo_producto AS "codigoProducto", pr.nombre AS "productoNombre",
               dc.cantidad, dc.costo_unitario AS "costoUnitario",
               (dc.cantidad*dc.costo_unitario) AS subtotal
        FROM detalle_compra dc JOIN producto pr ON pr.codigo=dc.codigo_producto
        WHERE dc.id_compra=$1`, [id]);
      const total = detalles.rows.reduce((s, d) => s + Number(d.subtotal), 0);
      return { ...compra.rows[0], detalles: detalles.rows, total };
    },
    createCompra: async ({ idProveedor, detalles }) => {
      return withTransaction(async (client) => {
        const compra = await client.query('INSERT INTO compra (id_proveedor) VALUES ($1) RETURNING id_compra', [idProveedor]);
        const idCompra = compra.rows[0].id_compra;
        for (const item of detalles) {
          await client.query(
            'INSERT INTO detalle_compra (id_compra, codigo_producto, cantidad, costo_unitario) VALUES ($1,$2,$3,$4)',
            [idCompra, item.codigoProducto, item.cantidad, item.costoUnitario]
          );
          await client.query('UPDATE producto SET stock=stock+$1 WHERE codigo=$2', [item.cantidad, item.codigoProducto]);
        }
        return { idCompra };
      });
    },

    getDashboardStats: async () => {
      const [productos, clientes, proveedores, ventasHoy, comprasHoy, stockBajo, ventasRecientes] = await Promise.all([
        query('SELECT COUNT(*)::int AS count FROM producto'),
        query('SELECT COUNT(*)::int AS count FROM cliente'),
        query('SELECT COUNT(*)::int AS count FROM proveedor'),
        query(`SELECT COUNT(*)::int AS count, COALESCE(SUM(dv.subtotal),0) AS total
               FROM venta v LEFT JOIN detalle_venta dv ON dv.id_venta=v.id_venta WHERE v.fecha::date=CURRENT_DATE`),
        query('SELECT COUNT(*)::int AS count FROM compra WHERE fecha::date=CURRENT_DATE'),
        query('SELECT codigo, nombre, stock FROM producto WHERE stock < 20 ORDER BY stock ASC LIMIT 5'),
        query(`SELECT v.id_venta AS "idVenta", c.nombre AS cliente, v.estado,
                      COALESCE(SUM(dv.subtotal),0) AS total, v.fecha
               FROM venta v JOIN cliente c ON c.id_cliente=v.id_cliente
               LEFT JOIN detalle_venta dv ON dv.id_venta=v.id_venta
               GROUP BY v.id_venta, c.nombre ORDER BY v.fecha DESC LIMIT 5`),
      ]);
      return {
        totalProductos: productos.rows[0].count,
        totalClientes: clientes.rows[0].count,
        totalProveedores: proveedores.rows[0].count,
        ventasHoy: ventasHoy.rows[0].count,
        ingresosHoy: Number(ventasHoy.rows[0].total),
        comprasHoy: comprasHoy.rows[0].count,
        stockBajo: stockBajo.rows,
        ventasRecientes: ventasRecientes.rows,
      };
    },
  };
}
