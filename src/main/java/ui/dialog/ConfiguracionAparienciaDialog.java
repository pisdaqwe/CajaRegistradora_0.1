package ui.dialog;

import config.ConfigLoader;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import ui.theme.TpvLookAndFeelManager;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfiguracionAparienciaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JRadioButton rbCafe;
    private JRadioButton rbFlatLight;
    private JRadioButton rbFlatDark;
    private JRadioButton rbFlatIntellij;
    private JRadioButton rbFlatDarcula;
    private boolean saved;

    public ConfiguracionAparienciaDialog(Window owner) {
        super(owner, I18n.t("settings.appearance.title"), ModalityType.APPLICATION_MODAL);

        setSize(600, 600);
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        cargarTemaActual();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildOptions(), BorderLayout.CENTER);
        card.add(buildActions(), BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = transparentPanel(new BorderLayout(14, 0));

        JLabel icon = new JLabel(TpvIconFactory.palette(40, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(I18n.t("settings.appearance.title"));
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("settings.appearance.subtitle"));
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildOptions() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 0, 10));
        panel.setOpaque(false);

        rbCafe = createOption(
                I18n.t("settings.appearance.cafe"),
                I18n.t("settings.appearance.cafe.description")
        );

        rbFlatLight = createOption(
                I18n.t("settings.appearance.flatLight"),
                I18n.t("settings.appearance.flatLight.description")
        );

        rbFlatDark = createOption(
                I18n.t("settings.appearance.flatDark"),
                I18n.t("settings.appearance.flatDark.description")
        );

        rbFlatIntellij = createOption(
                I18n.t("settings.appearance.flatIntellij"),
                I18n.t("settings.appearance.flatIntellij.description")
        );

        rbFlatDarcula = createOption(
                I18n.t("settings.appearance.flatDarcula"),
                I18n.t("settings.appearance.flatDarcula.description")
        );

        ButtonGroup group = new ButtonGroup();
        group.add(rbCafe);
        group.add(rbFlatLight);
        group.add(rbFlatDark);
        group.add(rbFlatIntellij);
        group.add(rbFlatDarcula);

        panel.add(wrapOption(rbCafe));
        panel.add(wrapOption(rbFlatLight));
        panel.add(wrapOption(rbFlatDark));
        panel.add(wrapOption(rbFlatIntellij));
        panel.add(wrapOption(rbFlatDarcula));

        return panel;
    }

    private JRadioButton createOption(String title, String description) {
        JRadioButton radio = new JRadioButton(
                "<html><b>" + title + "</b><br><span style='font-size: 11px;'>"
                        + description
                        + "</span></html>"
        );

        radio.setOpaque(false);
        radio.setForeground(InformeUiTheme.TEXT_PRIMARY);
        radio.setFont(InformeUiTheme.FONT_BODY);
        radio.setFocusPainted(false);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return radio;
    }

    private JPanel wrapOption(JRadioButton radio) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(InformeUiTheme.CARD_BG_2);
        wrapper.setBorder(InformeUiTheme.createInnerCardBorder());
        wrapper.add(radio, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildActions() {
        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        JButton btnGuardar = new JButton(I18n.t("common.save"));

        InformeUiTheme.styleSecondaryButton(btnCancelar);
        InformeUiTheme.stylePrimaryButton(btnGuardar);

        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);

        btnGuardar.setIcon(TpvIconFactory.save(18, Color.WHITE));
        btnGuardar.setIconTextGap(8);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarApariencia());

        actions.add(btnCancelar);
        actions.add(btnGuardar);

        return actions;
    }

    private void cargarTemaActual() {
        String current = ConfigLoader.getAppLookAndFeel();

        switch (TpvLookAndFeelManager.normalize(current)) {
            case TpvLookAndFeelManager.FLAT_LIGHT:
                rbFlatLight.setSelected(true);
                break;

            case TpvLookAndFeelManager.FLAT_DARK:
                rbFlatDark.setSelected(true);
                break;

            case TpvLookAndFeelManager.FLAT_INTELLIJ:
                rbFlatIntellij.setSelected(true);
                break;

            case TpvLookAndFeelManager.FLAT_DARCULA:
                rbFlatDarcula.setSelected(true);
                break;

            case TpvLookAndFeelManager.CAFE:
            default:
                rbCafe.setSelected(true);
                break;
        }
    }

    private void guardarApariencia() {
        String selected = getSelectedLookAndFeel();
        String current = ConfigLoader.getAppLookAndFeel();

        if (selected.equalsIgnoreCase(TpvLookAndFeelManager.normalize(current))) {
            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("settings.appearance.title"),
                    I18n.t("settings.appearance.noChanges")
            );
            return;
        }

        try {
            ConfigLoader.updateAppLookAndFeel(selected);
            saved = true;

            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("settings.appearance.saved"),
                    I18n.t("settings.restartRequired")
            );

            dispose();

        } catch (Exception ex) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("common.error"),
                    I18n.t("settings.appearance.saveError", ex.getMessage())
            );
        }
    }

    private String getSelectedLookAndFeel() {
        if (rbFlatDarcula.isSelected()) {
            return TpvLookAndFeelManager.FLAT_DARCULA;
        }

        if (rbFlatIntellij.isSelected()) {
            return TpvLookAndFeelManager.FLAT_INTELLIJ;
        }

        if (rbFlatDark.isSelected()) {
            return TpvLookAndFeelManager.FLAT_DARK;
        }

        if (rbFlatLight.isSelected()) {
            return TpvLookAndFeelManager.FLAT_LIGHT;
        }

        return TpvLookAndFeelManager.CAFE;
    }

    public boolean isSaved() {
        return saved;
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