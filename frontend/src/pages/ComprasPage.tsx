import { useEffect, useState } from 'react';
import { Plus, Eye, Trash2 } from 'lucide-react';
import { api } from '../lib/api';
import { formatCurrency, formatDate } from '../lib/format';
import type { Compra, Proveedor, Producto } from '../types';
import { Modal } from '../components/Modal';
import { LoadingSpinner, EmptyState } from '../components/LoadingSpinner';
import { useToast } from '../context/ToastContext';

interface LineaCompra {
  codigoProducto: string;
  cantidad: number;
  costoUnitario: number;
}

export function ComprasPage() {
  const { showToast } = useToast();
  const [compras, setCompras] = useState<Compra[]>([]);
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [selected, setSelected] = useState<Compra | null>(null);
  const [idProveedor, setIdProveedor] = useState('');
  const [lineas, setLineas] = useState<LineaCompra[]>([{ codigoProducto: '', cantidad: 1, costoUnitario: 0 }]);
  const [saving, setSaving] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const [co, pr, prod] = await Promise.all([
        api.compras.list(),
        api.proveedores.list(),
        api.productos.list(),
      ]);
      setCompras(co);
      setProveedores(pr);
      setProductos(prod);
    } catch (e) {
      showToast(e instanceof Error ? e.message : 'Error', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => {
    setIdProveedor(proveedores[0]?.idProveedor ? String(proveedores[0].idProveedor) : '');
    setLineas([{ codigoProducto: productos[0]?.codigo || '', cantidad: 1, costoUnitario: 0 }]);
    setModalOpen(true);
  };

  const addLinea = () => setLineas([...lineas, { codigoProducto: '', cantidad: 1, costoUnitario: 0 }]);
  const removeLinea = (i: number) => setLineas(lineas.filter((_, idx) => idx !== i));

  const updateLinea = (i: number, field: keyof LineaCompra, value: string | number) => {
    const updated = [...lineas];
    updated[i] = { ...updated[i], [field]: value };
    setLineas(updated);
  };

  const totalCompra = lineas.reduce((sum, l) => sum + l.cantidad * l.costoUnitario, 0);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.compras.create({
        idProveedor: Number(idProveedor),
        detalles: lineas.filter((l) => l.codigoProducto && l.cantidad > 0),
      });
      showToast('Compra registrada — stock actualizado');
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
      const compra = await api.compras.get(id);
      setSelected(compra);
      setDetailOpen(true);
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Error', 'error');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="page-title">Compras</h1>
          <p className="mt-1 text-sm text-slate-500">Registra ingreso de mercadería desde proveedores</p>
        </div>
        <button className="btn-primary" onClick={openCreate} disabled={proveedores.length === 0 || productos.length === 0}>
          <Plus className="h-4 w-4" /> Nueva compra
        </button>
      </div>

      <div className="card overflow-hidden p-0">
        {loading ? (
          <LoadingSpinner />
        ) : compras.length === 0 ? (
          <EmptyState message="No hay compras registradas" />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-100 bg-slate-50 text-left text-xs font-medium uppercase tracking-wide text-slate-500">
                  <th className="px-6 py-3">#</th>
                  <th className="px-6 py-3">Proveedor</th>
                  <th className="px-6 py-3">Total</th>
                  <th className="px-6 py-3">Fecha</th>
                  <th className="px-6 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {compras.map((c) => (
                  <tr key={c.idCompra} className="hover:bg-slate-50/50">
                    <td className="px-6 py-4 font-medium">#{c.idCompra}</td>
                    <td className="px-6 py-4">{c.proveedorNombre}</td>
                    <td className="px-6 py-4 font-semibold">{formatCurrency(Number(c.total))}</td>
                    <td className="px-6 py-4 text-slate-500">{formatDate(c.fecha)}</td>
                    <td className="px-6 py-4">
                      <div className="flex justify-end">
                        <button className="btn-secondary !px-2 !py-1.5" onClick={() => viewDetail(c.idCompra)}>
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

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Nueva compra" size="lg">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Proveedor</label>
            <select className="input-field" value={idProveedor} onChange={(e) => setIdProveedor(e.target.value)} required>
              <option value="">Seleccionar...</option>
              {proveedores.map((p) => (
                <option key={p.idProveedor} value={p.idProveedor}>{p.nombre}</option>
              ))}
            </select>
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
                      <option key={p.codigo} value={p.codigo}>{p.codigo} — {p.nombre}</option>
                    ))}
                  </select>
                  <input
                    className="input-field w-20"
                    type="number"
                    min="1"
                    placeholder="Cant."
                    value={linea.cantidad}
                    onChange={(e) => updateLinea(i, 'cantidad', Number(e.target.value))}
                    required
                  />
                  <input
                    className="input-field w-28"
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Costo"
                    value={linea.costoUnitario}
                    onChange={(e) => updateLinea(i, 'costoUnitario', Number(e.target.value))}
                    required
                  />
                  <span className="w-24 text-right text-sm font-medium text-slate-600">
                    {formatCurrency(linea.cantidad * linea.costoUnitario)}
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
            <p className="text-lg font-bold text-slate-900">Total: {formatCurrency(totalCompra)}</p>
            <div className="flex gap-3">
              <button type="button" className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
              <button type="submit" className="btn-primary" disabled={saving}>{saving ? 'Procesando...' : 'Registrar compra'}</button>
            </div>
          </div>
        </form>
      </Modal>

      <Modal open={detailOpen} onClose={() => setDetailOpen(false)} title={`Compra #${selected?.idCompra}`} size="lg">
        {selected && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div><span className="text-slate-500">Proveedor:</span> <span className="font-medium">{selected.proveedorNombre}</span></div>
              <div><span className="text-slate-500">Fecha:</span> <span className="font-medium">{formatDate(selected.fecha)}</span></div>
            </div>
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b text-left text-xs text-slate-500">
                  <th className="py-2">Producto</th>
                  <th className="py-2">Cant.</th>
                  <th className="py-2">Costo unit.</th>
                  <th className="py-2 text-right">Subtotal</th>
                </tr>
              </thead>
              <tbody>
                {selected.detalles?.map((d) => (
                  <tr key={d.idDetalle} className="border-b border-slate-50">
                    <td className="py-2">{d.productoNombre}</td>
                    <td className="py-2">{d.cantidad}</td>
                    <td className="py-2">{formatCurrency(d.costoUnitario)}</td>
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
