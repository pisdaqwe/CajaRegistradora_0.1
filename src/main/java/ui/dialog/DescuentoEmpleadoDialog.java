package ui.dialog;

import ui.common.TecladoAlfaNumericoPanel;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DescuentoEmpleadoDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int MAX_CODIGO_LENGTH = 20;

    private DescuentoEmpleadoDialogResult result =
            new DescuentoEmpleadoDialogResult(false, null);

    private final JTextField txtCodigoEmpleado = new JTextField();

    public DescuentoEmpleadoDialog(Window owner) {
        super(owner, I18n.t("sales.employeeDiscount.title"), ModalityType.APPLICATION_MODAL);
        buildUI();
        configurarAtajos();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildActions(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();

        setMinimumSize(new Dimension(720, 520));
        setResizable(true);
        setLocationRelativeTo(getOwner());

        SwingUtilities.invokeLater(() -> txtCodigoEmpleado.requestFocusInWindow());
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        JLabel title = new JLabel(I18n.t("sales.employeeDiscount.header"), SwingConstants.CENTER);
        title.setIcon(TpvIconFactory.idCard(24, InformeUiTheme.ACCENT_GOLD));
        title.setHorizontalTextPosition(SwingConstants.RIGHT);
        title.setIconTextGap(10);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel(I18n.t("sales.employeeDiscount.subtitle"), SwingConstants.CENTER);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);

        panel.add(title, BorderLayout.NORTH);
        panel.add(subtitle, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCenter() {
        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 12));
        card.setBorder(InformeUiTheme.createCardBorder());

        JLabel lblCampo = InformeUiTheme.createFieldLabel(I18n.t("sales.employeeDiscount.code"));
        lblCampo.setHorizontalAlignment(SwingConstants.CENTER);

        txtCodigoEmpleado.setFont(new Font("SansSerif", Font.BOLD, 22));
        txtCodigoEmpleado.setHorizontalAlignment(SwingConstants.CENTER);
        txtCodigoEmpleado.setPreferredSize(new Dimension(420, 50));
        txtCodigoEmpleado.setEditable(true);
        txtCodigoEmpleado.setBackground(InformeUiTheme.CARD_BG_2);
        txtCodigoEmpleado.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtCodigoEmpleado.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtCodigoEmpleado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        TecladoAlfaNumericoPanel teclado = new TecladoAlfaNumericoPanel(txtCodigoEmpleado, MAX_CODIGO_LENGTH);

        card.add(lblCampo, BorderLayout.NORTH);
        card.add(txtCodigoEmpleado, BorderLayout.CENTER);
        card.add(teclado, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.setPreferredSize(new Dimension(150, 42));

        JButton btnAceptar = new JButton(I18n.t("sales.employeeDiscount.apply"));
        btnAceptar.setIcon(TpvIconFactory.check(18, Color.WHITE));
        btnAceptar.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnAceptar);
        btnAceptar.setPreferredSize(new Dimension(150, 42));

        btnAceptar.addActionListener(e -> onAceptar());
        btnCancelar.addActionListener(e -> onCancelar());

        actions.add(btnCancelar);
        actions.add(btnAceptar);

        return actions;
    }

    private void configurarAtajos() {
        JRootPane rootPane = getRootPane();

        rootPane.registerKeyboardAction(
                e -> onAceptar(),
                KeyStroke.getKeyStroke("ENTER"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        rootPane.registerKeyboardAction(
                e -> onCancelar(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void onAceptar() {
        String codigo = txtCodigoEmpleado.getText() != null ? txtCodigoEmpleado.getText().trim() : "";

        if (codigo.isBlank()) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("sales.employeeDiscount.title"),
                    I18n.t("sales.employeeDiscount.validation.empty")
            );
            txtCodigoEmpleado.requestFocusInWindow();
            return;
        }

        result = new DescuentoEmpleadoDialogResult(true, codigo);
        dispose();
    }

    private void onCancelar() {
        result = new DescuentoEmpleadoDialogResult(false, null);
        dispose();
    }

    public DescuentoEmpleadoDialogResult showDialog() {
        txtCodigoEmpleado.setText("");
        result = new DescuentoEmpleadoDialogResult(false, null);
        SwingUtilities.invokeLater(() -> txtCodigoEmpleado.requestFocusInWindow());
        setVisible(true);
        return result;
    }
}
