import { Router } from 'express';
import { data } from '../data/index.js';

const router = Router();

router.get('/', (_req, res) => {
  res.json(data.listCajas());
});

router.patch('/:id/estado', (req, res) => {
  const { estado } = req.body;
  if (!['ABIERTA', 'CERRADA'].includes(estado)) {
    return res.status(400).json({ error: 'Estado inválido. Use ABIERTA o CERRADA' });
  }
  const caja = data.setCajaEstado(Number(req.params.id), estado);
  if (!caja) return res.status(404).json({ error: 'Caja no encontrada' });
  res.json(caja);
});

export default router;
