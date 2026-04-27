package ui.dialog;

import app.AppContext;
import dtoS.EmpleadoDetalleDTO;
import dtoS.EmpleadoSaveRequest;
import model.Rol;
import service.AppServices;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class EmpleadoFormDialog extends JDialog {

    private final AppServices services;
    private final Integer idUsuarioEditar;
    private final boolean modoEdicion;

    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtPin;
    private JPasswordField txtConfirmarPin;
    private JComboBox<Rol> cmbRol;
    private JCheckBox chkActivo;
    private JLabel lblSucursalValor;

    private boolean confirmed;
    private Integer idEmpleadoCreado;

    public EmpleadoFormDialog(Window owner, AppServices services, Integer idUsuarioEditar) {
        super(owner,
                idUsuarioEditar == null ? "Nuevo empleado" : "Editar empleado",
                ModalityType.APPLICATION_MODAL);

        this.services = services;
        this.idUsuarioEditar = idUsuarioEditar;
        this.modoEdicion = idUsuarioEditar != null && idUsuarioEditar > 0;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(580, 450);
        setLocationRelativeTo(owner);

        buildUI();
        cargarCombos();
        cargarDatosIniciales();
    }

    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    public Integer getIdEmpleadoCreado() {
        return idEmpleadoCreado;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(InformeUiTheme.APP_BG);

        JPanel form = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(22);
        txtUsuario = new JTextField(22);
        txtPin = new JPasswordField(22);
        txtConfirmarPin = new JPasswordField(22);
        cmbRol = new JComboBox<>();
        chkActivo = new JCheckBox("Empleado activo");
        lblSucursalValor = createValueLabel();

        InformeUiTheme.styleTextField(txtNombre);
        InformeUiTheme.styleTextField(txtUsuario);
        InformeUiTheme.styleTextField(txtPin);
        InformeUiTheme.styleTextField(txtConfirmarPin);
        InformeUiTheme.styleCombo(cmbRol);
        InformeUiTheme.styleCheckBox(chkActivo);

        int y = 0;
        addRow(form, gbc, y++, "Nombre:", txtNombre);
        addRow(form, gbc, y++, "Usuario / código:", txtUsuario);
        addRow(form, gbc, y++, "Rol:", cmbRol);
        addRow(form, gbc, y++, "Sucursal:", lblSucursalValor);
        addRow(form, gbc, y++, "PIN:", txtPin);
        addRow(form, gbc, y++, "Confirmar PIN:", txtConfirmarPin);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        form.add(chkActivo, gbc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton(modoEdicion ? "Guardar cambios" : "Crear empleado");
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

    private void cargarCombos() {
        cmbRol.removeAllItems();
        List<Rol> roles = services.usuarioService.getRoles();
        for (Rol rol : roles) {
            cmbRol.addItem(rol);
        }
    }

    private void cargarDatosIniciales() {
        chkActivo.setSelected(true);

        if (AppContext.hasTerminalContext()) {
            lblSucursalValor.setText("Sucursal ID: " + AppContext.getIdSucursal());
        }

        if (!modoEdicion) {
            return;
        }

        Optional<EmpleadoDetalleDTO> opt = services.usuarioService.getDetalleEmpleado(idUsuarioEditar);
        if (opt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el empleado a editar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        EmpleadoDetalleDTO detalle = opt.get();

        txtNombre.setText(detalle.getNombre());
        txtUsuario.setText(detalle.getUsuario());
        chkActivo.setSelected(detalle.isActivo());
        lblSucursalValor.setText(detalle.getNombreSucursal() != null
                ? detalle.getNombreSucursal()
                : ("Sucursal ID: " + detalle.getIdSucursal()));

        seleccionarRol(detalle.getIdRol());

        txtPin.setEnabled(false);
        txtConfirmarPin.setEnabled(false);
        txtPin.setText("");
        txtConfirmarPin.setText("");
    }

    private void seleccionarRol(int idRol) {
        ComboBoxModel<Rol> model = cmbRol.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Rol rol = model.getElementAt(i);
            if (rol != null && rol.getIdRol() == idRol) {
                cmbRol.setSelectedIndex(i);
                return;
            }
        }
    }

    private EmpleadoSaveRequest leerRequest() {
        EmpleadoSaveRequest request = new EmpleadoSaveRequest();
        request.setIdUsuario(idUsuarioEditar);
        request.setNombre(txtNombre.getText());
        request.setUsuario(txtUsuario.getText());

        if (!modoEdicion) {
            request.setPinPlano(new String(txtPin.getPassword()));
            request.setConfirmarPin(new String(txtConfirmarPin.getPassword()));
        }

        Rol rol = (Rol) cmbRol.getSelectedItem();
        request.setIdRol(rol != null ? rol.getIdRol() : 0);

        int idSucursal = 0;
        if (modoEdicion) {
            Optional<EmpleadoDetalleDTO> detalle = services.usuarioService.getDetalleEmpleado(idUsuarioEditar);
            idSucursal = detalle.map(EmpleadoDetalleDTO::getIdSucursal).orElse(0);
        } else if (AppContext.hasTerminalContext()) {
            idSucursal = AppContext.getIdSucursal();
        }

        request.setIdSucursal(idSucursal);
        request.setActivo(chkActivo.isSelected());

        // NUEVO: datos del admin para auditoría
        request.setIdUsuarioAdmin(AppContext.getUsuarioId());
        request.setIdSucursalAdmin(AppContext.getIdSucursal());

        return request;
    }

    private void validarFormulario() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (txtUsuario.getText() == null || txtUsuario.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario/código es obligatorio.");
        }

        Rol rol = (Rol) cmbRol.getSelectedItem();
        if (rol == null) {
            throw new IllegalArgumentException("Debes seleccionar un rol.");
        }

        if (!modoEdicion) {
            String pin = new String(txtPin.getPassword());
            String confirmar = new String(txtConfirmarPin.getPassword());

            if (pin.trim().isEmpty()) {
                throw new IllegalArgumentException("El PIN es obligatorio.");
            }

            if (!pin.equals(confirmar)) {
                throw new IllegalArgumentException("La confirmación del PIN no coincide.");
            }
        }
    }

    private void onGuardar() {
        try {
            validarFormulario();

            EmpleadoSaveRequest request = leerRequest();

            if (modoEdicion) {
                services.usuarioService.actualizarEmpleado(request);
                idEmpleadoCreado = request.getIdUsuario();
            } else {
                idEmpleadoCreado = services.usuarioService.crearEmpleado(request);
            }

            confirmed = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "No se pudo guardar el empleado",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}