import { Router } from 'express';
import { data } from '../data/index.js';

const router = Router();

router.get('/', (_req, res) => {
  res.json(data.listClientes());
});

router.post('/', (req, res, next) => {
  try {
    const { nombre, documento } = req.body;
    if (!nombre?.trim() || !documento?.trim()) {
      return res.status(400).json({ error: 'Nombre y documento son obligatorios' });
    }
    const cliente = data.createCliente({ nombre: nombre.trim(), documento: documento.trim() });
    res.status(201).json(cliente);
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

router.put('/:id', (req, res, next) => {
  try {
    const cliente = data.updateCliente(Number(req.params.id), {
      nombre: req.body.nombre?.trim(),
      documento: req.body.documento?.trim(),
    });
    if (!cliente) return res.status(404).json({ error: 'Cliente no encontrado' });
    res.json(cliente);
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

router.delete('/:id', (req, res, next) => {
  try {
    const ok = data.deleteCliente(Number(req.params.id));
    if (!ok) return res.status(404).json({ error: 'Cliente no encontrado' });
    res.status(204).send();
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

export default router;
