export interface Producto {
  codigo: string;
  nombre: string;
  precioVenta: number;
  stock: number;
}

export interface Cliente {
  idCliente: number;
  nombre: string;
  documento: string;
}

export interface Proveedor {
  idProveedor: number;
  nombre: string;
  contacto: string | null;
}

export interface Caja {
  idCaja: number;
  nombre: string;
  estado: 'ABIERTA' | 'CERRADA';
}

export interface DetalleVenta {
  idDetalle?: number;
  codigoProducto: string;
  productoNombre?: string;
  cantidad: number;
  subtotal?: number;
}

export interface Venta {
  idVenta: number;
  estado: string;
  fecha: string;
  idCliente?: number;
  clienteNombre?: string;
  clienteDocumento?: string;
  idCaja?: number;
  cajaNombre?: string;
  total?: number;
  detalles?: DetalleVenta[];
}

export interface DetalleCompra {
  idDetalle?: number;
  codigoProducto: string;
  productoNombre?: string;
  cantidad: number;
  costoUnitario: number;
  subtotal?: number;
}

export interface Compra {
  idCompra: number;
  fecha: string;
  idProveedor?: number;
  proveedorNombre?: string;
  total?: number;
  detalles?: DetalleCompra[];
}

export interface DashboardStats {
  totalProductos: number;
  totalClientes: number;
  totalProveedores: number;
  ventasHoy: number;
  ingresosHoy: number;
  comprasHoy: number;
  stockBajo: Pick<Producto, 'codigo' | 'nombre' | 'stock'>[];
  ventasRecientes: {
    idVenta: number;
    cliente: string;
    estado: string;
    total: number;
    fecha: string;
  }[];
}

export interface ApiError {
  error: string;
}
