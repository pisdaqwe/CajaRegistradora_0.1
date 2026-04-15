package service;

import dtoS.ExtraDTO;
import dtoS.MermaExtraRequest;
import dtoS.MermaItemRequest;
import dtoS.MermaPersonalizacionRequest;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import dtoS.TipoCafeDTO;
import model.TicketItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper auxiliar que convierte una línea de merma
 * en un TicketItem temporal para reutilizar
 * RecipeResolverService sin duplicar lógica.
 *
 * IDEA:
 * - MermaService trabaja con MermaItemRequest
 * - RecipeResolverService ya sabe trabajar con TicketItem
 * - este mapper hace de puente entre ambos
 *
 * IMPORTANTE:
 * - NO persiste nada
 * - NO calcula precios comerciales reales
 * - el precio base del TicketItem aquí se usa a 0
 *   porque para recetas/stock no hace falta
 */
public final class MermaTicketMapper {

    private MermaTicketMapper() {
        // Utility class
    }

    /**
     * Convierte una línea de merma en un TicketItem temporal.
     */
    public static TicketItem toTicketItem(MermaItemRequest item) {
        if (item == null) {
            throw new IllegalArgumentException("MermaItemRequest no puede ser null");
        }
        if (item.getIdProducto() <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }
        if (item.getIdTamano() == null || item.getIdTamano() <= 0) {
            throw new IllegalArgumentException("idTamano debe ser > 0");
        }

        ProductoDTO producto = buildProductoDTO(item);
        TamanoDTO tamano = buildTamanoDTO(item);

        TicketItem ticketItem = new TicketItem(producto, tamano, BigDecimal.ZERO);

        applyTipoCafe(ticketItem, item);
        applyExtras(ticketItem, item.getExtras());
        applyPersonalizaciones(ticketItem, item.getPersonalizaciones());
        applyAskMes(ticketItem, item.getAskMes());

        return ticketItem;
    }

    private static ProductoDTO buildProductoDTO(MermaItemRequest item) {
        String nombre = safeText(item.getNombreProductoSnapshot(), "PRODUCTO_MERMA");

        return new ProductoDTO(
                item.getIdProducto(),
                0,                      // idSubcategoria no es necesario para receta
                nombre,
                0,                      // orden no relevante aquí
                true,                   // no bloqueamos extras en el mapper
                true,                   // no bloqueamos personalizaciones en el mapper
                BigDecimal.ZERO,        // IVA no relevante para receta/stock
                false                   // stock por cantidad no relevante aquí
        );
    }

    private static TamanoDTO buildTamanoDTO(MermaItemRequest item) {
        String nombre = safeText(item.getNombreTamanoSnapshot(), "Tamaño");
        return new TamanoDTO(item.getIdTamano(), nombre, buildAbreviatura(nombre));
    }

    private static void applyTipoCafe(TicketItem ticketItem, MermaItemRequest item) {
        if (item.getIdTipoCafeSeleccionado() == null || item.getIdTipoCafeSeleccionado() <= 0) {
            return;
        }

        TipoCafeDTO tipoCafe = new TipoCafeDTO();
        tipoCafe.setIdTipoCafe(item.getIdTipoCafeSeleccionado());
        tipoCafe.setNombre(safeText(item.getNombreTipoCafeSnapshot(), "Café"));
        tipoCafe.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado() != null
                ? item.getIdIngredienteTipoCafeSeleccionado()
                : 0);
        tipoCafe.setSuplementoPrecio(
                item.getSuplementoTipoCafe() != null
                        ? item.getSuplementoTipoCafe()
                        : BigDecimal.ZERO
        );

        ticketItem.setTipoCafe(tipoCafe);
    }

    private static void applyExtras(TicketItem ticketItem, List<MermaExtraRequest> extras) {
        if (extras == null || extras.isEmpty()) {
            return;
        }

        for (MermaExtraRequest extra : extras) {
            if (extra == null || extra.getIdExtra() <= 0) {
                continue;
            }

            ExtraDTO dto = new ExtraDTO(
                    extra.getIdExtra(),
                    safeText(extra.getNombreExtra(), "Extra"),
                    safeText(extra.getTipoExtra(), "OTRO"),
                    extra.getPrecioExtra() != null ? extra.getPrecioExtra() : BigDecimal.ZERO,
                    true
            );

            BigDecimal cantidad = extra.getCantidad() != null ? extra.getCantidad() : BigDecimal.ONE;
            int repeticiones = Math.max(1, cantidad.intValue());

            for (int i = 0; i < repeticiones; i++) {
                ticketItem.addExtra(dto);
            }
        }
    }

    private static void applyPersonalizaciones(TicketItem ticketItem,
                                               List<MermaPersonalizacionRequest> personalizaciones) {
        if (personalizaciones == null || personalizaciones.isEmpty()) {
            return;
        }

        for (MermaPersonalizacionRequest p : personalizaciones) {
            if (p == null || p.getIdPersonalizacion() <= 0) {
                continue;
            }

            PersonalizacionDTO dto = new PersonalizacionDTO(
                    p.getIdPersonalizacion(),
                    safeText(p.getNombrePersonalizacion(), "Personalización"),
                    safeText(p.getTipoPersonalizacion(), "PREP"),
                    p.getPrecioPersonalizacion() != null
                            ? p.getPrecioPersonalizacion()
                            : BigDecimal.ZERO
            );

            ticketItem.togglePersonalizacion(dto);
        }
    }

    private static void applyAskMes(TicketItem ticketItem, List<String> askMes) {
        if (askMes == null || askMes.isEmpty()) {
            return;
        }

        for (String ask : askMes) {
            if (ask == null || ask.isBlank()) {
                continue;
            }
            ticketItem.addAskMe(ask.trim());
        }
    }

    private static String safeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private static String buildAbreviatura(String nombre) {
        String limpio = safeText(nombre, "T");
        if (limpio.length() <= 3) {
            return limpio.toUpperCase();
        }
        return limpio.substring(0, 3).toUpperCase();
    }
}