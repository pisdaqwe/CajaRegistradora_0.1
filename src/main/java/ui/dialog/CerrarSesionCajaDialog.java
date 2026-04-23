package ui.dialog;

import app.AppContext;
import dtoS.CajaEstadoDTO;
import dtoS.CierreCajaResumenDTO;
import model.SesionCaja;
import model.Usuario;
import service.AppServices;
import ui.common.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CerrarSesionCajaDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

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
        super(owner, "Cerrar sesión de caja", ModalityType.APPLICATION_MODAL);
        this.appServices = appServices;

        buildUI();
        cargarEstadoInicial();

        setSize(980, 890);
        setLocationRelativeTo(owner);
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
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setOpaque(false);

        JLabel title = new JLabel("Cerrar sesión de caja");
        title.setFont(InformeUiTheme.FONT_TITLE);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Valida al empleado, cuenta efectivo y confirma el cierre");
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.ACCENT_GOLD);

        panel.add(title);
        panel.add(subtitle);
        return panel;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(buildCajasPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(buildEmpleadoPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(buildConteoPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(buildResumenPanel());

        return center;
    }

    private JComponent buildCajasPanel() {
        JPanel wrapper = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle("Cajas con sesión abierta");
        wrapper.add(title, BorderLayout.NORTH);

        panelCajas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelCajas.setOpaque(false);

        wrapper.add(panelCajas, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent buildEmpleadoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));

        panel.add(InformeUiTheme.createFieldLabel("Código empleado:"));

        txtCodigoEmpleado = new JTextField(10);
        InformeUiTheme.styleTextField(txtCodigoEmpleado);
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

        panel.add(txtCodigoEmpleado);
        panel.add(lblEmpleadoValidado);

        return panel;
    }

    private JComponent buildConteoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));

        panel.add(InformeUiTheme.createFieldLabel("Importe contado (€):"));

        txtImporteContado = new JTextField(10);
        InformeUiTheme.styleTextField(txtImporteContado);
        txtImporteContado.setEnabled(false);

        btnComprobar = new JButton("Comprobar");
        InformeUiTheme.stylePrimaryButton(btnComprobar);
        btnComprobar.setEnabled(false);
        btnComprobar.addActionListener(e -> abrirDialogoConteo());

        panel.add(txtImporteContado);
        panel.add(btnComprobar);

        return panel;
    }

    private JComponent buildResumenPanel() {
        panelResumen = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));
        panelResumen.setVisible(false);
        return panelResumen;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        btnConfirmar = new JButton("Confirmar cierre");
        InformeUiTheme.styleDangerButton(btnConfirmar);
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

        appServices.sesionCajaService.getEstadoCajas().stream()
                .filter(CajaEstadoDTO::isOcupada)
                .forEach(caja -> {
                    JToggleButton btn = crearBotonCaja(caja);
                    grupoCajas.add(btn);
                    panelCajas.add(btn);
                });

        if (panelCajas.getComponentCount() == 0) {
            JLabel lbl = new JLabel("No hay sesiones de caja abiertas");
            lbl.setForeground(InformeUiTheme.TEXT_SECONDARY);
            panelCajas.add(lbl);
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }

    private JToggleButton crearBotonCaja(CajaEstadoDTO caja) {
        JToggleButton btn = new JToggleButton();
        btn.setPreferredSize(new Dimension(185, 82));
        btn.setFocusPainted(false);
        btn.setForeground(InformeUiTheme.TEXT_PRIMARY);
        btn.setFont(InformeUiTheme.FONT_BODY);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(140, 56, 56));
        btn.setBorder(InformeUiTheme.createInnerCardBorder());

        String texto = "<html><center><b>" + caja.getNombreCaja() + "</b><br/>"
                + "Ocupada<br/>(" + safe(caja.getEmpleadoAsignado()) + ")</center></html>";
        btn.setText(texto);

        btn.addActionListener(e -> onCajaSeleccionada(caja, btn));
        return btn;
    }

    private void onCajaSeleccionada(CajaEstadoDTO caja, JToggleButton botonSeleccionado) {
        this.cajaSeleccionada = caja;

        for (Component c : panelCajas.getComponents()) {
            if (c instanceof JToggleButton btn) {
                btn.setBackground(new Color(140, 56, 56));
            }
        }
        botonSeleccionado.setBackground(new Color(189, 110, 65));

        sesionAbierta = null;
        empleadoValidado = null;
        resumenCierre = null;
        lblEmpleadoValidado.setText(" ");

        txtCodigoEmpleado.setText("");
        txtCodigoEmpleado.setEnabled(true);

        txtImporteContado.setText("");
        txtImporteContado.setEnabled(false);

        btnComprobar.setEnabled(false);
        btnConfirmar.setEnabled(false);

        panelResumen.setVisible(false);
    }

    private void abrirTecladoEmpleado() {
        PinDialog dialog = new PinDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                PinDialog.PinDialogMode.LOGIN_RAPIDO,
                "Empleado sesión"
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
            if (sesionAbierta == null) {
                sesionAbierta = appServices.sesionCajaService
                        .getSesionAbiertaOrThrow(cajaSeleccionada.getIdCaja());
            }

            Usuario usuario = appServices.usuarioService.findByCodigo(codigo)
                    .orElseThrow(() -> new IllegalArgumentException("Código de empleado no válido"));

            if (usuario.getIdUsuario() != sesionAbierta.getIdUsuarioApertura()) {
                throw new IllegalStateException("El empleado no corresponde con la sesión");
            }

            empleadoValidado = usuario;
            lblEmpleadoValidado.setText(
                    "✔ Empleado validado: " + empleadoValidado.getNombre() +
                    " (" + empleadoValidado.getUsuario() + ")"
            );
            lblEmpleadoValidado.setForeground(new Color(105, 197, 125));

            txtImporteContado.setEnabled(true);
            btnComprobar.setEnabled(true);

        } catch (Exception ex) {
            empleadoValidado = null;
            txtCodigoEmpleado.setText("");
            lblEmpleadoValidado.setText("✖ " + ex.getMessage());
            lblEmpleadoValidado.setForeground(InformeUiTheme.DANGER);

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validación de empleado",
                    JOptionPane.ERROR_MESSAGE
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

        JLabel title = InformeUiTheme.createSectionTitle(
                desfase.compareTo(BigDecimal.ZERO) == 0
                        ? "Resumen de cierre"
                        : "Resumen de cierre (descuadre)"
        );

        JTextArea areaTicket = new JTextArea(generarTicketCierre());
        areaTicket.setEditable(false);
        areaTicket.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaTicket.setBackground(InformeUiTheme.CARD_BG_2);
        areaTicket.setForeground(
                desfase.compareTo(BigDecimal.ZERO) == 0
                        ? InformeUiTheme.TEXT_PRIMARY
                        : new Color(255, 190, 190)
        );
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

        sb.append("CIERRE DE CAJA\n");
        sb.append("================================\n");
        sb.append("Caja: ").append(cajaSeleccionada.getNombreCaja()).append("\n");
        sb.append("Empleado sesión: ").append(empleadoValidado.getNombre())
                .append(" (").append(empleadoValidado.getUsuario()).append(")\n");
        sb.append("Apertura: ").append(sesionAbierta.getFechaApertura()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("IMPORTE INICIAL:      ").append(fmt(resumenCierre.getImporteInicial())).append(" €\n");
        sb.append("VENTAS EFECTIVO:      ").append(fmt(resumenCierre.getVentasEfectivo())).append(" €\n");
        sb.append("DEV. EFECTIVO:       -").append(fmt(resumenCierre.getDevolucionesEfectivo())).append(" €\n");
        sb.append("--------------------------------\n");
        sb.append("EFECTIVO ESPERADO:    ").append(fmt(resumenCierre.getEfectivoEsperado())).append(" €\n");
        sb.append("EFECTIVO CONTADO:     ").append(fmt(importeContado)).append(" €\n");
        sb.append("DESFASE:              ").append(fmt(desfase)).append(" €\n");
        sb.append("--------------------------------\n");
        sb.append("VENTAS TARJETA:       ").append(fmt(resumenCierre.getVentasTarjeta())).append(" €\n");
        sb.append("DEV. TARJETA:        -").append(fmt(resumenCierre.getDevolucionesTarjeta())).append(" €\n");
        sb.append("NETO TARJETA:         ").append(fmt(resumenCierre.getTarjetaNeta())).append(" €\n");
        sb.append("--------------------------------\n");
        sb.append("TOTAL VENTAS:         ").append(fmt(resumenCierre.getTotalVentas())).append(" €\n");
        sb.append("TOTAL DEVOLUCIONES:  -").append(fmt(resumenCierre.getTotalDevoluciones())).append(" €\n");
        sb.append("TOTAL NETO:           ").append(fmt(resumenCierre.getTotalNeto())).append(" €\n");

        return sb.toString();
    }

    private String fmt(BigDecimal value) {
        return MONEY.format(value == null ? BigDecimal.ZERO : value);
    }

    private void confirmarCierreCaja() {
        if (importeContado == null || resumenCierre == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe comprobar el conteo antes de cerrar la caja.",
                    "Cierre de caja",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmar el cierre de la caja?\nEsta acción no se puede deshacer.",
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            appServices.cajaFacade.cerrarSesionCaja(
                    cajaSeleccionada,
                    importeContado,
                    generarTicketCierre(),
                    AppContext.getUsuarioId()
            );

            JOptionPane.showMessageDialog(this,
                    "Caja cerrada correctamente",
                    "Cierre de caja",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al cerrar caja",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}