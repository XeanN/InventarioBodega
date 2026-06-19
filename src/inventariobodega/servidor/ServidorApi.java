package servidor;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controlador.CajaControlador;
import controlador.ClienteControlador;
import controlador.CompraControlador;
import controlador.DashboardControlador;
import controlador.InventarioControlador;
import controlador.ProveedorControlador;
import controlador.VentaControlador;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import util.ConfigBD;
import util.ConexionBD;
import util.JsonUtil;

/**
 * API REST del backend Java para el frontend web.
 * Expone los mismos endpoints que consume React en /api/*
 */
public class ServidorApi {
    private final InventarioControlador productos = new InventarioControlador();
    private final ClienteControlador clientes = new ClienteControlador();
    private final ProveedorControlador proveedores = new ProveedorControlador();
    private final CajaControlador cajas = new CajaControlador();
    private final VentaControlador ventas = new VentaControlador();
    private final CompraControlador compras = new CompraControlador();
    private final DashboardControlador dashboard = new DashboardControlador();

    public static void main(String[] args) throws IOException {
        int puerto = ConfigBD.getInt("api.puerto", 8080);
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        ServidorApi api = new ServidorApi();
        server.createContext("/api", api::manejar);
        server.setExecutor(null);
        server.start();
        System.out.println("Backend Java API → http://localhost:" + puerto + "/api");
        System.out.println("Conectado a PostgreSQL. El frontend React usa este servidor.");
    }

    private void manejar(HttpExchange exchange) throws IOException {
        addCors(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath().replaceFirst("^/api", "");
            String method = exchange.getRequestMethod().toUpperCase();
            String body = leerBody(exchange);

            if (path.equals("/health") && "GET".equals(method)) {
                responder(exchange, 200, JsonUtil.obj(java.util.Map.of(
                        "status", "ok",
                        "service", "Inventario Bodega API",
                        "mode", "java-backend"
                )));
                return;
            }

            if (path.equals("/productos")) {
                if ("GET".equals(method)) {
                    responder(exchange, 200, productos.listarJson());
                } else if ("POST".equals(method)) {
                    responder(exchange, 201, productos.crearJson(body));
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.startsWith("/productos/")) {
                String codigo = path.substring("/productos/".length());
                if ("GET".equals(method)) {
                    String json = productos.buscarJson(codigo);
                    if (json == null) {
                        error(exchange, 404, "Producto no encontrado");
                    } else {
                        responder(exchange, 200, json);
                    }
                } else if ("PUT".equals(method)) {
                    String json = productos.actualizarJson(codigo, body);
                    if (json == null) {
                        error(exchange, 404, "Producto no encontrado");
                    } else {
                        responder(exchange, 200, json);
                    }
                } else if ("DELETE".equals(method)) {
                    productos.eliminar(codigo);
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.equals("/clientes")) {
                if ("GET".equals(method)) {
                    responder(exchange, 200, clientes.listarJson());
                } else if ("POST".equals(method)) {
                    responder(exchange, 201, clientes.crearJson(body));
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.startsWith("/clientes/")) {
                int id = Integer.parseInt(path.substring("/clientes/".length()));
                if ("PUT".equals(method)) {
                    responder(exchange, 200, clientes.actualizarJson(id, body));
                } else if ("DELETE".equals(method)) {
                    clientes.eliminar(id);
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.equals("/proveedores")) {
                if ("GET".equals(method)) {
                    responder(exchange, 200, proveedores.listarJson());
                } else if ("POST".equals(method)) {
                    responder(exchange, 201, proveedores.crearJson(body));
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.startsWith("/proveedores/")) {
                int id = Integer.parseInt(path.substring("/proveedores/".length()));
                if ("PUT".equals(method)) {
                    responder(exchange, 200, proveedores.actualizarJson(id, body));
                } else if ("DELETE".equals(method)) {
                    proveedores.eliminar(id);
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.equals("/cajas") && "GET".equals(method)) {
                responder(exchange, 200, cajas.listarJson());
                return;
            }

            if (path.matches("/cajas/\\d+/estado") && "PATCH".equals(method)) {
                int id = Integer.parseInt(path.split("/")[2]);
                String json = cajas.cambiarEstadoJson(id, body);
                if (json == null) {
                    error(exchange, 404, "Caja no encontrada");
                } else {
                    responder(exchange, 200, json);
                }
                return;
            }

            if (path.equals("/ventas")) {
                if ("GET".equals(method)) {
                    responder(exchange, 200, ventas.listarJson());
                } else if ("POST".equals(method)) {
                    responder(exchange, 201, ventas.crearJson(body));
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.startsWith("/ventas/")) {
                int id = Integer.parseInt(path.substring("/ventas/".length()));
                if ("GET".equals(method)) {
                    String json = ventas.buscarJson(id);
                    if (json == null) {
                        error(exchange, 404, "Venta no encontrada");
                    } else {
                        responder(exchange, 200, json);
                    }
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.equals("/compras")) {
                if ("GET".equals(method)) {
                    responder(exchange, 200, compras.listarJson());
                } else if ("POST".equals(method)) {
                    responder(exchange, 201, compras.crearJson(body));
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.startsWith("/compras/")) {
                int id = Integer.parseInt(path.substring("/compras/".length()));
                if ("GET".equals(method)) {
                    String json = compras.buscarJson(id);
                    if (json == null) {
                        error(exchange, 404, "Compra no encontrada");
                    } else {
                        responder(exchange, 200, json);
                    }
                } else {
                    metodoNoPermitido(exchange);
                }
                return;
            }

            if (path.equals("/dashboard/stats") && "GET".equals(method)) {
                responder(exchange, 200, dashboard.statsJson());
                return;
            }

            error(exchange, 404, "Ruta no encontrada");
        } catch (IllegalArgumentException e) {
            error(exchange, 400, e.getMessage());
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "Error interno" : e.getMessage();
            if (msg.contains("duplicate key") || msg.contains("ya existe")) {
                error(exchange, 409, msg);
            } else if (msg.contains("foreign key") || msg.contains("asociad")) {
                error(exchange, 409, msg);
            } else if (msg.contains("no encontrad") || msg.contains("Stock insuficiente") || msg.contains("ABIERTA")) {
                error(exchange, 400, msg);
            } else {
                e.printStackTrace();
                error(exchange, 500, msg);
            }
        }
    }

    private static void addCors(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String leerBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void responder(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static void error(HttpExchange exchange, int code, String message) throws IOException {
        responder(exchange, code, JsonUtil.obj(java.util.Map.of("error", message)));
    }

    private static void metodoNoPermitido(HttpExchange exchange) throws IOException {
        error(exchange, 405, "Método no permitido");
    }
}
