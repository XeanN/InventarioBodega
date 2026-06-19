import { Router } from 'express';
import { data } from '../data/index.js';

const router = Router();

router.get('/', (_req, res) => {
  res.json(data.listProductos());
});

router.get('/:codigo', (req, res) => {
  const producto = data.getProducto(req.params.codigo);
  if (!producto) return res.status(404).json({ error: 'Producto no encontrado' });
  res.json(producto);
});

router.post('/', (req, res, next) => {
  try {
    const { codigo, nombre, precioVenta, stock } = req.body;
    if (!codigo?.trim() || !nombre?.trim()) {
      return res.status(400).json({ error: 'Código y nombre son obligatorios' });
    }
    const producto = data.createProducto({
      codigo: codigo.trim(),
      nombre: nombre.trim(),
      precioVenta: Number(precioVenta) || 0,
      stock: Number(stock) || 0,
    });
    res.status(201).json(producto);
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

router.put('/:codigo', (req, res, next) => {
  try {
    const { nombre, precioVenta, stock } = req.body;
    const producto = data.updateProducto(req.params.codigo, {
      nombre: nombre?.trim(),
      precioVenta: precioVenta != null ? Number(precioVenta) : undefined,
      stock: stock != null ? Number(stock) : undefined,
    });
    if (!producto) return res.status(404).json({ error: 'Producto no encontrado' });
    res.json(producto);
  } catch (error) {
    next(error);
  }
});

router.delete('/:codigo', (req, res, next) => {
  try {
    const ok = data.deleteProducto(req.params.codigo);
    if (!ok) return res.status(404).json({ error: 'Producto no encontrado' });
    res.status(204).send();
  } catch (error) {
    if (error.status) return res.status(error.status).json({ error: error.message });
    next(error);
  }
});

export default router;
