package app;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import config.ConfigLoader;
import config.DbPool;
import dao.CajaDao;
import dao.CategoriaDao;
import dao.FichajeDao;
import dao.ProductoDao;
import dao.ProductoTamanoDao;
import dao.SesionCajaDao;
import dao.SubcategoriaDao;
import dao.UsuarioDao;
import dao.UsuarioRecordadoDao;
import facade.CajaFacade;
import facade.FichajeFacade;
import model.ProductoTamano;
import service.AppServices;
import service.AuthService;
import service.CatalogoService;
import service.FichajeService;
import service.SesionCajaService;
import service.UsuarioRecordadoService;
import service.UsuarioService;
import ui.screens.LoginScreen;

public class MainApp {

    public static void main(String[] args) {
    	System.out.println(
    		    Thread.currentThread()
    		          .getContextClassLoader()
    		          .getResource("logback.xml")
    		);
        ConfigLoader.load();
        DbPool.init();
       

        // =========================
        // DAOs
        // =========================
        UsuarioDao usuarioDao = new UsuarioDao();
        FichajeDao fichajeDao = new FichajeDao();
        SesionCajaDao sesionCajaDao = new SesionCajaDao();
        CajaDao cajaDao = new CajaDao();
        UsuarioRecordadoDao usuarioRecordadoDao = new UsuarioRecordadoDao();
        CategoriaDao categoriaDao = new CategoriaDao();
        SubcategoriaDao subcategoriaDao = new SubcategoriaDao();
        ProductoDao productoDao = new ProductoDao();
        ProductoTamanoDao productoTamanoDao = new ProductoTamanoDao();

        // =========================
        // SERVICES
        // =========================
        AuthService authService = new AuthService(usuarioDao);
        FichajeService fichajeService = new FichajeService(fichajeDao);
        UsuarioRecordadoService usuarioRecordadoService =
                new UsuarioRecordadoService(usuarioRecordadoDao);

        SesionCajaService sesionCajaService =
                new SesionCajaService(cajaDao, sesionCajaDao);
        UsuarioService usuarioService = new UsuarioService(usuarioDao);
        CatalogoService catalogoService = new CatalogoService(
                new CategoriaDao(),
                new SubcategoriaDao(),
                new ProductoDao(),
                new ProductoTamanoDao()
        );
        // =========================
        // FACADES
        // =========================
        FichajeFacade fichajeFacade =
                new FichajeFacade(usuarioDao, fichajeService);

        CajaFacade cajaFacade =new CajaFacade(sesionCajaService);

        // =========================
        // APP SERVICES (CLAVE)
        // =========================
        AppServices appServices = new AppServices(
                authService,
                fichajeService,
                sesionCajaService,
                usuarioRecordadoService,
                fichajeFacade,
                cajaFacade,
                usuarioService,
                catalogoService
        );

        int terminal = 1;

        // =========================
        // UI
        // =========================
        SwingUtilities.invokeLater(() -> {
            LoginScreen screen =
                    new LoginScreen(appServices, terminal);
            screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        });
    }
}


