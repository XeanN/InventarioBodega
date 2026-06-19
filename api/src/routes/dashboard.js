import { Router } from 'express';
import { data } from '../data/index.js';

const router = Router();

router.get('/stats', (_req, res) => {
  res.json(data.getDashboardStats());
});

export default router;
