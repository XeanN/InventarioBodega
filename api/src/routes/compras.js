import { Router } from 'express';
import { data } from '../data/index.js';

const router = Router();

router.get('/', (_req, res) => {
  res.json(data.listCompras());
});

router.get('/:id', (req, res) => {
  const compra = data.getCompra(Number(req.params.id));
  if (!compra) return res.status(404).json({ error: 'Compra no encontrada' });
  res.json(compra);
});

router.post('/', (req, res, next) => {
  try {
    const { idProveedor, detalles } = req.body;
    if (!idProveedor || !Array.isArray(detalles) || detalles.length === 0) {
      return res.status(400).json({ error: 'Proveedor y al menos un detalle son obligatorios' });
    }
    const compra = data.createCompra({ idProveedor, detalles });
    res.status(201).json(compra);
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

export default router;
