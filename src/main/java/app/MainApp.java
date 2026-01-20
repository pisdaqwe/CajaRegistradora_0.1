package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import config.ConfigLoader;
import config.DbPool;
import dao.FichajeDao;
import dao.UsuarioDao;
import dao.UsuarioRecordadoDao;
import dao.CajaDao;
import dao.SesionCajaDao;
import service.AppServices;
import service.AuthService;
import service.FichajeFacade;
import service.FichajeService;
import service.UsuarioRecordadoService;
import service.SesionCajaService;
import ui.screens.LoginScreen;

public class MainApp {

    public static void main(String[] args) {

        // ===== CARGA CONFIGURACIÓN =====
        ConfigLoader.load();
        DbPool.init();

        // ===== DAOs =====
        UsuarioDao usuarioDao = new UsuarioDao();
        FichajeDao fichajeDao = new FichajeDao();
        UsuarioRecordadoDao usuarioRecordadoDao = new UsuarioRecordadoDao();
        CajaDao cajaDao = new CajaDao();
        SesionCajaDao sesionCajaDao = new SesionCajaDao();

        // ===== SERVICES =====
        AuthService authService = new AuthService(usuarioDao);
        FichajeService fichajeService = new FichajeService(fichajeDao);
        FichajeFacade fichajeFacade = new FichajeFacade(usuarioDao, fichajeService);
        UsuarioRecordadoService usuarioRecordadoService =
                new UsuarioRecordadoService(usuarioRecordadoDao);
        SesionCajaService sesionCajaService =
                new SesionCajaService(cajaDao, sesionCajaDao);

        // ===== CONTENEDOR DE SERVICES =====
        AppServices services = new AppServices(
                authService,
                fichajeFacade,
                fichajeService,
                sesionCajaService,
                usuarioRecordadoService
        );

        int terminal = 1;

        // ===== ARRANQUE UI =====
        SwingUtilities.invokeLater(() -> {
            LoginScreen screen = new LoginScreen(services, terminal);
            screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        });
    }
}

