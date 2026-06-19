import { Router } from 'express';
import { data } from '../data/index.js';

const router = Router();

router.get('/', (_req, res) => {
  res.json(data.listProveedores());
});

router.post('/', (req, res, next) => {
  try {
    const { nombre, contacto } = req.body;
    if (!nombre?.trim()) return res.status(400).json({ error: 'El nombre es obligatorio' });
    const proveedor = data.createProveedor({ nombre: nombre.trim(), contacto: contacto?.trim() });
    res.status(201).json(proveedor);
  } catch (error) {
    next(error);
  }
});

router.put('/:id', (req, res, next) => {
  try {
    const proveedor = data.updateProveedor(Number(req.params.id), {
      nombre: req.body.nombre?.trim(),
      contacto: req.body.contacto?.trim(),
    });
    if (!proveedor) return res.status(404).json({ error: 'Proveedor no encontrado' });
    res.json(proveedor);
  } catch (error) {
    next(error);
  }
});

router.delete('/:id', (req, res, next) => {
  try {
    const ok = data.deleteProveedor(Number(req.params.id));
    if (!ok) return res.status(404).json({ error: 'Proveedor no encontrado' });
    res.status(204).send();
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

export default router;
