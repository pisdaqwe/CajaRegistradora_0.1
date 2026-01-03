import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ui.dialog.PinDialog;

public class TestPinDialog {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame dummy = new JFrame();
            dummy.setSize(800, 600);
            dummy.setLocationRelativeTo(null);
            dummy.setVisible(true);

            PinDialog dialog = new PinDialog(
                    dummy,
                    PinDialog.PinDialogMode.LOGIN_RAPIDO,
                    "Carmen"
            );

            dialog.setVisible(true);
        });
    }
}

