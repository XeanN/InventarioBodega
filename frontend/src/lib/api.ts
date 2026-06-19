import type { ApiError } from '../types';

const BASE = '/api';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options?.headers },
    ...options,
  });

  if (!response.ok) {
    let message = 'Error en la solicitud';
    try {
      const data = (await response.json()) as ApiError;
      message = data.error || message;
    } catch {
      message = response.statusText || message;
    }
    throw new Error(message);
  }

  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export const api = {
  health: () => request<{ status: string }>('/health'),

  productos: {
    list: () => request<import('../types').Producto[]>('/productos'),
    get: (codigo: string) => request<import('../types').Producto>(`/productos/${codigo}`),
    create: (data: Omit<import('../types').Producto, never>) =>
      request<import('../types').Producto>('/productos', { method: 'POST', body: JSON.stringify(data) }),
    update: (codigo: string, data: Partial<import('../types').Producto>) =>
      request<import('../types').Producto>(`/productos/${codigo}`, { method: 'PUT', body: JSON.stringify(data) }),
    remove: (codigo: string) => request<void>(`/productos/${codigo}`, { method: 'DELETE' }),
  },

  clientes: {
    list: () => request<import('../types').Cliente[]>('/clientes'),
    create: (data: Omit<import('../types').Cliente, 'idCliente'>) =>
      request<import('../types').Cliente>('/clientes', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: Partial<import('../types').Cliente>) =>
      request<import('../types').Cliente>(`/clientes/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    remove: (id: number) => request<void>(`/clientes/${id}`, { method: 'DELETE' }),
  },

  proveedores: {
    list: () => request<import('../types').Proveedor[]>('/proveedores'),
    create: (data: Omit<import('../types').Proveedor, 'idProveedor'>) =>
      request<import('../types').Proveedor>('/proveedores', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: Partial<import('../types').Proveedor>) =>
      request<import('../types').Proveedor>(`/proveedores/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    remove: (id: number) => request<void>(`/proveedores/${id}`, { method: 'DELETE' }),
  },

  cajas: {
    list: () => request<import('../types').Caja[]>('/cajas'),
    setEstado: (id: number, estado: 'ABIERTA' | 'CERRADA') =>
      request<import('../types').Caja>(`/cajas/${id}/estado`, { method: 'PATCH', body: JSON.stringify({ estado }) }),
  },

  ventas: {
    list: () => request<import('../types').Venta[]>('/ventas'),
    get: (id: number) => request<import('../types').Venta>(`/ventas/${id}`),
    create: (data: { idCliente: number; idCaja: number; detalles: { codigoProducto: string; cantidad: number }[] }) =>
      request<import('../types').Venta>('/ventas', { method: 'POST', body: JSON.stringify(data) }),
    setEstado: (id: number, estado: string) =>
      request<import('../types').Venta>(`/ventas/${id}/estado`, { method: 'PATCH', body: JSON.stringify({ estado }) }),
  },

  compras: {
    list: () => request<import('../types').Compra[]>('/compras'),
    get: (id: number) => request<import('../types').Compra>(`/compras/${id}`),
    create: (data: { idProveedor: number; detalles: { codigoProducto: string; cantidad: number; costoUnitario: number }[] }) =>
      request<import('../types').Compra>('/compras', { method: 'POST', body: JSON.stringify(data) }),
  },

  dashboard: {
    stats: () => request<import('../types').DashboardStats>('/dashboard/stats'),
  },
};
