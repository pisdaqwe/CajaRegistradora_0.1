package app;

import javax.swing.SwingUtilities;

import config.ConfigLoader;
import config.DbPool;
import dao.FichajeDao;
import dao.UsuarioDao;
import model.Fichaje;
import model.Usuario;
import service.FichajeFacade;
import service.FichajeService;
import ui.screens.LoginScreen;

public class MainApp {

	public static void main(String[] args) {
		ConfigLoader.load();
		DbPool.init();
		FichajeDao dao = new FichajeDao();
		FichajeService fichajeService = new FichajeService(dao);
		
		UsuarioDao usuarioDao = new UsuarioDao();
		FichajeFacade fichajeFacade = new FichajeFacade(usuarioDao, fichajeService);
		SwingUtilities.invokeLater(() -> {
            LoginScreen screen = new LoginScreen(fichajeFacade);
            screen.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
            screen.setVisible(true);
        });
		
	
//		Fichaje f = fichajeService.ficharEntrada(1);
//		System.out.println("Fichaje creado:");
//	    System.out.println("ID: " + f.getIdFichaje());
//	    System.out.println("Usuario: " + f.getIdUsuario());
//	    System.out.println("Entrada: " + f.getFechaEntrada());
//	    System.out.println("Estado: " + f.getEstado());
//	    fichajeService.ficharSalida(f.getIdFichaje());
//	    fichajeService.ficharSalida(1);
	    
	    
	  
//		System.out.println("✅ Iniciando TPV Cafetería desde MainApp...");
//		
//		// 3. Probar acceso a usuario
//        UsuarioDao usuarioDao = new UsuarioDao();
//        Usuario admin = usuarioDao.findByUsuario("1000");
//
//        if (admin != null) {
//            System.out.println("✅ Usuario encontrado");
//            System.out.println("Nombre: " + admin.getNombre());
//            System.out.println("Usuario: " + admin.getUsuario());
//            System.out.println("Rol: " + admin.getRol().getNombre());
//            System.out.println("Activo: " + admin.isActivo());
//        } else {
//            System.out.println("❌ Usuario NO encontrado");
//        }
//	
//     // 4. Cierre opcional
//        DbPool.shutdown();
		
		
		
	}

}
