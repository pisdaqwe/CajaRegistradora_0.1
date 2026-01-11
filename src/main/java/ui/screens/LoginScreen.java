package ui.screens;

import javax.swing.*;
import javax.swing.border.LineBorder;

import app.AppContext;
import enums.PostLoginDestination;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import model.Fichaje;
import model.Usuario;
import model.UsuarioRecordado;
import service.AuthService;
import service.FichajeFacade;
import service.UsuarioRecordadoService;
import ui.dialog.PinDialog;
import ui.dialog.PinDialog.PinDialogMode;
import ui.dialog.PinDialogResult;
import ui.router.PostLoginRouter;

public class LoginScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    private final FichajeFacade fichajeFacade;
    private final UsuarioRecordadoService usuarioRecordadoService;
    private final int idTerminal;

    private JTextField txtUsuario;
    private JTextArea txtTicket;
    private JPanel panelBotonesRecordados;
    private AuthService authService;

    public LoginScreen(
            FichajeFacade fichajeFacade,
            UsuarioRecordadoService usuarioRecordadoService,
            int idTerminal,
            AuthService  authService
    ) {
        this.fichajeFacade = fichajeFacade;
        this.usuarioRecordadoService = usuarioRecordadoService;
        this.idTerminal = idTerminal;
        this.authService= authService;

        initUI();
        cargarBotonesRecordados();
    }

    private void initUI() {
        setTitle("TPV - Identificación");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // =========================
        // PANEL FONDO
        // =========================
        JPanel panelFondo = new JPanel(new BorderLayout());
        panelFondo.setBackground(new Color(30, 30, 30));
        setContentPane(panelFondo);

        // =========================
        // RELOJ
        // =========================
        JLabel lblReloj = new JLabel();
        lblReloj.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblReloj.setForeground(new Color(200, 200, 200));
        lblReloj.setHorizontalAlignment(SwingConstants.RIGHT);
        lblReloj.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 30));
        panelFondo.add(lblReloj, BorderLayout.NORTH);
        iniciarReloj(lblReloj);

        // =========================
        // WRAPPER DERECHO
        // =========================
        JPanel panelWrapper = new JPanel(new GridBagLayout());
        panelWrapper.setOpaque(false);
        panelFondo.add(panelWrapper, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 50, 40);

        // =========================
        // PANEL CENTRAL TPV
        // =========================
        JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
        panelCentral.setPreferredSize(new Dimension(400, 650));
        panelCentral.setBackground(new Color(245, 245, 245));
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 3, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

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
        // PANEL BOTONES RECORDADOS
        // =========================
        panelBotonesRecordados = new JPanel(new GridLayout(2, 3, 12, 12));
        panelBotonesRecordados.setBorder(
                BorderFactory.createTitledBorder("Partner Sign-In")
        );
        panelBotonesRecordados.setBackground(panelCentral.getBackground());

        // =========================
        // TECLADO NUMÉRICO
        // =========================
        JPanel keypad = new JPanel(new GridLayout(4, 3, 5, 10));
        keypad.setBackground(panelCentral.getBackground());

        for (int i = 1; i <= 9; i++) {
            keypad.add(createNumberButton(String.valueOf(i)));
        }
        keypad.add(createClearButton());
        keypad.add(createNumberButton("0"));
        keypad.add(createBackButton());

        // =========================
        // PANEL SUPERIOR (USUARIOS + TECLADO)
        // =========================
        JPanel panelSuperior = new JPanel(new BorderLayout(15, 15));
        panelSuperior.setBackground(panelCentral.getBackground());
        panelSuperior.add(panelBotonesRecordados, BorderLayout.NORTH);
        panelSuperior.add(keypad, BorderLayout.CENTER);

        // =========================
        // BOTONES DE ACCIÓN
        // =========================
        JButton btnClock = new JButton("Clock In / Out");
        btnClock.setFont(new Font("Arial", Font.BOLD, 18));
        btnClock.addActionListener(e -> fichar());

        JButton btnSignIn = new JButton("Sign In");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 18));
        btnSignIn.addActionListener(e -> {

            // 1. Abrir el PinDialog en modo LOGIN_COMPLETO
            PinDialog dialog = new PinDialog(
                    this,
                    PinDialog.PinDialogMode.LOGIN_COMPLETO,
                    null
            );

            // 2. Mostrar el diálogo y esperar resultado
            PinDialogResult result = dialog.showDialog();

            // 3. Si canceló, no hacemos nada
            if (result == null) {
                return;
            }

            try {
                // 4. Obtener credenciales
                String usuario = result.getUsuario();
                String pin = result.getPin();

                // 5. Autenticación
                Usuario usuarioLogueado =
                        authService.loginCompleto(usuario, pin);

                // 6. Guardar sesión
                AppContext.setUsuario(usuarioLogueado);

                // 7. Registrar / actualizar botón amarillo
                usuarioRecordadoService.registrarAccesoExitoso(
                        usuarioLogueado,
                        idTerminal
                );
                continuarPostLogin();


               

            } catch (Exception ex) {
                // 9. Mostrar error controlado
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error de login",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });


        JPanel actions = new JPanel(new GridLayout(2, 1, 10, 10));
        actions.setBackground(panelCentral.getBackground());
        actions.add(btnClock);
        actions.add(btnSignIn);

        // =========================
        // CENTER
        // =========================
        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setBackground(panelCentral.getBackground());
        center.add(panelSuperior, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        panelCentral.add(center, BorderLayout.CENTER);

        // =========================
        // TICKET
        // =========================
        txtTicket = new JTextArea(6, 20);
        txtTicket.setEditable(false);
        txtTicket.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtTicket.setBorder(BorderFactory.createTitledBorder("Ticket Fichaje"));
        panelCentral.add(new JScrollPane(txtTicket), BorderLayout.SOUTH);
    }
 // ===============================
 // POST-LOGIN (común para TODOS)
 // ===============================
 private void continuarPostLogin() {

     try {
         PostLoginRouter router = new PostLoginRouter();
         PostLoginDestination destino = router.route();

         // Ocultamos el login (no lo destruimos) para volver rápido tras logout
         this.setVisible(false);

         switch (destino) {

             case ADMIN_PANEL:
                 abrirAdminDashboard();
                 break;

             case OPERATIVE_HOME:
                 abrirOperativeHomePlaceholder(); // de momento placeholder
                 break;

             default:
                 throw new IllegalStateException("Destino post-login no soportado: " + destino);
         }

     } catch (Exception ex) {
         // Si algo falla, volvemos a mostrar el login
         this.setVisible(true);

         JOptionPane.showMessageDialog(
                 this,
                 ex.getMessage(),
                 "Error post-login",
                 JOptionPane.ERROR_MESSAGE
         );
     }
 }

 private void abrirAdminDashboard() {

     AdminDashboardFrame dash = new AdminDashboardFrame(() -> {
         // Este callback se ejecuta cuando el dashboard hace logout
         mostrarLoginDeNuevo();
     });

     dash.setVisible(true);
 }

 private void abrirOperativeHomePlaceholder() {
     // TEMPORAL: hasta que creemos OperativeHomeFrame
     JOptionPane.showMessageDialog(this, "OperativeHome (pendiente)");
     mostrarLoginDeNuevo();
 }

 private void mostrarLoginDeNuevo() {
     // Si quieres, limpia campos aquí también
     // txtUsuario.setText("");
     // txtPin.setText("");

     this.setVisible(true);
     this.toFront();
     this.requestFocus();
 }

	// =========================
    // BOTONES TECLADO
    // =========================
    private JButton createNumberButton(String number) {
        JButton btn = new JButton(number);
        btn.setFont(new Font("Arial", Font.BOLD, 50));
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
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    // =========================
    // RELOJ
    // =========================
    private void iniciarReloj(JLabel label) {
        DateTimeFormatter fechaFormato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM");
        DateTimeFormatter horaFormato = DateTimeFormatter.ofPattern("HH:mm:ss");

        Timer timer = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            label.setText(
                    "<html><div style='text-align: right;'>"
                            + ahora.format(fechaFormato) + "<br>"
                            + "<span style='font-size: 20px; color: white;'>"
                            + ahora.format(horaFormato)
                            + "</span></div></html>"
            );
        });
        timer.start();
    }

    // =========================
    // USUARIOS RECORDADOS
    // =========================
    private void cargarBotonesRecordados() {
        panelBotonesRecordados.removeAll();

        List<UsuarioRecordado> usuarios =
                usuarioRecordadoService.getUsuariosRecordadosDelTerminal(idTerminal);

        for (UsuarioRecordado ur : usuarios) {
            JButton boton = crearBotonUsuarioRecordado(ur);
            panelBotonesRecordados.add(boton);
        }

        panelBotonesRecordados.revalidate();
        panelBotonesRecordados.repaint();
    }

    private JButton crearBotonUsuarioRecordado(UsuarioRecordado ur) {

        JButton btn = new JButton(ur.getNombreBoton());
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(255, 215, 0));
        btn.setFocusPainted(false);

        // 🔑 El botón "transporta" el usuario
        btn.putClientProperty("usuarioRecordado", ur);

        // ✅ Listener de LOGIN RÁPIDO
        btn.addActionListener(e -> {

            // 1. Recuperar el usuario asociado al botón
            UsuarioRecordado usuario =
                    (UsuarioRecordado) ((JButton) e.getSource())
                            .getClientProperty("usuarioRecordado");

            // 2. Abrir PinDialog en modo LOGIN_RAPIDO
            PinDialog dialog = new PinDialog(
                    this,
                    PinDialog.PinDialogMode.LOGIN_RAPIDO,
                    usuario.getNombreBoton()
            );

            PinDialogResult result = dialog.showDialog();

            // 3. Si cancela → no hacer nada
            if (result == null) {
                return;
            }

            try {
                // 4. Validar PIN usando el idUsuario del botón
                Usuario usuarioLogueado =
                        authService.loginRapido(
                                usuario.getIdUsuario(),
                                result.getPin()
                        );

                // 5. Guardar sesión
                AppContext.setUsuario(usuarioLogueado);

                // 6. Actualizar último acceso del botón
                usuarioRecordadoService.registrarAccesoExitoso(
                        usuarioLogueado,
                        idTerminal
                );

                // 7. Continuar flujo (temporal)
                JOptionPane.showMessageDialog(
                        this,
                        "Bienvenido " + usuarioLogueado.getNombre()
                );
                continuarPostLogin();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error de autenticación",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        return btn;
    }

}
