package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import config.ConfigLoader;
import config.DbPool;
import dao.CajaDao;
import dao.CategoriaDao;
import dao.ColaImpresionDAO;
import dao.ComboDao;
import dao.ComboItemDao;
import dao.DescuentoDao;
import dao.DevolucionDao;
import dao.DevolucionItemDao;
import dao.DevolucionRegistroDao;
import dao.DevolucionTicketJsonDao;
import dao.EstacionDao;
import dao.ExtraDao;
import dao.ExtraRecetaReglaDao;
import dao.FichajeDao;
import dao.InformesDao;
import dao.MermaDao;
import dao.MermaItemDao;
import dao.MovimientoStockDao;
import dao.PersonalizacionDao;
import dao.PersonalizacionRecetaReglaDao;
import dao.ProductoDao;
import dao.ProductoEstacionDao;
import dao.ProductoTamanoDao;
import dao.ProductoTipoCafeDao;
import dao.RecetaIngredienteDao;
import dao.SesionCajaDao;
import dao.StockIngredienteDao;
import dao.StockProductoDao;
import dao.SubcategoriaDao;
import dao.SucursalDao;
import dao.TicketJsonDao;
import dao.UsuarioDao;
import dao.VentaDao;
import dao.VentaItemDao;
import dao.VentaRegistroDao;
import facade.CajaFacade;
import facade.DevolucionFacade;
import facade.FichajeFacade;
import facade.MermaFacade;
import facade.VentaFacade;
import service.AppServices;
import service.AuthService;
import service.CatalogoService;
import service.ColaImpresionService;
import service.ComboMatcherService;
import service.ComboService;
import service.DescuentoService;
import service.DevolucionService;
import service.DevolucionTicketService;
import service.DisponibilidadExtraService;
import service.DisponibilidadProductoService;
import service.FichajeService;
import service.InformesService;
import service.MermaService;
import service.MovimientoStockService;
import service.ProductoPersonalizacionService;
import service.RecipeResolverService;
import service.SesionCajaService;
import service.StockIngredienteService;
import service.SucursalService;
import service.TicketClienteService;
import service.UsuarioService;
import service.VentaService;
import service.VentaStockIngredienteService;
import ui.screens.LoginScreen;

public class MainApp {

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getContextClassLoader().getResource("logback.xml"));

        // =====================================================
        // 1) CONFIGURACIÓN GENERAL
        // =====================================================
        ConfigLoader.load();
        DbPool.init();

        // =====================================================
        // 2) DAOs GENERALES
        // =====================================================
        UsuarioDao usuarioDao = new UsuarioDao();
        FichajeDao fichajeDao = new FichajeDao();
        SesionCajaDao sesionCajaDao = new SesionCajaDao();
        CajaDao cajaDao = new CajaDao();
        SucursalDao sucursalDao = new SucursalDao();
        
        // =====================================================
        // 3) DAOs DE CATÁLOGO / CUSTOMIZACIÓN
        // =====================================================
        CategoriaDao categoriaDao = new CategoriaDao();
        SubcategoriaDao subcategoriaDao = new SubcategoriaDao();
        ProductoDao productoDao = new ProductoDao();
        ProductoTamanoDao productoTamanoDao = new ProductoTamanoDao();
        ExtraDao extraDao = new ExtraDao();
        PersonalizacionDao personalizacionDao = new PersonalizacionDao();
        ProductoTipoCafeDao productoTipoCafeDao = new ProductoTipoCafeDao();

        // =====================================================
        // 4) DAOs DE VENTAS / IMPRESIÓN / TICKETS
        // =====================================================
        VentaRegistroDao ventaRegistroDao = new VentaRegistroDao();
        VentaDao ventaDao = new VentaDao();
        VentaItemDao ventaItemDao = new VentaItemDao();
        EstacionDao estacionDao = new EstacionDao();
        ColaImpresionDAO colaImpresionDAO = new ColaImpresionDAO();
        ProductoEstacionDao productoEstacionDao = new ProductoEstacionDao();
        TicketJsonDao ticketJsonDao = new TicketJsonDao();

        // =====================================================
        // 5) DAOs DE DISPONIBILIDAD / STOCK PRODUCTO
        // =====================================================
        StockProductoDao stockProductoDao = new StockProductoDao();

        // =====================================================
        // 6) DAOs DE RECETAS / STOCK INGREDIENTES / MOVIMIENTOS
        // =====================================================
        RecetaIngredienteDao recetaIngredienteDao = new RecetaIngredienteDao();
        ExtraRecetaReglaDao extraRecetaReglaDao = new ExtraRecetaReglaDao();
        PersonalizacionRecetaReglaDao personalizacionRecetaReglaDao = new PersonalizacionRecetaReglaDao();
        StockIngredienteDao stockIngredienteDao = new StockIngredienteDao();
        MovimientoStockDao movimientoStockDao = new MovimientoStockDao();

        // =====================================================
        // 7) DAOs DE MERMA
        // =====================================================
        MermaDao mermaDao = new MermaDao();
        MermaItemDao mermaItemDao = new MermaItemDao();

        // =====================================================
        // 8) DAOs DE COMBOS / DESCUENTOS
        // =====================================================
        ComboDao comboDao = new ComboDao();
        ComboItemDao comboItemDao = new ComboItemDao();
        DescuentoDao descuentoDao = new DescuentoDao();

        // =====================================================
        // 9) DAOs DE DEVOLUCIONES
        // =====================================================
        DevolucionDao devolucionDao = new DevolucionDao();
        DevolucionItemDao devolucionItemDao = new DevolucionItemDao();
        DevolucionTicketJsonDao devolucionTicketJsonDao = new DevolucionTicketJsonDao();

        /**
         * Ajuste ya existente:
         * - DevolucionRegistroDao también repone stock de producto dentro de la transacción.
         */
        DevolucionRegistroDao devolucionRegistroDao = new DevolucionRegistroDao(
                devolucionDao,
                devolucionItemDao,
                devolucionTicketJsonDao,
                stockProductoDao
        );
        
        // =====================================================
        // 10) DAOs DE DEVOLUCIONES
        // =====================================================
        
        InformesDao informesDao = new InformesDao();

        // =====================================================
        // 11) SERVICES GENERALES
        // =====================================================
        AuthService authService = new AuthService(usuarioDao);
        FichajeService fichajeService = new FichajeService(fichajeDao, sesionCajaDao);
        SesionCajaService sesionCajaService = new SesionCajaService(cajaDao, sesionCajaDao);
        UsuarioService usuarioService = new UsuarioService(usuarioDao);

        // =====================================================
        // 11) SERVICES DE CATÁLOGO / CUSTOMIZACIÓN
        // =====================================================
        CatalogoService catalogoService = new CatalogoService(
                categoriaDao,
                subcategoriaDao,
                productoDao,
                productoTamanoDao
        );

        ProductoPersonalizacionService productoPersonalizacionService =
                new ProductoPersonalizacionService(
                        productoTamanoDao,
                        extraDao,
                        personalizacionDao,
                        productoTipoCafeDao
                );

        // =====================================================
        // 13) SERVICES DE RECETA / STOCK INGREDIENTE / MOVIMIENTOS
        // =====================================================
        RecipeResolverService recipeResolverService =
                new RecipeResolverService(
                        recetaIngredienteDao,
                        extraRecetaReglaDao,
                        personalizacionRecetaReglaDao
                );

        StockIngredienteService stockIngredienteService =
                new StockIngredienteService(stockIngredienteDao);

        MovimientoStockService movimientoStockService =
                new MovimientoStockService(movimientoStockDao);

        VentaStockIngredienteService ventaStockIngredienteService =
                new VentaStockIngredienteService(
                        recipeResolverService,
                        stockIngredienteService,
                        movimientoStockService
                );

        // =====================================================
        // 14) SERVICES DE MERMA
        // =====================================================
        MermaService mermaService = new MermaService(
                mermaDao,
                mermaItemDao,
                recipeResolverService,
                stockIngredienteService, 
                movimientoStockService
        );

        // =====================================================
        // 15) SERVICES DE VENTAS / IMPRESIÓN / TICKETS
        // =====================================================
        /**
         * AJUSTE NUEVO IMPORTANTE:
         * - VentaService ya no deja la transacción en el DAO.
         * - Ahora orquesta la transacción completa y usa también
         *   VentaStockIngredienteService para receta + stock + movimientos.
         */
        VentaService ventaService = new VentaService(
                ventaRegistroDao,
                ventaStockIngredienteService
        );

        ColaImpresionService colaImpresionService =
                new ColaImpresionService(
                        colaImpresionDAO,
                        productoEstacionDao,
                        estacionDao
                );

        TicketClienteService ticketClienteService = new TicketClienteService(ticketJsonDao);

        // =====================================================
        // 16) SERVICES DE DISPONIBILIDAD / STOCK PRODUCTO
        // =====================================================
        DisponibilidadProductoService disponibilidadProductoService =
                new DisponibilidadProductoService(stockProductoDao);

        DisponibilidadExtraService disponibilidadExtraService =
                new DisponibilidadExtraService(extraDao);

        // =====================================================
        // 17) SERVICES DE COMBOS / DESCUENTOS
        // =====================================================
        ComboService comboService = new ComboService(comboDao, comboItemDao);
        ComboMatcherService comboMatcherService = new ComboMatcherService();
        DescuentoService descuentoService = new DescuentoService(descuentoDao);

        // =====================================================
        // 18) SERVICES DE DEVOLUCIONES
        // =====================================================
        DevolucionService devolucionService = new DevolucionService(
                ventaDao,
                ventaItemDao,
                devolucionRegistroDao
        );

        /**
         * Servicio lector del ticket de devolución.
         */
        DevolucionTicketService devolucionTicketService =
                new DevolucionTicketService(devolucionTicketJsonDao);
        
        // =====================================================
        // 19) SERVICES DE DEVOLUCIONES
        // =====================================================
        	InformesService informesService = new InformesService(informesDao );
        	SucursalService sucursalService = new SucursalService(sucursalDao);
        // =====================================================
        // 19) FACADES
        // =====================================================
        FichajeFacade fichajeFacade = new FichajeFacade(usuarioDao, fichajeService);
        CajaFacade cajaFacade = new CajaFacade(sesionCajaService);
        VentaFacade ventaFacade = new VentaFacade(ventaService);
        DevolucionFacade devolucionFacade = new DevolucionFacade(devolucionService);
        MermaFacade mermaFacade = new MermaFacade(mermaService);

        // =====================================================
        // 20) APP SERVICES
        // =====================================================
        AppServices appServices = new AppServices(
                authService,
                fichajeFacade,
                fichajeService,
                sesionCajaService,
                cajaFacade,
                usuarioService,

                catalogoService,
                productoPersonalizacionService,
                ventaFacade,
                colaImpresionService,
                ticketClienteService,

                disponibilidadProductoService,
                disponibilidadExtraService,

                comboService,
                comboMatcherService,

                descuentoService,

                devolucionService,
                devolucionFacade,
                devolucionTicketService,

                mermaFacade,
                informesService,
                sucursalService
                
        );
     // =====================================================
     // 20) CONTEXTO INICIAL DEL TERMINAL
     // =====================================================
     int idCajaTerminal = ConfigLoader.getTerminalIdCaja();

     var cajaTerminal = cajaDao.findById(idCajaTerminal)
             .orElseThrow(() -> new IllegalStateException(
                     "La caja configurada en terminal.id_caja no existe: " + idCajaTerminal
             ));

     AppContext.initTerminal(
             cajaTerminal.getIdCaja(),
             cajaTerminal.getIdSucursal(),
             cajaTerminal.getNombre()
     );

     // =====================================================
     // 21) UI
     // =====================================================
     SwingUtilities.invokeLater(() -> {
         LoginScreen screen = new LoginScreen(appServices, idCajaTerminal);
         screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
         screen.setVisible(true);
     });
      
    }
}