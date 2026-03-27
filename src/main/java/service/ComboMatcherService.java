package service;

import dtoS.ComboDTO;
import dtoS.ComboItemDTO;
import model.ComboDefinition;
import model.TicketCombo;
import model.TicketItem;
import model.TicketSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio encargado de detectar automáticamente qué combos del catálogo
 * pueden aplicarse al TicketSession actual.
 *
 * Regla importante:
 * - El matching del combo se hace por PRODUCTO BASE
 * - Los extras / leche / personalizaciones NO rompen el combo
 * - El precio original del combo se calcula solo con la BASE de los productos
 */
public final class ComboMatcherService {

    /**
     * Detecta todos los combos aplicables sobre el ticket actual.
     *
     * Flujo:
     * 1. Ordena combos por prioridad DESC
     * 2. Recorre cada combo
     * 3. Comprueba si está disponible ahora (fecha/hora/días)
     * 4. Intenta construirlo tantas veces como sea posible
     * 5. Reserva los items usados para que no entren en otro combo
     */
    public List<TicketCombo> detectAppliedCombos(List<ComboDefinition> comboDefinitions,
                                                 TicketSession ticketSession,
                                                 LocalDateTime now) {
        List<TicketCombo> result = new ArrayList<>();

        // Si no hay combos definidos o el ticket está vacío, no hay nada que hacer
        if (comboDefinitions == null || comboDefinitions.isEmpty()) {
            return result;
        }

        if (ticketSession == null || ticketSession.getItems().isEmpty()) {
            return result;
        }

        // Copiamos y ordenamos los combos:
        // primero los de mayor prioridad
        List<ComboDefinition> ordered = new ArrayList<>(comboDefinitions);
        ordered.sort(
                Comparator.comparingInt((ComboDefinition d) -> d.getCombo().getPrioridad()).reversed()
                        .thenComparingInt(ComboDefinition::getIdCombo)
        );

        // Guarda los índices de items del ticket que ya han sido usados por un combo
        Set<Integer> reservedIndexes = new HashSet<>();

        // Intentamos aplicar cada combo
        for (ComboDefinition definition : ordered) {

            // Si el combo no está disponible por fecha/hora/día, lo saltamos
            if (!isComboDisponibleAhora(definition.getCombo(), now)) {
                continue;
            }

            // Intentamos aplicar el mismo combo tantas veces como sea posible
            while (true) {
                List<Integer> matchedIndexes = tryMatchOnce(definition, ticketSession, reservedIndexes);

                // Si no encontramos otra combinación válida, pasamos al siguiente combo
                if (matchedIndexes == null || matchedIndexes.isEmpty()) {
                    break;
                }

                // Precio original = suma de bases de los items que forman el combo
                BigDecimal precioOriginal = calcularPrecioOriginalBase(ticketSession, matchedIndexes);

                // Precio final = depende del tipo de combo
                BigDecimal precioFinal = calcularPrecioFinal(definition.getCombo(), precioOriginal);

                // Ahorro = diferencia entre precio original y precio final
                BigDecimal ahorro = precioOriginal.subtract(precioFinal);
                if (ahorro.compareTo(BigDecimal.ZERO) < 0) {
                    ahorro = BigDecimal.ZERO;
                }

                // Creamos el combo aplicado en memoria
                TicketCombo applied = new TicketCombo(
                        definition,
                        matchedIndexes,
                        precioOriginal,
                        precioFinal,
                        ahorro
                );

                result.add(applied);

                // Reservamos esos índices para que no entren en otro combo
                reservedIndexes.addAll(matchedIndexes);
            }
        }

        return result;
    }

    /**
     * Intenta construir UNA única aparición del combo.
     *
     * Ejemplo:
     * Si el combo pide:
     * - Latte x1
     * - Croissant x1
     * - Cookie x1
     *
     * intenta localizar esos productos en el ticket.
     *
     * Si no puede encontrar todos, devuelve null.
     */
    private List<Integer> tryMatchOnce(ComboDefinition definition,
                                       TicketSession ticketSession,
                                       Set<Integer> reservedIndexes) {
        List<Integer> matchedIndexes = new ArrayList<>();

        // Ordenamos los requisitos del combo por el orden definido en BD
        List<ComboItemDTO> requiredItems = new ArrayList<>(definition.getItems());
        requiredItems.sort(Comparator.comparingInt(ComboItemDTO::getOrden));

        // Para cada producto requerido por el combo...
        for (ComboItemDTO required : requiredItems) {
            int cantidadNecesaria = required.getCantidad();

            // Si el combo pide cantidad > 1, repetimos la búsqueda tantas veces como haga falta
            for (int i = 0; i < cantidadNecesaria; i++) {
                Integer foundIndex = findNextMatchingItemIndex(
                        ticketSession,
                        required.getIdProducto(),
                        reservedIndexes,
                        matchedIndexes
                );

                // Si falta alguno de los productos requeridos, el combo no puede aplicarse
                if (foundIndex == null) {
                    return null;
                }

                matchedIndexes.add(foundIndex);
            }
        }

        return matchedIndexes;
    }

    /**
     * Busca el siguiente item del ticket que cumpla:
     * - mismo idProducto
     * - no esté ya reservado por otro combo
     * - no haya sido ya usado en esta construcción temporal
     */
    private Integer findNextMatchingItemIndex(TicketSession ticketSession,
                                              int idProducto,
                                              Set<Integer> reservedIndexes,
                                              List<Integer> tempMatchedIndexes) {
        List<TicketItem> items = ticketSession.getItems();

        for (int i = 0; i < items.size(); i++) {
            if (reservedIndexes.contains(i)) {
                continue;
            }

            if (tempMatchedIndexes.contains(i)) {
                continue;
            }

            TicketItem item = items.get(i);

            // Matching SOLO por producto base
            if (item.getProducto().getIdProducto() == idProducto) {
                return i;
            }
        }

        return null;
    }

    /**
     * Calcula el precio original del combo usando SOLO la base de los productos.
     *
     * Importante:
     * - NO suma extras
     * - NO suma personalizaciones
     * - NO suma leche aparte
     */
    private BigDecimal calcularPrecioOriginalBase(TicketSession ticketSession, List<Integer> matchedIndexes) {
        BigDecimal total = BigDecimal.ZERO;

        for (Integer index : matchedIndexes) {
            TicketItem item = ticketSession.getItems().get(index);
            total = total.add(item.getPrecioBase());
        }

        return total;
    }

    /**
     * Calcula el precio final del combo según su tipo.
     *
     * Tipos soportados:
     * - PRECIO_CERRADO
     * - DESCUENTO_FIJO
     * - DESCUENTO_PORCENTAJE
     */
    private BigDecimal calcularPrecioFinal(ComboDTO combo, BigDecimal precioOriginal) {
        if (combo.getTipo() == null) {
            return precioOriginal;
        }

        BigDecimal valor = combo.getValor() != null ? combo.getValor() : BigDecimal.ZERO;

        return switch (combo.getTipo()) {

            // Ejemplo: combo vale siempre 7.50
            case PRECIO_CERRADO -> valor.max(BigDecimal.ZERO);

            // Ejemplo: descuento fijo de 2.00€
            case DESCUENTO_FIJO -> {
                BigDecimal total = precioOriginal.subtract(valor);
                yield total.max(BigDecimal.ZERO);
            }

            // Ejemplo: descuento del 20%
            case DESCUENTO_PORCENTAJE -> {
                BigDecimal porcentajeRestante = BigDecimal.valueOf(100).subtract(valor);
                BigDecimal total = precioOriginal.multiply(porcentajeRestante)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                yield total.max(BigDecimal.ZERO);
            }
        };
    }

    /**
     * Comprueba si el combo puede aplicarse en este momento:
     * - debe estar activo
     * - debe cumplir fechas si las tiene
     * - debe cumplir horario si lo tiene
     * - debe cumplir días de semana si los tiene
     */
    private boolean isComboDisponibleAhora(ComboDTO combo, LocalDateTime now) {
        if (combo == null || !combo.isActivo()) {
            return false;
        }

        // Restricción por fecha
        if (combo.tieneRestriccionFecha()) {
            if (combo.getFechaInicio() != null && now.toLocalDate().isBefore(combo.getFechaInicio())) {
                return false;
            }
            if (combo.getFechaFin() != null && now.toLocalDate().isAfter(combo.getFechaFin())) {
                return false;
            }
        }

        // Restricción por hora
        if (combo.tieneRestriccionHora()) {
            if (combo.getHoraInicio() != null && now.toLocalTime().isBefore(combo.getHoraInicio())) {
                return false;
            }
            if (combo.getHoraFin() != null && now.toLocalTime().isAfter(combo.getHoraFin())) {
                return false;
            }
        }

        // Restricción por días de semana
        if (combo.tieneRestriccionDias()) {
            EnumSet<ComboDTO.DiaSemanaCombo> dias = combo.getDiasSemana();

            if (dias == null || dias.isEmpty()) {
                return true;
            }

            ComboDTO.DiaSemanaCombo hoy = toDiaSemanaCombo(now.getDayOfWeek());
            return dias.contains(hoy);
        }

        return true;
    }

    /**
     * Convierte el DayOfWeek de Java al enum de tu DTO.
     */
    private ComboDTO.DiaSemanaCombo toDiaSemanaCombo(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> ComboDTO.DiaSemanaCombo.LUN;
            case TUESDAY -> ComboDTO.DiaSemanaCombo.MAR;
            case WEDNESDAY -> ComboDTO.DiaSemanaCombo.MIE;
            case THURSDAY -> ComboDTO.DiaSemanaCombo.JUE;
            case FRIDAY -> ComboDTO.DiaSemanaCombo.VIE;
            case SATURDAY -> ComboDTO.DiaSemanaCombo.SAB;
            case SUNDAY -> ComboDTO.DiaSemanaCombo.DOM;
        };
    }
}
