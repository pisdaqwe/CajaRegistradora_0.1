package ui.ventas;



import model.TicketItem;
import model.TicketSession;
import service.AppServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomizationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final TicketSession ticketSession;
    @SuppressWarnings("unused")
    private final AppServices services;

    private final JLabel lblTitle = new JLabel("CUSTOM", SwingConstants.LEFT);
    private final JTextArea info = new JTextArea();

    public CustomizationPanel(TicketSession ticketSession, AppServices services) {
        this.ticketSession = ticketSession;
        this.services = services;

        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(420, 220));

        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        info.setEditable(false);
        info.setFont(new Font("Monospaced", Font.PLAIN, 14));
        info.setBackground(new Color(30, 30, 30));
        info.setForeground(new Color(220, 220, 220));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(lblTitle, BorderLayout.NORTH);
        add(new JScrollPane(info), BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        TicketItem item = ticketSession.getSelectedItemOrNull();
        if (item == null) {
            info.setText("Selecciona un producto del ticket\npara ver sus opciones.");
            return;
        }

        String txt =
                "Producto: " + item.getProducto().getNombre() + "\n" +
                "Tamaño:   " + item.getTamano().getNombre() + "\n\n" +
                "Extras: " + item.getExtras().size() + "\n" +
                "Personalizaciones: " + item.getPersonalizaciones().size() + "\n\n" +
                "(Panel placeholder: en el Paso 4\nmeteremos tamaños/extras/toggles)";
        info.setText(txt);
    }
}
