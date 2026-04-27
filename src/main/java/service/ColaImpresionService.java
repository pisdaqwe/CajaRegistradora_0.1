package service;

import app.AppContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ColaImpresionDAO;
import dao.EstacionDao;
import dao.ProductoEstacionDao;
import dtoS.ColaItemDescripcionDTO;
import dtoS.ColaMonitorItemDTO;
import model.ColaImpresion;
import model.Estacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service del módulo de cola de impresión / monitor de preparación.
 *
 * Responsabilidades:
 * - registrar items de cola tras una venta
 * - consultar pendientes del día por estación
 * - consumir el siguiente item pendiente de una estación
 * - transformar el modelo persistente a DTO de UI
 *
 * Ajuste multisucursal:
 * - la cola se registra filtrando producto + sucursal
 * - las estaciones ya no se validan por ids fijos 1/2/3
 * - los nombres de estación se resuelven desde BD
 *
 * Auditoría:
 * - NO audita el registro automático tras la venta
 * - SÍ audita acciones manuales:
 *   - imprimir siguiente
 *   - cancelar item
 */
public class ColaImpresionService {

    private final EstacionDao estacionDao;
    private final ColaImpresionDAO colaImpresionDAO;
    private final ProductoEstacionDao productoEstacionDao;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    public ColaImpresionService() {
        this.colaImpresionDAO = new ColaImpresionDAO();
        this.productoEstacionDao = new ProductoEstacionDao();
        this.estacionDao = new EstacionDao();
        this.auditoriaService = null;
        this.objectMapper = new ObjectMapper();
    }

    public ColaImpresionService(ColaImpresionDAO colaImpresionDao,
                                ProductoEstacionDao productoEstacionDao,
                                EstacionDao estacionDao) {
        this.colaImpresionDAO = colaImpresionDao;
        this.productoEstacionDao = productoEstacionDao;
        this.estacionDao = estacionDao;
        this.auditoriaService = null;
        this.objectMapper = new ObjectMapper();
    }

    public ColaImpresionService(ColaImpresionDAO colaImpresionDao,
                                ProductoEstacionDao productoEstacionDao,
                                EstacionDao estacionDao,
                                AuditoriaService auditoriaService) {
        this.colaImpresionDAO = colaImpresionDao;
        this.productoEstacionDao = productoEstacionDao;
        this.estacionDao = estacionDao;
        this.auditoriaService = auditoriaService;
        this.objectMapper = new ObjectMapper();
    }

    public int requireIdEstacionByCodigoYSucursal(String codigo, int idSucursal) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de estación no puede estar vacío.");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("El id de sucursal debe ser mayor que 0.");
        }

        Estacion estacion = estacionDao.findByCodigoAndSucursal(codigo, idSucursal)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe la estación " + codigo + " en la sucursal " + idSucursal
                ));

        return estacion.getIdEstacion();
    }

    // =========================================================
    // 1. REGISTRO DE COLA DESDE VENTA
    // =========================================================

    public void registrarItemEnCola(int idVenta,
                                    int idItem,
                                    int idProducto,
                                    int idSucursal,
                                    ColaItemDescripcionDTO descripcionDto) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("idVenta inválido");
        }
        if (idItem <= 0) {
            throw new IllegalArgumentException("idItem inválido");
        }
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto inválido");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal inválido");
        }
        if (descripcionDto == null) {
            throw new IllegalArgumentException("descripcionDto no puede ser null");
        }

        List<Integer> idsEstacion =
                productoEstacionDao.findIdsEstacionByProductoYSucursal(idProducto, idSucursal);

        if (idsEstacion == null || idsEstacion.isEmpty()) {
            return;
        }

        String descripcionSerializada = serializeDescripcion(descripcionDto);
        LocalDateTime ahora = LocalDateTime.now();

        for (Integer idEstacion : idsEstacion) {
            ColaImpresion cola = new ColaImpresion();
            cola.setIdVenta(idVenta);
            cola.setIdItem(idItem);
            cola.setIdEstacion(idEstacion);
            cola.setDescripcion(descripcionSerializada);
            cola.setImpreso(false);
            cola.setPreparado(false);
            cola.setCancelado(false);
            cola.setFechaCreacion(ahora);
            cola.setFechaImpresion(null);
            cola.setFechaPreparado(null);

            colaImpresionDAO.insert(cola);
        }
    }

    public void registrarItemsEnCola(List<ColaRegistroItemCommand> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (ColaRegistroItemCommand item : items) {
            registrarItemEnCola(
                    item.getIdVenta(),
                    item.getIdItem(),
                    item.getIdProducto(),
                    item.getIdSucursal(),
                    item.getDescripcion()
            );
        }
    }

    // =========================================================
    // 2. CONSULTA DE PENDIENTES DEL DÍA
    // =========================================================

    public List<ColaMonitorItemDTO> getPendientesHoyByEstacion(int idEstacion) {
        validarEstacion(idEstacion);

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        List<ColaImpresion> filas = colaImpresionDAO.findPendientesHoyByEstacion(idEstacion, inicioDia, finDia);

        if (filas == null || filas.isEmpty()) {
            return Collections.emptyList();
        }

        List<ColaMonitorItemDTO> result = new ArrayList<>();
        for (ColaImpresion fila : filas) {
            result.add(toMonitorDto(fila));
        }
        return result;
    }

    // =========================================================
    // 3. IMPRIMIR SIGUIENTE
    // =========================================================

    public ColaMonitorItemDTO imprimirSiguiente(int idEstacion) {
        validarEstacion(idEstacion);

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        Optional<ColaImpresion> nextOpt =
                colaImpresionDAO.findSiguientePendienteHoyByEstacion(idEstacion, inicioDia, finDia);

        if (nextOpt.isEmpty()) {
            return null;
        }

        ColaImpresion siguiente = nextOpt.get();

        LocalDateTime ahora = LocalDateTime.now();
        colaImpresionDAO.marcarImpresoYPreparado(siguiente.getIdCola(), ahora, ahora);

        siguiente.setImpreso(true);
        siguiente.setPreparado(true);
        siguiente.setFechaImpresion(ahora);
        siguiente.setFechaPreparado(ahora);

        ColaMonitorItemDTO dto = toMonitorDto(siguiente);

        auditarSeguro(
                "COLA_IMPRESION_IMPRIMIR_SIGUIENTE",
                detallesImprimirSiguiente(siguiente, dto)
        );

        return dto;
    }

    // =========================================================
    // 4. CANCELACIÓN
    // =========================================================

    public void cancelar(int idCola) {
        if (idCola <= 0) {
            throw new IllegalArgumentException("idCola inválido");
        }

        colaImpresionDAO.marcarCancelado(idCola);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idCola", idCola);

        auditarSeguro("COLA_IMPRESION_CANCELAR_ITEM", data);
    }

    // =========================================================
    // 5. TRANSFORMACIONES A DTO DE UI
    // =========================================================

    private ColaMonitorItemDTO toMonitorDto(ColaImpresion fila) {
        ColaItemDescripcionDTO desc = deserializeDescripcion(fila.getDescripcion());

        String nombreEstacion = getNombreEstacionById(fila.getIdEstacion());
        String resumen = buildResumenLista(desc);
        String detalle = buildDetalleTexto(desc, fila, nombreEstacion);

        ColaMonitorItemDTO dto = new ColaMonitorItemDTO();
        dto.setIdCola(fila.getIdCola());
        dto.setIdVenta(fila.getIdVenta());
        dto.setIdItem(fila.getIdItem());
        dto.setIdEstacion(fila.getIdEstacion());
        dto.setNombreEstacion(nombreEstacion);
        dto.setResumenLista(resumen);
        dto.setDetalleTexto(detalle);
        dto.setFechaCreacion(fila.getFechaCreacion());

        return dto;
    }

    /**
     * Texto corto de la lista del monitor.
     * Ahora también mete el café si existe.
     */
    private String buildResumenLista(ColaItemDescripcionDTO desc) {
        String producto = safe(desc.getProducto(), "Producto");
        String tamano = safe(desc.getTamano(), "");
        String tipoCafe = safe(desc.getTipoCafe(), "");
        String nombrePedido = safe(desc.getNombrePedido(), "SIN_NOMBRE");

        StringBuilder sb = new StringBuilder();
        sb.append(producto);

        if (!tamano.isBlank()) {
            sb.append(" - ").append(tamano);
        }

        if (!tipoCafe.isBlank()) {
            sb.append(" - ").append(tipoCafe);
        }

        sb.append(" | ").append(nombrePedido);
        return sb.toString();
    }

    /**
     * Texto largo del mini-ticket simulado de estación.
     * Ahora también mete el café si existe.
     */
    private String buildDetalleTexto(ColaItemDescripcionDTO desc, ColaImpresion fila, String nombreEstacion) {
        StringBuilder sb = new StringBuilder();

        sb.append("ESTACIÓN: ").append(nombreEstacion).append("\n");
        sb.append("COLA #: ").append(fila.getIdCola()).append("\n");
        sb.append("VENTA #: ").append(fila.getIdVenta()).append("\n");
        sb.append("ITEM #: ").append(fila.getIdItem()).append("\n");
        sb.append("----------------------------------------").append("\n");

        sb.append("PEDIDO: ").append(safe(desc.getNombrePedido(), "SIN_NOMBRE")).append("\n");
        sb.append("SERVICIO: ").append(safe(desc.getTipoServicio(), "SIN_SERVICIO")).append("\n");
        sb.append("\n");

        sb.append("PRODUCTO: ").append(safe(desc.getProducto(), "SIN_PRODUCTO"));
        if (!safe(desc.getTamano(), "").isBlank()) {
            sb.append(" - ").append(desc.getTamano());
        }
        sb.append("\n");

        if (!safe(desc.getTipoCafe(), "").isBlank()) {
            sb.append("CAFÉ: ").append(desc.getTipoCafe()).append("\n");
        }

        if (desc.getCantidad() != null) {
            sb.append("CANTIDAD: ").append(desc.getCantidad()).append("\n");
        }

        if (desc.getExtras() != null && !desc.getExtras().isEmpty()) {
            sb.append("\nEXTRAS:\n");
            for (String extra : desc.getExtras()) {
                sb.append("- ").append(extra).append("\n");
            }
        }

        if (desc.getPersonalizaciones() != null && !desc.getPersonalizaciones().isEmpty()) {
            sb.append("\nPERSONALIZACIONES:\n");
            for (String p : desc.getPersonalizaciones()) {
                sb.append("- ").append(p).append("\n");
            }
        }

        if (desc.getAskMe() != null && !desc.getAskMe().isEmpty()) {
            sb.append("\nASK ME:\n");
            for (String ask : desc.getAskMe()) {
                sb.append("- ").append(ask).append("\n");
            }
        }

        return sb.toString().trim();
    }

    // =========================================================
    // 6. SERIALIZACIÓN / DESERIALIZACIÓN
    // =========================================================

    private String serializeDescripcion(ColaItemDescripcionDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando descripcion de cola a JSON.", e);
        }
    }

    private ColaItemDescripcionDTO deserializeDescripcion(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ColaItemDescripcionDTO();
        }

        try {
            return objectMapper.readValue(raw, ColaItemDescripcionDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializando descripcion de cola desde JSON.", e);
        }
    }

    // =========================================================
    // 7. HELPERS
    // =========================================================

    private void validarEstacion(int idEstacion) {
        if (idEstacion <= 0) {
            throw new IllegalArgumentException("idEstacion no válida: " + idEstacion);
        }

        boolean exists = estacionDao.findById(idEstacion).isPresent();
        if (!exists) {
            throw new IllegalArgumentException("La estación no existe: " + idEstacion);
        }
    }

    public String getNombreEstacionById(int idEstacion) {
        if (idEstacion <= 0) {
            throw new IllegalArgumentException("El id de estación debe ser mayor que 0.");
        }

        Estacion estacion = estacionDao.findById(idEstacion)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe la estación con id " + idEstacion
                ));

        return estacion.getNombre();
    }

    private String safe(String value, String fallback) {
        return value != null ? value : fallback;
    }

    private Integer parseIntegerSafe(String raw, int fallback) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    public List<Estacion> getEstacionesBySucursal(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("El id de sucursal debe ser mayor que 0.");
        }

        return estacionDao.findBySucursal(idSucursal);
    }

    private Map<String, Object> detallesImprimirSiguiente(ColaImpresion fila, ColaMonitorItemDTO dto) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idCola", fila.getIdCola());
        data.put("idVenta", fila.getIdVenta());
        data.put("idItem", fila.getIdItem());
        data.put("idEstacion", fila.getIdEstacion());
        data.put("nombreEstacion", dto != null ? dto.getNombreEstacion() : getNombreEstacionById(fila.getIdEstacion()));
        data.put("fechaImpresion", fila.getFechaImpresion() != null ? fila.getFechaImpresion().toString() : null);
        return data;
    }

    private void auditarSeguro(String accion, Map<String, Object> detalles) {
        if (auditoriaService == null) {
            return;
        }

        int idUsuario = AppContext.getUsuarioId();
        int idSucursal = AppContext.getIdSucursal();

        if (idUsuario <= 0 || idSucursal <= 0) {
            return;
        }

        try {
            auditoriaService.registrarEvento(
                    idUsuario,
                    idSucursal,
                    accion,
                    detalles
            );
        } catch (Exception ex) {
            System.err.println("[AUDITORIA] No se pudo registrar evento " + accion + ": " + ex.getMessage());
        }
    }

    // =========================================================
    // 8. COMMAND AUXILIAR PARA REGISTRO MASIVO
    // =========================================================

    public static class ColaRegistroItemCommand {

        private final int idVenta;
        private final int idItem;
        private final int idProducto;
        private final int idSucursal;
        private final ColaItemDescripcionDTO descripcion;

        public ColaRegistroItemCommand(int idVenta,
                                       int idItem,
                                       int idProducto,
                                       int idSucursal,
                                       ColaItemDescripcionDTO descripcion) {
            this.idVenta = idVenta;
            this.idItem = idItem;
            this.idProducto = idProducto;
            this.idSucursal = idSucursal;
            this.descripcion = descripcion;
        }

        public int getIdVenta() {
            return idVenta;
        }

        public int getIdItem() {
            return idItem;
        }

        public int getIdProducto() {
            return idProducto;
        }

        public int getIdSucursal() {
            return idSucursal;
        }

        public ColaItemDescripcionDTO getDescripcion() {
            return descripcion;
        }
    }
}