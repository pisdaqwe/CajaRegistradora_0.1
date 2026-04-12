package service;

import dao.ExtraRecetaReglaDao;
import dao.PersonalizacionRecetaReglaDao;
import dao.RecetaIngredienteDao;
import dtoS.ExtraRecetaReglaDTO;
import dtoS.IngredienteConsumidoDTO;
import dtoS.PersonalizacionRecetaReglaDTO;
import dtoS.RecetaFinalItemDTO;
import dtoS.RecetaIngredienteDTO;
import dtoS.RegistrarVentaExtraRequest;
import dtoS.RegistrarVentaItemRequest;
import dtoS.RegistrarVentaPersonalizacionRequest;
import model.TicketExtra;
import model.TicketItem;
import model.TicketPersonalizacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resuelve la receta final real de un item.
 *
 * Soporta dos entradas:
 * - TicketItem (flujo en memoria/UI)
 * - RegistrarVentaItemRequest (flujo real de persistencia de venta)
 */
public class RecipeResolverService {

    private final RecetaIngredienteDao recetaIngredienteDao;
    private final ExtraRecetaReglaDao extraRecetaReglaDao;
    private final PersonalizacionRecetaReglaDao personalizacionRecetaReglaDao;

    public RecipeResolverService(RecetaIngredienteDao recetaIngredienteDao,
                                 ExtraRecetaReglaDao extraRecetaReglaDao,
                                 PersonalizacionRecetaReglaDao personalizacionRecetaReglaDao) {
        if (recetaIngredienteDao == null) {
            throw new IllegalArgumentException("recetaIngredienteDao no puede ser null");
        }
        if (extraRecetaReglaDao == null) {
            throw new IllegalArgumentException("extraRecetaReglaDao no puede ser null");
        }
        if (personalizacionRecetaReglaDao == null) {
            throw new IllegalArgumentException("personalizacionRecetaReglaDao no puede ser null");
        }

        this.recetaIngredienteDao = recetaIngredienteDao;
        this.extraRecetaReglaDao = extraRecetaReglaDao;
        this.personalizacionRecetaReglaDao = personalizacionRecetaReglaDao;
    }

    // =====================================================
    // 1) RESOLVE DESDE TICKETITEM
    // =====================================================

    public RecetaFinalItemDTO resolve(TicketItem item) {
        if (item == null) {
            throw new IllegalArgumentException("item no puede ser null");
        }
        if (item.getProducto() == null) {
            throw new IllegalArgumentException("item.getProducto() no puede ser null");
        }
        if (item.getTamano() == null) {
            throw new IllegalArgumentException("item.getTamano() no puede ser null");
        }

        int idProducto = item.getProducto().getIdProducto();
        int idTamano = item.getTamano().getIdTamano();

        List<RecetaIngredienteDTO> recetaBase =
                recetaIngredienteDao.findByProductoYTamano(idProducto, idTamano);

        RecetaIngredienteDTO cafeBaseOriginal = findCafeBaseEnReceta(recetaBase);

        List<IngredienteConsumidoDTO> ingredientesFinales =
                buildIngredientesConsumidosDesdeRecetaBase(recetaBase);

        applyTipoCafeSeleccionadoFromTicketItem(item, ingredientesFinales);
        applyExtrasFromTicketItem(item, ingredientesFinales, cafeBaseOriginal);
        applyPersonalizacionesFromTicketItem(item, ingredientesFinales, cafeBaseOriginal);

        RecetaFinalItemDTO result = new RecetaFinalItemDTO();
        result.setIdProducto(item.getProducto().getIdProducto());
        result.setNombreProducto(item.getProducto().getNombre());
        result.setIdTamano(item.getTamano().getIdTamano());
        result.setNombreTamano(item.getTamano().getNombre());
        result.setIngredientesConsumidos(ingredientesFinales);

        return result;
    }

    // =====================================================
    // 2) RESOLVE DESDE REGISTRARVENTAITEMREQUEST
    // =====================================================

    public RecetaFinalItemDTO resolve(RegistrarVentaItemRequest item) {
        if (item == null) {
            throw new IllegalArgumentException("item no puede ser null");
        }
        if (item.getIdProducto() <= 0) {
            throw new IllegalArgumentException("idProducto inválido");
        }
        if (item.getIdTamano() <= 0) {
            throw new IllegalArgumentException("idTamano inválido");
        }

        int idProducto = item.getIdProducto();
        int idTamano = item.getIdTamano();

        List<RecetaIngredienteDTO> recetaBase =
                recetaIngredienteDao.findByProductoYTamano(idProducto, idTamano);

        RecetaIngredienteDTO cafeBaseOriginal = findCafeBaseEnReceta(recetaBase);

        List<IngredienteConsumidoDTO> ingredientesFinales =
                buildIngredientesConsumidosDesdeRecetaBase(recetaBase);

        applyTipoCafeSeleccionadoFromVentaItem(item, ingredientesFinales);
        applyExtrasFromVentaItem(item, ingredientesFinales, cafeBaseOriginal);
        applyPersonalizacionesFromVentaItem(item, ingredientesFinales, cafeBaseOriginal);

        RecetaFinalItemDTO result = new RecetaFinalItemDTO();
        result.setIdProducto(item.getIdProducto());
        result.setNombreProducto(item.getNombreProducto());
        result.setIdTamano(item.getIdTamano());
        result.setNombreTamano(item.getNombreTamano());
        result.setIngredientesConsumidos(ingredientesFinales);

        return result;
    }

    // =====================================================
    // BASE
    // =====================================================

    private List<IngredienteConsumidoDTO> buildIngredientesConsumidosDesdeRecetaBase(
            List<RecetaIngredienteDTO> recetaBase
    ) {
        List<IngredienteConsumidoDTO> result = new ArrayList<>();

        if (recetaBase == null || recetaBase.isEmpty()) {
            return result;
        }

        for (RecetaIngredienteDTO linea : recetaBase) {
            IngredienteConsumidoDTO dto = new IngredienteConsumidoDTO();
            dto.setIdIngrediente(linea.getIdIngrediente());
            dto.setNombreIngrediente(linea.getNombreIngrediente());
            dto.setCantidad(linea.getCantidad());
            dto.setIdUnidad(linea.getIdUnidad());
            dto.setNombreUnidad(linea.getNombreUnidad());
            result.add(dto);
        }

        return result;
    }

    private RecetaIngredienteDTO findCafeBaseEnReceta(List<RecetaIngredienteDTO> recetaBase) {
        if (recetaBase == null || recetaBase.isEmpty()) {
            return null;
        }

        for (RecetaIngredienteDTO linea : recetaBase) {
            String nombre = linea.getNombreIngrediente() != null
                    ? linea.getNombreIngrediente().toLowerCase()
                    : "";

            if (nombre.contains("espresso") || nombre.contains("cafe")) {
                return linea;
            }
        }

        return null;
    }

    // =====================================================
    // CAFÉ
    // =====================================================

    private void applyTipoCafeSeleccionadoFromTicketItem(TicketItem item,
                                                         List<IngredienteConsumidoDTO> ingredientesFinales) {
        if (item.getIdIngredienteTipoCafeSeleccionado() == null) {
            return;
        }

        for (IngredienteConsumidoDTO linea : ingredientesFinales) {
            String nombre = linea.getNombreIngrediente() != null
                    ? linea.getNombreIngrediente().toLowerCase()
                    : "";

            if (nombre.contains("espresso") || nombre.contains("cafe")) {
                linea.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());

                if (item.getNombreTipoCafeSeleccionado() != null
                        && !item.getNombreTipoCafeSeleccionado().isBlank()) {
                    linea.setNombreIngrediente(item.getNombreTipoCafeSeleccionado().trim());
                }
                return;
            }
        }
    }

    private void applyTipoCafeSeleccionadoFromVentaItem(RegistrarVentaItemRequest item,
                                                        List<IngredienteConsumidoDTO> ingredientesFinales) {
        if (item.getIdIngredienteTipoCafeSeleccionado() == null) {
            return;
        }

        for (IngredienteConsumidoDTO linea : ingredientesFinales) {
            String nombre = linea.getNombreIngrediente() != null
                    ? linea.getNombreIngrediente().toLowerCase()
                    : "";

            if (nombre.contains("espresso") || nombre.contains("cafe")) {
                linea.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());

                if (item.getNombreTipoCafeSnapshot() != null
                        && !item.getNombreTipoCafeSnapshot().isBlank()) {
                    linea.setNombreIngrediente(item.getNombreTipoCafeSnapshot().trim());
                }
                return;
            }
        }
    }

    // =====================================================
    // EXTRAS DESDE TICKETITEM
    // =====================================================

    private void applyExtrasFromTicketItem(TicketItem item,
                                           List<IngredienteConsumidoDTO> ingredientesFinales,
                                           RecetaIngredienteDTO cafeBaseOriginal) {
        if (item.getExtras() == null || item.getExtras().isEmpty()) {
            return;
        }

        int idProducto = item.getProducto().getIdProducto();
        int idTamano = item.getTamano().getIdTamano();

        for (TicketExtra extra : item.getExtras()) {
            if (extra == null) {
                continue;
            }

            List<ExtraRecetaReglaDTO> reglas =
                    extraRecetaReglaDao.findByProductoTamanoYExtra(
                            idProducto,
                            idTamano,
                            extra.getIdExtra()
                    );

            for (ExtraRecetaReglaDTO regla : reglas) {
                applyExtraRuleFromTicketItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            }
        }
    }

    // =====================================================
    // EXTRAS DESDE VENTA ITEM
    // =====================================================

    private void applyExtrasFromVentaItem(RegistrarVentaItemRequest item,
                                          List<IngredienteConsumidoDTO> ingredientesFinales,
                                          RecetaIngredienteDTO cafeBaseOriginal) {
        if (item.getExtras() == null || item.getExtras().isEmpty()) {
            return;
        }

        int idProducto = item.getIdProducto();
        int idTamano = item.getIdTamano();

        for (RegistrarVentaExtraRequest extra : item.getExtras()) {
            if (extra == null) {
                continue;
            }

            List<ExtraRecetaReglaDTO> reglas =
                    extraRecetaReglaDao.findByProductoTamanoYExtra(
                            idProducto,
                            idTamano,
                            extra.getIdExtra()
                    );

            for (ExtraRecetaReglaDTO regla : reglas) {
                applyExtraRuleFromVentaItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            }
        }
    }

    // =====================================================
    // PERSONALIZACIONES DESDE TICKETITEM
    // =====================================================

    private void applyPersonalizacionesFromTicketItem(TicketItem item,
                                                      List<IngredienteConsumidoDTO> ingredientesFinales,
                                                      RecetaIngredienteDTO cafeBaseOriginal) {
        if (item.getPersonalizaciones() == null || item.getPersonalizaciones().isEmpty()) {
            return;
        }

        int idProducto = item.getProducto().getIdProducto();
        int idTamano = item.getTamano().getIdTamano();

        for (Map.Entry<Integer, TicketPersonalizacion> entry : item.getPersonalizaciones().entrySet()) {
            TicketPersonalizacion p = entry.getValue();
            if (p == null) {
                continue;
            }

            List<PersonalizacionRecetaReglaDTO> reglas =
                    personalizacionRecetaReglaDao.findByProductoTamanoYPersonalizacion(
                            idProducto,
                            idTamano,
                            p.getIdPersonalizacion()
                    );

            for (PersonalizacionRecetaReglaDTO regla : reglas) {
                applyPersonalizacionRuleFromTicketItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            }
        }
    }

    // =====================================================
    // PERSONALIZACIONES DESDE VENTA ITEM
    // =====================================================

    private void applyPersonalizacionesFromVentaItem(RegistrarVentaItemRequest item,
                                                     List<IngredienteConsumidoDTO> ingredientesFinales,
                                                     RecetaIngredienteDTO cafeBaseOriginal) {
        if (item.getPersonalizaciones() == null || item.getPersonalizaciones().isEmpty()) {
            return;
        }

        int idProducto = item.getIdProducto();
        int idTamano = item.getIdTamano();

        for (RegistrarVentaPersonalizacionRequest p : item.getPersonalizaciones()) {
            if (p == null) {
                continue;
            }

            List<PersonalizacionRecetaReglaDTO> reglas =
                    personalizacionRecetaReglaDao.findByProductoTamanoYPersonalizacion(
                            idProducto,
                            idTamano,
                            p.getIdPersonalizacion()
                    );

            for (PersonalizacionRecetaReglaDTO regla : reglas) {
                applyPersonalizacionRuleFromVentaItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            }
        }
    }

    // =====================================================
    // EXTRAS - APLICACIÓN DESDE TICKETITEM
    // =====================================================

    private void applyExtraRuleFromTicketItem(ExtraRecetaReglaDTO regla,
                                              TicketItem item,
                                              List<IngredienteConsumidoDTO> ingredientesFinales,
                                              RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla == null || regla.getTipoRegla() == null) {
            return;
        }

        String tipo = regla.getTipoRegla().trim().toUpperCase();

        switch (tipo) {
            case "ADD" -> applyAddExtraFromTicketItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REPLACE" -> applyReplaceExtraFromTicketItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REMOVE" -> applyRemoveExtra(regla, ingredientesFinales);
            case "NO_STOCK_EFFECT" -> { }
            default -> { }
        }
    }

    private void applyAddExtraFromTicketItem(ExtraRecetaReglaDTO regla,
                                             TicketItem item,
                                             List<IngredienteConsumidoDTO> ingredientesFinales,
                                             RecetaIngredienteDTO cafeBaseOriginal) {
        IngredienteConsumidoDTO nuevo = buildIngredienteDesdeFuenteExtraForTicketItem(regla, item, cafeBaseOriginal);
        if (nuevo == null) {
            return;
        }

        BigDecimal cantidadFinal = resolveCantidadParaAddExtra(regla, ingredientesFinales);
        nuevo.setCantidad(cantidadFinal);

        if (regla.getIdUnidad() != null) {
            nuevo.setIdUnidad(regla.getIdUnidad());
            nuevo.setNombreUnidad(regla.getNombreUnidad());
        }

        ingredientesFinales.add(nuevo);
    }

    private void applyReplaceExtraFromTicketItem(ExtraRecetaReglaDTO regla,
                                                 TicketItem item,
                                                 List<IngredienteConsumidoDTO> ingredientesFinales,
                                                 RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla.getIdIngredienteOrigen() == null) {
            return;
        }

        for (IngredienteConsumidoDTO linea : ingredientesFinales) {
            if (linea.getIdIngrediente() == regla.getIdIngredienteOrigen()) {

                IngredienteConsumidoDTO nuevo = buildIngredienteDesdeFuenteExtraForTicketItem(regla, item, cafeBaseOriginal);
                if (nuevo == null) {
                    return;
                }

                linea.setIdIngrediente(nuevo.getIdIngrediente());
                linea.setNombreIngrediente(nuevo.getNombreIngrediente());

                if (!regla.isHeredaCantidadOrigen() && regla.getCantidad() != null) {
                    linea.setCantidad(regla.getCantidad());
                }

                if (!regla.isHeredaCantidadOrigen() && regla.getIdUnidad() != null) {
                    linea.setIdUnidad(regla.getIdUnidad());
                    linea.setNombreUnidad(regla.getNombreUnidad());
                }

                return;
            }
        }
    }

    private IngredienteConsumidoDTO buildIngredienteDesdeFuenteExtraForTicketItem(ExtraRecetaReglaDTO regla,
                                                                                  TicketItem item,
                                                                                  RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla.getFuenteIngrediente() == null) {
            return null;
        }

        String fuente = regla.getFuenteIngrediente().trim().toUpperCase();
        IngredienteConsumidoDTO dto = new IngredienteConsumidoDTO();

        switch (fuente) {
            case "FIJO" -> {
                if (regla.getIdIngredienteDestino() == null) return null;
                dto.setIdIngrediente(regla.getIdIngredienteDestino());
                dto.setNombreIngrediente(regla.getNombreIngredienteDestino());
            }
            case "CAFE_SELECCIONADO" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() == null) return null;
                dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                dto.setNombreIngrediente(item.getNombreTipoCafeSeleccionado());
            }
            case "CAFE_BASE" -> {
                if (cafeBaseOriginal == null) return null;
                dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
            }
            case "CAFE_SELECCIONADO_O_BASE" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() != null) {
                    dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                    dto.setNombreIngrediente(item.getNombreTipoCafeSeleccionado());
                } else if (cafeBaseOriginal != null) {
                    dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                    dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
                } else {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }

        return dto;
    }

    // =====================================================
    // EXTRAS - APLICACIÓN DESDE VENTA ITEM
    // =====================================================

    private void applyExtraRuleFromVentaItem(ExtraRecetaReglaDTO regla,
                                             RegistrarVentaItemRequest item,
                                             List<IngredienteConsumidoDTO> ingredientesFinales,
                                             RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla == null || regla.getTipoRegla() == null) {
            return;
        }

        String tipo = regla.getTipoRegla().trim().toUpperCase();

        switch (tipo) {
            case "ADD" -> applyAddExtraFromVentaItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REPLACE" -> applyReplaceExtraFromVentaItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REMOVE" -> applyRemoveExtra(regla, ingredientesFinales);
            case "NO_STOCK_EFFECT" -> { }
            default -> { }
        }
    }

    private void applyAddExtraFromVentaItem(ExtraRecetaReglaDTO regla,
                                            RegistrarVentaItemRequest item,
                                            List<IngredienteConsumidoDTO> ingredientesFinales,
                                            RecetaIngredienteDTO cafeBaseOriginal) {
        IngredienteConsumidoDTO nuevo = buildIngredienteDesdeFuenteExtraForVentaItem(regla, item, cafeBaseOriginal);
        if (nuevo == null) {
            return;
        }

        BigDecimal cantidadFinal = resolveCantidadParaAddExtra(regla, ingredientesFinales);
        nuevo.setCantidad(cantidadFinal);

        if (regla.getIdUnidad() != null) {
            nuevo.setIdUnidad(regla.getIdUnidad());
            nuevo.setNombreUnidad(regla.getNombreUnidad());
        }

        ingredientesFinales.add(nuevo);
    }

    private void applyReplaceExtraFromVentaItem(ExtraRecetaReglaDTO regla,
                                                RegistrarVentaItemRequest item,
                                                List<IngredienteConsumidoDTO> ingredientesFinales,
                                                RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla.getIdIngredienteOrigen() == null) {
            return;
        }

        for (IngredienteConsumidoDTO linea : ingredientesFinales) {
            if (linea.getIdIngrediente() == regla.getIdIngredienteOrigen()) {

                IngredienteConsumidoDTO nuevo = buildIngredienteDesdeFuenteExtraForVentaItem(regla, item, cafeBaseOriginal);
                if (nuevo == null) {
                    return;
                }

                linea.setIdIngrediente(nuevo.getIdIngrediente());
                linea.setNombreIngrediente(nuevo.getNombreIngrediente());

                if (!regla.isHeredaCantidadOrigen() && regla.getCantidad() != null) {
                    linea.setCantidad(regla.getCantidad());
                }

                if (!regla.isHeredaCantidadOrigen() && regla.getIdUnidad() != null) {
                    linea.setIdUnidad(regla.getIdUnidad());
                    linea.setNombreUnidad(regla.getNombreUnidad());
                }

                return;
            }
        }
    }

    private IngredienteConsumidoDTO buildIngredienteDesdeFuenteExtraForVentaItem(ExtraRecetaReglaDTO regla,
                                                                                 RegistrarVentaItemRequest item,
                                                                                 RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla.getFuenteIngrediente() == null) {
            return null;
        }

        String fuente = regla.getFuenteIngrediente().trim().toUpperCase();
        IngredienteConsumidoDTO dto = new IngredienteConsumidoDTO();

        switch (fuente) {
            case "FIJO" -> {
                if (regla.getIdIngredienteDestino() == null) return null;
                dto.setIdIngrediente(regla.getIdIngredienteDestino());
                dto.setNombreIngrediente(regla.getNombreIngredienteDestino());
            }
            case "CAFE_SELECCIONADO" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() == null) return null;
                dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                dto.setNombreIngrediente(item.getNombreTipoCafeSnapshot());
            }
            case "CAFE_BASE" -> {
                if (cafeBaseOriginal == null) return null;
                dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
            }
            case "CAFE_SELECCIONADO_O_BASE" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() != null) {
                    dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                    dto.setNombreIngrediente(item.getNombreTipoCafeSnapshot());
                } else if (cafeBaseOriginal != null) {
                    dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                    dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
                } else {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }

        return dto;
    }

    private void applyRemoveExtra(ExtraRecetaReglaDTO regla,
                                  List<IngredienteConsumidoDTO> ingredientesFinales) {
        if (regla.getIdIngredienteOrigen() == null) {
            return;
        }

        ingredientesFinales.removeIf(i -> i.getIdIngrediente() == regla.getIdIngredienteOrigen());
    }

    private BigDecimal resolveCantidadParaAddExtra(ExtraRecetaReglaDTO regla,
                                                   List<IngredienteConsumidoDTO> ingredientesFinales) {
        if (regla.isHeredaCantidadOrigen() && regla.getIdIngredienteOrigen() != null) {
            for (IngredienteConsumidoDTO linea : ingredientesFinales) {
                if (linea.getIdIngrediente() == regla.getIdIngredienteOrigen()) {
                    return linea.getCantidad();
                }
            }
        }

        return regla.getCantidad() != null ? regla.getCantidad() : BigDecimal.ZERO;
    }

    // =====================================================
    // PERSONALIZACIONES - TICKETITEM
    // =====================================================

    private void applyPersonalizacionRuleFromTicketItem(PersonalizacionRecetaReglaDTO regla,
                                                        TicketItem item,
                                                        List<IngredienteConsumidoDTO> ingredientesFinales,
                                                        RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla == null || regla.getTipoRegla() == null) {
            return;
        }

        String tipo = regla.getTipoRegla().trim().toUpperCase();

        switch (tipo) {
            case "ADD" -> applyAddPersonalizacionFromTicketItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REPLACE" -> applyReplacePersonalizacionFromTicketItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REMOVE" -> applyRemovePersonalizacion(regla, ingredientesFinales);
            case "NO_STOCK_EFFECT" -> { }
            default -> { }
        }
    }

    private void applyAddPersonalizacionFromTicketItem(PersonalizacionRecetaReglaDTO regla,
                                                       TicketItem item,
                                                       List<IngredienteConsumidoDTO> ingredientesFinales,
                                                       RecetaIngredienteDTO cafeBaseOriginal) {
        IngredienteConsumidoDTO nuevo =
                buildIngredienteDesdeFuentePersonalizacionForTicketItem(regla, item, cafeBaseOriginal);
        if (nuevo == null) {
            return;
        }

        BigDecimal cantidadFinal = resolveCantidadParaAddPersonalizacion(regla, ingredientesFinales);
        nuevo.setCantidad(cantidadFinal);

        if (regla.getIdUnidad() != null) {
            nuevo.setIdUnidad(regla.getIdUnidad());
            nuevo.setNombreUnidad(regla.getNombreUnidad());
        }

        ingredientesFinales.add(nuevo);
    }

    private void applyReplacePersonalizacionFromTicketItem(PersonalizacionRecetaReglaDTO regla,
                                                           TicketItem item,
                                                           List<IngredienteConsumidoDTO> ingredientesFinales,
                                                           RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla.getIdIngredienteOrigen() == null) {
            return;
        }

        for (IngredienteConsumidoDTO linea : ingredientesFinales) {
            if (linea.getIdIngrediente() == regla.getIdIngredienteOrigen()) {

                IngredienteConsumidoDTO nuevo =
                        buildIngredienteDesdeFuentePersonalizacionForTicketItem(regla, item, cafeBaseOriginal);
                if (nuevo == null) {
                    return;
                }

                linea.setIdIngrediente(nuevo.getIdIngrediente());
                linea.setNombreIngrediente(nuevo.getNombreIngrediente());

                if (!regla.isHeredaCantidadOrigen() && regla.getCantidad() != null) {
                    linea.setCantidad(regla.getCantidad());
                }

                if (!regla.isHeredaCantidadOrigen() && regla.getIdUnidad() != null) {
                    linea.setIdUnidad(regla.getIdUnidad());
                    linea.setNombreUnidad(regla.getNombreUnidad());
                }

                return;
            }
        }
    }

    private IngredienteConsumidoDTO buildIngredienteDesdeFuentePersonalizacionForTicketItem(
            PersonalizacionRecetaReglaDTO regla,
            TicketItem item,
            RecetaIngredienteDTO cafeBaseOriginal
    ) {
        if (regla.getFuenteIngrediente() == null) {
            return null;
        }

        String fuente = regla.getFuenteIngrediente().trim().toUpperCase();
        IngredienteConsumidoDTO dto = new IngredienteConsumidoDTO();

        switch (fuente) {
            case "FIJO" -> {
                if (regla.getIdIngredienteDestino() == null) return null;
                dto.setIdIngrediente(regla.getIdIngredienteDestino());
                dto.setNombreIngrediente(regla.getNombreIngredienteDestino());
            }
            case "CAFE_SELECCIONADO" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() == null) return null;
                dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                dto.setNombreIngrediente(item.getNombreTipoCafeSeleccionado());
            }
            case "CAFE_BASE" -> {
                if (cafeBaseOriginal == null) return null;
                dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
            }
            case "CAFE_SELECCIONADO_O_BASE" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() != null) {
                    dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                    dto.setNombreIngrediente(item.getNombreTipoCafeSeleccionado());
                } else if (cafeBaseOriginal != null) {
                    dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                    dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
                } else {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }

        return dto;
    }

    // =====================================================
    // PERSONALIZACIONES - VENTA ITEM
    // =====================================================

    private void applyPersonalizacionRuleFromVentaItem(PersonalizacionRecetaReglaDTO regla,
                                                       RegistrarVentaItemRequest item,
                                                       List<IngredienteConsumidoDTO> ingredientesFinales,
                                                       RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla == null || regla.getTipoRegla() == null) {
            return;
        }

        String tipo = regla.getTipoRegla().trim().toUpperCase();

        switch (tipo) {
            case "ADD" -> applyAddPersonalizacionFromVentaItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REPLACE" -> applyReplacePersonalizacionFromVentaItem(regla, item, ingredientesFinales, cafeBaseOriginal);
            case "REMOVE" -> applyRemovePersonalizacion(regla, ingredientesFinales);
            case "NO_STOCK_EFFECT" -> { }
            default -> { }
        }
    }

    private void applyAddPersonalizacionFromVentaItem(PersonalizacionRecetaReglaDTO regla,
                                                      RegistrarVentaItemRequest item,
                                                      List<IngredienteConsumidoDTO> ingredientesFinales,
                                                      RecetaIngredienteDTO cafeBaseOriginal) {
        IngredienteConsumidoDTO nuevo =
                buildIngredienteDesdeFuentePersonalizacionForVentaItem(regla, item, cafeBaseOriginal);
        if (nuevo == null) {
            return;
        }

        BigDecimal cantidadFinal = resolveCantidadParaAddPersonalizacion(regla, ingredientesFinales);
        nuevo.setCantidad(cantidadFinal);

        if (regla.getIdUnidad() != null) {
            nuevo.setIdUnidad(regla.getIdUnidad());
            nuevo.setNombreUnidad(regla.getNombreUnidad());
        }

        ingredientesFinales.add(nuevo);
    }

    private void applyReplacePersonalizacionFromVentaItem(PersonalizacionRecetaReglaDTO regla,
                                                          RegistrarVentaItemRequest item,
                                                          List<IngredienteConsumidoDTO> ingredientesFinales,
                                                          RecetaIngredienteDTO cafeBaseOriginal) {
        if (regla.getIdIngredienteOrigen() == null) {
            return;
        }

        for (IngredienteConsumidoDTO linea : ingredientesFinales) {
            if (linea.getIdIngrediente() == regla.getIdIngredienteOrigen()) {

                IngredienteConsumidoDTO nuevo =
                        buildIngredienteDesdeFuentePersonalizacionForVentaItem(regla, item, cafeBaseOriginal);
                if (nuevo == null) {
                    return;
                }

                linea.setIdIngrediente(nuevo.getIdIngrediente());
                linea.setNombreIngrediente(nuevo.getNombreIngrediente());

                if (!regla.isHeredaCantidadOrigen() && regla.getCantidad() != null) {
                    linea.setCantidad(regla.getCantidad());
                }

                if (!regla.isHeredaCantidadOrigen() && regla.getIdUnidad() != null) {
                    linea.setIdUnidad(regla.getIdUnidad());
                    linea.setNombreUnidad(regla.getNombreUnidad());
                }

                return;
            }
        }
    }

    private IngredienteConsumidoDTO buildIngredienteDesdeFuentePersonalizacionForVentaItem(
            PersonalizacionRecetaReglaDTO regla,
            RegistrarVentaItemRequest item,
            RecetaIngredienteDTO cafeBaseOriginal
    ) {
        if (regla.getFuenteIngrediente() == null) {
            return null;
        }

        String fuente = regla.getFuenteIngrediente().trim().toUpperCase();
        IngredienteConsumidoDTO dto = new IngredienteConsumidoDTO();

        switch (fuente) {
            case "FIJO" -> {
                if (regla.getIdIngredienteDestino() == null) return null;
                dto.setIdIngrediente(regla.getIdIngredienteDestino());
                dto.setNombreIngrediente(regla.getNombreIngredienteDestino());
            }
            case "CAFE_SELECCIONADO" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() == null) return null;
                dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                dto.setNombreIngrediente(item.getNombreTipoCafeSnapshot());
            }
            case "CAFE_BASE" -> {
                if (cafeBaseOriginal == null) return null;
                dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
            }
            case "CAFE_SELECCIONADO_O_BASE" -> {
                if (item.getIdIngredienteTipoCafeSeleccionado() != null) {
                    dto.setIdIngrediente(item.getIdIngredienteTipoCafeSeleccionado());
                    dto.setNombreIngrediente(item.getNombreTipoCafeSnapshot());
                } else if (cafeBaseOriginal != null) {
                    dto.setIdIngrediente(cafeBaseOriginal.getIdIngrediente());
                    dto.setNombreIngrediente(cafeBaseOriginal.getNombreIngrediente());
                } else {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }

        return dto;
    }

    private void applyRemovePersonalizacion(PersonalizacionRecetaReglaDTO regla,
                                            List<IngredienteConsumidoDTO> ingredientesFinales) {
        if (regla.getIdIngredienteOrigen() == null) {
            return;
        }

        ingredientesFinales.removeIf(i -> i.getIdIngrediente() == regla.getIdIngredienteOrigen());
    }

    private BigDecimal resolveCantidadParaAddPersonalizacion(PersonalizacionRecetaReglaDTO regla,
                                                             List<IngredienteConsumidoDTO> ingredientesFinales) {
        if (regla.isHeredaCantidadOrigen() && regla.getIdIngredienteOrigen() != null) {
            for (IngredienteConsumidoDTO linea : ingredientesFinales) {
                if (linea.getIdIngrediente() == regla.getIdIngredienteOrigen()) {
                    return linea.getCantidad();
                }
            }
        }

        return regla.getCantidad() != null ? regla.getCantidad() : BigDecimal.ZERO;
    }
}