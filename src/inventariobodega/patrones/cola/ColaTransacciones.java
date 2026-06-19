package patrones.cola;

import java.util.LinkedList;
import java.util.Queue;

/**
 * TAD Cola: procesa una solicitud de reserva de stock a la vez,
 * como un turno. Evita que Caja 1 y Caja 2 confirmen la venta
 * del mismo producto al mismo tiempo.
 *
 * synchronized en encolar/procesarSiguiente asegura que dos hilos
 * (dos cajas) nunca modifiquen la cola al mismo tiempo.
 */
public class ColaTransacciones {
    private Queue<TicketVenta> turnos = new LinkedList<>();

    public synchronized void encolar(TicketVenta ticket) {
        turnos.add(ticket);
    }

    public synchronized TicketVenta procesarSiguiente() {
        return turnos.poll();
    }

    public synchronized boolean estaVacia() {
        return turnos.isEmpty();
    }

    public synchronized int tamano() {
        return turnos.size();
    }
}