package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DevolucionTicketJsonDao;
import dtoS.TicketDevolucionDTO;
import dtoS.TicketDevolucionItemDTO;
import dtoS.TicketHoyRowDTO;
import model.DevolucionTicketJson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de lectura y construcción de tickets de devolución.
 *
 * Responsabilidades:
 * - leer devolucion_ticket_json desde DAO
 * - parsear json_data
 * - construir TicketDevolucionDTO
 * - construir líneas TicketDevolucionItemDTO
 * - exponer lista resumen de devoluciones para el diálogo de tickets
 *
 * IMPORTANTE:
 * - la UI NO debe parsear JSON
 * - la UI solo recibe DTOs listos para pintar
 */
public class DevolucionTicketService {

    // =====================================================
    // 1) DEPENDENCIAS
    // =====================================================

    private final DevolucionTicketJsonDao devolucionTicketJsonDao;
    private final ObjectMapper objectMapper;

    // =====================================================
    // 2) CONSTRUCTOR
    // =====================================================

    public DevolucionTicketService(DevolucionTicketJsonDao devolucionTicketJsonDao) {
        if (devolucionTicketJsonDao == null) {
            throw new IllegalArgumentException("DevolucionTicketJsonDao no puede ser null");
        }

        this.devolucionTicketJsonDao = devolucionTicketJsonDao;
        this.objectMapper = new ObjectMapper();
    }

    // =====================================================
    // 3) OBTENER TICKET DE DEVOLUCIÓN COMPLETO
    // =====================================================

    public TicketDevolucionDTO getTicketByDevolucion(int idDevolucion) {
        DevolucionTicketJson ticketJson = devolucionTicketJsonDao.findByIdDevolucion(idDevolucion)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe devolucion_ticket_json para la devolución id=" + idDevolucion
                ));

        return parseTicketJson(ticketJson);
    }

    // =====================================================
    // 4) LISTADO RESUMEN DE DEVOLUCIONES
    // =====================================================

    /**
     * Devuelve las devoluciones de hoy en formato fila resumen
     * para JTable/listados.
     */
    public List<TicketHoyRowDTO> getDevolucionesHoy() {
        return devolucionTicketJsonDao.findDevolucionesHoy();
    }

    /**
     * Busca devoluciones por texto para el diálogo de tickets.
     */
    public List<TicketHoyRowDTO> searchDevoluciones(String query) {
        return devolucionTicketJsonDao.searchDevoluciones(query, 200);
    }

    // =====================================================
    // 5) PARSE DEL TICKET COMPLETO
    // =====================================================

    private TicketDevolucionDTO parseTicketJson(DevolucionTicketJson ticketJson) {
        try {
            JsonNode root = objectMapper.readTree(ticketJson.getJsonData());

            TicketDevolucionDTO dto = new TicketDevolucionDTO();

            // -------------------------------------------------
            // Cabecera general
            // -------------------------------------------------
            dto.setIdDevolucion(intOrZero(root, "idDevolucion"));
            dto.setIdVentaOriginal(intOrZero(root, "idVentaOriginal"));
            dto.setFechaGeneracion(ticketJson.getFechaGeneracion());
            dto.setFechaVentaOriginal(textOrNull(root, "fechaVentaOriginal"));

            // -------------------------------------------------
            // Datos del pedido original
            // -------------------------------------------------
            dto.setNombrePedido(textOrDefault(root, "nombrePedido", "Cliente"));
            dto.setTipoServicio(textOrNull(root, "tipoServicio"));
            dto.setMetodoPagoOriginal(textOrNull(root, "metodoPagoOriginal"));

            // -------------------------------------------------
            // Datos de la devolución
            // -------------------------------------------------
            dto.setMetodoReembolso(textOrNull(root, "metodoReembolso"));
            dto.setMotivo(textOrNull(root, "motivo"));
            dto.setObservaciones(textOrNull(root, "observaciones"));
            dto.setTotalDevuelto(decimalOrZero(root, "totalDevuelto"));

            // -------------------------------------------------
            // Líneas
            // -------------------------------------------------
            List<TicketDevolucionItemDTO> items = new ArrayList<>();
            JsonNode itemsNode = root.get("items");

            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    items.add(parseItemNode(itemNode));
                }
            }

            dto.setItems(items);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error parseando json_data del ticket de devolución idDevolucion="
                            + ticketJson.getIdDevolucion(),
                    e
            );
        }
    }

    // =====================================================
    // 6) PARSE DE CADA LÍNEA DEL TICKET
    // =====================================================

    private TicketDevolucionItemDTO parseItemNode(JsonNode itemNode) {
        TicketDevolucionItemDTO item = new TicketDevolucionItemDTO();

        item.setIdVentaItem(intOrZero(itemNode, "idVentaItem"));
        item.setIdProducto(intOrZero(itemNode, "idProducto"));
        item.setNombreProducto(textOrNull(itemNode, "nombreProducto"));
        item.setTamano(textOrNull(itemNode, "tamano"));
        item.setDescripcionResumen(textOrNull(itemNode, "descripcionResumen"));

        item.setCantidadDevuelta(intOrDefault(itemNode, "cantidadDevuelta", 0));

        item.setPrecioUnitario(decimalOrZero(itemNode, "precioUnitario"));
        item.setSubtotalBrutoDevuelto(decimalOrZero(itemNode, "subtotalBrutoDevuelto"));
        item.setImporteDescuentoDevuelto(decimalOrZero(itemNode, "importeDescuentoDevuelto"));
        item.setSubtotalFinalDevuelto(decimalOrZero(itemNode, "subtotalFinalDevuelto"));

        item.setReponeStock(booleanOrFalse(itemNode, "reponeStock"));

        return item;
    }

    // =====================================================
    // 7) HELPERS DE LECTURA SEGURA
    // =====================================================

    private String textOrNull(JsonNode node, String field) {
        if (node == null || field == null) {
            return null;
        }

        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return null;
        }

        String value = child.asText(null);
        if (value == null) {
            return null;
        }

        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private String textOrDefault(JsonNode node, String field, String defaultValue) {
        String value = textOrNull(node, field);
        return (value != null) ? value : defaultValue;
    }

    private int intOrZero(JsonNode node, String field) {
        return intOrDefault(node, field, 0);
    }

    private int intOrDefault(JsonNode node, String field, int defaultValue) {
        if (node == null || field == null) {
            return defaultValue;
        }

        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return defaultValue;
        }

        return child.asInt(defaultValue);
    }

    private BigDecimal decimalOrZero(JsonNode node, String field) {
        if (node == null || field == null) {
            return BigDecimal.ZERO;
        }

        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return BigDecimal.ZERO;
        }

        try {
            return child.decimalValue();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private boolean booleanOrFalse(JsonNode node, String field) {
        if (node == null || field == null) {
            return false;
        }

        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return false;
        }

        return child.asBoolean(false);
    }
}
