package ui.ventas;

import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import enums.CustomizationCard;
import enums.CustomizationMode;
import model.TicketItem;
import model.TicketSession;
import service.AppServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
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
    private final JLabel lblDrinkTitle = new JLabel("CUSTOM DRINK", SwingConstants.LEFT);

    private final DefaultListModel<TamanoDTO> sizeListModel = new DefaultListModel<>();
    private final JList<TamanoDTO> lstSizes = new JList<>(sizeListModel);

    /**
     * NUEVO:
     * Botón lateral para abrir la card de selección de tipo de café.
     *
     * IMPORTANTE:
     * - solo aparece en el modo bebida
     * - no sustituye ningún extra
     * - solo navega hacia la nueva card CAFE del panel central
     */
    private final JButton btnCafe = createNavButton("CAFE", CustomizationCard.CAFE);

    private final JButton btnShots = createNavButton("SHOTS", CustomizationCard.SHOTS);
    private final JButton btnSyrups = createNavButton("SYRUPS", CustomizationCard.SYRUPS);
    private final JButton btnToppings = createNavButton("TOPPINGS", CustomizationCard.TOPPINGS);
    private final JButton btnMilk = createNavButton("MILK", CustomizationCard.MILK);
    private final JButton btnPrep = createNavButton("PREP", CustomizationCard.PREP);

    private final JButton btnPrepFood = createNavButton("PREP", CustomizationCard.PREP_FOOD);
    private final JButton btnOpcionesFood = createNavButton("EXTRAS", CustomizationCard.OPCIONES_FOOD);

    // =========================================================
    // CARD COMIDA
    // =========================================================
    private final JLabel lblFoodTitle = new JLabel("CUSTOM FOOD", SwingConstants.LEFT);
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
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(170, 0));

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
            emptyInfo.setText("Selecciona un producto del ticket\npara ver sus opciones.");
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
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(new Color(20, 20, 20));
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel lblTitle = new JLabel("CUSTOM", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        emptyInfo.setEditable(false);
        emptyInfo.setFont(new Font("Monospaced", Font.PLAIN, 13));
        emptyInfo.setBackground(new Color(30, 30, 30));
        emptyInfo.setForeground(new Color(220, 220, 220));
        emptyInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        emptyInfo.setLineWrap(true);
        emptyInfo.setWrapStyleWord(true);

        root.add(lblTitle, BorderLayout.NORTH);
        root.add(new JScrollPane(emptyInfo), BorderLayout.CENTER);

        return root;
    }

    private void refreshEmptyCard(TicketItem item) {
        emptyInfo.setText(
                "Producto:\n" + safeProductName(item) + "\n\n" +
                "Este producto no tiene customización lateral."
        );
    }

    // =========================================================
    // CARD BEBIDA
    // =========================================================

    private JPanel buildDrinkCard() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(20, 20, 20));
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.setOpaque(false);

        lblDrinkTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblDrinkTitle.setForeground(Color.WHITE);

        top.add(lblDrinkTitle, BorderLayout.NORTH);
        top.add(buildSizesBlock(), BorderLayout.CENTER);

        JPanel nav = new JPanel(new GridLayout(0, 1, 8, 8));
        nav.setOpaque(false);

        // =====================================================
        // NUEVO BLOQUE AÑADIDO:
        // El café aparece como una navegación propia dentro
        // del flujo de bebida.
        // =====================================================
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
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel lblSizes = new JLabel("TAMAÑOS");
        lblSizes.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblSizes.setForeground(new Color(230, 230, 230));

        lstSizes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSizes.setVisibleRowCount(3);
        lstSizes.setFixedCellHeight(28);
        lstSizes.setBackground(new Color(30, 30, 30));
        lstSizes.setForeground(Color.WHITE);
        lstSizes.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        lstSizes.setCellRenderer(new DefaultListCellRenderer() {
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

                setFont(new Font("Monospaced", Font.BOLD, 12));
                return this;
            }
        });

        lstSizes.setEnabled(true);
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
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(130, 95));
        sp.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(lblSizes, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildFoodSizesBlock() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel lblSizes = new JLabel("TAMAÑOS");
        lblSizes.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblSizes.setForeground(new Color(230, 230, 230));

        lstFoodSizes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstFoodSizes.setVisibleRowCount(3);
        lstFoodSizes.setFixedCellHeight(28);
        lstFoodSizes.setBackground(new Color(30, 30, 30));
        lstFoodSizes.setForeground(Color.WHITE);
        lstFoodSizes.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        lstFoodSizes.setCellRenderer(new DefaultListCellRenderer() {
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

                setFont(new Font("Monospaced", Font.BOLD, 12));
                return this;
            }
        });

        lstFoodSizes.setEnabled(true);
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
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(130, 95));
        sp.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(lblSizes, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
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
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(20, 20, 20));
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.setOpaque(false);

        lblFoodTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblFoodTitle.setForeground(Color.WHITE);

        top.add(lblFoodTitle, BorderLayout.NORTH);
        top.add(buildFoodSizesBlock(), BorderLayout.CENTER);

        JPanel nav = new JPanel(new GridLayout(0, 1, 8, 8));
        nav.setOpaque(false);
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
        b.setFont(new Font("Monospaced", Font.BOLD, 12));
        b.setBackground(new Color(255, 210, 0));
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));

        b.addActionListener(e -> {
            if (onCardSelected != null) {
                onCardSelected.accept(card);
            }
        });

        return b;
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
            return "(sin producto)";
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