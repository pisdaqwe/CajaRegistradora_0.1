package ui.screens;

import app.AppContext;
import dtoS.LoginRapidoButtonDTO;
import dtoS.SesionCajaRefDTO;
import enums.PostLoginDestination;
import model.Fichaje;
import model.Usuario;
import service.AppServices;
import ui.dialog.PinDialog;
import ui.dialog.PinDialog.PinDialogMode;
import ui.dialog.PinDialogResult;
import ui.router.PostLoginRouter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoginScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    private final int idCaja;
    private final AppServices services;

    private JTextField txtUsuario;
    private JTextArea txtTicket;
    private JPanel panelBotonesRapidos;

    public LoginScreen(AppServices services, int id_caja) {
        this.services = services;
        this.idCaja = id_caja;

        initUI();
        cargarBotonesRapidos();
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
        // DISPLAY USUARIO / CÓDIGO
        // =========================
        txtUsuario = new JTextField();
        txtUsuario.setEditable(false);
        txtUsuario.setHorizontalAlignment(JTextField.CENTER);
        txtUsuario.setFont(new Font("Monospaced", Font.BOLD, 28));
        txtUsuario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCentral.add(txtUsuario, BorderLayout.NORTH);

        // =========================
        // PANEL BOTONES RÁPIDOS
        // =========================
        panelBotonesRapidos = new JPanel(new GridLayout(2, 3, 12, 12));
        panelBotonesRapidos.setBorder(BorderFactory.createTitledBorder("Partner Sign-In"));
        panelBotonesRapidos.setBackground(panelCentral.getBackground());

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
        // PANEL SUPERIOR
        // =========================
        JPanel panelSuperior = new JPanel(new BorderLayout(15, 15));
        panelSuperior.setBackground(panelCentral.getBackground());
        panelSuperior.add(panelBotonesRapidos, BorderLayout.NORTH);
        panelSuperior.add(keypad, BorderLayout.CENTER);

        // =========================
        // BOTONES DE ACCIÓN
        // =========================
        JButton btnClock = new JButton("Clock In / Out");
        btnClock.setFont(new Font("Arial", Font.BOLD, 18));
        btnClock.addActionListener(e -> fichar());

        JButton btnSignIn = new JButton("Sign In");
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 18));
        btnSignIn.addActionListener(e -> hacerLoginCompleto());

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
        // TICKET FICHAJE
        // =========================
        txtTicket = new JTextArea(6, 20);
        txtTicket.setEditable(false);
        txtTicket.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtTicket.setBorder(BorderFactory.createTitledBorder("Ticket Fichaje"));
        panelCentral.add(new JScrollPane(txtTicket), BorderLayout.SOUTH);
    }

    // ===============================
    // LOGIN COMPLETO
    // ===============================
    private void hacerLoginCompleto() {
        PinDialog dialog = new PinDialog(this, PinDialogMode.LOGIN_COMPLETO, null);
        PinDialogResult result = dialog.showDialog();

        if (result == null) {
            return;
        }

        try {
            String usuario = result.getUsuario();
            String pin = result.getPin();

            Usuario usuarioLogueado = services.authService.loginCompleto(usuario, pin);
            completarContextoPostAutenticacion(usuarioLogueado);
            continuarPostLogin();

        } catch (Exception ex) {
            AppContext.clear();
            mostrarLoginLimpio();
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de login",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ===============================
    // LOGIN RÁPIDO
    // ===============================
    private void hacerLoginRapido(LoginRapidoButtonDTO dto) {
        PinDialog dialog = new PinDialog(this, PinDialogMode.LOGIN_RAPIDO, dto.getNombreBoton());
        PinDialogResult result = dialog.showDialog();

        if (result == null) {
            return;
        }

        try {
            Usuario usuarioLogueado = services.authService.loginRapido(dto.getIdUsuario(), result.getPin());
            completarContextoPostAutenticacion(usuarioLogueado);
            continuarPostLogin();

        } catch (Exception ex) {
            AppContext.clear();
            mostrarLoginLimpio();
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ===============================
    // CONTEXTO POST-AUTENTICACIÓN
    // ===============================
    private void completarContextoPostAutenticacion(Usuario usuarioLogueado) {
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario autenticado no puede ser null");
        }

        AppContext.clear();

        if (esCajero(usuarioLogueado)) {
            SesionCajaRefDTO ref = services.sesionCajaService
                    .requireSesionAbiertaPorUsuario(usuarioLogueado.getIdUsuario());

            AppContext.setUsuario(usuarioLogueado);
            AppContext.setSesionCajaActual(ref);
            return;
        }

        AppContext.setUsuario(usuarioLogueado);
    }

    private boolean esCajero(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getNombre() == null) {
            throw new IllegalStateException(
                    "Usuario sin rol válido: " + usuario.getIdUsuario()
            );
        }

        return "CAJERO".equalsIgnoreCase(usuario.getRol().getNombre());
    }

    // ===============================
    // POST-LOGIN
    // ===============================
    private void continuarPostLogin() {
        try {
            PostLoginRouter router = new PostLoginRouter();
            PostLoginDestination destino = router.route();

            this.setVisible(false);

            switch (destino) {
                case ADMIN_PANEL:
                    abrirAdminDashboard();
                    break;

                case OPERATIVE_HOME:
                    abrirOperativeHome();
                    break;

                default:
                    throw new IllegalStateException(
                            "Destino post-login no soportado: " + destino
                    );
            }

        } catch (Exception ex) {
            AppContext.clear();
            mostrarLoginLimpio();
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error post-login",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void abrirAdminDashboard() {
        AdminDashboardFrame dash = new AdminDashboardFrame(
                this::mostrarLoginLimpio,
                services
        );

        dash.setVisible(true);
    }

    private void abrirOperativeHome() {
        try {
            if (!AppContext.hasSesionCajaActual()) {
                throw new IllegalStateException(
                        "El usuario operativo no tiene sesión de caja asignada. " +
                        "Asigna primero una caja desde la gestión de cajas."
                );
            }

            VentasFrame ventas = new VentasFrame(
                    this::mostrarLoginLimpio,
                    this::mostrarLoginLimpio,
                    services
            );

            ventas.setVisible(true);

        } catch (Exception ex) {
            AppContext.clear();
            mostrarLoginLimpio();
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error al abrir Ventas",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void mostrarLoginLimpio() {
        AppContext.clear();
        txtUsuario.setText("");
        txtTicket.setText("");
        cargarBotonesRapidos();

        this.setVisible(true);
        this.toFront();
        this.requestFocus();
    }

    // =========================
    // BOTONES RÁPIDOS
    // =========================
    private void cargarBotonesRapidos() {
        panelBotonesRapidos.removeAll();

        List<LoginRapidoButtonDTO> botones =
                services.sesionCajaService.getBotonesLoginRapido(idCaja);

        for (LoginRapidoButtonDTO dto : botones) {
            JButton boton = crearBotonLoginRapido(dto);
            panelBotonesRapidos.add(boton);
        }

        panelBotonesRapidos.revalidate();
        panelBotonesRapidos.repaint();
    }

    private JButton crearBotonLoginRapido(LoginRapidoButtonDTO dto) {
        JButton btn = new JButton(dto.getNombreBoton());
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(255, 215, 0));
        btn.setFocusPainted(false);

        btn.addActionListener(e -> hacerLoginRapido(dto));

        return btn;
    }

    // =========================
    // BOTONES TECLADO
    // =========================
    private JButton createNumberButton(String number) {
        JButton btn = new JButton(number);
        btn.setFont(new Font("Arial", Font.BOLD, 50));
        btn.addActionListener(e -> txtUsuario.setText(txtUsuario.getText() + number));
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
                f = services.fichajeFacade.ficharEntradaPorUsuario(usuario);
                tipo = "ENTRADA";
            } catch (IllegalStateException ex) {
                f = services.fichajeFacade.ficharSalidaPorUsuario(usuario);
                tipo = "SALIDA";
            }

            txtTicket.setText(generarTicket(f, tipo));
            txtUsuario.setText("");
            cargarBotonesRapidos();

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
                            + ahora.format(fechaFormato)
                            + "<br><span style='font-size: 20px; color: white;'>"
                            + ahora.format(horaFormato)
                            + "</span></div></html>"
            );
        });
        timer.start();
    }
}
