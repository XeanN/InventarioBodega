import dotenv from 'dotenv';
import { memoryStore } from './memoryStore.js';
import { createPgStore } from './pgStore.js';

dotenv.config();

const useMock = process.env.USE_MOCK === 'true';
const hasDatabase = Boolean(process.env.DATABASE_URL);

let data;
if (useMock || !hasDatabase) {
  data = memoryStore;
  console.log('⚠ Modo DEMO (memoria). Para usar PostgreSQL: configura DATABASE_URL en api/.env');
  console.log('  Para producción con tu compañero: usa el backend Java (servidor.ServidorApi) en puerto 8080');
} else {
  data = createPgStore();
  console.log('✓ API Node conectada a PostgreSQL');
}

export { data };
