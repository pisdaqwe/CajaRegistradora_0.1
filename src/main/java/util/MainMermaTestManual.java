package util;

import config.ConfigLoader;
import config.DbPool;
import dao.ExtraRecetaReglaDao;
import dao.MermaDao;
import dao.MermaItemDao;
import dao.MovimientoStockDao;
import dao.PersonalizacionRecetaReglaDao;
import dao.RecetaIngredienteDao;
import dao.StockIngredienteDao;
import dtoS.MermaExtraRequest;
import dtoS.MermaItemRequest;
import dtoS.MermaPersonalizacionRequest;
import dtoS.MermaRequest;
import dtoS.MermaResultDTO;
import facade.MermaFacade;
import service.MermaService;
import service.MovimientoStockService;
import service.RecipeResolverService;
import service.StockIngredienteService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test manual del corazón de merma.
 *
 * OBJETIVO:
 * - probar el flujo completo sin UI
 * - validar inserción en merma
 * - validar inserción en merma_item
 * - validar descuento de stock_ingrediente
 * - validar inserción de movimiento_stock
 *
 * IMPORTANTE:
 * - ajusta los IDs a datos reales de tu BD
 * - empieza con una bebida simple y controlada
 */
public class MainMermaTestManual {

    public static void main(String[] args) {
        try {
            // =====================================================
            // 1) CONFIG / DB
            // =====================================================
            ConfigLoader.load();
            DbPool.init();

            // =====================================================
            // 2) DAOs
            // =====================================================
            MermaDao mermaDao = new MermaDao();
            MermaItemDao mermaItemDao = new MermaItemDao();

            RecetaIngredienteDao recetaIngredienteDao = new RecetaIngredienteDao();
            ExtraRecetaReglaDao extraRecetaReglaDao = new ExtraRecetaReglaDao();
            PersonalizacionRecetaReglaDao personalizacionRecetaReglaDao = new PersonalizacionRecetaReglaDao();

            StockIngredienteDao stockIngredienteDao = new StockIngredienteDao();
            MovimientoStockDao movimientoStockDao = new MovimientoStockDao();

            // =====================================================
            // 3) SERVICES
            // =====================================================
            RecipeResolverService recipeResolverService = new RecipeResolverService(
                    recetaIngredienteDao,
                    extraRecetaReglaDao,
                    personalizacionRecetaReglaDao
            );

            StockIngredienteService stockIngredienteService = new StockIngredienteService(stockIngredienteDao);
            MovimientoStockService movimientoStockService = new MovimientoStockService(movimientoStockDao);

            MermaService mermaService = new MermaService(
                    mermaDao,
                    mermaItemDao,
                    recipeResolverService,
                    stockIngredienteService,
                    movimientoStockService
            );

            // =====================================================
            // 4) FACADE
            // =====================================================
            MermaFacade mermaFacade = new MermaFacade(mermaService);

            // =====================================================
            // 5) REQUEST DE PRUEBA
            // =====================================================
            MermaRequest request = buildRequestPrueba();

            System.out.println("=======================================");
            System.out.println("INICIO TEST MANUAL DE MERMA");
            System.out.println("=======================================");
            System.out.println("Sucursal: " + request.getIdSucursal());
            System.out.println("Usuario: " + request.getIdUsuario());
            System.out.println("Tipo merma: " + request.getTipoMerma());
            System.out.println("Origen: " + request.getOrigen());
            System.out.println("Motivo: " + request.getMotivo());
            System.out.println("Items: " + request.getItems().size());
            System.out.println("=======================================");

            // =====================================================
            // 6) EJECUCIÓN
            // =====================================================
            MermaResultDTO result = mermaFacade.registrarMerma(request);

            // =====================================================
            // 7) RESULTADO
            // =====================================================
            System.out.println("MERMA REGISTRADA OK");
            System.out.println("idMerma = " + result.getIdMerma());

            if (result.getItemsPersistidos() != null) {
                for (int i = 0; i < result.getItemsPersistidos().size(); i++) {
                    System.out.println(
                            "item[" + i + "] -> idMermaItem="
                                    + result.getItemsPersistidos().get(i).getIdMermaItem()
                                    + ", idProducto="
                                    + result.getItemsPersistidos().get(i).getIdProducto()
                    );
                }
            }

            System.out.println("=======================================");
            System.out.println("FIN TEST MANUAL DE MERMA");
            System.out.println("=======================================");

        } catch (Exception e) {
            System.err.println("=======================================");
            System.err.println("ERROR EN TEST MANUAL DE MERMA");
            System.err.println("=======================================");
            e.printStackTrace();
        }
    }

    /**
     * Construye un request de prueba.
     *
     * AJUSTA ESTOS IDs A TU BD REAL:
     * - idSucursal
     * - idUsuario
     * - idProducto
     * - idTamano
     * - idTipoCafe
     * - idIngredienteTipoCafe
     * - idExtra
     * - idPersonalizacion
     */
    private static MermaRequest buildRequestPrueba() {
        MermaRequest request = new MermaRequest();
        request.setIdSucursal(1);
        request.setIdUsuario(1);
        request.setTipoMerma("PRODUCTO_TERMINADO");
        request.setOrigen("VENTAS");
        request.setMotivo("PRUEBA_MANUAL");
        request.setObservaciones("Test manual Espresso Solo");

        MermaItemRequest item = new MermaItemRequest();
        item.setIdProducto(1);
        item.setNombreProductoSnapshot("Espresso");
        item.setIdTamano(6);
        item.setNombreTamanoSnapshot("Solo");
        item.setUsarReceta(true);
        item.setCantidad(BigDecimal.ONE);

        item.setIdTipoCafeSeleccionado(1);
        item.setNombreTipoCafeSnapshot("Espresso");
        item.setIdIngredienteTipoCafeSeleccionado(1);
        item.setSuplementoTipoCafe(BigDecimal.ZERO);

        // Extra válido para Espresso
        MermaExtraRequest extra1 = new MermaExtraRequest();
        extra1.setIdExtra(18);
        extra1.setNombreExtra("Espresso Shot");
        extra1.setTipoExtra("SHOT");
        extra1.setPrecioExtra(new BigDecimal("0.70"));
        extra1.setCantidad(BigDecimal.ONE);

        // Personalización válida
        MermaPersonalizacionRequest p1 = new MermaPersonalizacionRequest();
        p1.setIdPersonalizacion(21);
        p1.setNombrePersonalizacion("Extra caliente");
        p1.setTipoPersonalizacion("PREP");
        p1.setPrecioPersonalizacion(BigDecimal.ZERO);

        item.setExtras(List.of(extra1));
        item.setPersonalizaciones(List.of(p1));
        item.setAskMes(List.of("Prueba merma espresso solo"));

        item.setConfiguracionJson("""
            {
              "tamano": "Solo",
              "tipoCafe": "Espresso",
              "extras": ["Espresso Shot"],
              "personalizaciones": ["Extra caliente"],
              "askMes": ["Prueba merma espresso solo"]
            }
            """);

        item.setDescripcionSnapshot("Espresso Solo | Café: Espresso + Espresso Shot + Extra caliente");

        request.setItems(List.of(item));
        return request;
    }

    /**
     * Versión simple para no depender todavía de ObjectMapper aquí.
     */
    private static String buildConfiguracionJsonBasico(MermaItemRequest item) {
        String tipoCafe = item.getNombreTipoCafeSnapshot() != null
                ? escapeJson(item.getNombreTipoCafeSnapshot())
                : "";

        String extra = "";
        if (item.getExtras() != null && !item.getExtras().isEmpty()) {
            extra = escapeJson(item.getExtras().get(0).getNombreExtra());
        }

        String personalizacion = "";
        if (item.getPersonalizaciones() != null && !item.getPersonalizaciones().isEmpty()) {
            personalizacion = escapeJson(item.getPersonalizaciones().get(0).getNombrePersonalizacion());
        }

        String askMe = "";
        if (item.getAskMes() != null && !item.getAskMes().isEmpty()) {
            askMe = escapeJson(item.getAskMes().get(0));
        }

        return """
                {
                  "tamano": "%s",
                  "tipoCafe": "%s",
                  "extras": ["%s"],
                  "personalizaciones": ["%s"],
                  "askMes": ["%s"]
                }
                """.formatted(
                escapeJson(nullSafe(item.getNombreTamanoSnapshot())),
                tipoCafe,
                extra,
                personalizacion,
                askMe
        );
    }

    private static String buildDescripcionSnapshotBasico(MermaItemRequest item) {
        StringBuilder sb = new StringBuilder();

        sb.append(nullSafe(item.getNombreProductoSnapshot()));

        if (item.getNombreTamanoSnapshot() != null && !item.getNombreTamanoSnapshot().isBlank()) {
            sb.append(" ").append(item.getNombreTamanoSnapshot().trim());
        }

        if (item.getNombreTipoCafeSnapshot() != null && !item.getNombreTipoCafeSnapshot().isBlank()) {
            sb.append(" | Café: ").append(item.getNombreTipoCafeSnapshot().trim());
        }

        if (item.getExtras() != null) {
            for (MermaExtraRequest e : item.getExtras()) {
                if (e != null && e.getNombreExtra() != null && !e.getNombreExtra().isBlank()) {
                    sb.append(" + ").append(e.getNombreExtra().trim());
                }
            }
        }

        if (item.getPersonalizaciones() != null) {
            for (MermaPersonalizacionRequest p : item.getPersonalizaciones()) {
                if (p != null && p.getNombrePersonalizacion() != null && !p.getNombrePersonalizacion().isBlank()) {
                    sb.append(" + ").append(p.getNombrePersonalizacion().trim());
                }
            }
        }

        return sb.toString();
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String escapeJson(String value) {
        return nullSafe(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
