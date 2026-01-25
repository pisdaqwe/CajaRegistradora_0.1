import config.ConfigLoader;
import config.DbPool;
import dao.CajaDao;
import dao.FichajeDao;
import dao.SesionCajaDao;
import dao.UsuarioDao;
import dao.UsuarioRecordadoDao;
import facade.CajaFacade;
import facade.FichajeFacade;
import service.AuthService;
import service.FichajeService;
import service.SesionCajaService;
import service.UsuarioRecordadoService;

public class MainApp {

    public static void main(String[] args) {

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

        // =========================
        // FACADES
        // =========================
        FichajeFacade fichajeFacade =
                new FichajeFacade(usuarioDao, fichajeService);

        CajaFacade cajaFacade =
                new CajaFacade(sesionCajaDao, fichajeService);

        // =========================
        // APP SERVICES (CLAVE)
        // =========================
        AppServices appServices = new AppServices(
                authService,
                fichajeService,
                sesionCajaService,
                usuarioRecordadoService,
                fichajeFacade,
                cajaFacade
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


