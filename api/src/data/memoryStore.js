const today = () => new Date().toISOString();

let seq = { cliente: 4, proveedor: 3, venta: 1, compra: 1, detalleVenta: 1, detalleCompra: 1 };

const db = {
  productos: [
    { codigo: 'P001', nombre: 'Arroz 1kg', precioVenta: 2.5, stock: 120 },
    { codigo: 'P002', nombre: 'Aceite 1L', precioVenta: 4.8, stock: 85 },
    { codigo: 'P003', nombre: 'Azúcar 1kg', precioVenta: 1.9, stock: 200 },
    { codigo: 'P004', nombre: 'Leche 1L', precioVenta: 1.2, stock: 60 },
    { codigo: 'P005', nombre: 'Fideos 500g', precioVenta: 1.5, stock: 150 },
  ],
  clientes: [
    { idCliente: 1, nombre: 'Juan Pérez', documento: '12345678' },
    { idCliente: 2, nombre: 'María García', documento: '87654321' },
    { idCliente: 3, nombre: 'Carlos López', documento: '11223344' },
  ],
  proveedores: [
    { idProveedor: 1, nombre: 'Distribuidora Norte', contacto: 'contacto@norte.com' },
    { idProveedor: 2, nombre: 'Alimentos del Sur', contacto: 'ventas@suralimentos.com' },
  ],
  cajas: [
    { idCaja: 1, nombre: 'Caja 1', estado: 'ABIERTA' },
    { idCaja: 2, nombre: 'Caja 2', estado: 'CERRADA' },
  ],
  ventas: [],
  detalleVenta: [],
  compras: [],
  detalleCompra: [],
};

function isToday(iso) {
  const d = new Date(iso);
  const n = new Date();
  return d.toDateString() === n.toDateString();
}

export const memoryStore = {
  // Productos
  listProductos() {
    return [...db.productos].sort((a, b) => a.nombre.localeCompare(b.nombre));
  },
  getProducto(codigo) {
    return db.productos.find((p) => p.codigo === codigo) || null;
  },
  createProducto({ codigo, nombre, precioVenta, stock }) {
    if (db.productos.some((p) => p.codigo === codigo)) {
      throw Object.assign(new Error('El código ya existe'), { status: 409 });
    }
    const p = { codigo, nombre, precioVenta: Number(precioVenta), stock: Number(stock) };
    db.productos.push(p);
    return p;
  },
  updateProducto(codigo, { nombre, precioVenta, stock }) {
    const p = db.productos.find((x) => x.codigo === codigo);
    if (!p) return null;
    if (nombre != null) p.nombre = nombre;
    if (precioVenta != null) p.precioVenta = Number(precioVenta);
    if (stock != null) p.stock = Number(stock);
    return { ...p };
  },
  deleteProducto(codigo) {
    const used = db.detalleVenta.some((d) => d.codigoProducto === codigo) ||
      db.detalleCompra.some((d) => d.codigoProducto === codigo);
    if (used) throw Object.assign(new Error('No se puede eliminar: el producto tiene ventas o compras asociadas'), { status: 409 });
    const idx = db.productos.findIndex((p) => p.codigo === codigo);
    if (idx === -1) return false;
    db.productos.splice(idx, 1);
    return true;
  },

  // Clientes
  listClientes() {
    return [...db.clientes].sort((a, b) => a.nombre.localeCompare(b.nombre));
  },
  createCliente({ nombre, documento }) {
    if (db.clientes.some((c) => c.documento === documento)) {
      throw Object.assign(new Error('El documento ya existe'), { status: 409 });
    }
    const c = { idCliente: seq.cliente++, nombre, documento };
    db.clientes.push(c);
    return c;
  },
  updateCliente(id, { nombre, documento }) {
    const c = db.clientes.find((x) => x.idCliente === id);
    if (!c) return null;
    if (documento && db.clientes.some((x) => x.documento === documento && x.idCliente !== id)) {
      throw Object.assign(new Error('El documento ya existe'), { status: 409 });
    }
    if (nombre != null) c.nombre = nombre;
    if (documento != null) c.documento = documento;
    return { ...c };
  },
  deleteCliente(id) {
    if (db.ventas.some((v) => v.idCliente === id)) {
      throw Object.assign(new Error('No se puede eliminar: el cliente tiene ventas asociadas'), { status: 409 });
    }
    const idx = db.clientes.findIndex((c) => c.idCliente === id);
    if (idx === -1) return false;
    db.clientes.splice(idx, 1);
    return true;
  },

  // Proveedores
  listProveedores() {
    return [...db.proveedores].sort((a, b) => a.nombre.localeCompare(b.nombre));
  },
  createProveedor({ nombre, contacto }) {
    const p = { idProveedor: seq.proveedor++, nombre, contacto: contacto || null };
    db.proveedores.push(p);
    return p;
  },
  updateProveedor(id, { nombre, contacto }) {
    const p = db.proveedores.find((x) => x.idProveedor === id);
    if (!p) return null;
    if (nombre != null) p.nombre = nombre;
    if (contacto != null) p.contacto = contacto;
    return { ...p };
  },
  deleteProveedor(id) {
    if (db.compras.some((c) => c.idProveedor === id)) {
      throw Object.assign(new Error('No se puede eliminar: el proveedor tiene compras asociadas'), { status: 409 });
    }
    const idx = db.proveedores.findIndex((p) => p.idProveedor === id);
    if (idx === -1) return false;
    db.proveedores.splice(idx, 1);
    return true;
  },

  // Cajas
  listCajas() {
    return [...db.cajas].sort((a, b) => a.idCaja - b.idCaja);
  },
  setCajaEstado(id, estado) {
    const c = db.cajas.find((x) => x.idCaja === id);
    if (!c) return null;
    c.estado = estado;
    return { ...c };
  },

  // Ventas
  listVentas() {
    return db.ventas.map((v) => {
      const cliente = db.clientes.find((c) => c.idCliente === v.idCliente);
      const caja = db.cajas.find((c) => c.idCaja === v.idCaja);
      const detalles = db.detalleVenta.filter((d) => d.idVenta === v.idVenta);
      const total = detalles.reduce((s, d) => s + d.subtotal, 0);
      return {
        idVenta: v.idVenta,
        estado: v.estado,
        fecha: v.fecha,
        idCliente: v.idCliente,
        clienteNombre: cliente?.nombre,
        clienteDocumento: cliente?.documento,
        idCaja: v.idCaja,
        cajaNombre: caja?.nombre,
        total,
      };
    }).sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
  },
  getVenta(id) {
    const v = db.ventas.find((x) => x.idVenta === id);
    if (!v) return null;
    const cliente = db.clientes.find((c) => c.idCliente === v.idCliente);
    const caja = db.cajas.find((c) => c.idCaja === v.idCaja);
    const detalles = db.detalleVenta
      .filter((d) => d.idVenta === id)
      .map((d) => {
        const p = db.productos.find((pr) => pr.codigo === d.codigoProducto);
        return { ...d, productoNombre: p?.nombre };
      });
    const total = detalles.reduce((s, d) => s + d.subtotal, 0);
    return {
      idVenta: v.idVenta,
      estado: v.estado,
      fecha: v.fecha,
      idCliente: v.idCliente,
      clienteNombre: cliente?.nombre,
      clienteDocumento: cliente?.documento,
      idCaja: v.idCaja,
      cajaNombre: caja?.nombre,
      detalles,
      total,
    };
  },
  createVenta({ idCliente, idCaja, detalles }) {
    const caja = db.cajas.find((c) => c.idCaja === idCaja);
    if (!caja) throw Object.assign(new Error('Caja no encontrada'), { status: 404 });
    if (caja.estado !== 'ABIERTA') {
      throw Object.assign(new Error('La caja debe estar ABIERTA para registrar ventas'), { status: 400 });
    }
    if (!db.clientes.find((c) => c.idCliente === idCliente)) {
      throw Object.assign(new Error('Cliente no encontrado'), { status: 404 });
    }

    for (const item of detalles) {
      const p = db.productos.find((pr) => pr.codigo === item.codigoProducto);
      if (!p) throw Object.assign(new Error(`Producto ${item.codigoProducto} no encontrado`), { status: 404 });
      const cantidad = Number(item.cantidad);
      if (!cantidad || cantidad <= 0) throw Object.assign(new Error('Cantidad inválida'), { status: 400 });
      if (p.stock < cantidad) {
        throw Object.assign(new Error(`Stock insuficiente para ${p.nombre}. Disponible: ${p.stock}`), { status: 400 });
      }
    }

    const idVenta = seq.venta++;
    const fecha = today();
    db.ventas.push({ idVenta, idCliente, idCaja, estado: 'PAGADO', fecha });
    const insertados = [];

    for (const item of detalles) {
      const p = db.productos.find((pr) => pr.codigo === item.codigoProducto);
      const cantidad = Number(item.cantidad);
      const subtotal = p.precioVenta * cantidad;
      p.stock -= cantidad;
      const det = {
        idDetalle: seq.detalleVenta++,
        idVenta,
        codigoProducto: item.codigoProducto,
        cantidad,
        subtotal,
      };
      db.detalleVenta.push(det);
      insertados.push(det);
    }

    const total = insertados.reduce((s, d) => s + d.subtotal, 0);
    return { idVenta, estado: 'PAGADO', detalles: insertados, total };
  },

  // Compras
  listCompras() {
    return db.compras.map((c) => {
      const prov = db.proveedores.find((p) => p.idProveedor === c.idProveedor);
      const detalles = db.detalleCompra.filter((d) => d.idCompra === c.idCompra);
      const total = detalles.reduce((s, d) => s + d.cantidad * d.costoUnitario, 0);
      return {
        idCompra: c.idCompra,
        fecha: c.fecha,
        idProveedor: c.idProveedor,
        proveedorNombre: prov?.nombre,
        total,
      };
    }).sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
  },
  getCompra(id) {
    const c = db.compras.find((x) => x.idCompra === id);
    if (!c) return null;
    const prov = db.proveedores.find((p) => p.idProveedor === c.idProveedor);
    const detalles = db.detalleCompra
      .filter((d) => d.idCompra === id)
      .map((d) => {
        const p = db.productos.find((pr) => pr.codigo === d.codigoProducto);
        return { ...d, productoNombre: p?.nombre, subtotal: d.cantidad * d.costoUnitario };
      });
    const total = detalles.reduce((s, d) => s + d.subtotal, 0);
    return {
      idCompra: c.idCompra,
      fecha: c.fecha,
      idProveedor: c.idProveedor,
      proveedorNombre: prov?.nombre,
      detalles,
      total,
    };
  },
  createCompra({ idProveedor, detalles }) {
    if (!db.proveedores.find((p) => p.idProveedor === idProveedor)) {
      throw Object.assign(new Error('Proveedor no encontrado'), { status: 404 });
    }
    for (const item of detalles) {
      const cantidad = Number(item.cantidad);
      const costo = Number(item.costoUnitario);
      if (!item.codigoProducto || !cantidad || cantidad <= 0 || costo < 0) {
        throw Object.assign(new Error('Detalle inválido'), { status: 400 });
      }
      if (!db.productos.find((p) => p.codigo === item.codigoProducto)) {
        throw Object.assign(new Error(`Producto ${item.codigoProducto} no encontrado`), { status: 404 });
      }
    }

    const idCompra = seq.compra++;
    const fecha = today();
    db.compras.push({ idCompra, idProveedor, fecha });
    const insertados = [];

    for (const item of detalles) {
      const cantidad = Number(item.cantidad);
      const costoUnitario = Number(item.costoUnitario);
      const p = db.productos.find((pr) => pr.codigo === item.codigoProducto);
      p.stock += cantidad;
      const det = {
        idDetalle: seq.detalleCompra++,
        idCompra,
        codigoProducto: item.codigoProducto,
        cantidad,
        costoUnitario,
      };
      db.detalleCompra.push(det);
      insertados.push(det);
    }

    const total = insertados.reduce((s, d) => s + d.cantidad * d.costoUnitario, 0);
    return { idCompra, detalles: insertados, total };
  },

  // Dashboard
  getDashboardStats() {
    const ventasHoy = db.ventas.filter((v) => isToday(v.fecha));
    let ingresosHoy = 0;
    for (const v of ventasHoy) {
      ingresosHoy += db.detalleVenta
        .filter((d) => d.idVenta === v.idVenta)
        .reduce((s, d) => s + d.subtotal, 0);
    }
    return {
      totalProductos: db.productos.length,
      totalClientes: db.clientes.length,
      totalProveedores: db.proveedores.length,
      ventasHoy: ventasHoy.length,
      ingresosHoy,
      comprasHoy: db.compras.filter((c) => isToday(c.fecha)).length,
      stockBajo: [...db.productos].filter((p) => p.stock < 20).sort((a, b) => a.stock - b.stock).slice(0, 5),
      ventasRecientes: this.listVentas().slice(0, 5).map((v) => ({
        idVenta: v.idVenta,
        cliente: v.clienteNombre,
        estado: v.estado,
        total: v.total,
        fecha: v.fecha,
      })),
    };
  },
};
