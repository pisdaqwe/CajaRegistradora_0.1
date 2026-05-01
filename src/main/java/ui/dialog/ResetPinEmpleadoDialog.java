package ui.dialog;

import app.AppContext;
import dtoS.EmpleadoDetalleDTO;
import dtoS.ResetPinEmpleadoRequest;
import service.AppServices;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class ResetPinEmpleadoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final AppServices services;
    private final int idUsuarioObjetivo;

    private JLabel lblEmpleadoValor;
    private JPasswordField txtNuevoPin;
    private JPasswordField txtConfirmarPin;

    private boolean confirmed;

    public ResetPinEmpleadoDialog(Window owner, AppServices services, int idUsuarioObjetivo) {
        super(owner, I18n.t("employee.resetPin.title"), ModalityType.APPLICATION_MODAL);

        this.services = services;
        this.idUsuarioObjetivo = idUsuarioObjetivo;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(540, 460);
        setResizable(false);
        setLocationRelativeTo(owner);

        buildUI();
        cargarEmpleado();
    }

    public boolean showDialog() {
        setVisible(true);
        return confirmed;
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

        JLabel icon = new JLabel(TpvIconFactory.key(38, InformeUiTheme.ACCENT_GOLD));

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(I18n.t("employee.resetPin.header"));
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("employee.resetPin.subtitle"));
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
        gbc.insets = new Insets(9, 8, 9, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblEmpleadoValor = createValueLabel();
        txtNuevoPin = new JPasswordField(18);
        txtConfirmarPin = new JPasswordField(18);

        InformeUiTheme.styleTextField(txtNuevoPin);
        InformeUiTheme.styleTextField(txtConfirmarPin);

        addRow(
                form,
                gbc,
                0,
                I18n.t("employee.resetPin.employee"),
                lblEmpleadoValor,
                TpvIconFactory.user(16, InformeUiTheme.TEXT_SECONDARY)
        );

        addKeyboardRow(
                form,
                gbc,
                1,
                I18n.t("employee.resetPin.newPin"),
                txtNuevoPin,
                I18n.t("employee.resetPin.keyboard.newPin"),
                8,
                TpvIconFactory.key(16, InformeUiTheme.TEXT_SECONDARY)
        );

        addKeyboardRow(
                form,
                gbc,
                2,
                I18n.t("employee.resetPin.confirmPin"),
                txtConfirmarPin,
                I18n.t("employee.resetPin.keyboard.confirmPin"),
                8,
                TpvIconFactory.key(16, InformeUiTheme.TEXT_SECONDARY)
        );

        return form;
    }

    private JPanel buildFooter() {
        JPanel footer = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton(I18n.t("common.save"));
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
        wrapper.add(createKeyboardButton(field, tituloTeclado, maxLength), BorderLayout.EAST);

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

    private void cargarEmpleado() {
        Optional<EmpleadoDetalleDTO> opt = services.usuarioService.getDetalleEmpleado(idUsuarioObjetivo);

        if (opt.isEmpty()) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("common.error"),
                    I18n.t("employee.resetPin.loadError")
            );
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

        request.setIdUsuarioAdmin(AppContext.getUsuarioId());
        request.setIdSucursalAdmin(AppContext.getIdSucursal());

        return request;
    }

    private void validar() {
        String nuevo = new String(txtNuevoPin.getPassword());
        String confirmar = new String(txtConfirmarPin.getPassword());

        if (nuevo.trim().isEmpty()) {
            throw new IllegalArgumentException(I18n.t("employee.resetPin.validation.required"));
        }

        if (!nuevo.equals(confirmar)) {
            throw new IllegalArgumentException(I18n.t("employee.resetPin.validation.mismatch"));
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
            TpvDialogUtils.showError(
                    this,
                    I18n.t("employee.resetPin.saveErrorTitle"),
                    ex.getMessage()
            );
        }
    }

    private JButton createKeyboardButton(JTextField field, String titulo, int maxLength) {
        JButton button = new JButton();
        InformeUiTheme.styleSecondaryButton(button);
        button.setIcon(TpvIconFactory.key(16, InformeUiTheme.TEXT_PRIMARY));
        button.setMargin(new Insets(6, 10, 6, 10));

        button.addActionListener(e ->
                TecladoVirtualDialog.showNumerico(this, field, titulo, maxLength)
        );

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