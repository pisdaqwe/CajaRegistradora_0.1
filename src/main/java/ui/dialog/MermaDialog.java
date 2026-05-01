package ui.dialog;

import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MermaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtMotivo;
    private JTextArea txtObservaciones;

    private MermaDialogResult result = MermaDialogResult.cancelled();

    public MermaDialog(Window owner) {
        super(owner, I18n.t("sales.waste.title"), ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        buildUI();
        configureDialogSize(owner);
        configurarAtajos();
    }

    public MermaDialogResult showDialog() {
        setVisible(true);
        return result;
    }

    private void configureDialogSize(Window owner) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int targetW = (int) (screen.width * 0.52);
        int targetH = (int) (screen.height * 0.66);

        int finalW = Math.max(680, Math.min(820, targetW));
        int finalH = Math.max(500, Math.min(620, targetH));

        setMinimumSize(new Dimension(650, 480));
        setPreferredSize(new Dimension(finalW, finalH));
        setSize(finalW, finalH);
        setLocationRelativeTo(owner);
    }

    private void configurarAtajos() {
        JRootPane rootPane = getRootPane();

        rootPane.registerKeyboardAction(
                e -> cancelar(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        rootPane.registerKeyboardAction(
                e -> onAceptar(),
                KeyStroke.getKeyStroke("control ENTER"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(14, 16, 14, 16));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 2, 2, 2));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        textPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel(I18n.t("sales.waste.header"));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(I18n.t("sales.waste.subtitle"));
        lblSub.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSub.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(lblTitulo);
        textPanel.add(lblSub);

        JLabel lblIcon = new JLabel(TpvIconFactory.warning(32, InformeUiTheme.ACCENT_GOLD), SwingConstants.RIGHT);
        lblIcon.setPreferredSize(new Dimension(48, 42));

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(lblIcon, BorderLayout.EAST);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 14));
        card.setBorder(InformeUiTheme.createCardBorder());

        card.add(buildInfoPanel(), BorderLayout.NORTH);
        card.add(buildFormPanel(), BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private JComponent buildInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout(10, 0));
        infoPanel.setBackground(InformeUiTheme.PANEL_BG);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblIcon = new JLabel(TpvIconFactory.info(22, InformeUiTheme.ACCENT_GOLD), SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(34, 34));

        JLabel lblInfo = new JLabel(I18n.t("sales.waste.infoHtml"));
        lblInfo.setFont(InformeUiTheme.FONT_BODY);
        lblInfo.setForeground(InformeUiTheme.TEXT_SECONDARY);

        infoPanel.add(lblIcon, BorderLayout.WEST);
        infoPanel.add(lblInfo, BorderLayout.CENTER);

        return infoPanel;
    }

    private JComponent buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        txtMotivo = new JTextField();
        InformeUiTheme.styleTextField(txtMotivo);
        txtMotivo.setPreferredSize(new Dimension(0, 38));

        JPanel motivoWrapper = new JPanel(new BorderLayout(6, 0));
        motivoWrapper.setOpaque(false);
        motivoWrapper.add(txtMotivo, BorderLayout.CENTER);

        JButton btnTecladoMotivo = new JButton("⌨");
        InformeUiTheme.styleSecondaryButton(btnTecladoMotivo);
        btnTecladoMotivo.setPreferredSize(new Dimension(60, 38));
        btnTecladoMotivo.setToolTipText(I18n.t("common.openTouchKeyboard"));
        btnTecladoMotivo.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtMotivo,
                        I18n.t("sales.waste.keyboard.reason"),
                        80
                )
        );

        motivoWrapper.add(btnTecladoMotivo, BorderLayout.EAST);

        txtObservaciones = new JTextArea(4, 20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        styleObservacionesArea(txtObservaciones);

        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setBorder(BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1));
        scrollObs.setPreferredSize(new Dimension(0, 120));

        JLabel lblHint = new JLabel(I18n.t("sales.waste.reason.hint"));
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblHint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        form.add(createLabel(I18n.t("sales.waste.reason")), gbc);

        gbc.gridy = 1;
        form.add(motivoWrapper, gbc);

        gbc.gridy = 2;
        form.add(Box.createVerticalStrut(4), gbc);

        gbc.gridy = 3;
        form.add(createLabel(I18n.t("sales.waste.notes")), gbc);

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        form.add(scrollObs, gbc);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        form.add(lblHint, gbc);

        return form;
    }

    private JLabel createLabel(String text) {
        JLabel label = InformeUiTheme.createFieldLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private void styleObservacionesArea(JTextArea area) {
        area.setFont(InformeUiTheme.FONT_BODY);
        area.setBackground(InformeUiTheme.CARD_BG_2);
        area.setForeground(InformeUiTheme.TEXT_PRIMARY);
        area.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
    }

    private JComponent buildBottom() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel lblFooter = new JLabel(I18n.t("sales.waste.footer"));
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblFooter.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.setPreferredSize(new Dimension(140, 42));
        btnCancelar.addActionListener(e -> cancelar());

        JButton btnAceptar = new JButton(I18n.t("sales.waste.register"));
        btnAceptar.setIcon(TpvIconFactory.warning(18, Color.WHITE));
        btnAceptar.setIconTextGap(8);
        InformeUiTheme.styleDangerButton(btnAceptar);
        btnAceptar.setPreferredSize(new Dimension(190, 42));
        btnAceptar.addActionListener(e -> onAceptar());

        buttons.add(btnCancelar);
        buttons.add(btnAceptar);

        panel.add(lblFooter, BorderLayout.WEST);
        panel.add(buttons, BorderLayout.EAST);

        return panel;
    }

    private void cancelar() {
        result = MermaDialogResult.cancelled();
        dispose();
    }

    private void onAceptar() {
        String motivo = txtMotivo.getText() != null
                ? txtMotivo.getText().trim()
                : "";

        String observaciones = txtObservaciones.getText() != null
                ? txtObservaciones.getText().trim()
                : "";

        if (motivo.isBlank()) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("sales.waste.reason.required.title"),
                    I18n.t("sales.waste.reason.required.message")
            );
            txtMotivo.requestFocusInWindow();
            return;
        }

        result = MermaDialogResult.confirmed(motivo, observaciones);
        dispose();
    }
}
