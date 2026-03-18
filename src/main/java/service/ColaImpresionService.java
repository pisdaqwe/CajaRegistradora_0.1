package service;

import dao.ColaImpresionDAO;
import dao.ProductoEstacionDao;
import dtoS.ColaItemDescripcionDTO;
import dtoS.ColaMonitorItemDTO;
import model.ColaImpresion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service del módulo de cola de impresión / monitor de preparación.
 *
 * Responsabilidades:
 * - registrar items de cola tras una venta
 * - consultar pendientes del día por estación
 * - consumir el siguiente item pendiente de una estación
 * - transformar el modelo persistente a DTO de UI
 *
 * NOTA:
 * En descripcion guardamos un snapshot estructurado del item.
 * De momento se serializa como texto estructurado simple.
 * Más adelante se puede sustituir por JSON real con Jackson/Gson
 * sin cambiar la lógica general del service.
 */
public class ColaImpresionService {

    public static final int ESTACION_BEBIDAS_CALIENTES = 1;
    public static final int ESTACION_BEBIDAS_FRIAS = 2;
    public static final int ESTACION_COMIDA = 3;

    private final ColaImpresionDAO colaImpresionDAO;
    private final ProductoEstacionDao productoEstacionDao;
    
    private final ObjectMapper objectMapper;
    

    /**
     * Constructor principal.
     * Usa los DAOs reales del proyecto.
     */
    public ColaImpresionService() {
        this.colaImpresionDAO = new ColaImpresionDAO();
        this.productoEstacionDao = new ProductoEstacionDao();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor opcional para tests o para inyección manual.
     */
    public ColaImpresionService(ColaImpresionDAO colaImpresionDao,
                                ProductoEstacionDao productoEstacionDao) {
        this.colaImpresionDAO = colaImpresionDao;
        this.productoEstacionDao = productoEstacionDao;
        this.objectMapper = new ObjectMapper();
    }

    // =========================================================
    // 1. REGISTRO DE COLA DESDE VENTA
    // =========================================================

    /**
     * Registra en cola un único item persistido de venta.
     *
     * Caso típico:
     * - ya existe idVenta real
     * - ya existe idItem real
     * - ya conoces idProducto
     * - ya puedes construir ColaItemDescripcionDTO
     *
     * El service resuelve la/s estación/es del producto
     * y genera una fila de cola por cada estación.
     */
    public void registrarItemEnCola(int idVenta,
                                    int idItem,
                                    int idProducto,
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
        if (descripcionDto == null) {
            throw new IllegalArgumentException("descripcionDto no puede ser null");
        }

        List<Integer> idsEstacion = productoEstacionDao.findIdsEstacionByProducto(idProducto);

        // Regla de negocio:
        // si un producto no tiene estación asignada, NO rompemos la venta.
        // simplemente no generamos cola para ese item.
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

    /**
     * Variante cómoda para registrar varios items en cola.
     *
     * Te sirve para construir una lista desde el flujo de cobro real
     * y registrar todos los items de una vez.
     */
    public void registrarItemsEnCola(List<ColaRegistroItemCommand> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (ColaRegistroItemCommand item : items) {
            registrarItemEnCola(
                    item.getIdVenta(),
                    item.getIdItem(),
                    item.getIdProducto(),
                    item.getDescripcion()
            );
        }
    }

    // =========================================================
    // 2. CONSULTA DE PENDIENTES DEL DÍA
    // =========================================================

    /**
     * Devuelve los pendientes del día actual para una estación.
     *
     * Reglas:
     * - solo fecha de hoy
     * - no preparados
     * - no cancelados
     * - orden natural de cola (id_cola asc)
     */
    public List<ColaMonitorItemDTO> getPendientesHoyByEstacion(int idEstacion) {
        validarEstacion(idEstacion);

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        List<ColaImpresion> filas = colaImpresionDAO.findPendientesHoyByEstacion(
                idEstacion,
                inicioDia,
                finDia
        );

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

    /**
     * Simula "imprimir siguiente" en una estación.
     *
     * Flujo MVP:
     * - busca el primer pendiente del día por id_cola asc
     * - lo marca como impreso y preparado
     * - devuelve el DTO listo para mostrar en el JTextArea
     *
     * Si no hay pendientes, devuelve null.
     */
    public ColaMonitorItemDTO imprimirSiguiente(int idEstacion) {
        validarEstacion(idEstacion);

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        Optional<ColaImpresion> nextOpt = colaImpresionDAO.findSiguientePendienteHoyByEstacion(
                idEstacion,
                inicioDia,
                finDia
        );

        if (nextOpt.isEmpty()) {
            return null;
        }

        ColaImpresion siguiente = nextOpt.get();

        LocalDateTime ahora = LocalDateTime.now();
        colaImpresionDAO.marcarImpresoYPreparado(
                siguiente.getIdCola(),
                ahora,
                ahora
        );

        // Actualizamos también el objeto en memoria
        siguiente.setImpreso(true);
        siguiente.setPreparado(true);
        siguiente.setFechaImpresion(ahora);
        siguiente.setFechaPreparado(ahora);

        return toMonitorDto(siguiente);
    }

    // =========================================================
    // 4. CANCELACIÓN
    // =========================================================

    /**
     * Cancela un item de cola por id.
     */
    public void cancelar(int idCola) {
        if (idCola <= 0) {
            throw new IllegalArgumentException("idCola inválido");
        }
        colaImpresionDAO.marcarCancelado(idCola);
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

    private String buildResumenLista(ColaItemDescripcionDTO desc) {
        String producto = safe(desc.getProducto(), "Producto");
        String tamano = safe(desc.getTamano(), "");
        String nombrePedido = safe(desc.getNombrePedido(), "SIN_NOMBRE");

        if (!tamano.isBlank()) {
            return producto + " - " + tamano + " | " + nombrePedido;
        }
        return producto + " | " + nombrePedido;
    }

    private String buildDetalleTexto(ColaItemDescripcionDTO desc,
                                     ColaImpresion fila,
                                     String nombreEstacion) {

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
    // 6. SERIALIZACIÓN / DESERIALIZACIÓN SIMPLE
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
        if (idEstacion != ESTACION_BEBIDAS_CALIENTES
                && idEstacion != ESTACION_BEBIDAS_FRIAS
                && idEstacion != ESTACION_COMIDA) {
            throw new IllegalArgumentException("idEstacion no válida: " + idEstacion);
        }
    }

    private String getNombreEstacionById(Integer idEstacion) {
        if (idEstacion == null) {
            return "SIN_ESTACION";
        }

        return switch (idEstacion) {
            case ESTACION_BEBIDAS_CALIENTES -> "BEBIDAS_CALIENTES";
            case ESTACION_BEBIDAS_FRIAS -> "BEBIDAS_FRIAS";
            case ESTACION_COMIDA -> "COMIDA";
            default -> "ESTACION_" + idEstacion;
        };
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

    // =========================================================
    // 8. COMMAND AUXILIAR PARA REGISTRO MASIVO
    // =========================================================

    /**
     * Comando sencillo para registrar varios items en cola
     * sin depender todavía de tus DTOs concretos de cobro.
     */
    public static class ColaRegistroItemCommand {

        private final int idVenta;
        private final int idItem;
        private final int idProducto;
        private final ColaItemDescripcionDTO descripcion;

        public ColaRegistroItemCommand(int idVenta,
                                       int idItem,
                                       int idProducto,
                                       ColaItemDescripcionDTO descripcion) {
            this.idVenta = idVenta;
            this.idItem = idItem;
            this.idProducto = idProducto;
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

        public ColaItemDescripcionDTO getDescripcion() {
            return descripcion;
        }
    }
}
