package ui.screens;

import app.AppContext;
import dtoS.LoginRapidoButtonDTO;
import dtoS.SesionCajaRefDTO;
import enums.PostLoginDestination;
import model.Fichaje;
import model.Usuario;
import service.AppServices;
import ui.common.TpvDialogUtils;
import ui.dialog.PinDialog;
import ui.dialog.PinDialog.PinDialogMode;
import ui.dialog.PinDialogResult;
import ui.router.PostLoginRouter;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoginScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Font FONT_BRAND = new Font("SansSerif", Font.BOLD, 34);
    private static final Font FONT_INFO_TITLE = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_INFO_BODY = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font FONT_CLOCK = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 30);
    private static final Font FONT_KEYPAD = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_KEYPAD_SPECIAL = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_QUICK_LOGIN = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_ACTION = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_TICKET = new Font("Monospaced", Font.PLAIN, 13);

    private final int idCaja;
    private final AppServices services;

    private JTextField txtUsuario;
    private JTextArea txtTicket;
    private JPanel panelBotonesRapidos;

    public LoginScreen(AppServices services, int idCaja) {
        this.services = services;
        this.idCaja = idCaja;

        initUI();
        cargarBotonesRapidos();
    }

    private void initUI() {
        setTitle("TPV - Identificación");
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(18, 24, 24, 24));
        setContentPane(root);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildMainContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel topBar = createTransparentPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel brandPanel = createTransparentPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));

        JLabel lblMarca = new JLabel("TPV Cafetería");
        lblMarca.setFont(FONT_BRAND);
        lblMarca.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Acceso de empleados · Terminal " + AppContext.getNombreCajaTerminal());
        lblSub.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSub.setForeground(InformeUiTheme.TEXT_SECONDARY);

        brandPanel.add(lblMarca);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(lblSub);

        JLabel lblReloj = new JLabel("", SwingConstants.RIGHT);
        lblReloj.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lblReloj.setFont(FONT_CLOCK);

        topBar.add(brandPanel, BorderLayout.WEST);
        topBar.add(lblReloj, BorderLayout.EAST);

        iniciarReloj(lblReloj);

        return topBar;
    }

    private JPanel buildMainContent() {
        JPanel content = createTransparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.95;
        gbc.insets = new Insets(0, 0, 0, 18);
        content.add(buildLeftInfoPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.25;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buildRightLoginCard(), gbc);

        return content;
    }

    private JPanel buildLeftInfoPanel() {
        JPanel wrapper = createTransparentPanel(new BorderLayout());

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 16));
        card.setPreferredSize(new Dimension(430, 620));

        JPanel header = createTransparentPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Bienvenido");
        lblTitle.setFont(FONT_INFO_TITLE);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblBody = new JLabel(
                "<html>"
                        + "Identifícate para <b>fichar</b> o acceder al <b>TPV</b>.<br><br>"
                        + "Cada terminal trabaja sobre una caja concreta.<br>"
                        + "Usa tu código de empleado o selecciona un acceso rápido."
                        + "</html>"
        );
        lblBody.setFont(FONT_INFO_BODY);
        lblBody.setForeground(InformeUiTheme.TEXT_SECONDARY);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(10));
        header.add(lblBody);

        JPanel terminalCard = new JPanel();
        terminalCard.setBackground(InformeUiTheme.PANEL_BG);
        terminalCard.setBorder(InformeUiTheme.createInnerCardBorder());
        terminalCard.setLayout(new BoxLayout(terminalCard, BoxLayout.Y_AXIS));
        terminalCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        terminalCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        JLabel lblTerminal = new JLabel("Terminal actual");
        lblTerminal.setFont(InformeUiTheme.FONT_LABEL);
        lblTerminal.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JLabel lblCaja = new JLabel(AppContext.getNombreCajaTerminal());
        lblCaja.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblCaja.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblHint = new JLabel(
                "<html>Partner Sign-In muestra los empleados disponibles<br>"
                        + "para esta caja.</html>"
        );
        lblHint.setFont(InformeUiTheme.FONT_BODY);
        lblHint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        terminalCard.add(lblTerminal);
        terminalCard.add(Box.createVerticalStrut(6));
        terminalCard.add(lblCaja);
        terminalCard.add(Box.createVerticalStrut(10));
        terminalCard.add(lblHint);

        JPanel tipsCard = new JPanel();
        tipsCard.setBackground(InformeUiTheme.CARD_BG_2);
        tipsCard.setBorder(InformeUiTheme.createInnerCardBorder());
        tipsCard.setLayout(new BoxLayout(tipsCard, BoxLayout.Y_AXIS));
        tipsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        tipsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        tipsCard.add(createTipLabel("• Clock In / Out para registrar entrada o salida"));
        tipsCard.add(Box.createVerticalStrut(8));
        tipsCard.add(createTipLabel("• Sign In para acceder al sistema"));
        tipsCard.add(Box.createVerticalStrut(8));
        tipsCard.add(createTipLabel("• El teclado numérico permite introducir tu código"));
        tipsCard.add(Box.createVerticalStrut(8));
        tipsCard.add(createTipLabel("• El ticket inferior muestra el último fichaje realizado"));

        JPanel quickLoginPanel = buildQuickLoginPanel();
        quickLoginPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickLoginPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel centerStack = createTransparentPanel();
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));

        centerStack.add(terminalCard);
        centerStack.add(Box.createVerticalStrut(14));
        centerStack.add(quickLoginPanel);
        centerStack.add(Box.createVerticalStrut(14));
        centerStack.add(tipsCard);
        centerStack.add(Box.createVerticalGlue());

        card.add(header, BorderLayout.NORTH);
        card.add(centerStack, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildRightLoginCard() {
        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 14));
        card.setPreferredSize(new Dimension(620, 640));

        card.add(buildDisplayPanel(), BorderLayout.NORTH);
        card.add(buildCenterArea(), BorderLayout.CENTER);
        card.add(buildTicketPanel(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildDisplayPanel() {
        JPanel panel = createTransparentPanel(new BorderLayout(0, 10));

        JLabel lbl = new JLabel("Código de empleado");
        lbl.setFont(InformeUiTheme.FONT_LABEL);
        lbl.setForeground(InformeUiTheme.TEXT_SECONDARY);

        txtUsuario = new JTextField();
        txtUsuario.setEditable(false);
        txtUsuario.setHorizontalAlignment(JTextField.CENTER);
        txtUsuario.setFont(FONT_DISPLAY);
        txtUsuario.setBackground(InformeUiTheme.CARD_BG_2);
        txtUsuario.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtUsuario.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtUsuario.setBorder(createInputBorder());
        txtUsuario.setPreferredSize(new Dimension(100, 62));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(txtUsuario, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCenterArea() {
        JPanel center = createTransparentPanel(new BorderLayout(16, 0));
        center.add(buildKeypadPanel(), BorderLayout.CENTER);
        center.add(buildRightActionColumn(), BorderLayout.EAST);
        return center;
    }

 
    private JPanel buildQuickLoginPanel() {
        JPanel wrapper = createTransparentPanel(new BorderLayout(0, 10));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Partner Sign-In");
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Accesos rápidos disponibles para esta caja");
        lblSub.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSub.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel titlePanel = createTransparentPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblSub);

        panelBotonesRapidos = new JPanel();
        panelBotonesRapidos.setOpaque(false);

        JScrollPane scroll = new JScrollPane(panelBotonesRapidos);
        scroll.setBorder(InformeUiTheme.createInnerCardBorder());
        scroll.getViewport().setBackground(InformeUiTheme.CARD_BG_2);
        scroll.setBackground(InformeUiTheme.CARD_BG_2);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        scroll.setPreferredSize(new Dimension(100, 150));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }
    
    private JPanel buildKeypadPanel() {
        JPanel wrapper = createTransparentPanel(new BorderLayout(0, 10));
        wrapper.setPreferredSize(new Dimension(0, 380));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JLabel lblTitle = new JLabel("Teclado numérico");
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JPanel keypadCard = new JPanel(new GridLayout(4, 3, 10, 10));
        keypadCard.setBackground(InformeUiTheme.CARD_BG_2);
        keypadCard.setBorder(InformeUiTheme.createInnerCardBorder());
        keypadCard.setPreferredSize(new Dimension(0, 330));

        for (int i = 1; i <= 9; i++) {
            keypadCard.add(createNumberButton(String.valueOf(i)));
        }

        keypadCard.add(createClearButton());
        keypadCard.add(createNumberButton("0"));
        keypadCard.add(createBackButton());

        wrapper.add(lblTitle, BorderLayout.NORTH);
        wrapper.add(keypadCard, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel buildRightActionColumn() {
        JPanel right = new JPanel(new BorderLayout(0, 14));
        right.setBackground(InformeUiTheme.PANEL_BG);
        right.setBorder(InformeUiTheme.createInnerCardBorder());
        right.setPreferredSize(new Dimension(185, 100));

        JLabel lbl = new JLabel("<html><center>Acciones<br>principales</center></html>", SwingConstants.CENTER);
        lbl.setFont(InformeUiTheme.FONT_SECTION);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JButton btnClock = new JButton("Clock In / Out");
        InformeUiTheme.styleSecondaryButton(btnClock);
        btnClock.setFont(FONT_ACTION);
        btnClock.setPreferredSize(new Dimension(100, 64));
        btnClock.addActionListener(e -> fichar());

        JButton btnSignIn = new JButton("Sign In");
        InformeUiTheme.stylePrimaryButton(btnSignIn);
        btnSignIn.setFont(FONT_ACTION);
        btnSignIn.setPreferredSize(new Dimension(100, 64));
        btnSignIn.addActionListener(e -> hacerLoginCompleto());

        JPanel actions = createTransparentPanel(new GridLayout(2, 1, 0, 12));
        actions.add(btnClock);
        actions.add(btnSignIn);

        JLabel lblHint = new JLabel(
                "<html><center>Introduce tu código<br>o usa un acceso rápido</center></html>",
                SwingConstants.CENTER
        );
        lblHint.setFont(InformeUiTheme.FONT_BODY);
        lblHint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        right.add(lbl, BorderLayout.NORTH);
        right.add(actions, BorderLayout.CENTER);
        right.add(lblHint, BorderLayout.SOUTH);

        return right;
    }

    private JPanel buildTicketPanel() {
        JPanel wrapper = createTransparentPanel(new BorderLayout(0, 8));
        wrapper.setPreferredSize(new Dimension(0, 150));

        JLabel lblTitle = new JLabel("Último fichaje");
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        txtTicket = new JTextArea(4, 20);
        txtTicket.setEditable(false);
        txtTicket.setLineWrap(true);
        txtTicket.setWrapStyleWord(true);
        txtTicket.setFont(FONT_TICKET);
        txtTicket.setBackground(new Color(248, 243, 232));
        txtTicket.setForeground(new Color(35, 35, 35));
        txtTicket.setCaretColor(new Color(35, 35, 35));
        txtTicket.setBorder(new CompoundBorder(
                new LineBorder(new Color(214, 203, 182), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scroll = new JScrollPane(txtTicket);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 243, 232));
        scroll.setPreferredSize(new Dimension(100, 110));

        wrapper.add(lblTitle, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private JLabel createTipLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(InformeUiTheme.FONT_BODY);
        lbl.setForeground(InformeUiTheme.TEXT_SECONDARY);
        return lbl;
    }

    private JPanel createTransparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createTransparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private Border createInputBorder() {
        return new CompoundBorder(
                new LineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        );
    }

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
            TpvDialogUtils.showError(
                    this,
                    "Error post-login",
                    ex.getMessage()
            );
        }
    }

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
            TpvDialogUtils.showError(
                    this,
                    "Error de autenticación",
                    ex.getMessage()
            );
        }
    }

    private void completarContextoPostAutenticacion(Usuario usuarioLogueado) {
        if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario autenticado no puede ser null");
        }

        AppContext.clear();

        if (esCajero(usuarioLogueado)) {
            SesionCajaRefDTO ref = services.sesionCajaService
                    .requireSesionAbiertaPorUsuario(usuarioLogueado.getIdUsuario());

            if (ref.getIdCaja() != AppContext.getIdCajaTerminal()) {
                throw new IllegalStateException(
                        "Tienes una sesión abierta en " + ref.getNombreCaja()
                                + ". Este terminal pertenece a " + AppContext.getNombreCajaTerminal()
                                + ". Debes iniciar sesión en tu caja asignada o pedir reasignación."
                );
            }

            if (ref.getIdSucursal() != AppContext.getIdSucursal()) {
                throw new IllegalStateException(
                        "La sesión de caja no pertenece a la sucursal del terminal actual."
                );
            }

            AppContext.setUsuario(usuarioLogueado);
            AppContext.setSesionCajaActual(ref);
            return;
        }

        if (usuarioLogueado.getIdSucursal() != AppContext.getIdSucursal()) {
            throw new IllegalStateException(
                    "El usuario no pertenece a la sucursal del terminal actual."
            );
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
            TpvDialogUtils.showError(
                    this,
                    "Error post-login",
                    ex.getMessage()
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
            TpvDialogUtils.showError(
                    this,
                    "Error al abrir Ventas",
                    "No se pudo abrir la pantalla de ventas."
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

    private void cargarBotonesRapidos() {
        panelBotonesRapidos.removeAll();

        List<LoginRapidoButtonDTO> botones =
                services.sesionCajaService.getBotonesLoginRapido(idCaja);

        if (botones.isEmpty()) {
            panelBotonesRapidos.setLayout(new BorderLayout());

            JLabel empty = new JLabel(
                    "<html><center>Sin accesos rápidos<br>disponibles</center></html>",
                    SwingConstants.CENTER
            );
            empty.setForeground(InformeUiTheme.TEXT_SECONDARY);
            empty.setFont(InformeUiTheme.FONT_BODY);

            panelBotonesRapidos.add(empty, BorderLayout.CENTER);

        } else {
            int columnas = botones.size() <= 2 ? 1 : 2;
            int filas = (int) Math.ceil(botones.size() / (double) columnas);

            panelBotonesRapidos.setLayout(new GridLayout(filas, columnas, 12, 12));
            panelBotonesRapidos.setBorder(new EmptyBorder(12, 12, 12, 12));

            for (LoginRapidoButtonDTO dto : botones) {
                panelBotonesRapidos.add(crearBotonLoginRapido(dto));
            }

            int altura = Math.min(160, (filas * 70) + 24);
            panelBotonesRapidos.setPreferredSize(new Dimension(100, altura));
        }

        panelBotonesRapidos.revalidate();
        panelBotonesRapidos.repaint();
    }

    private JButton crearBotonLoginRapido(LoginRapidoButtonDTO dto) {
        JButton btn = new JButton(dto.getNombreBoton());
        btn.setFont(FONT_QUICK_LOGIN);
        btn.setFocusPainted(false);
        btn.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        btn.setForeground(InformeUiTheme.TEXT_PRIMARY);
        btn.setBorder(new CompoundBorder(
                new LineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(14, 12, 14, 12)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 58));

        btn.addActionListener(e -> hacerLoginRapido(dto));
        return btn;
    }

    private JButton createNumberButton(String number) {
        JButton btn = new JButton(number);
        btn.setFont(FONT_KEYPAD);
        btn.setFocusPainted(false);
        btn.setBackground(InformeUiTheme.CARD_BG);
        btn.setForeground(InformeUiTheme.TEXT_PRIMARY);
        btn.setBorder(new CompoundBorder(
                new LineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(12, 10, 12, 10)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> txtUsuario.setText(txtUsuario.getText() + number));
        return btn;
    }

    private JButton createClearButton() {
        JButton btn = new JButton("Clear");
        btn.setFont(FONT_KEYPAD_SPECIAL);
        btn.setFocusPainted(false);
        btn.setBackground(InformeUiTheme.DANGER);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(12, 10, 12, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> txtUsuario.setText(""));
        return btn;
    }

    private JButton createBackButton() {
        JButton btn = new JButton("Back");
        btn.setFont(FONT_KEYPAD_SPECIAL);
        btn.setFocusPainted(false);
        btn.setBackground(InformeUiTheme.STARBUCKS_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(12, 10, 12, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            String text = txtUsuario.getText();
            if (!text.isEmpty()) {
                txtUsuario.setText(text.substring(0, text.length() - 1));
            }
        });
        return btn;
    }

    private void fichar() {
        try {
            String usuario = txtUsuario.getText().trim();
            if (usuario.isEmpty()) {
            	TpvDialogUtils.showWarning(
            	        this,
            	        "Código requerido",
            	        "Introduce tu código."
            	);
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
        	TpvDialogUtils.showError(
        	        this,
        	        "Error",
        	        ex.getMessage()
        	);
        }
    }

    private String generarTicket(Fichaje f, String tipo) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("         FICHAJE EMPLEADO\n");
        sb.append("================================\n");
        sb.append("Caja:       ").append(AppContext.getNombreCajaTerminal()).append("\n");
        sb.append("Sucursal:   ").append(AppContext.getIdSucursal()).append("\n");
        sb.append("Tipo:       ").append(tipo).append("\n");
        sb.append("ID Usuario: ").append(f.getIdUsuario()).append("\n");
        sb.append("Entrada:    ").append(f.getFechaEntrada()).append("\n");

        if ("SALIDA".equals(tipo)) {
            sb.append("Salida:     ").append(f.getFechaSalida()).append("\n");
            sb.append("Duración:   ").append(f.getDuracion()).append(" min\n");
        }

        sb.append("================================\n");
        return sb.toString();
    }

    private void iniciarReloj(JLabel label) {
        DateTimeFormatter fechaFormato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM");
        DateTimeFormatter horaFormato = DateTimeFormatter.ofPattern("HH:mm:ss");

        Timer timer = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            label.setText(
                    "<html><div style='text-align: right;'>"
                            + ahora.format(fechaFormato)
                            + "<br><span style='font-size: 22px; font-weight: bold; color: #F5F2EB;'>"
                            + ahora.format(horaFormato)
                            + "</span></div></html>"
            );
        });
        timer.start();
    }
}