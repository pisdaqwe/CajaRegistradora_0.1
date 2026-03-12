package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ui.dialog.AskMeDialog;
import ui.dialog.AskMeDialogResult;

public class PruebaDIalogAskMe {
   public static void main(String[] args) {
	  SwingUtilities.invokeLater(()->{
		  JFrame frame = new JFrame("Test ask me");
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  frame.setSize(499,200);
		  frame.setLocationRelativeTo(null);
		  frame.setVisible(true);
		  AskMeDialog dialog = new AskMeDialog(frame, "lATE", 25);
		  AskMeDialogResult result = dialog.showDialog();
		// Mostrar resultado en consola
          System.out.println("==================================");
          System.out.println("¿Confirmado?: " + result.isConfirmed());
          System.out.println("Texto devuelto: [" + result.getText() + "]");
          System.out.println("==================================");

          // Cerrar frame de prueba al terminar
          frame.dispose();
		  
	  });
	
}

}
