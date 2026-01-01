package app;

import javax.swing.SwingUtilities;

import config.ConfigLoader;
import config.DbPool;
import dao.FichajeDao;
import dao.UsuarioDao;
import dao.UsuarioRecordadoDao;
import model.Fichaje;
import model.Usuario;
import service.FichajeFacade;
import service.FichajeService;
import service.UsuarioRecordadoService;
import ui.screens.LoginScreen;

public class MainApp {

	public static void main(String[] args) {
		ConfigLoader.load();
		DbPool.init();
		UsuarioDao usuarioDao = new UsuarioDao();
		FichajeDao fichajeDao = new FichajeDao();
		UsuarioRecordadoDao usuarioRecordadoDao= new UsuarioRecordadoDao();
		UsuarioRecordadoService usuarioRecordadoService = new UsuarioRecordadoService(usuarioRecordadoDao);
		FichajeService fichajeService = new FichajeService(fichajeDao);
		int terminal =1;
		
		FichajeFacade fichajeFacade = new FichajeFacade(usuarioDao, fichajeService);
		SwingUtilities.invokeLater(() -> {
            LoginScreen screen = new LoginScreen(fichajeFacade, usuarioRecordadoService, terminal);
            screen.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        });
		
	

		
		
		
	}

}
