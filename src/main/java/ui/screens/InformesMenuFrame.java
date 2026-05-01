package ui.screens;

import enums.TipoInforme;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InformesMenuFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private final Runnable onBack;
    private final Runnable onLogoutNavigate;
    private final AppServices appServices;

    public InformesMenuFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super(I18n.t("reports.menu.title"), onLogoutNavigate, services);

        this.onBack = onBack;
        this.onLogoutNavigate = onLogoutNavigate;
        this.appServices = services;

        requireAuthenticatedOrExit();

        buildUI();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(22, 22));
        root.setBorder(new EmptyBorder(24, 28, 28, 28));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCards(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = InformeUiTheme.createCardPanel(new BorderLayout(18, 0));

        JLabel icon = new JLabel(TpvIconFactory.report(46, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(I18n.t("reports.menu.header"));
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("reports.menu.subtitle"));
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildCards() {
        JPanel wrapper = transparentPanel(new GridBagLayout());

        JPanel grid = transparentPanel(new GridLayout(2, 2, 24, 24));
        grid.setPreferredSize(new Dimension(980, 550));

        grid.add(createReportCard(
                I18n.t("reports.menu.cash.title"),
                I18n.t("reports.menu.cash.description"),
                TpvIconFactory.cashRegister(48, InformeUiTheme.ACCENT_GOLD),
                TipoInforme.VENTAS_POR_CAJA
        ));

        grid.add(createReportCard(
                I18n.t("reports.menu.sales.title"),
                I18n.t("reports.menu.sales.description"),
                TpvIconFactory.chartBar(48, InformeUiTheme.ACCENT_GOLD),
                TipoInforme.VENTAS_POR_DIA
        ));

        grid.add(createReportCard(
                I18n.t("reports.menu.payments.title"),
                I18n.t("reports.menu.payments.description"),
                TpvIconFactory.creditCard(48, InformeUiTheme.ACCENT_GOLD),
                TipoInforme.PAGOS_POR_METODO
        ));

        grid.add(createReportCard(
                I18n.t("reports.menu.products.title"),
                I18n.t("reports.menu.products.description"),
                TpvIconFactory.product(48, InformeUiTheme.ACCENT_GOLD),
                TipoInforme.PRODUCTOS_MAS_VENDIDOS
        ));

        wrapper.add(grid);
        return wrapper;
    }

    private JPanel createReportCard(String title,
                                    String description,
                                    Icon icon,
                                    TipoInforme tipoInforme) {

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel content = transparentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescription = new JLabel(
                "<html><div style='width:340px; text-align:center;'>"
                        + description
                        + "</div></html>"
        );
        lblDescription.setFont(InformeUiTheme.FONT_BODY);
        lblDescription.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(lblIcon);
        content.add(Box.createVerticalStrut(18));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(12));
        content.add(lblDescription);

        JButton btnOpen = new JButton(I18n.t("reports.menu.open"));
        InformeUiTheme.stylePrimaryButton(btnOpen);
        btnOpen.setFont(new Font("SansSerif", Font.BOLD, 17));
        btnOpen.setPreferredSize(new Dimension(100, 56));
        btnOpen.setIcon(TpvIconFactory.report(18, Color.WHITE));
        btnOpen.setIconTextGap(10);
        btnOpen.addActionListener(e -> abrirExplorador(tipoInforme));

        card.add(content, BorderLayout.CENTER);
        card.add(btnOpen, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildBottom() {
        JPanel bottom = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnVolver = new JButton(I18n.t("common.back"));
        InformeUiTheme.styleSecondaryButton(btnVolver);
        btnVolver.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnVolver.setIconTextGap(8);
        btnVolver.addActionListener(e -> volver());

        JButton btnLogout = new JButton(I18n.t("common.logout"));
        InformeUiTheme.styleDangerButton(btnLogout);
        btnLogout.setIcon(TpvIconFactory.logout(18, Color.WHITE));
        btnLogout.setIconTextGap(8);
        btnLogout.addActionListener(e -> doLogout());

        bottom.add(btnVolver);
        bottom.add(btnLogout);

        return bottom;
    }

    private void abrirExplorador(TipoInforme tipoInforme) {
        this.setVisible(false);

        InformesFrame frame = new InformesFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                appServices,
                tipoInforme
        );

        frame.setVisible(true);
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