package ui.dialog;

import java.awt.*;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import app.AppContext;
import dtoS.CajaEstadoDTO;
import model.SesionCaja;
import model.Usuario;
import service.AppServices;

/**
 * Diálogo modal para el cierre de una sesión de caja.
 *
 * Flujo:
 * 1) Selección de caja con sesión abierta
 * 2) Validación del empleado que abrió la sesión (teclado numérico)
 * 3) Introducción de importe contado
 * 4) Comprobación y resumen (mini-ticket)
 * 5) Confirmación del cierre
 *
 * Este diálogo NO accede a DAOs directamente.
 * Toda la lógica pasa por Services / Facades.
 */
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

        // Al pulsar se abre el teclado numérico
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

        var cajasOcupadas = appServices.sesionCajaService.getEstadoCajas().stream()
                .filter(CajaEstadoDTO::isOcupada)
                .toList();

        if (cajasOcupadas.isEmpty()) {
            JLabel lbl = new JLabel("No hay sesiones de caja abiertas");
            lbl.setForeground(Color.LIGHT_GRAY);
            lbl.setFont(new Font("Arial", Font.ITALIC, 14));
            panelCajas.add(lbl);
        } else {
            for (CajaEstadoDTO caja : cajasOcupadas) {
                JToggleButton btn = crearBotonCaja(caja);
                grupoCajas.add(btn);
                panelCajas.add(btn);
            }
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }

    private JToggleButton crearBotonCaja(CajaEstadoDTO caja) {
        JToggleButton btn = new JToggleButton();
        btn.setPreferredSize(new Dimension(170, 75));
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_CAJA_OCUPADA);

        String texto = "<html><center><b>" + caja.getNombreCaja() + "</b><br/>"
                + "Ocupada<br/>(" + caja.getEmpleadoAsignado() + ")</center></html>";
        btn.setText(texto);

        btn.addActionListener(e -> onCajaSeleccionada(caja, btn));
        return btn;
    }

    private void onCajaSeleccionada(CajaEstadoDTO caja, JToggleButton botonSeleccionado) {
        this.cajaSeleccionada = caja;

        // Reset visual de botones
        for (Component c : panelCajas.getComponents()) {
            if (c instanceof JToggleButton btn) {
                btn.setBackground(COLOR_CAJA_OCUPADA);
            }
        }
        botonSeleccionado.setBackground(COLOR_CAJA_SELECCIONADA);

        // Reset flujo
        sesionAbierta = null;
        empleadoValidado = null;
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
        if (result == null) return;

        String codigo = result.getPin();
        if (codigo == null || codigo.isBlank()) return;

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

            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación de empleado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarEmpleadoValidado() {
        lblEmpleadoValidado.setText(
                "✔ Empleado validado: "
                        + empleadoValidado.getNombre()
                        + " (" + empleadoValidado.getUsuario() + ")"
        );
        lblEmpleadoValidado.setForeground(new Color(0, 180, 0));
    }

    private void habilitarConteoCaja() {
        txtImporteContado.setEnabled(true);
        btnComprobar.setEnabled(true);
    }
}
