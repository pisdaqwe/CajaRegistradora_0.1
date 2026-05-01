package ui.dialog;

import app.AppContext;
import dtoS.EmpleadoDetalleDTO;
import dtoS.EmpleadoSaveRequest;
import model.Rol;
import service.AppServices;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class EmpleadoFormDialog extends JDialog {

    private static final long serialVersionUID = 1L;

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
        super(
                owner,
                idUsuarioEditar == null
                        ? I18n.t("employee.form.title.new")
                        : I18n.t("employee.form.title.edit"),
                ModalityType.APPLICATION_MODAL
        );

        this.services = services;
        this.idUsuarioEditar = idUsuarioEditar;
        this.modoEdicion = idUsuarioEditar != null && idUsuarioEditar > 0;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(640, 660);
        setResizable(false);
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
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(InformeUiTheme.APP_BG);
        setContentPane(root);

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildForm(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = transparentPanel(new BorderLayout(14, 0));

        JLabel icon = new JLabel(
                modoEdicion
                        ? TpvIconFactory.idCard(38, InformeUiTheme.ACCENT_GOLD)
                        : TpvIconFactory.user(38, InformeUiTheme.ACCENT_GOLD)
        );

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(
                modoEdicion
                        ? I18n.t("employee.form.header.edit")
                        : I18n.t("employee.form.header.new")
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                modoEdicion
                        ? I18n.t("employee.form.subtitle.edit")
                        : I18n.t("employee.form.subtitle.new")
        );
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildForm() {
        JPanel form = transparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(22);
        txtUsuario = new JTextField(22);
        txtPin = new JPasswordField(22);
        txtConfirmarPin = new JPasswordField(22);
        cmbRol = new JComboBox<>();
        chkActivo = new JCheckBox(I18n.t("employee.form.active"));
        lblSucursalValor = createValueLabel();

        InformeUiTheme.styleTextField(txtNombre);
        InformeUiTheme.styleTextField(txtUsuario);
        InformeUiTheme.styleTextField(txtPin);
        InformeUiTheme.styleTextField(txtConfirmarPin);
        InformeUiTheme.styleCombo(cmbRol);
        InformeUiTheme.styleCheckBox(chkActivo);

        int y = 0;

        addKeyboardRow(
                form,
                gbc,
                y++,
                I18n.t("employee.form.name"),
                txtNombre,
                I18n.t("employee.form.keyboard.name"),
                60,
                false,
                TpvIconFactory.user(16, InformeUiTheme.TEXT_SECONDARY)
        );

        addKeyboardRow(
                form,
                gbc,
                y++,
                I18n.t("employee.form.username"),
                txtUsuario,
                I18n.t("employee.form.keyboard.username"),
                30,
                false,
                TpvIconFactory.idCard(16, InformeUiTheme.TEXT_SECONDARY)
        );

        addRow(
                form,
                gbc,
                y++,
                I18n.t("employee.form.role"),
                cmbRol,
                TpvIconFactory.shield(16, InformeUiTheme.TEXT_SECONDARY)
        );

        addRow(
                form,
                gbc,
                y++,
                I18n.t("employee.form.branch"),
                lblSucursalValor,
                TpvIconFactory.branch(16, InformeUiTheme.TEXT_SECONDARY)
        );

        addKeyboardRow(
                form,
                gbc,
                y++,
                I18n.t("employee.form.pin"),
                txtPin,
                I18n.t("employee.form.keyboard.pin"),
                8,
                true,
                TpvIconFactory.key(16, InformeUiTheme.TEXT_SECONDARY)
        );

        addKeyboardRow(
                form,
                gbc,
                y++,
                I18n.t("employee.form.confirmPin"),
                txtConfirmarPin,
                I18n.t("employee.form.keyboard.confirmPin"),
                8,
                true,
                TpvIconFactory.key(16, InformeUiTheme.TEXT_SECONDARY)
        );

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        form.add(chkActivo, gbc);

        return form;
    }

    private JPanel buildFooter() {
        JPanel footer = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton(
                modoEdicion
                        ? I18n.t("employee.form.saveChanges")
                        : I18n.t("employee.form.createEmployee")
        );
        InformeUiTheme.stylePrimaryButton(btnGuardar);
        btnGuardar.setIcon(TpvIconFactory.save(18, Color.WHITE));
        btnGuardar.setIconTextGap(8);
        btnGuardar.addActionListener(e -> onGuardar());

        footer.add(btnCancelar);
        footer.add(btnGuardar);

        return footer;
    }

    private void addRow(JPanel panel,
                        GridBagConstraints gbc,
                        int row,
                        String label,
                        JComponent field,
                        Icon icon) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        JLabel lbl = InformeUiTheme.createFieldLabel(label);
        lbl.setIcon(icon);
        lbl.setIconTextGap(7);

        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void addKeyboardRow(JPanel panel,
                                GridBagConstraints gbc,
                                int row,
                                String label,
                                JTextField field,
                                String tituloTeclado,
                                int maxLength,
                                boolean numerico,
                                Icon icon) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        JLabel lbl = InformeUiTheme.createFieldLabel(label);
        lbl.setIcon(icon);
        lbl.setIconTextGap(7);

        panel.add(lbl, gbc);

        JPanel wrapper = transparentPanel(new BorderLayout(8, 0));
        wrapper.add(field, BorderLayout.CENTER);
        wrapper.add(createKeyboardButton(field, tituloTeclado, maxLength, numerico), BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(wrapper, gbc);
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
            lblSucursalValor.setText(I18n.t("employee.form.branchId", AppContext.getIdSucursal()));
        }

        if (!modoEdicion) {
            return;
        }

        Optional<EmpleadoDetalleDTO> opt = services.usuarioService.getDetalleEmpleado(idUsuarioEditar);

        if (opt.isEmpty()) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("common.error"),
                    I18n.t("employee.form.loadError")
            );
            dispose();
            return;
        }

        EmpleadoDetalleDTO detalle = opt.get();

        txtNombre.setText(detalle.getNombre());
        txtUsuario.setText(detalle.getUsuario());
        chkActivo.setSelected(detalle.isActivo());

        lblSucursalValor.setText(
                detalle.getNombreSucursal() != null
                        ? detalle.getNombreSucursal()
                        : I18n.t("employee.form.branchId", detalle.getIdSucursal())
        );

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

        request.setIdUsuarioAdmin(AppContext.getUsuarioId());
        request.setIdSucursalAdmin(AppContext.getIdSucursal());

        return request;
    }

    private void validarFormulario() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            throw new IllegalArgumentException(I18n.t("employee.form.validation.nameRequired"));
        }

        if (txtUsuario.getText() == null || txtUsuario.getText().trim().isEmpty()) {
            throw new IllegalArgumentException(I18n.t("employee.form.validation.usernameRequired"));
        }

        Rol rol = (Rol) cmbRol.getSelectedItem();
        if (rol == null) {
            throw new IllegalArgumentException(I18n.t("employee.form.validation.roleRequired"));
        }

        if (!modoEdicion) {
            String pin = new String(txtPin.getPassword());
            String confirmar = new String(txtConfirmarPin.getPassword());

            if (pin.trim().isEmpty()) {
                throw new IllegalArgumentException(I18n.t("employee.form.validation.pinRequired"));
            }

            if (!pin.equals(confirmar)) {
                throw new IllegalArgumentException(I18n.t("employee.form.validation.pinMismatch"));
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
            TpvDialogUtils.showError(
                    this,
                    I18n.t("employee.form.saveErrorTitle"),
                    ex.getMessage()
            );
        }
    }

    private JButton createKeyboardButton(JTextField field, String titulo, int maxLength, boolean numerico) {
        JButton button = new JButton();
        InformeUiTheme.styleSecondaryButton(button);
        button.setIcon(TpvIconFactory.key(16, InformeUiTheme.TEXT_PRIMARY));
        button.setToolTipText(I18n.t("common.search"));
        button.setMargin(new Insets(6, 10, 6, 10));

        button.addActionListener(e -> {
            if (numerico) {
                TecladoVirtualDialog.showNumerico(this, field, titulo, maxLength);
            } else {
                TecladoVirtualDialog.showAlfanumerico(this, field, titulo, maxLength);
            }
        });

        return button;
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