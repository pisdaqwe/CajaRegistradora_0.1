package ui.dialog;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import app.AppContext;
import dtoS.CajaEstadoDTO;
import dtoS.CierreCajaResumenDTO;
import model.SesionCaja;
import model.Usuario;
import service.AppServices;

public class CerrarSesionCajaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // ==================================================
    // Infraestructura
    // ==================================================
    private final AppServices appServices;

    // ==================================================
    // Estado de negocio (flujo)
    // ==================================================
    private CajaEstadoDTO cajaSeleccionada;
    private SesionCaja sesionAbierta;
    private Usuario empleadoValidado;
    private CierreCajaResumenDTO resumenCierre;

    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

    private BigDecimal importeContado;
    private BigDecimal importeEsperado;
    private BigDecimal desfase;

    // ==================================================
    // Componentes UI
    // ==================================================

    // --- Cajas ---
    private JPanel panelCajas;
    private ButtonGroup grupoCajas;

    private static final Color COLOR_CAJA_OCUPADA = new Color(170, 60, 60);
    private static final Color COLOR_CAJA_SELECCIONADA = new Color(210, 120, 60);

    // --- Empleado ---
    private JTextField txtCodigoEmpleado;
    private JLabel lblEmpleadoValidado;

    // --- Conteo ---
    private JTextField txtImporteContado;
    private JButton btnComprobar;

    // --- Mini-ticket ---
    private JPanel panelResumen;

    // --- Footer ---
    private JButton btnConfirmar;
    private JButton btnCancelar;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================
    public CerrarSesionCajaDialog(Window owner, AppServices appServices) {
        super(owner, "Cerrar sesión de caja", ModalityType.APPLICATION_MODAL);
        this.appServices = appServices;

        buildUI();
        cargarEstadoInicial();

        setSize(900, 650);
        setLocationRelativeTo(owner);
    }

    // ==================================================
    // CONSTRUCCIÓN UI
    // ==================================================

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(25, 25, 25));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JLabel titulo = new JLabel("CERRAR SESIÓN DE CAJA");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        return titulo;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        center.add(buildCajasPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(buildEmpleadoPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(buildConteoPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(buildResumenPanel());

        return center;
    }

    // ==================================================
    // PANEL CAJAS
    // ==================================================

    private JComponent buildCajasPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder("Cajas con sesión abierta"));
        wrapper.setOpaque(false);

        panelCajas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelCajas.setOpaque(false);

        wrapper.add(panelCajas, BorderLayout.CENTER);
        return wrapper;
    }

    // ==================================================
    // PANEL EMPLEADO
    // ==================================================

    private JComponent buildEmpleadoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Validación del empleado"));
        panel.setOpaque(false);

        txtCodigoEmpleado = new JTextField(10);
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
        lblEmpleadoValidado.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblEmpleadoValidado.setForeground(Color.GRAY);

        panel.add(new JLabel("Código empleado:"));
        panel.add(txtCodigoEmpleado);
        panel.add(lblEmpleadoValidado);

        return panel;
    }

    // ==================================================
    // PANEL CONTEO
    // ==================================================

    private JComponent buildConteoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Conteo de caja"));
        panel.setOpaque(false);

        txtImporteContado = new JTextField(10);
        txtImporteContado.setEnabled(false);

        btnComprobar = new JButton("Comprobar");
        btnComprobar.setEnabled(false);
        btnComprobar.addActionListener(e -> abrirDialogoConteo());

        panel.add(new JLabel("Importe contado (€):"));
        panel.add(txtImporteContado);
        panel.add(btnComprobar);

        return panel;
    }

    // ==================================================
    // PANEL RESUMEN
    // ==================================================

    private JComponent buildResumenPanel() {
        panelResumen = new JPanel();
        panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen de cierre"));
        panelResumen.setOpaque(false);
        panelResumen.setVisible(false);
        return panelResumen;
    }

    // ==================================================
    // FOOTER
    // ==================================================

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        btnConfirmar = new JButton("Confirmar cierre");
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmarCierreCaja());

        footer.add(btnCancelar);
        footer.add(btnConfirmar);
        return footer;
    }

    // ==================================================
    // ESTADO INICIAL
    // ==================================================

    private void cargarEstadoInicial() {
        resetEstadoDialogo();
        cargarCajasOcupadas();
    }

    private void resetEstadoDialogo() {
        cajaSeleccionada = null;
        sesionAbierta = null;
        empleadoValidado = null;
        resumenCierre = null;

        importeContado = null;
        importeEsperado = null;
        desfase = null;

        txtCodigoEmpleado.setText("");
        txtCodigoEmpleado.setEnabled(false);

        txtImporteContado.setText("");
        txtImporteContado.setEnabled(false);

        btnComprobar.setEnabled(false);
        btnConfirmar.setEnabled(false);

        panelResumen.setVisible(false);
        lblEmpleadoValidado.setText(" ");
    }

    // ==================================================
    // CARGA Y SELECCIÓN DE CAJAS
    // ==================================================

    private void cargarCajasOcupadas() {
        panelCajas.removeAll();
        grupoCajas = new ButtonGroup();

        appServices.sesionCajaService.getEstadoCajas()
                .stream()
                .filter(CajaEstadoDTO::isOcupada)
                .forEach(caja -> {
                    JToggleButton btn = crearBotonCaja(caja);
                    grupoCajas.add(btn);
                    panelCajas.add(btn);
                });

        panelCajas.revalidate();
        if (panelCajas.getComponentCount() == 0) {
            JLabel lbl = new JLabel("No hay sesiones de caja abiertas");
            lbl.setForeground(Color.LIGHT_GRAY);
            panelCajas.add(lbl);
        }

        panelCajas.repaint();
    }

    private JToggleButton crearBotonCaja(CajaEstadoDTO caja) {
        JToggleButton btn = new JToggleButton();
        btn.setPreferredSize(new Dimension(170, 75));
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_CAJA_OCUPADA);

        String texto = "<html><center><b>" + caja.getNombreCaja() + "</b><br/>"
                + "Ocupada<br/>("
                + caja.getEmpleadoAsignado() + ")</center></html>";
        btn.setText(texto);

        btn.addActionListener(e -> onCajaSeleccionada(caja, btn));
        return btn;
    }

    private void onCajaSeleccionada(CajaEstadoDTO caja, JToggleButton botonSeleccionado) {
        this.cajaSeleccionada = caja;

        for (Component c : panelCajas.getComponents()) {
            if (c instanceof JToggleButton btn) {
                btn.setBackground(COLOR_CAJA_OCUPADA);
            }
        }
        botonSeleccionado.setBackground(COLOR_CAJA_SELECCIONADA);

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

    // ==================================================
    // VALIDACIÓN EMPLEADO
    // ==================================================

    private void abrirTecladoEmpleado() {
        PinDialogResult result = abrirDialogoPin();
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

    private PinDialogResult abrirDialogoPin() {
        PinDialog dialog = new PinDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                PinDialog.PinDialogMode.LOGIN_RAPIDO,
                "Empleado sesión"
        );
        return dialog.showDialog();
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
            mostrarEmpleadoValidado();
            habilitarConteoCaja();

        } catch (Exception ex) {
            empleadoValidado = null;
            txtCodigoEmpleado.setText("");
            lblEmpleadoValidado.setText("✖ " + ex.getMessage());
            lblEmpleadoValidado.setForeground(Color.RED);

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validación de empleado",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void mostrarEmpleadoValidado() {
        lblEmpleadoValidado.setText(
                "✔ Empleado validado: " + empleadoValidado.getNombre()
                        + " (" + empleadoValidado.getUsuario() + ")"
        );
        lblEmpleadoValidado.setForeground(new Color(0, 180, 0));
    }

    private void habilitarConteoCaja() {
        txtImporteContado.setEnabled(true);
        btnComprobar.setEnabled(true);
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
        panelResumen.setLayout(new BorderLayout());

        JTextArea areaTicket = new JTextArea(generarTicketCierre());
        areaTicket.setEditable(false);
        areaTicket.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaTicket.setBackground(new Color(245, 245, 245));
        areaTicket.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (desfase.compareTo(BigDecimal.ZERO) != 0) {
            areaTicket.setForeground(Color.RED);
            panelResumen.setBorder(
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(Color.RED, 2),
                            "Resumen de cierre (DESCUADRE)"
                    )
            );
        } else {
            areaTicket.setForeground(new Color(0, 140, 0));
            panelResumen.setBorder(
                    BorderFactory.createTitledBorder("Resumen de cierre")
            );
        }

        JScrollPane scroll = new JScrollPane(areaTicket);
        scroll.setBorder(null);

        panelResumen.add(scroll, BorderLayout.CENTER);
        panelResumen.setVisible(true);

        panelResumen.revalidate();
        panelResumen.repaint();
    }

    private String generarTicketCierre() {
        StringBuilder sb = new StringBuilder();

        sb.append("CIERRE DE CAJA\n");
        sb.append("================================\n");

        sb.append("Caja: ")
          .append(cajaSeleccionada.getNombreCaja())
          .append("\n");

        sb.append("Empleado sesión: ")
          .append(empleadoValidado.getNombre())
          .append(" (").append(empleadoValidado.getUsuario()).append(")\n");

        sb.append("Apertura: ")
          .append(sesionAbierta.getFechaApertura())
          .append("\n");

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
            JOptionPane.showMessageDialog(
                    this,
                    "Debe comprobar el conteo antes de cerrar la caja.",
                    "Cierre de caja",
                    JOptionPane.WARNING_MESSAGE
            );
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

            JOptionPane.showMessageDialog(
                    this,
                    "Caja cerrada correctamente",
                    "Cierre de caja",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error al cerrar caja",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}