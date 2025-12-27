package ui.screens;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Fichaje;
import service.FichajeFacade;
import javax.swing.border.LineBorder;

public class LoginScreen extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final FichajeFacade fichajeFacade;

    private JTextField txtUsuario;
    private JTextArea txtTicket;

    public LoginScreen(FichajeFacade fichajeFacade) {
        this.fichajeFacade = fichajeFacade;
        initUI();
    }

    private void initUI() {
        setTitle("TPV - Identificación");
        setSize(1280, 800);                 // ✔ para WindowBuilder
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // =========================
        // PANEL FONDO (pantalla completa)
        // =========================
        JPanel panelFondo = new JPanel(new BorderLayout());
        panelFondo.setBackground(new Color(30, 30, 30));
        setContentPane(panelFondo);
     // Dentro de initUI, después de configurar el panelFondo
        JLabel lblReloj = new JLabel();
        lblReloj.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblReloj.setForeground(new Color(200, 200, 200)); // Gris claro elegante
        lblReloj.setHorizontalAlignment(SwingConstants.RIGHT);
        // Margen para que no pegue al borde de la pantalla
        lblReloj.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 30)); 

        panelFondo.add(lblReloj, BorderLayout.NORTH);

        // Llamamos al motor del reloj
        iniciarReloj(lblReloj);

     // =========================
     // PANEL WRAPPER (Control de posición)
     // =========================
     JPanel panelWrapper = new JPanel(new GridBagLayout());
     panelWrapper.setOpaque(false); 
     // Lo mantenemos al ESTE para que esté en la zona derecha
     panelFondo.add(panelWrapper, BorderLayout.EAST);

     GridBagConstraints gbc = new GridBagConstraints();
     gbc.gridx = 0;
     gbc.gridy = 0;

     /* EXPLICACIÓN DE LOS CAMBIOS:
        1. anchor = SOUTH: Empuja el componente hacia la parte inferior del espacio disponible.
        2. weighty = 1.0: Le dice al layout que use todo el espacio vertical sobrante para empujar.
        3. Insets: 
           - El tercer valor (bottom: 50) separa el panel del suelo.
           - El cuarto valor (right: 40) separa el panel de la pared derecha.
     */
     gbc.anchor = GridBagConstraints.SOUTH; 
     gbc.weighty = 1.0; 
     gbc.insets = new Insets(0, 0, 50, 40); 

     // =========================
     // PANEL CENTRAL (Card TPV)
     // =========================
     JPanel panelCentral = new JPanel(new BorderLayout(15, 15));

  // CORRECCIÓN: Debes usar setBorder() y asignar el borde creado
  panelCentral.setBorder(BorderFactory.createCompoundBorder(
      new LineBorder(new Color(0, 0, 0), 3, true), // Borde exterior negro redondeado
      BorderFactory.createEmptyBorder(10, 10, 10, 10) // MARGEN INTERNO (Top, Left, Bottom, Right)
  ));

  panelCentral.setPreferredSize(new Dimension(350, 500)); 
  panelCentral.setBackground(new Color(245, 245, 245));
     // ... resto de tu configuración de bordes ...

     // IMPORTANTE: Al añadirlo al wrapper, usamos el objeto GridBagConstraints
     panelWrapper.add(panelCentral, gbc);

        // =========================
        // DISPLAY USUARIO
        // =========================
        txtUsuario = new JTextField();
        txtUsuario.setEditable(false);
        txtUsuario.setHorizontalAlignment(JTextField.CENTER);
        txtUsuario.setFont(new Font("Monospaced", Font.BOLD, 28));
        txtUsuario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelCentral.add(txtUsuario, BorderLayout.NORTH);

        // =========================
        // TECLADO NUMÉRICO
        // =========================
        JPanel keypad = new JPanel(new GridLayout(4, 3, 10, 10));
        keypad.setBackground(panelCentral.getBackground());

        for (int i = 1; i <= 9; i++) {
            keypad.add(createNumberButton(String.valueOf(i)));
        }

        keypad.add(createClearButton());
        keypad.add(createNumberButton("0"));
        keypad.add(createBackButton());

        // =========================
        // BOTONES DE ACCIÓN
        // =========================
        JButton btnClock = new JButton("Clock In / Out");
        btnClock.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnSignIn = new JButton("Sign In");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel actions = new JPanel(new GridLayout(2, 1, 10, 10));
        actions.setBackground(panelCentral.getBackground());
        actions.add(btnClock);
        actions.add(btnSignIn);

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setBackground(panelCentral.getBackground());
        center.add(keypad, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        panelCentral.add(center, BorderLayout.CENTER);

        // =========================
        // TICKET SIMULADO
        // =========================
        txtTicket = new JTextArea(6, 20);
        txtTicket.setEditable(false);
        txtTicket.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtTicket.setBorder(BorderFactory.createTitledBorder("Ticket Fichaje"));

        panelCentral.add(new JScrollPane(txtTicket), BorderLayout.SOUTH);

        // =========================
        // ACCIONES
        // =========================
        btnClock.addActionListener(e -> fichar());

        btnSignIn.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Login de caja se implementará después",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
    }

  

	// =========================
    // BOTONES TECLADO
    // =========================
    private JButton createNumberButton(String number) {
        JButton btn = new JButton(number);
        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.addActionListener(e ->
                txtUsuario.setText(txtUsuario.getText() + number)
        );
        return btn;
    }

    private JButton createClearButton() {
        JButton btn = new JButton("Clear");
        btn.addActionListener(e -> txtUsuario.setText(""));
        return btn;
    }

    private JButton createBackButton() {
        JButton btn = new JButton("Back");
        btn.addActionListener(e -> {
            String text = txtUsuario.getText();
            if (!text.isEmpty()) {
                txtUsuario.setText(text.substring(0, text.length() - 1));
            }
        });
        return btn;
    }

    // =========================
    // FICHAJE
    // =========================
    private void fichar() {
        try {
            String usuario = txtUsuario.getText().trim();

            if (usuario.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduce tu código");
                return;
            }

            Fichaje f;
            String tipo;

            try {
                f = fichajeFacade.ficharEntradaPorUsuario(usuario);
                tipo = "ENTRADA";
            } catch (IllegalStateException ex) {
                f = fichajeFacade.ficharSalidaPorUsuario(usuario);
                tipo = "SALIDA";
            }

            txtTicket.setText(generarTicket(f, tipo));
            txtUsuario.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String generarTicket(Fichaje f, String tipo) {
        StringBuilder sb = new StringBuilder();

        sb.append("=============================\n");
        sb.append("        FICHAJE EMPLEADO\n");
        sb.append("=============================\n");
        sb.append("Tipo:       ").append(tipo).append("\n");
        sb.append("ID Usuario: ").append(f.getIdUsuario()).append("\n");
        sb.append("Entrada:    ").append(f.getFechaEntrada()).append("\n");

        if ("SALIDA".equals(tipo)) {
            sb.append("Salida:     ").append(f.getFechaSalida()).append("\n");
            sb.append("Duración:   ").append(f.getDuracion()).append(" min\n");
        }

        sb.append("=============================\n");

        return sb.toString();
    }
    private void iniciarReloj(JLabel label) {
        // Formatos para fecha y hora
        DateTimeFormatter fechaFormato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM");
        DateTimeFormatter horaFormato = DateTimeFormatter.ofPattern("HH:mm:ss");

        Timer timer = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            
            // Usamos HTML para que el Label tenga dos líneas
            String textoReloj = "<html><div style='text-align: right;'>"
                                + ahora.format(fechaFormato) + "<br>"
                                + "<span style='font-size: 20px; color: white;'>" 
                                + ahora.format(horaFormato) + "</span>"
                                + "</div></html>";
                                
            label.setText(textoReloj);
        });
        
        timer.start();
    }
}

