package ui.screens;

import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.TpvDialogUtils;
import ui.dialog.ConfiguracionAparienciaDialog;
import ui.dialog.ConfiguracionIdiomaDialog;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfiguracionMenuFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private final Runnable onBack;
    private final AppServices services;

    public ConfiguracionMenuFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super(I18n.t("settings.title"), onLogoutNavigate, services);

        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(22, 22));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(24, 28, 28, 28));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = InformeUiTheme.createCardPanel(new BorderLayout(18, 0));

        JLabel icon = new JLabel(TpvIconFactory.settings(46, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(I18n.t("settings.menu.title"));
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("settings.menu.subtitle"));
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildCenter() {
        JPanel wrapper = transparentPanel(new GridBagLayout());

        JPanel cards = transparentPanel(new GridLayout(1, 2, 28, 0));
        cards.setPreferredSize(new Dimension(860, 360));

        cards.add(createOptionCard(
                I18n.t("settings.language"),
                I18n.t("settings.language.card.description"),
                TpvIconFactory.language(54, InformeUiTheme.ACCENT_GOLD),
                I18n.t("settings.language.action"),
                this::onIdioma
        ));

        cards.add(createOptionCard(
                I18n.t("settings.appearance"),
                I18n.t("settings.appearance.card.description"),
                TpvIconFactory.palette(54, InformeUiTheme.ACCENT_GOLD),
                I18n.t("settings.appearance.action"),
                this::onApariencia
        ));

        wrapper.add(cards);
        return wrapper;
    }

    private JPanel createOptionCard(String title,
                                    String description,
                                    Icon icon,
                                    String buttonText,
                                    Runnable action) {

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 22));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel top = transparentPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 27));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescription = new JLabel(
                "<html><div style='width:310px; text-align:center;'>"
                        + description
                        + "</div></html>"
        );
        lblDescription.setFont(InformeUiTheme.FONT_BODY);
        lblDescription.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(lblIcon);
        top.add(Box.createVerticalStrut(20));
        top.add(lblTitle);
        top.add(Box.createVerticalStrut(14));
        top.add(lblDescription);

        JButton btnOpen = new JButton(buttonText);
        InformeUiTheme.stylePrimaryButton(btnOpen);
        btnOpen.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnOpen.setPreferredSize(new Dimension(100, 58));
        btnOpen.setIcon(TpvIconFactory.settings(20, Color.WHITE));
        btnOpen.setIconTextGap(10);
        btnOpen.addActionListener(e -> action.run());

        card.add(top, BorderLayout.CENTER);
        card.add(btnOpen, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildBottom() {
        JPanel bottom = transparentPanel(new BorderLayout());

        JButton btnVolver = new JButton(I18n.t("common.back"));
        InformeUiTheme.styleSecondaryButton(btnVolver);
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnVolver.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnVolver.setIconTextGap(8);
        btnVolver.addActionListener(e -> volver());

        bottom.add(btnVolver, BorderLayout.WEST);

        return bottom;
    }

    private void onIdioma() {
        ConfiguracionIdiomaDialog dialog = new ConfiguracionIdiomaDialog(this);
        dialog.setVisible(true);
    }

    private void onApariencia() {
        ConfiguracionAparienciaDialog dialog = new ConfiguracionAparienciaDialog(this);
        dialog.setVisible(true);
    }

    private void volver() {
        safeDispose();

        if (onBack != null) {
            onBack.run();
        }
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