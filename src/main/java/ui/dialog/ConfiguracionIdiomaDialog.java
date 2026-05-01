package ui.dialog;

import config.ConfigLoader;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfiguracionIdiomaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JRadioButton rbEspanol;
    private JRadioButton rbIngles;

    private boolean saved;

    public ConfiguracionIdiomaDialog(Window owner) {
        super(owner, I18n.t("settings.language.title"), ModalityType.APPLICATION_MODAL);

        setSize(520, 468);
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        cargarIdiomaActual();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildLanguageOptions(), BorderLayout.CENTER);
        card.add(buildActions(), BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = InformeUiTheme.createTransparentPanel(new BorderLayout(14, 0));

        JLabel icon = new JLabel(TpvIconFactory.language(38, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = InformeUiTheme.createTransparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(I18n.t("settings.language.title"));
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("settings.language.subtitle"));
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildLanguageOptions() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 12));
        panel.setOpaque(false);

        rbEspanol = createOption(
                I18n.t("settings.language.spanish"),
                I18n.t("settings.language.spanish.description")
        );

        rbIngles = createOption(
                I18n.t("settings.language.english"),
                I18n.t("settings.language.english.description")
        );

        ButtonGroup group = new ButtonGroup();
        group.add(rbEspanol);
        group.add(rbIngles);

        panel.add(wrapOption(rbEspanol));
        panel.add(wrapOption(rbIngles));

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
        btnGuardar.addActionListener(e -> guardarIdioma());

        actions.add(btnCancelar);
        actions.add(btnGuardar);

        return actions;
    }

    private void cargarIdiomaActual() {
        String language = ConfigLoader.getAppLanguage();

        if ("en".equalsIgnoreCase(language)) {
            rbIngles.setSelected(true);
        } else {
            rbEspanol.setSelected(true);
        }
    }

    private void guardarIdioma() {
        String selectedLanguage = rbIngles.isSelected() ? "en" : "es";
        String currentLanguage = ConfigLoader.getAppLanguage();

        if (selectedLanguage.equalsIgnoreCase(currentLanguage)) {
            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("settings.language.title"),
                    I18n.t("settings.language.noChanges")
            );
            return;
        }

        try {
            ConfigLoader.updateAppLanguage(selectedLanguage);
            saved = true;

            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("settings.language.saved"),
                    I18n.t("settings.restartRequired")
            );

            dispose();

        } catch (Exception ex) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("common.error"),
                    I18n.t("settings.language.saveError", ex.getMessage())
            );
        }
    }

    public boolean isSaved() {
        return saved;
    }
}