import { Router } from 'express';
import { data } from '../data/index.js';

const router = Router();

router.get('/', (_req, res) => {
  res.json(data.listVentas());
});

router.get('/:id', (req, res) => {
  const venta = data.getVenta(Number(req.params.id));
  if (!venta) return res.status(404).json({ error: 'Venta no encontrada' });
  res.json(venta);
});

router.post('/', (req, res, next) => {
  try {
    const { idCliente, idCaja, detalles } = req.body;
    if (!idCliente || !idCaja || !Array.isArray(detalles) || detalles.length === 0) {
      return res.status(400).json({ error: 'Cliente, caja y al menos un detalle son obligatorios' });
    }
    const venta = data.createVenta({ idCliente, idCaja, detalles });
    res.status(201).json(venta);
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

export default router;
