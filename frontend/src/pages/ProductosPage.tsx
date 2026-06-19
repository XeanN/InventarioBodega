import { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Search } from 'lucide-react';
import { api } from '../lib/api';
import { formatCurrency } from '../lib/format';
import type { Producto } from '../types';
import { Modal } from '../components/Modal';
import { LoadingSpinner, EmptyState } from '../components/LoadingSpinner';
import { Badge } from '../components/Badge';
import { useToast } from '../context/ToastContext';

const emptyForm = { codigo: '', nombre: '', precioVenta: '', stock: '' };

export function ProductosPage() {
  const { showToast } = useToast();
  const [productos, setProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Producto | null>(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  const load = () => {
    setLoading(true);
    api.productos.list().then(setProductos).catch((e) => showToast(e.message, 'error')).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const filtered = productos.filter(
    (p) =>
      p.nombre.toLowerCase().includes(search.toLowerCase()) ||
      p.codigo.toLowerCase().includes(search.toLowerCase())
  );

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setModalOpen(true);
  };

  const openEdit = (p: Producto) => {
    setEditing(p);
    setForm({
      codigo: p.codigo,
      nombre: p.nombre,
      precioVenta: String(p.precioVenta),
      stock: String(p.stock),
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const data = {
        codigo: form.codigo,
        nombre: form.nombre,
        precioVenta: Number(form.precioVenta),
        stock: Number(form.stock),
      };
      if (editing) {
        await api.productos.update(editing.codigo, data);
        showToast('Producto actualizado');
      } else {
        await api.productos.create(data);
        showToast('Producto creado');
      }
      setModalOpen(false);
      load();
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Error', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (codigo: string) => {
    if (!confirm('¿Eliminar este producto?')) return;
    try {
      await api.productos.remove(codigo);
      showToast('Producto eliminado');
      load();
    } catch (err) {
      showToast(err instanceof Error ? err.message : 'Error', 'error');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="page-title">Productos</h1>
          <p className="mt-1 text-sm text-slate-500">Gestiona el inventario de productos</p>
        </div>
        <button className="btn-primary" onClick={openCreate}>
          <Plus className="h-4 w-4" /> Nuevo producto
        </button>
      </div>

      <div className="relative max-w-md">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
        <input
          className="input-field pl-9"
          placeholder="Buscar por código o nombre..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="card overflow-hidden p-0">
        {loading ? (
          <LoadingSpinner />
        ) : filtered.length === 0 ? (
          <EmptyState message="No hay productos registrados" />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-100 bg-slate-50 text-left text-xs font-medium uppercase tracking-wide text-slate-500">
                  <th className="px-6 py-3">Código</th>
                  <th className="px-6 py-3">Nombre</th>
                  <th className="px-6 py-3">Precio</th>
                  <th className="px-6 py-3">Stock</th>
                  <th className="px-6 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filtered.map((p) => (
                  <tr key={p.codigo} className="hover:bg-slate-50/50">
                    <td className="px-6 py-4 font-mono text-xs font-medium text-slate-600">{p.codigo}</td>
                    <td className="px-6 py-4 font-medium text-slate-900">{p.nombre}</td>
                    <td className="px-6 py-4">{formatCurrency(p.precioVenta)}</td>
                    <td className="px-6 py-4">
                      <Badge variant={p.stock < 20 ? 'warning' : 'success'}>{p.stock}</Badge>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex justify-end gap-2">
                        <button className="btn-secondary !px-2 !py-1.5" onClick={() => openEdit(p)}>
                          <Pencil className="h-3.5 w-3.5" />
                        </button>
                        <button className="btn-danger !px-2 !py-1.5" onClick={() => handleDelete(p.codigo)}>
                          <Trash2 className="h-3.5 w-3.5" />
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

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Editar producto' : 'Nuevo producto'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Código</label>
            <input
              className="input-field"
              value={form.codigo}
              onChange={(e) => setForm({ ...form, codigo: e.target.value })}
              disabled={!!editing}
              required
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Nombre</label>
            <input className="input-field" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Precio venta</label>
              <input className="input-field" type="number" step="0.01" min="0" value={form.precioVenta} onChange={(e) => setForm({ ...form, precioVenta: e.target.value })} required />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Stock</label>
              <input className="input-field" type="number" min="0" value={form.stock} onChange={(e) => setForm({ ...form, stock: e.target.value })} required />
            </div>
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" className="btn-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary" disabled={saving}>{saving ? 'Guardando...' : 'Guardar'}</button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
