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
import dao.ExtraDao;
import dao.FichajeDao;
import dao.PersonalizacionDao;
import dao.ProductoDao;
import dao.ProductoEstacionDao;
import dao.ProductoTamanoDao;
import dao.SesionCajaDao;
import dao.StockProductoDao;
import dao.SubcategoriaDao;
import dao.TicketJsonDao;
import dao.UsuarioDao;
import dao.UsuarioRecordadoDao;
import dao.VentaRegistroDao;
import facade.CajaFacade;
import facade.FichajeFacade;
import facade.VentaFacade;
import service.AppServices;
import service.AuthService;
import service.CatalogoService;
import service.ColaImpresionService;
import service.ComboMatcherService;
import service.ComboService;
import service.DisponibilidadExtraService;
import service.DisponibilidadProductoService;
import service.FichajeService;
import service.ProductoPersonalizacionService;
import service.SesionCajaService;
import service.TicketClienteService;
import service.UsuarioRecordadoService;
import service.UsuarioService;
import service.VentaService;
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
        // 2) DAOs
        // =====================================================
        UsuarioDao usuarioDao = new UsuarioDao();
        FichajeDao fichajeDao = new FichajeDao();
        SesionCajaDao sesionCajaDao = new SesionCajaDao();
        CajaDao cajaDao = new CajaDao();
        UsuarioRecordadoDao usuarioRecordadoDao = new UsuarioRecordadoDao();

        CategoriaDao categoriaDao = new CategoriaDao();
        SubcategoriaDao subcategoriaDao = new SubcategoriaDao();
        ProductoDao productoDao = new ProductoDao();
        ProductoTamanoDao productoTamanoDao = new ProductoTamanoDao();
        ExtraDao extraDao = new ExtraDao();
        PersonalizacionDao personalizacionDao = new PersonalizacionDao();

        VentaRegistroDao ventaRegistroDao = new VentaRegistroDao();
        ColaImpresionDAO colaImpresionDAO = new ColaImpresionDAO();
        ProductoEstacionDao productoEstacionDao = new ProductoEstacionDao();
        TicketJsonDao jsonDao = new TicketJsonDao();
        StockProductoDao stockProductoDao = new StockProductoDao();

        // --- DAOs de combos ---
        ComboDao comboDao = new ComboDao();
        ComboItemDao comboItemDao = new ComboItemDao();

        // =====================================================
        // 3) SERVICES
        // =====================================================
        AuthService authService = new AuthService(usuarioDao);
        FichajeService fichajeService = new FichajeService(fichajeDao, sesionCajaDao);
        UsuarioRecordadoService usuarioRecordadoService = new UsuarioRecordadoService(usuarioRecordadoDao);
        SesionCajaService sesionCajaService = new SesionCajaService(cajaDao, sesionCajaDao);
        UsuarioService usuarioService = new UsuarioService(usuarioDao);

        // Catálogo / personalización
        CatalogoService catalogoService = new CatalogoService(
                categoriaDao,
                subcategoriaDao,
                productoDao,
                productoTamanoDao
        );

        ProductoPersonalizacionService productoPersonalizacionService =
                new ProductoPersonalizacionService(productoTamanoDao, extraDao, personalizacionDao);

        // Venta / impresión / ticket
        VentaService ventaService = new VentaService(ventaRegistroDao);
        ColaImpresionService colaImpresionService =
                new ColaImpresionService(colaImpresionDAO, productoEstacionDao);
        TicketClienteService ticketClienteService = new TicketClienteService(jsonDao);

        // Disponibilidad / stock
        DisponibilidadProductoService disponibilidadProductoService =
                new DisponibilidadProductoService(stockProductoDao);
        DisponibilidadExtraService disponibilidadExtraService =
                new DisponibilidadExtraService(extraDao);

        // --- Servicios de combos ---
        ComboService comboService = new ComboService(comboDao, comboItemDao);
        ComboMatcherService comboMatcherService = new ComboMatcherService();

        // =====================================================
        // 4) FACADES
        // =====================================================
        FichajeFacade fichajeFacade = new FichajeFacade(usuarioDao, fichajeService);
        CajaFacade cajaFacade = new CajaFacade(sesionCajaService);
        VentaFacade ventaFacade = new VentaFacade(ventaService);

        // =====================================================
        // 5) APP SERVICES
        // =====================================================
        AppServices appServices = new AppServices(
                authService,
                fichajeService,
                sesionCajaService,
                usuarioRecordadoService,
                fichajeFacade,
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
                comboMatcherService
        );

        // =====================================================
        // 6) CONTEXTO INICIAL DE TERMINAL / SUCURSAL
        // =====================================================
        // TODO:
        // De momento se deja fijo.
        // Más adelante habrá que cargar idCaja desde config.properties
        // o desde una configuración central de terminal.
        int id_caja = 1;

        AppContext.setIdSucursal(1);

        // =====================================================
        // 7) UI
        // =====================================================
        SwingUtilities.invokeLater(() -> {
            LoginScreen screen = new LoginScreen(appServices, id_caja);
            screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        });
    }
}
