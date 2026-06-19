import { useEffect, useState } from 'react';
import { Plus, Eye, Trash2 } from 'lucide-react';
import { api } from '../lib/api';
import { formatCurrency, formatDate } from '../lib/format';
import type { Venta, Cliente, Caja, Producto } from '../types';
import { Modal } from '../components/Modal';
import { LoadingSpinner, EmptyState } from '../components/LoadingSpinner';
import { Badge, estadoVentaVariant } from '../components/Badge';
import { useToast } from '../context/ToastContext';

interface LineaVenta {
  codigoProducto: string;
  cantidad: number;
}

export function VentasPage() {
  const { showToast } = useToast();
  const [ventas, setVentas] = useState<Venta[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [cajas, setCajas] = useState<Caja[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [selected, setSelected] = useState<Venta | null>(null);
  const [idCliente, setIdCliente] = useState('');
  const [idCaja, setIdCaja] = useState('');
  const [lineas, setLineas] = useState<LineaVenta[]>([{ codigoProducto: '', cantidad: 1 }]);
  const [saving, setSaving] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const [v, c, ca, p] = await Promise.all([
        api.ventas.list(),
        api.clientes.list(),
        api.cajas.list(),
        api.productos.list(),
      ]);
      setVentas(v);
      setClientes(c);
      setCajas(ca);
      setProductos(p);
    } catch (e) {
      showToast(e instanceof Error ? e.message : 'Error', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => {
    setIdCliente(clientes[0]?.idCliente ? String(clientes[0].idCliente) : '');
    const cajaAbierta = cajas.find((c) => c.estado === 'ABIERTA');
    setIdCaja(cajaAbierta ? String(cajaAbierta.idCaja) : '');
    setLineas([{ codigoProducto: productos[0]?.codigo || '', cantidad: 1 }]);
    setModalOpen(true);
  };

  const addLinea = () => setLineas([...lineas, { codigoProducto: '', cantidad: 1 }]);
  const removeLinea = (i: number) => setLineas(lineas.filter((_, idx) => idx !== i));

  const updateLinea = (i: number, field: keyof LineaVenta, value: string | number) => {
    const updated = [...lineas];
    updated[i] = { ...updated[i], [field]: value };
    setLineas(updated);
  };

  const calcSubtotal = (codigo: string, cantidad: number) => {
    const prod = productos.find((p) => p.codigo === codigo);
    return prod ? prod.precioVenta * cantidad : 0;
  };

  const totalVenta = lineas.reduce((sum, l) => sum + calcSubtotal(l.codigoProducto, l.cantidad), 0);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.ventas.create({
        idCliente: Number(idCliente),
        idCaja: Number(idCaja),
        detalles: lineas.filter((l) => l.codigoProducto && l.cantidad > 0),
      });
      showToast('Venta registrada correctamente');
      setModalOpen(false);
      load();
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Error', 'error');
    } finally {
      setSaving(false);
    }
  };

  const viewDetail = async (id: number) => {
    try {
      const venta = await api.ventas.get(id);
      setSelected(venta);
      setDetailOpen(true);
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Error', 'error');
    }
  };

  const toggleCaja = async (caja: Caja) => {
    const nuevoEstado = caja.estado === 'ABIERTA' ? 'CERRADA' : 'ABIERTA';
    try {
      await api.cajas.setEstado(caja.idCaja, nuevoEstado);
      showToast(`Caja ${caja.nombre} ${nuevoEstado === 'ABIERTA' ? 'abierta' : 'cerrada'}`);
      load();
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Error', 'error');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="page-title">Ventas</h1>
          <p className="mt-1 text-sm text-slate-500">Registra ventas y consulta el historial</p>
        </div>
        <button className="btn-primary" onClick={openCreate} disabled={clientes.length === 0 || productos.length === 0}>
          <Plus className="h-4 w-4" /> Nueva venta
        </button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {cajas.map((caja) => (
          <div key={caja.idCaja} className="card flex items-center justify-between !py-4">
            <div>
              <p className="font-medium text-slate-900">{caja.nombre}</p>
              <Badge variant={caja.estado === 'ABIERTA' ? 'success' : 'default'}>{caja.estado}</Badge>
            </div>
            <button className="btn-secondary" onClick={() => toggleCaja(caja)}>
              {caja.estado === 'ABIERTA' ? 'Cerrar caja' : 'Abrir caja'}
            </button>
          </div>
        ))}
      </div>

      <div className="card overflow-hidden p-0">
        {loading ? (
          <LoadingSpinner />
        ) : ventas.length === 0 ? (
          <EmptyState message="No hay ventas registradas" />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-100 bg-slate-50 text-left text-xs font-medium uppercase tracking-wide text-slate-500">
                  <th className="px-6 py-3">#</th>
                  <th className="px-6 py-3">Cliente</th>
                  <th className="px-6 py-3">Caja</th>
                  <th className="px-6 py-3">Estado</th>
                  <th className="px-6 py-3">Total</th>
                  <th className="px-6 py-3">Fecha</th>
                  <th className="px-6 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {ventas.map((v) => (
                  <tr key={v.idVenta} className="hover:bg-slate-50/50">
                    <td className="px-6 py-4 font-medium">#{v.idVenta}</td>
                    <td className="px-6 py-4">{v.clienteNombre}</td>
                    <td className="px-6 py-4">{v.cajaNombre}</td>
                    <td className="px-6 py-4"><Badge variant={estadoVentaVariant(v.estado)}>{v.estado}</Badge></td>
                    <td className="px-6 py-4 font-semibold">{formatCurrency(Number(v.total))}</td>
                    <td className="px-6 py-4 text-slate-500">{formatDate(v.fecha)}</td>
                    <td className="px-6 py-4">
                      <div className="flex justify-end">
                        <button className="btn-secondary !px-2 !py-1.5" onClick={() => viewDetail(v.idVenta)}>
                          <Eye className="h-3.5 w-3.5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Nueva venta" size="lg">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Cliente</label>
              <select className="input-field" value={idCliente} onChange={(e) => setIdCliente(e.target.value)} required>
                <option value="">Seleccionar...</option>
                {clientes.map((c) => (
                  <option key={c.idCliente} value={c.idCliente}>{c.nombre} ({c.documento})</option>
                ))}
              </select>
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Caja</label>
              <select className="input-field" value={idCaja} onChange={(e) => setIdCaja(e.target.value)} required>
                <option value="">Seleccionar...</option>
                {cajas.filter((c) => c.estado === 'ABIERTA').map((c) => (
                  <option key={c.idCaja} value={c.idCaja}>{c.nombre}</option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <div className="mb-2 flex items-center justify-between">
              <label className="text-sm font-medium text-slate-700">Productos</label>
              <button type="button" className="text-sm text-brand-600 hover:text-brand-700" onClick={addLinea}>+ Agregar línea</button>
            </div>
            <div className="space-y-2">
              {lineas.map((linea, i) => (
                <div key={i} className="flex items-center gap-2">
                  <select
                    className="input-field flex-1"
                    value={linea.codigoProducto}
                    onChange={(e) => updateLinea(i, 'codigoProducto', e.target.value)}
                    required
                  >
                    <option value="">Producto...</option>
                    {productos.map((p) => (
                      <option key={p.codigo} value={p.codigo}>{p.codigo} — {p.nombre} (stock: {p.stock})</option>
                    ))}
                  </select>
                  <input
                    className="input-field w-20"
                    type="number"
                    min="1"
                    value={linea.cantidad}
                    onChange={(e) => updateLinea(i, 'cantidad', Number(e.target.value))}
                    required
                  />
                  <span className="w-24 text-right text-sm font-medium text-slate-600">
                    {formatCurrency(calcSubtotal(linea.codigoProducto, linea.cantidad))}
                  </span>
                  {lineas.length > 1 && (
                    <button type="button" onClick={() => removeLinea(i)} className="text-red-500 hover:text-red-700">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="flex items-center justify-between border-t border-slate-100 pt-4">
            <p className="text-lg font-bold text-slate-900">Total: {formatCurrency(totalVenta)}</p>
            <div className="flex gap-3">
              <button type="button" className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
              <button type="submit" className="btn-primary" disabled={saving}>{saving ? 'Procesando...' : 'Registrar venta'}</button>
            </div>
          </div>
        </form>
      </Modal>

      <Modal open={detailOpen} onClose={() => setDetailOpen(false)} title={`Venta #${selected?.idVenta}`} size="lg">
        {selected && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div><span className="text-slate-500">Cliente:</span> <span className="font-medium">{selected.clienteNombre}</span></div>
              <div><span className="text-slate-500">Caja:</span> <span className="font-medium">{selected.cajaNombre}</span></div>
              <div><span className="text-slate-500">Estado:</span> <Badge variant={estadoVentaVariant(selected.estado)}>{selected.estado}</Badge></div>
              <div><span className="text-slate-500">Fecha:</span> <span className="font-medium">{formatDate(selected.fecha)}</span></div>
            </div>
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b text-left text-xs text-slate-500">
                  <th className="py-2">Producto</th>
                  <th className="py-2">Cant.</th>
                  <th className="py-2 text-right">Subtotal</th>
                </tr>
              </thead>
              <tbody>
                {selected.detalles?.map((d) => (
                  <tr key={d.idDetalle} className="border-b border-slate-50">
                    <td className="py-2">{d.productoNombre}</td>
                    <td className="py-2">{d.cantidad}</td>
                    <td className="py-2 text-right">{formatCurrency(Number(d.subtotal))}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <p className="text-right text-lg font-bold">Total: {formatCurrency(Number(selected.total))}</p>
          </div>
        )}
      </Modal>
    </div>
  );
}
