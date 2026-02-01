package app;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import config.ConfigLoader;
import config.DbPool;
import dao.CajaDao;
import dao.FichajeDao;
import dao.SesionCajaDao;
import dao.UsuarioDao;
import dao.UsuarioRecordadoDao;
import facade.CajaFacade;
import facade.FichajeFacade;
import service.AppServices;
import service.AuthService;
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
                usuarioService
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


