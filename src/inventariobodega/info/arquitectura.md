inventariobodega/
│
├── modelo/                              → Solo datos (atributos + getters/setters), sin lógica de negocio
│   ├── Producto.java
│   ├── Cliente.java
│   ├── Proveedor.java
│   ├── Caja.java
│   ├── Venta.java
│   ├── DetalleVenta.java
│   ├── Compra.java
│   ├── DetalleCompra.java
│   ├── ReservaStock.java                → clave para resolver concurrencia entre cajas
│   └── EstadoPedido.java                → enum: PENDIENTE, RESERVADO, PAGADO, ENTREGADO
│
├── repositorio/
│   ├── ProductoRepositorio.java         → interfaz
│   ├── ProductoRepositorioJDBC.java     → implementación con SQL
│   ├── ClienteRepositorio.java
│   ├── ClienteRepositorioJDBC.java
│   ├── ProveedorRepositorio.java
│   ├── ProveedorRepositorioJDBC.java
│   ├── VentaRepositorio.java
│   ├── VentaRepositorioJDBC.java
│   ├── CompraRepositorio.java
│   └── CompraRepositorioJDBC.java
│
├── servicio/
│   ├── InventarioService.java           → consulta y descuenta stock real (Observer escucha aquí)
│   ├── VentaService.java                → orquesta venta: reserva → cobro → confirma
│   ├── CompraService.java               → registra ingreso de mercadería
│   ├── ClienteService.java
│   ├── ProveedorService.java
│   └── CajaService.java                 → administra apertura/cierre y las dos cajas activas
│
├── controlador/
│   ├── VentaControlador.java
│   ├── InventarioControlador.java
│   ├── CompraControlador.java
│   ├── ClienteControlador.java
│   └── ProveedorControlador.java
│
├── vista/
│   ├── MenuPrincipal.java
│   ├── VentaForm.java
│   ├── InventarioForm.java
│   ├── CompraForm.java
│   ├── ClienteForm.java
│   └── ProveedorForm.java
│
├── patrones/
│   ├── observer/
│   │   ├── InventarioObservable.java    → Venta avisa cuando se confirma
│   │   └── InventarioObserver.java      → interfaz que implementa InventarioService
│   └── cola/
│       ├── ColaTransacciones.java       → TAD Queue, una venta a la vez por producto
│       └── TicketVenta.java             → objeto que representa el "turno"
│
└── util/
    ├── LE.java                          → 
    ├── ConexionBD.java                  → Singleton de conexión a Supabase (única ubicación)
    └── Validaciones.java                → validar stock negativo, campos vacíos, etc.