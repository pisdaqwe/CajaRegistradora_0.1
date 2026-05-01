package ui.ventas;

import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import enums.CustomizationCard;
import enums.CustomizationMode;
import model.TicketItem;
import model.TicketSession;
import service.AppServices;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CustomizationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private boolean syncingSizesSelection = false;

    private final TicketSession ticketSession;
    private final AppServices services;
    private final Consumer<CustomizationCard> onCardSelected;
    private final Consumer<TamanoDTO> onTamanoSelected;

    // =========================================================
    // SUBCATEGORÍAS REALES
    // =========================================================
    private static final Set<Integer> SUBCATEGORIAS_BEBIDA = Set.of(1, 2, 3, 4);
    private static final Set<Integer> SUBCATEGORIAS_COMIDA = Set.of(5, 6, 7);
    private static final Set<Integer> SUBCATEGORIAS_VACIO = Set.of(8, 9, 10);

    // =========================================================
    // CARDLAYOUT GENERAL DEL PANEL LATERAL
    // =========================================================
    private final CardLayout cardLayout = new CardLayout();

    // =========================================================
    // CARD VACIO
    // =========================================================
    private final JTextArea emptyInfo = new JTextArea();

    // =========================================================
    // CARD BEBIDA
    // =========================================================
    private final JLabel lblDrinkTitle = new JLabel(I18n.t("sales.custom.drinkTitle"), SwingConstants.LEFT);

    private final DefaultListModel<TamanoDTO> sizeListModel = new DefaultListModel<>();
    private final JList<TamanoDTO> lstSizes = new JList<>(sizeListModel);

    private final JButton btnCafe = createNavButton(I18n.t("sales.custom.card.cafe"), CustomizationCard.CAFE);
    private final JButton btnShots = createNavButton(I18n.t("sales.custom.card.shots"), CustomizationCard.SHOTS);
    private final JButton btnSyrups = createNavButton(I18n.t("sales.custom.card.syrups"), CustomizationCard.SYRUPS);
    private final JButton btnToppings = createNavButton(I18n.t("sales.custom.card.toppings"), CustomizationCard.TOPPINGS);
    private final JButton btnMilk = createNavButton(I18n.t("sales.custom.card.milk"), CustomizationCard.MILK);
    private final JButton btnPrep = createNavButton(I18n.t("sales.custom.card.prep"), CustomizationCard.PREP);

    private final JButton btnPrepFood = createNavButton(I18n.t("sales.custom.card.prep"), CustomizationCard.PREP_FOOD);
    private final JButton btnOpcionesFood = createNavButton(I18n.t("sales.custom.card.foodExtras"), CustomizationCard.OPCIONES_FOOD);

    // =========================================================
    // CARD COMIDA
    // =========================================================
    private final JLabel lblFoodTitle = new JLabel(I18n.t("sales.custom.foodTitle"), SwingConstants.LEFT);
    private final DefaultListModel<TamanoDTO> foodSizeListModel = new DefaultListModel<>();
    private final JList<TamanoDTO> lstFoodSizes = new JList<>(foodSizeListModel);

    public CustomizationPanel(
            TicketSession ticketSession,
            AppServices services,
            Consumer<CustomizationCard> onCardSelected,
            Consumer<TamanoDTO> onTamanoSelected
    ) {
        this.ticketSession = ticketSession;
        this.services = services;
        this.onCardSelected = onCardSelected;
        this.onTamanoSelected = onTamanoSelected;

        setLayout(cardLayout);
        setBackground(InformeUiTheme.APP_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(178, 0));

        add(buildEmptyCard(), CustomizationMode.VACIO.name());
        add(buildDrinkCard(), CustomizationMode.BEBIDA.name());
        add(buildFoodCard(), CustomizationMode.COMIDA.name());

        refresh();
    }

    // =========================================================
    // REFRESH PRINCIPAL
    // =========================================================

    public void refresh() {
        TicketItem item = ticketSession.getSelectedItemOrNull();

        if (item == null) {
            emptyInfo.setText(I18n.t("sales.custom.empty.selectProduct"));
            showMode(CustomizationMode.VACIO);
            return;
        }

        ProductoDTO dto = item.getProducto();
        CustomizationMode mode = resolveMode(item);

        switch (mode) {
            case BEBIDA -> {
                List<TamanoDTO> tamanos =
                        services.productoPersonalizacionService.getTamanosByProducto(dto.getIdProducto());
                refreshDrinkCard(item, tamanos);
            }
            case COMIDA -> {
                List<TamanoDTO> tamanos =
                        services.productoPersonalizacionService.getTamanosByProducto(dto.getIdProducto());
                refreshFoodCard(item, tamanos);
            }
            case VACIO -> refreshEmptyCard(item);
        }

        showMode(mode);
    }

    private void showMode(CustomizationMode mode) {
        cardLayout.show(this, mode.name());
    }

    // =========================================================
    // CARD VACIO
    // =========================================================

    private JPanel buildEmptyCard() {
        JPanel root = InformeUiTheme.createTransparentPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel lblTitle = new JLabel(I18n.t("sales.custom.genericTitle"), SwingConstants.LEFT);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblTitle.setIcon(TpvIconFactory.settings(18, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(8);

        emptyInfo.setEditable(false);
        emptyInfo.setFont(InformeUiTheme.FONT_BODY);
        emptyInfo.setBackground(InformeUiTheme.CARD_BG_2);
        emptyInfo.setForeground(InformeUiTheme.TEXT_SECONDARY);
        emptyInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        emptyInfo.setLineWrap(true);
        emptyInfo.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(emptyInfo);
        InformeUiTheme.styleScrollPane(scroll);

        root.add(lblTitle, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);

        return root;
    }

    private void refreshEmptyCard(TicketItem item) {
        emptyInfo.setText(
                I18n.t("sales.custom.empty.product") + "\n" + safeProductName(item) + "\n\n" +
                I18n.t("sales.custom.empty.noCustomization")
        );
    }

    // =========================================================
    // CARD BEBIDA
    // =========================================================

    private JPanel buildDrinkCard() {
        JPanel root = InformeUiTheme.createTransparentPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel top = InformeUiTheme.createTransparentPanel(new BorderLayout(6, 6));

        styleTitleLabel(lblDrinkTitle, TpvIconFactory.product(18, InformeUiTheme.ACCENT_GOLD));

        top.add(lblDrinkTitle, BorderLayout.NORTH);
        top.add(buildSizesBlock(), BorderLayout.CENTER);

        JPanel nav = InformeUiTheme.createTransparentPanel(new GridLayout(0, 1, 8, 8));

        nav.add(btnCafe);
        nav.add(btnShots);
        nav.add(btnSyrups);
        nav.add(btnToppings);
        nav.add(btnMilk);
        nav.add(btnPrep);

        root.add(top, BorderLayout.NORTH);
        root.add(nav, BorderLayout.CENTER);

        return root;
    }

    private JComponent buildSizesBlock() {
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel lblSizes = new JLabel(I18n.t("sales.custom.sizes"));
        lblSizes.setFont(InformeUiTheme.FONT_LABEL);
        lblSizes.setForeground(InformeUiTheme.TEXT_SECONDARY);

        configureSizeList(lstSizes);

        lstSizes.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            if (syncingSizesSelection) return;

            TamanoDTO seleccionado = lstSizes.getSelectedValue();
            if (seleccionado == null) return;

            if (onTamanoSelected != null) {
                onTamanoSelected.accept(seleccionado);
            }
        });

        JScrollPane sp = new JScrollPane(lstSizes);
        InformeUiTheme.styleScrollPane(sp);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(130, 95));
        sp.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(lblSizes, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildFoodSizesBlock() {
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel lblSizes = new JLabel(I18n.t("sales.custom.sizes"));
        lblSizes.setFont(InformeUiTheme.FONT_LABEL);
        lblSizes.setForeground(InformeUiTheme.TEXT_SECONDARY);

        configureSizeList(lstFoodSizes);

        lstFoodSizes.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            if (syncingSizesSelection) return;

            TamanoDTO seleccionado = lstFoodSizes.getSelectedValue();
            if (seleccionado == null) return;

            if (onTamanoSelected != null) {
                onTamanoSelected.accept(seleccionado);
            }
        });

        JScrollPane sp = new JScrollPane(lstFoodSizes);
        InformeUiTheme.styleScrollPane(sp);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(130, 95));
        sp.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(lblSizes, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private void configureSizeList(JList<TamanoDTO> list) {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(3);
        list.setFixedCellHeight(28);
        InformeUiTheme.styleList(list);
        list.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof TamanoDTO t) {
                    setText(t.getNombre());
                }

                setFont(InformeUiTheme.FONT_LABEL);
                return this;
            }
        });

        list.setEnabled(true);
    }

    private void refreshDrinkCard(TicketItem item, List<TamanoDTO> tamanos) {
        lblDrinkTitle.setText(safeProductName(item));

        sizeListModel.clear();

        if (tamanos != null && !tamanos.isEmpty()) {
            for (TamanoDTO tamanoDTO : tamanos) {
                sizeListModel.addElement(tamanoDTO);
            }

            seleccionarTamanoActual(item.getTamano());
        } else {
            lstSizes.clearSelection();
        }
    }

    // =========================================================
    // CARD COMIDA
    // =========================================================

    private JPanel buildFoodCard() {
        JPanel root = InformeUiTheme.createTransparentPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel top = InformeUiTheme.createTransparentPanel(new BorderLayout(6, 6));

        styleTitleLabel(lblFoodTitle, TpvIconFactory.product(18, InformeUiTheme.ACCENT_GOLD));

        top.add(lblFoodTitle, BorderLayout.NORTH);
        top.add(buildFoodSizesBlock(), BorderLayout.CENTER);

        JPanel nav = InformeUiTheme.createTransparentPanel(new GridLayout(0, 1, 8, 8));
        nav.add(btnOpcionesFood);
        nav.add(btnPrepFood);

        root.add(top, BorderLayout.NORTH);
        root.add(nav, BorderLayout.CENTER);

        return root;
    }

    private void refreshFoodCard(TicketItem item, List<TamanoDTO> tamanos) {
        lblFoodTitle.setText(safeProductName(item));

        foodSizeListModel.clear();

        if (tamanos != null && !tamanos.isEmpty()) {
            for (TamanoDTO tamanoDTO : tamanos) {
                foodSizeListModel.addElement(tamanoDTO);
            }

            seleccionarTamanoActualFood(item.getTamano());
        } else {
            lstFoodSizes.clearSelection();
        }
    }

    // =========================================================
    // BOTONES LATERALES DE NAVEGACIÓN
    // =========================================================

    private JButton createNavButton(String text, CustomizationCard card) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(InformeUiTheme.FONT_BUTTON.deriveFont(Font.BOLD, 12f));
        b.setBackground(InformeUiTheme.ACCENT_GOLD);
        b.setForeground(new Color(28, 28, 22));
        b.setBorder(BorderFactory.createEmptyBorder(13, 10, 13, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setIcon(resolveNavIcon(card));
        b.setIconTextGap(7);

        b.addActionListener(e -> {
            if (onCardSelected != null) {
                onCardSelected.accept(card);
            }
        });

        return b;
    }

    private Icon resolveNavIcon(CustomizationCard card) {
        return switch (card) {
            case PREP, PREP_FOOD -> TpvIconFactory.settings(16, new Color(28, 28, 22));
            case OPCIONES_FOOD -> TpvIconFactory.product(16, new Color(28, 28, 22));
            default -> TpvIconFactory.product(16, new Color(28, 28, 22));
        };
    }

    private void styleTitleLabel(JLabel label, Icon icon) {
        label.setFont(InformeUiTheme.FONT_SECTION);
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);
        label.setIcon(icon);
        label.setIconTextGap(8);
    }

    // =========================================================
    // RESOLUCIÓN DEL MODO POR SUBCATEGORÍA
    // =========================================================

    private CustomizationMode resolveMode(TicketItem item) {
        if (item == null || item.getProducto() == null) {
            return CustomizationMode.VACIO;
        }

        int idSubcategoria = item.getProducto().getIdSubcategoria();

        if (SUBCATEGORIAS_BEBIDA.contains(idSubcategoria)) {
            return CustomizationMode.BEBIDA;
        }

        if (SUBCATEGORIAS_COMIDA.contains(idSubcategoria)) {
            return CustomizationMode.COMIDA;
        }

        if (SUBCATEGORIAS_VACIO.contains(idSubcategoria)) {
            return CustomizationMode.VACIO;
        }

        return CustomizationMode.VACIO;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String safeProductName(TicketItem item) {
        if (item == null || item.getProducto() == null || item.getProducto().getNombre() == null) {
            return I18n.t("sales.custom.noProduct");
        }
        return item.getProducto().getNombre();
    }

    private void seleccionarTamanoActual(TamanoDTO tamanoActual) {
        syncingSizesSelection = true;
        try {
            if (tamanoActual == null) {
                lstSizes.clearSelection();
                return;
            }

            for (int i = 0; i < sizeListModel.size(); i++) {
                TamanoDTO candidato = sizeListModel.getElementAt(i);
                if (candidato.getIdTamano() == tamanoActual.getIdTamano()) {
                    lstSizes.setSelectedIndex(i);
                    lstSizes.ensureIndexIsVisible(i);
                    return;
                }
            }

            lstSizes.clearSelection();
        } finally {
            syncingSizesSelection = false;
        }
    }

    private void seleccionarTamanoActualFood(TamanoDTO tamanoActual) {
        syncingSizesSelection = true;
        try {
            if (tamanoActual == null) {
                lstFoodSizes.clearSelection();
                return;
            }

            for (int i = 0; i < foodSizeListModel.size(); i++) {
                TamanoDTO candidato = foodSizeListModel.getElementAt(i);
                if (candidato.getIdTamano() == tamanoActual.getIdTamano()) {
                    lstFoodSizes.setSelectedIndex(i);
                    lstFoodSizes.ensureIndexIsVisible(i);
                    return;
                }
            }

            lstFoodSizes.clearSelection();
        } finally {
            syncingSizesSelection = false;
        }
    }
}
