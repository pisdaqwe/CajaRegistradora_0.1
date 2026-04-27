package ui.dialog;

import dtoS.EmpleadoDetalleDTO;
import dtoS.ResetPinEmpleadoRequest;
import service.AppServices;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import app.AppContext;

import java.awt.*;
import java.util.Optional;

public class ResetPinEmpleadoDialog extends JDialog {

    private final AppServices services;
    private final int idUsuarioObjetivo;

    private JLabel lblEmpleadoValor;
    private JPasswordField txtNuevoPin;
    private JPasswordField txtConfirmarPin;

    private boolean confirmed;

    public ResetPinEmpleadoDialog(Window owner, AppServices services, int idUsuarioObjetivo) {
        super(owner, "Reset PIN empleado", ModalityType.APPLICATION_MODAL);

        this.services = services;
        this.idUsuarioObjetivo = idUsuarioObjetivo;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 280);
        setLocationRelativeTo(owner);

        buildUI();
        cargarEmpleado();
    }

    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(InformeUiTheme.APP_BG);

        JPanel form = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblEmpleadoValor = createValueLabel();
        txtNuevoPin = new JPasswordField(18);
        txtConfirmarPin = new JPasswordField(18);

        InformeUiTheme.styleTextField(txtNuevoPin);
        InformeUiTheme.styleTextField(txtConfirmarPin);

        addRow(form, gbc, 0, "Empleado:", lblEmpleadoValor);
        addRow(form, gbc, 1, "Nuevo PIN:", txtNuevoPin);
        addRow(form, gbc, 2, "Confirmar PIN:", txtConfirmarPin);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Guardar");
        InformeUiTheme.stylePrimaryButton(btnGuardar);
        btnGuardar.addActionListener(e -> onGuardar());

        footer.add(btnCancelar);
        footer.add(btnGuardar);

        root.add(form, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(InformeUiTheme.createFieldLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("-");
        lbl.setFont(InformeUiTheme.FONT_BODY);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return lbl;
    }

    private void cargarEmpleado() {
        Optional<EmpleadoDetalleDTO> opt = services.usuarioService.getDetalleEmpleado(idUsuarioObjetivo);
        if (opt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el empleado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        EmpleadoDetalleDTO detalle = opt.get();
        lblEmpleadoValor.setText(detalle.getNombre() + " (" + detalle.getUsuario() + ")");
    }

    private ResetPinEmpleadoRequest leerRequest() {
        ResetPinEmpleadoRequest request = new ResetPinEmpleadoRequest();
        request.setIdUsuarioObjetivo(idUsuarioObjetivo);
        request.setNuevoPin(new String(txtNuevoPin.getPassword()));
        request.setConfirmarPin(new String(txtConfirmarPin.getPassword()));

        // NUEVO: datos del admin para auditoría
        request.setIdUsuarioAdmin(AppContext.getUsuarioId());
        request.setIdSucursalAdmin(AppContext.getIdSucursal());

        return request;
    }

    private void validar() {
        String nuevo = new String(txtNuevoPin.getPassword());
        String confirmar = new String(txtConfirmarPin.getPassword());

        if (nuevo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo PIN es obligatorio.");
        }

        if (!nuevo.equals(confirmar)) {
            throw new IllegalArgumentException("La confirmación del PIN no coincide.");
        }
    }

    private void onGuardar() {
        try {
            validar();
            ResetPinEmpleadoRequest request = leerRequest();
            services.usuarioService.resetPin(request);
            confirmed = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "No se pudo resetear el PIN",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}