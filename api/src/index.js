import express from 'express';
import cors from 'cors';
import productosRouter from './routes/productos.js';
import clientesRouter from './routes/clientes.js';
import proveedoresRouter from './routes/proveedores.js';
import cajasRouter from './routes/cajas.js';
import ventasRouter from './routes/ventas.js';
import comprasRouter from './routes/compras.js';
import dashboardRouter from './routes/dashboard.js';

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

app.get('/api/health', (_req, res) => {
  res.json({ status: 'ok', service: 'Inventario Bodega API', mode: 'demo' });
});

app.use('/api/productos', productosRouter);
app.use('/api/clientes', clientesRouter);
app.use('/api/proveedores', proveedoresRouter);
app.use('/api/cajas', cajasRouter);
app.use('/api/ventas', ventasRouter);
app.use('/api/compras', comprasRouter);
app.use('/api/dashboard', dashboardRouter);

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(500).json({ error: 'Error interno del servidor' });
});

app.listen(PORT, () => {
  console.log(`API Inventario Bodega → http://localhost:${PORT}`);
});
