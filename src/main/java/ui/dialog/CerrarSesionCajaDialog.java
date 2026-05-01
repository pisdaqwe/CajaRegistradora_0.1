package ui.dialog;

import app.AppContext;
import dtoS.CajaEstadoDTO;
import dtoS.CierreCajaResumenDTO;
import model.SesionCaja;
import model.Usuario;
import service.AppServices;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class CerrarSesionCajaDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

    private static final Color OK_GREEN = new Color(46, 125, 50);
    private static final Color SELECTED_ORANGE = new Color(189, 110, 65);
    private static final Color OCCUPIED_RED = new Color(140, 56, 56);

    private final AppServices appServices;

    private CajaEstadoDTO cajaSeleccionada;
    private SesionCaja sesionAbierta;
    private Usuario empleadoValidado;
    private CierreCajaResumenDTO resumenCierre;

    private BigDecimal importeContado;
    private BigDecimal importeEsperado;
    private BigDecimal desfase;

    private JPanel panelCajas;
    private ButtonGroup grupoCajas;

    private JTextField txtCodigoEmpleado;
    private JLabel lblEmpleadoValidado;

    private JTextField txtImporteContado;
    private JButton btnComprobar;

    private JPanel panelResumen;

    private JButton btnConfirmar;

    public CerrarSesionCajaDialog(Window owner, AppServices appServices) {
        super(owner, I18n.t("cashClose.title"), ModalityType.APPLICATION_MODAL);
        this.appServices = appServices;

        buildUI();
        cargarEstadoInicial();

        setSize(1050, 780);
        setMinimumSize(new Dimension(980, 720));
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = transparentPanel(new BorderLayout(14, 0));

        JLabel icon = new JLabel(TpvIconFactory.cashRegister(40, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel texts = transparentPanel(new GridLayout(2, 1, 0, 4));

        JLabel title = new JLabel(I18n.t("cashClose.header.title"));
        title.setFont(InformeUiTheme.FONT_TITLE);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("cashClose.header.subtitle"));
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.ACCENT_GOLD);

        texts.add(title);
        texts.add(subtitle);

        panel.add(icon, BorderLayout.WEST);
        panel.add(texts, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel center = transparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.weighty = 0.28;
        gbc.insets = new Insets(0, 0, 10, 0);
        center.add(buildCajasPanel(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.18;
        gbc.insets = new Insets(0, 0, 10, 0);
        center.add(buildEmpleadoPanel(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.16;
        gbc.insets = new Insets(0, 0, 10, 0);
        center.add(buildConteoPanel(), gbc);

        gbc.gridy = 3;
        gbc.weighty = 0.38;
        gbc.insets = new Insets(0, 0, 0, 0);
        center.add(buildResumenPanel(), gbc);

        return center;
    }

    private JComponent buildCajasPanel() {
        JPanel wrapper = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("cashClose.openSessions.title"));
        title.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        wrapper.add(title, BorderLayout.NORTH);

        panelCajas = new JPanel();
        panelCajas.setOpaque(false);
        panelCajas.setBorder(new EmptyBorder(4, 4, 4, 4));

        wrapper.add(panelCajas, BorderLayout.CENTER);

        return wrapper;
    }

    private JComponent buildEmpleadoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCodigo = InformeUiTheme.createFieldLabel(I18n.t("cashClose.employeeCode"));
        lblCodigo.setIcon(TpvIconFactory.key(16, InformeUiTheme.TEXT_SECONDARY));
        lblCodigo.setIconTextGap(6);

        txtCodigoEmpleado = new JTextField(10);
        InformeUiTheme.styleTextField(txtCodigoEmpleado);
        txtCodigoEmpleado.setPreferredSize(new Dimension(120, 38));
        txtCodigoEmpleado.setEditable(false);
        txtCodigoEmpleado.setEnabled(false);

        txtCodigoEmpleado.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (txtCodigoEmpleado.isEnabled()) {
                    abrirTecladoEmpleado();
                }
            }
        });

        lblEmpleadoValidado = new JLabel(" ");
        lblEmpleadoValidado.setFont(InformeUiTheme.FONT_BODY);
        lblEmpleadoValidado.setForeground(InformeUiTheme.TEXT_SECONDARY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblCodigo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        panel.add(txtCodigoEmpleado, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        panel.add(lblEmpleadoValidado, gbc);

        return panel;
    }

    private JComponent buildConteoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblImporte = InformeUiTheme.createFieldLabel(I18n.t("cashClose.countedAmount"));
        lblImporte.setIcon(TpvIconFactory.cashRegister(16, InformeUiTheme.TEXT_SECONDARY));
        lblImporte.setIconTextGap(6);

        txtImporteContado = new JTextField(10);
        InformeUiTheme.styleTextField(txtImporteContado);
        txtImporteContado.setPreferredSize(new Dimension(140, 38));
        txtImporteContado.setEnabled(false);

        btnComprobar = new JButton(I18n.t("cashClose.checkCount"));
        InformeUiTheme.stylePrimaryButton(btnComprobar);
        btnComprobar.setPreferredSize(new Dimension(150, 42));
        btnComprobar.setIcon(TpvIconFactory.check(18, Color.WHITE));
        btnComprobar.setIconTextGap(8);
        btnComprobar.setEnabled(false);
        btnComprobar.addActionListener(e -> abrirDialogoConteo());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblImporte, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        panel.add(txtImporteContado, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(btnComprobar, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(Box.createHorizontalGlue(), gbc);

        return panel;
    }

    private JComponent buildResumenPanel() {
        panelResumen = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));
        panelResumen.setVisible(false);
        return panelResumen;
    }

    private JComponent buildFooter() {
        JPanel footer = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.setPreferredSize(new Dimension(140, 48));
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        btnCancelar.addActionListener(e -> dispose());

        btnConfirmar = new JButton(I18n.t("cashClose.confirmButton"));
        InformeUiTheme.styleDangerButton(btnConfirmar);
        btnConfirmar.setPreferredSize(new Dimension(190, 48));
        btnConfirmar.setIcon(TpvIconFactory.warning(18, Color.WHITE));
        btnConfirmar.setIconTextGap(8);
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmarCierreCaja());

        footer.add(btnCancelar);
        footer.add(btnConfirmar);

        return footer;
    }

    private void cargarEstadoInicial() {
        panelCajas.removeAll();
        grupoCajas = new ButtonGroup();
        cajaSeleccionada = null;

        List<CajaEstadoDTO> cajasOcupadas = appServices.sesionCajaService
                .getEstadoCajas()
                .stream()
                .filter(CajaEstadoDTO::isOcupada)
                .toList();

        if (cajasOcupadas.isEmpty()) {
            panelCajas.setLayout(new BorderLayout());

            JLabel lbl = new JLabel(I18n.t("cashClose.openSessions.empty"), SwingConstants.CENTER);
            lbl.setForeground(InformeUiTheme.TEXT_SECONDARY);
            lbl.setFont(InformeUiTheme.FONT_BODY);
            lbl.setIcon(TpvIconFactory.info(18, InformeUiTheme.TEXT_SECONDARY));
            lbl.setIconTextGap(8);

            panelCajas.add(lbl, BorderLayout.CENTER);

        } else {
            int total = cajasOcupadas.size();
            int columnas = calcularColumnasCajas(total);
            int filas = (int) Math.ceil(total / (double) columnas);

            panelCajas.setLayout(new GridLayout(filas, columnas, 10, 10));

            for (CajaEstadoDTO caja : cajasOcupadas) {
                JToggleButton btn = crearBotonCaja(caja, columnas);
                grupoCajas.add(btn);
                panelCajas.add(btn);
            }
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }

    private int calcularColumnasCajas(int totalCajas) {
        if (totalCajas <= 1) {
            return 1;
        }

        if (totalCajas == 2) {
            return 2;
        }

        if (totalCajas == 3) {
            return 3;
        }

        return 2;
    }

    private JToggleButton crearBotonCaja(CajaEstadoDTO caja, int columnas) {
        JToggleButton btn = new JToggleButton();

        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(InformeUiTheme.createInnerCardBorder());
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setIconTextGap(5);
        btn.setBackground(OCCUPIED_RED);

        if (columnas >= 3) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(150, 70));
        } else {
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setPreferredSize(new Dimension(220, 76));
        }

        String nombreCaja = safe(caja.getNombreCaja());
        String empleado = safe(caja.getEmpleadoAsignado());

        btn.setText(
                "<html><center><b>" + nombreCaja + "</b><br>"
                        + I18n.t("cashClose.openSessions.occupied")
                        + (empleado.isBlank()
                        ? ""
                        : "<br><span style='font-size:10px;'>(" + empleado + ")</span>")
                        + "</center></html>"
        );

        btn.setIcon(TpvIconFactory.warning(16, Color.WHITE));
        btn.setToolTipText(nombreCaja + (empleado.isBlank() ? "" : " - " + empleado));

        btn.addActionListener(e -> onCajaSeleccionada(caja, btn));

        return btn;
    }

    private void onCajaSeleccionada(CajaEstadoDTO caja, JToggleButton botonSeleccionado) {
        this.cajaSeleccionada = caja;

        for (Component c : panelCajas.getComponents()) {
            if (c instanceof JToggleButton btn) {
                btn.setBackground(OCCUPIED_RED);
            }
        }

        botonSeleccionado.setBackground(SELECTED_ORANGE);

        sesionAbierta = null;
        empleadoValidado = null;
        resumenCierre = null;

        importeContado = null;
        importeEsperado = null;
        desfase = null;

        lblEmpleadoValidado.setText(" ");
        lblEmpleadoValidado.setIcon(null);
        lblEmpleadoValidado.setForeground(InformeUiTheme.TEXT_SECONDARY);

        txtCodigoEmpleado.setText("");
        txtCodigoEmpleado.setEnabled(true);

        txtImporteContado.setText("");
        txtImporteContado.setEnabled(false);

        btnComprobar.setEnabled(false);
        btnConfirmar.setEnabled(false);

        panelResumen.setVisible(false);
        panelResumen.removeAll();
        panelResumen.revalidate();
        panelResumen.repaint();
    }

    private void abrirTecladoEmpleado() {
        Window owner = SwingUtilities.getWindowAncestor(this);

        JFrame parentFrame = owner instanceof JFrame
                ? (JFrame) owner
                : null;

        PinDialog dialog = new PinDialog(
                parentFrame,
                PinDialog.PinDialogMode.LOGIN_RAPIDO,
                I18n.t("cashClose.sessionEmployee")
        );

        PinDialogResult result = dialog.showDialog();
        if (result == null) {
            return;
        }

        String codigo = result.getPin();
        if (codigo == null || codigo.isBlank()) {
            return;
        }

        txtCodigoEmpleado.setText(codigo);
        validarEmpleadoSesion(codigo);
    }

    private void validarEmpleadoSesion(String codigo) {
        try {
            if (cajaSeleccionada == null) {
                throw new IllegalStateException(I18n.t("cashClose.validation.selectCashBoxFirst"));
            }

            if (sesionAbierta == null) {
                sesionAbierta = appServices.sesionCajaService
                        .getSesionAbiertaOrThrow(cajaSeleccionada.getIdCaja());
            }

            Usuario usuario = appServices.usuarioService.findByCodigo(codigo)
                    .orElseThrow(() -> new IllegalArgumentException(I18n.t("cashClose.validation.invalidEmployeeCode")));

            if (usuario.getIdUsuario() != sesionAbierta.getIdUsuarioApertura()) {
                throw new IllegalStateException(I18n.t("cashClose.validation.employeeMismatch"));
            }

            empleadoValidado = usuario;

            lblEmpleadoValidado.setText(
                    I18n.t(
                            "cashClose.employeeValidated",
                            empleadoValidado.getNombre(),
                            empleadoValidado.getUsuario()
                    )
            );
            lblEmpleadoValidado.setIcon(TpvIconFactory.check(16, OK_GREEN));
            lblEmpleadoValidado.setIconTextGap(6);
            lblEmpleadoValidado.setForeground(OK_GREEN);

            txtImporteContado.setEnabled(true);
            btnComprobar.setEnabled(true);

        } catch (Exception ex) {
            empleadoValidado = null;
            txtCodigoEmpleado.setText("");

            lblEmpleadoValidado.setText(ex.getMessage());
            lblEmpleadoValidado.setIcon(TpvIconFactory.cancel(16, InformeUiTheme.DANGER));
            lblEmpleadoValidado.setIconTextGap(6);
            lblEmpleadoValidado.setForeground(InformeUiTheme.DANGER);

            TpvDialogUtils.showError(
                    this,
                    I18n.t("cashClose.employeeValidationTitle"),
                    ex.getMessage()
            );
        }
    }

    private void abrirDialogoConteo() {
        BigDecimal cantidadIntroducida = new ConteoCajaDialog(this).showDialog();
        if (cantidadIntroducida == null) {
            return;
        }

        importeContado = cantidadIntroducida;
        txtImporteContado.setText(MONEY.format(importeContado));

        resumenCierre = appServices.sesionCajaService.calcularResumenCierre(sesionAbierta.getIdSesion());
        importeEsperado = resumenCierre.getEfectivoEsperado();
        desfase = importeContado.subtract(importeEsperado);

        mostrarResultadoConteo();
        btnConfirmar.setEnabled(true);
    }

    private void mostrarResultadoConteo() {
        panelResumen.removeAll();
        panelResumen.setLayout(new BorderLayout(8, 8));

        boolean sinDesfase = desfase.compareTo(BigDecimal.ZERO) == 0;

        JLabel title = InformeUiTheme.createSectionTitle(
                sinDesfase
                        ? I18n.t("cashClose.summary.title")
                        : I18n.t("cashClose.summary.titleMismatch")
        );

        title.setIcon(sinDesfase
                ? TpvIconFactory.check(20, OK_GREEN)
                : TpvIconFactory.warning(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        JTextArea areaTicket = new JTextArea(generarTicketCierre());
        areaTicket.setEditable(false);
        areaTicket.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaTicket.setBackground(InformeUiTheme.CARD_BG_2);
        areaTicket.setForeground(
                sinDesfase
                        ? InformeUiTheme.TEXT_PRIMARY
                        : new Color(255, 190, 190)
        );
        areaTicket.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        areaTicket.setBorder(InformeUiTheme.createInnerCardBorder());

        JScrollPane scroll = new JScrollPane(areaTicket);
        InformeUiTheme.styleScrollPane(scroll);

        panelResumen.add(title, BorderLayout.NORTH);
        panelResumen.add(scroll, BorderLayout.CENTER);
        panelResumen.setVisible(true);
        panelResumen.revalidate();
        panelResumen.repaint();
    }

    private String generarTicketCierre() {
        StringBuilder sb = new StringBuilder();

        sb.append(I18n.t("cashClose.ticket.title")).append("\n");
        sb.append("================================\n");
        sb.append(I18n.t("cashClose.ticket.cashBox")).append(": ").append(cajaSeleccionada.getNombreCaja()).append("\n");
        sb.append(I18n.t("cashClose.ticket.sessionEmployee")).append(": ")
                .append(empleadoValidado.getNombre())
                .append(" (").append(empleadoValidado.getUsuario()).append(")\n");
        sb.append(I18n.t("cashClose.ticket.opening")).append(": ").append(sesionAbierta.getFechaApertura()).append("\n");
        sb.append("--------------------------------\n");
        sb.append(I18n.t("cashClose.ticket.initialAmount")).append(":      ").append(fmt(resumenCierre.getImporteInicial())).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.cashSales")).append(":      ").append(fmt(resumenCierre.getVentasEfectivo())).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.cashReturns")).append(":       -").append(fmt(resumenCierre.getDevolucionesEfectivo())).append(" €\n");
        sb.append("--------------------------------\n");
        sb.append(I18n.t("cashClose.ticket.expectedCash")).append(":    ").append(fmt(resumenCierre.getEfectivoEsperado())).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.countedCash")).append(":     ").append(fmt(importeContado)).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.difference")).append(":              ").append(fmt(desfase)).append(" €\n");
        sb.append("--------------------------------\n");
        sb.append(I18n.t("cashClose.ticket.cardSales")).append(":       ").append(fmt(resumenCierre.getVentasTarjeta())).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.cardReturns")).append(":        -").append(fmt(resumenCierre.getDevolucionesTarjeta())).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.netCard")).append(":         ").append(fmt(resumenCierre.getTarjetaNeta())).append(" €\n");
        sb.append("--------------------------------\n");
        sb.append(I18n.t("cashClose.ticket.totalSales")).append(":         ").append(fmt(resumenCierre.getTotalVentas())).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.totalReturns")).append(":  -").append(fmt(resumenCierre.getTotalDevoluciones())).append(" €\n");
        sb.append(I18n.t("cashClose.ticket.netTotal")).append(":           ").append(fmt(resumenCierre.getTotalNeto())).append(" €\n");

        return sb.toString();
    }

    private String fmt(BigDecimal value) {
        return MONEY.format(value == null ? BigDecimal.ZERO : value);
    }

    private void confirmarCierreCaja() {
        if (cajaSeleccionada == null) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("cashClose.title"),
                    I18n.t("cashClose.validation.selectCashBoxFirst")
            );
            return;
        }

        if (empleadoValidado == null) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("cashClose.title"),
                    I18n.t("cashClose.validation.validateEmployeeFirst")
            );
            return;
        }

        if (importeContado == null || resumenCierre == null) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("cashClose.title"),
                    I18n.t("cashClose.validation.checkCountFirst")
            );
            return;
        }

        boolean confirm = TpvDialogUtils.confirm(
                this,
                I18n.t("cashClose.confirmTitle"),
                I18n.t("cashClose.confirmMessage")
        );

        if (!confirm) {
            return;
        }

        try {
            appServices.cajaFacade.cerrarSesionCaja(
                    cajaSeleccionada,
                    importeContado,
                    generarTicketCierre(),
                    AppContext.getUsuarioId()
            );

            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("cashClose.success.title"),
                    I18n.t("cashClose.success.message")
            );

            dispose();

        } catch (Exception ex) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("cashClose.error.title"),
                    ex.getMessage()
            );
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private JPanel transparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private JPanel transparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }
}