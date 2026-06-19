import { useEffect, useState } from 'react';
import { Package, Users, Truck, TrendingUp, AlertTriangle } from 'lucide-react';
import { api } from '../lib/api';
import { formatCurrency, formatDate } from '../lib/format';
import type { DashboardStats } from '../types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Badge, estadoVentaVariant } from '../components/Badge';

function StatCard({
  title,
  value,
  subtitle,
  icon: Icon,
  color,
}: {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: React.ElementType;
  color: string;
}) {
  return (
    <div className="card flex items-start gap-4">
      <div className={`flex h-12 w-12 items-center justify-center rounded-xl ${color}`}>
        <Icon className="h-6 w-6" />
      </div>
      <div>
        <p className="text-sm text-slate-500">{title}</p>
        <p className="mt-1 text-2xl font-bold text-slate-900">{value}</p>
        {subtitle && <p className="mt-0.5 text-xs text-slate-400">{subtitle}</p>}
      </div>
    </div>
  );
}

export function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.dashboard
      .stats()
      .then(setStats)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;
  if (error) {
    return (
      <div className="card border-red-200 bg-red-50 text-red-700">
        <p className="font-medium">No se pudo conectar con la API</p>
        <p className="mt-1 text-sm">{error}</p>
        <p className="mt-3 text-sm">Asegúrate de tener la API corriendo en el puerto 3001 y la base de datos configurada.</p>
      </div>
    );
  }
  if (!stats) return null;

  return (
    <div className="space-y-8">
      <div>
        <h1 className="page-title">Dashboard</h1>
        <p className="mt-1 text-sm text-slate-500">Resumen general del inventario y operaciones del día</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <StatCard title="Productos" value={stats.totalProductos} icon={Package} color="bg-blue-50 text-blue-600" />
        <StatCard title="Clientes" value={stats.totalClientes} icon={Users} color="bg-purple-50 text-purple-600" />
        <StatCard title="Proveedores" value={stats.totalProveedores} icon={Truck} color="bg-amber-50 text-amber-600" />
        <StatCard
          title="Ventas hoy"
          value={stats.ventasHoy}
          subtitle={formatCurrency(stats.ingresosHoy)}
          icon={TrendingUp}
          color="bg-brand-50 text-brand-600"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="card">
          <div className="mb-4 flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-amber-500" />
            <h2 className="font-semibold text-slate-900">Stock bajo</h2>
          </div>
          {stats.stockBajo.length === 0 ? (
            <p className="text-sm text-slate-500">Todos los productos tienen stock suficiente.</p>
          ) : (
            <div className="space-y-3">
              {stats.stockBajo.map((p) => (
                <div key={p.codigo} className="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2">
                  <div>
                    <p className="text-sm font-medium text-slate-900">{p.nombre}</p>
                    <p className="text-xs text-slate-500">{p.codigo}</p>
                  </div>
                  <Badge variant="warning">{p.stock} uds.</Badge>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="card">
          <h2 className="mb-4 font-semibold text-slate-900">Ventas recientes</h2>
          {stats.ventasRecientes.length === 0 ? (
            <p className="text-sm text-slate-500">No hay ventas registradas aún.</p>
          ) : (
            <div className="space-y-3">
              {stats.ventasRecientes.map((v) => (
                <div key={v.idVenta} className="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2">
                  <div>
                    <p className="text-sm font-medium text-slate-900">#{v.idVenta} — {v.cliente}</p>
                    <p className="text-xs text-slate-500">{formatDate(v.fecha)}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-semibold text-slate-900">{formatCurrency(Number(v.total))}</p>
                    <Badge variant={estadoVentaVariant(v.estado)}>{v.estado}</Badge>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
