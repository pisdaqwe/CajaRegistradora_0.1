package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.TicketJsonDao;
import dtoS.TicketClienteDTO;
import dtoS.TicketClienteItemDTO;
import dtoS.TicketHoyRowDTO;
import model.TicketJson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de lectura y construcción de tickets cliente.
 *
 * Responsabilidades:
 * - leer ticket_json desde DAO
 * - parsear json_data
 * - construir TicketClienteDTO
 * - construir líneas TicketClienteItemDTO
 * - exponer lista de tickets del día
 *
 * IMPORTANTE:
 * La UI NO debe parsear JSON.
 * La UI solo recibe DTOs listos para pintar.
 */
public class TicketClienteService {

    private final TicketJsonDao ticketJsonDao;
    private final ObjectMapper objectMapper;

    public TicketClienteService(TicketJsonDao ticketJsonDao) {
        if (ticketJsonDao == null) {
            throw new IllegalArgumentException("TicketJsonDao no puede ser null");
        }
        this.ticketJsonDao = ticketJsonDao;
        this.objectMapper = new ObjectMapper();
    }

    // =====================================================
    // 1. OBTENER TICKET POR ID DE VENTA
    // =====================================================

    public TicketClienteDTO getTicketByVenta(int idVenta) {
        TicketJson ticketJson = ticketJsonDao.findByVenta(idVenta)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe ticket_json para la venta id=" + idVenta
                ));

        return parseTicketJson(ticketJson);
    }

    // =====================================================
    // 2. OBTENER ÚLTIMO TICKET DE LA SESIÓN ACTUAL
    // =====================================================

    public TicketClienteDTO getUltimoTicketDeSesion(int idSesion) {
        TicketJson ticketJson = ticketJsonDao.findUltimoTicketDeSesion(idSesion)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe ningún ticket para la sesión id=" + idSesion
                ));

        return parseTicketJson(ticketJson);
    }

    // =====================================================
    // 3. LISTAR TICKETS DE HOY
    // =====================================================

    public List<TicketHoyRowDTO> getTicketsHoy() {
        return ticketJsonDao.findTicketsHoy();
    }

    // =====================================================
    // 4. LISTAR TICKETS POR FECHA
    // =====================================================

    public List<TicketHoyRowDTO> getTicketsByFecha(LocalDate fecha) {
        return ticketJsonDao.findTicketsByFecha(fecha);
    }

    // =====================================================
    // 5. PARSE DEL TICKET COMPLETO
    // =====================================================

    private TicketClienteDTO parseTicketJson(TicketJson ticketJson) {
        try {
            JsonNode root = objectMapper.readTree(ticketJson.getJsonData());

            TicketClienteDTO dto = new TicketClienteDTO();
            dto.setIdVenta(ticketJson.getIdVenta());
            dto.setFechaGeneracion(ticketJson.getFechaGeneracion());

            dto.setNombrePedido(textOrDefault(root, "nombrePedido", "Cliente"));
            dto.setTipoServicio(textOrNull(root, "tipoServicio"));
            dto.setMetodoPago(textOrNull(root, "metodoPago"));

            BigDecimal montoPagado = decimalOrZero(root, "montoPagado");
            BigDecimal total = decimalOrZero(root, "total");
            BigDecimal cambio = montoPagado.subtract(total);

            dto.setMontoPagado(montoPagado);
            dto.setTotal(total);
            dto.setCambio(cambio);

            List<TicketClienteItemDTO> items = new ArrayList<>();
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
                    "Error parseando json_data del ticket idVenta=" + ticketJson.getIdVenta(), e
            );
        }
    }

    // =====================================================
    // 6. PARSE DE CADA ITEM DEL TICKET
    // =====================================================

    private TicketClienteItemDTO parseItemNode(JsonNode itemNode) {
        TicketClienteItemDTO item = new TicketClienteItemDTO();

        int idProducto = intOrZero(itemNode, "idProducto");
        item.setIdProducto(idProducto);

        String nombreProducto = textOrNull(itemNode, "nombreProducto");
        if (nombreProducto == null || nombreProducto.isBlank()) {
            // Compatibilidad con tickets antiguos que no guardaban nombreProducto
            nombreProducto = "Producto #" + idProducto;
        }
        item.setNombreProducto(nombreProducto);

        item.setCantidad(intOrDefault(itemNode, "cantidad", 1));
        item.setPrecioUnitario(decimalOrZero(itemNode, "precioUnitario"));
        item.setSubtotal(decimalOrZero(itemNode, "subtotal"));
        item.setIva(decimalOrZero(itemNode, "iva"));

        // Extras
        List<String> extras = new ArrayList<>();
        JsonNode extrasNode = itemNode.get("extras");
        if (extrasNode != null && extrasNode.isArray()) {
            for (JsonNode extraNode : extrasNode) {
                String nombreExtra = textOrNull(extraNode, "nombreExtra");
                if (nombreExtra != null && !nombreExtra.isBlank()) {
                    extras.add(nombreExtra);
                }
            }
        }
        item.setExtras(extras);

        // descripcionPersonalizacion viene guardada como String JSON
        String descripcionPersonalizacion = textOrNull(itemNode, "descripcionPersonalizacion");
        parseDescripcionPersonalizacion(item, descripcionPersonalizacion);

        return item;
    }

    // =====================================================
    // 7. PARSE DE DESCRIPCION_PERSONALIZACION
    // =====================================================

    private void parseDescripcionPersonalizacion(TicketClienteItemDTO item, String descripcionPersonalizacion) {
        if (descripcionPersonalizacion == null || descripcionPersonalizacion.isBlank()) {
            item.setTamano(null);
            item.setPersonalizaciones(new ArrayList<>());
            item.setAskMe(new ArrayList<>());
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(descripcionPersonalizacion);

            item.setTamano(textOrNull(root, "tamano"));

            List<String> personalizaciones = new ArrayList<>();
            JsonNode persNode = root.get("personalizaciones");
            if (persNode != null && persNode.isArray()) {
                for (JsonNode p : persNode) {
                    if (p != null && !p.isNull()) {
                        String txt = p.asText(null);
                        if (txt != null && !txt.isBlank()) {
                            personalizaciones.add(txt);
                        }
                    }
                }
            }
            item.setPersonalizaciones(personalizaciones);

            List<String> askMe = new ArrayList<>();
            JsonNode askNode = root.get("askMes");
            if (askNode != null && askNode.isArray()) {
                for (JsonNode a : askNode) {
                    if (a != null && !a.isNull()) {
                        String txt = a.asText(null);
                        if (txt != null && !txt.isBlank()) {
                            askMe.add(txt);
                        }
                    }
                }
            }
            item.setAskMe(askMe);

        } catch (Exception e) {
            // Si el JSON interno está mal, no rompemos todo el ticket
            item.setTamano(null);
            item.setPersonalizaciones(new ArrayList<>());
            item.setAskMe(new ArrayList<>());
        }
    }

    // =====================================================
    // 8. HELPERS DE LECTURA SEGURA
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
}