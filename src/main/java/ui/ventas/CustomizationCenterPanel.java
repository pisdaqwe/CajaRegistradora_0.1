package ui.ventas;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoCustomizationDTO;
import dtoS.TipoCafeDTO;
import enums.CustomizationCard;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Panel central grande de customización.
 *
 * Vive dentro de CARD_CUSTOM de VentasCenterPanel, contiene un CardLayout
 * interno y emite callbacks hacia VentasFrame sin tocar TicketSession.
 */
public class CustomizationCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final CardLayout cardLayout = new CardLayout();

    /**
     * Card actualmente visible.
     */
    private CustomizationCard currentCard = CustomizationCard.MILK;

    /**
     * Guardamos el grid de cada card para reconstruirlo dinámicamente.
     */
    private final Map<CustomizationCard, JPanel> gridByCard = new EnumMap<>(CustomizationCard.class);

    /**
     * Datos actualmente cargados.
     */
    private ProductoCustomizationDTO currentData =
            new ProductoCustomizationDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    /**
     * Listener hacia fuera.
     */
    private CustomizationActionListener actionListener;

    private final DecimalFormat moneyFormat = new DecimalFormat("0.00 €");

    public interface CustomizationActionListener {
        void onExtraClicked(ExtraDTO extra);

        void onPersonalizacionClicked(PersonalizacionDTO personalizacion);

        void onTipoCafeClicked(TipoCafeDTO tipoCafe);

        void onAskMeClicked();
    }

    public CustomizationCenterPanel() {
        setLayout(cardLayout);
        setBackground(InformeUiTheme.APP_BG);

        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.cafe"), CustomizationCard.CAFE), CustomizationCard.CAFE.name());
        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.shots"), CustomizationCard.SHOTS), CustomizationCard.SHOTS.name());
        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.syrups"), CustomizationCard.SYRUPS), CustomizationCard.SYRUPS.name());
        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.toppings"), CustomizationCard.TOPPINGS), CustomizationCard.TOPPINGS.name());
        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.milk"), CustomizationCard.MILK), CustomizationCard.MILK.name());
        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.prep"), CustomizationCard.PREP), CustomizationCard.PREP.name());
        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.prepFood"), CustomizationCard.PREP_FOOD), CustomizationCard.PREP_FOOD.name());
        add(buildEmptyDynamicCard(I18n.t("sales.custom.card.foodExtras"), CustomizationCard.OPCIONES_FOOD), CustomizationCard.OPCIONES_FOOD.name());

        showCard(CustomizationCard.MILK);
        clearCustomizationData();
    }

    // =========================================================
    // API PÚBLICA
    // =========================================================

    public void setActionListener(CustomizationActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void showCard(CustomizationCard card) {
        if (card == null) {
            return;
        }
        this.currentCard = card;
        cardLayout.show(this, card.name());
    }

    public void loadCustomizationData(ProductoCustomizationDTO dto) {
        if (dto == null) {
            clearCustomizationData();
            return;
        }

        this.currentData = dto;
        rebuildAllCards();

        // Mantener la card que el usuario estaba viendo
        showCard(currentCard);
    }

    public void clearCustomizationData() {
        this.currentData = new ProductoCustomizationDTO(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        rebuildAllCards();
        showCard(currentCard);
    }

    // =========================================================
    // CONSTRUCCIÓN BASE DE CARDS
    // =========================================================

    private JComponent buildEmptyDynamicCard(String title, CustomizationCard card) {
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(InformeUiTheme.FONT_TITLE.deriveFont(Font.BOLD, 24f));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblTitle.setIcon(resolveCardIcon(card, 24, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(10);

        JPanel grid = InformeUiTheme.createTransparentPanel(new GridLayout(0, 3, 12, 12));

        JScrollPane scroll = new JScrollPane(grid);
        InformeUiTheme.styleScrollPane(scroll);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        gridByCard.put(card, grid);

        return panel;
    }

    // =========================================================
    // REBUILD DINÁMICO
    // =========================================================

    private void rebuildAllCards() {
        rebuildCafeCard(filterTiposCafe());

        rebuildExtrasCard(CustomizationCard.SHOTS, filterExtrasByCard(CustomizationCard.SHOTS));
        rebuildExtrasCard(CustomizationCard.SYRUPS, filterExtrasByCard(CustomizationCard.SYRUPS));
        rebuildExtrasCard(CustomizationCard.TOPPINGS, filterExtrasByCard(CustomizationCard.TOPPINGS));
        rebuildExtrasCard(CustomizationCard.MILK, filterExtrasByCard(CustomizationCard.MILK));

        rebuildExtrasCard(CustomizationCard.OPCIONES_FOOD, filterFoodExtras());

        List<PersonalizacionDTO> preps = filterPreps();
        rebuildPrepCard(CustomizationCard.PREP, preps);
        rebuildPrepCard(CustomizationCard.PREP_FOOD, preps);
    }

    private void rebuildCafeCard(List<TipoCafeDTO> tiposCafe) {
        JPanel grid = gridByCard.get(CustomizationCard.CAFE);
        if (grid == null) {
            return;
        }

        grid.removeAll();

        if (tiposCafe.isEmpty()) {
            grid.add(createEmptyState(I18n.t("sales.custom.empty.noCoffee")));
        } else {
            for (TipoCafeDTO tipoCafe : tiposCafe) {
                grid.add(createTipoCafeButton(tipoCafe));
            }
        }

        grid.revalidate();
        grid.repaint();
    }

    private void rebuildExtrasCard(CustomizationCard card, List<ExtraDTO> extras) {
        JPanel grid = gridByCard.get(card);
        if (grid == null) {
            return;
        }

        grid.removeAll();

        if (extras.isEmpty()) {
            grid.add(createEmptyState(I18n.t("sales.custom.empty.noOptions")));
        } else {
            for (ExtraDTO extra : extras) {
                grid.add(createExtraButton(extra));
            }
        }

        grid.revalidate();
        grid.repaint();
    }

    private void rebuildPrepCard(CustomizationCard card, List<PersonalizacionDTO> preps) {
        JPanel grid = gridByCard.get(card);
        if (grid == null) {
            return;
        }

        grid.removeAll();

        // Botón fijo Ask Me siempre visible en PREP y PREP_FOOD
        grid.add(createAskMeButton());

        if (preps.isEmpty()) {
            grid.add(createEmptyState(I18n.t("sales.custom.empty.noOptions")));
        } else {
            for (PersonalizacionDTO p : preps) {
                grid.add(createPersonalizacionButton(p));
            }
        }

        grid.revalidate();
        grid.repaint();
    }

    // =========================================================
    // FILTRADO DE DATOS
    // =========================================================

    private List<TipoCafeDTO> filterTiposCafe() {
        List<TipoCafeDTO> result = new ArrayList<>();

        if (currentData.getTiposCafe() == null || currentData.getTiposCafe().isEmpty()) {
            return result;
        }

        for (TipoCafeDTO tipoCafe : currentData.getTiposCafe()) {
            if (tipoCafe == null) {
                continue;
            }

            if (!tipoCafe.isActivo()) {
                continue;
            }

            result.add(tipoCafe);
        }

        return result;
    }

    private List<ExtraDTO> filterExtrasByCard(CustomizationCard card) {
        List<ExtraDTO> result = new ArrayList<>();

        for (ExtraDTO extra : currentData.getExtras()) {
            if (extra == null || extra.getTipo() == null) {
                continue;
            }

            String tipo = normalize(extra.getTipo());

            if (matchesExtraCard(card, tipo)) {
                result.add(extra);
            }
        }

        return result;
    }

    private List<PersonalizacionDTO> filterPreps() {
        List<PersonalizacionDTO> result = new ArrayList<>();

        for (PersonalizacionDTO p : currentData.getPersonalizaciones()) {
            if (p == null || p.getTipo() == null) {
                continue;
            }

            String tipo = normalize(p.getTipo());
            if ("PREP".equals(tipo)) {
                result.add(p);
            }
        }

        return result;
    }

    private List<ExtraDTO> filterFoodExtras() {
        List<ExtraDTO> result = new ArrayList<>();

        for (ExtraDTO extra : currentData.getExtras()) {
            if (extra == null || extra.getTipo() == null) {
                continue;
            }

            String tipo = normalize(extra.getTipo());
            if ("FOOD_EXTRA".equals(tipo)) {
                result.add(extra);
            }
        }

        return result;
    }

    private boolean matchesExtraCard(CustomizationCard card, String tipo) {
        return switch (card) {
            case CAFE -> false;
            case SHOTS -> "SHOT".equals(tipo) || "SHOTS".equals(tipo);
            case SYRUPS -> "SYRUP".equals(tipo) || "SYRUPS".equals(tipo);
            case TOPPINGS -> "TOPPING".equals(tipo) || "TOPPINGS".equals(tipo);
            case MILK -> "MILK".equals(tipo);
            case PREP -> false;
            case PREP_FOOD -> false;
            case OPCIONES_FOOD -> false;
        };
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    // =========================================================
    // BOTONES DINÁMICOS
    // =========================================================

    private void fireAskMeClicked() {
        if (actionListener != null) {
            actionListener.onAskMeClicked();
        }
    }

    private JButton createAskMeButton() {
        JButton b = new JButton(I18n.t("sales.custom.askMe"));
        b.setIcon(TpvIconFactory.info(18, new Color(28, 28, 22)));
        styleOptionButton(b);
        b.addActionListener(e -> fireAskMeClicked());
        return b;
    }

    private JButton createTipoCafeButton(TipoCafeDTO tipoCafe) {
        String text = buildTipoCafeButtonText(tipoCafe);

        JButton b = new JButton(text);
        b.setIcon(TpvIconFactory.product(18, new Color(28, 28, 22)));
        styleOptionButton(b);

        b.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onTipoCafeClicked(tipoCafe);
            }
        });

        return b;
    }

    private JButton createExtraButton(ExtraDTO extra) {
        JButton b = new JButton(buildButtonText(extra.getNombre(), extra.getPrecio()));
        b.setIcon(extra.isDisponible()
                ? TpvIconFactory.product(18, new Color(28, 28, 22))
                : TpvIconFactory.warning(18, Color.WHITE));
        styleExtraButton(b, extra);

        if (extra.isDisponible()) {
            b.addActionListener(e -> {
                if (actionListener != null) {
                    actionListener.onExtraClicked(extra);
                }
            });
        }

        return b;
    }

    private JButton createPersonalizacionButton(PersonalizacionDTO p) {
        JButton b = new JButton(buildButtonText(p.getNombre(), p.getPrecio()));
        b.setIcon(TpvIconFactory.settings(18, new Color(28, 28, 22)));
        styleOptionButton(b);

        b.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onPersonalizacionClicked(p);
            }
        });

        return b;
    }

    private void styleOptionButton(JButton b) {
        b.setFocusPainted(false);
        b.setFont(InformeUiTheme.FONT_BUTTON.deriveFont(Font.BOLD, 15f));
        b.setBackground(InformeUiTheme.ACCENT_GOLD);
        b.setForeground(new Color(28, 28, 22));
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setIconTextGap(8);
    }

    private void styleExtraButton(JButton b, ExtraDTO extra) {
        b.setFocusPainted(false);
        b.setFont(InformeUiTheme.FONT_BUTTON.deriveFont(Font.BOLD, 15f));
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        b.setIconTextGap(8);

        if (!extra.isDisponible()) {
            b.setEnabled(false);
            b.setBackground(new Color(96, 106, 100));
            b.setForeground(Color.WHITE);
            return;
        }

        b.setBackground(InformeUiTheme.ACCENT_GOLD);
        b.setForeground(new Color(28, 28, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String buildButtonText(String nombre, BigDecimal precio) {
        if (precio != null && precio.compareTo(BigDecimal.ZERO) > 0) {
            return nombre + " (+" + moneyFormat.format(precio) + ")";
        }
        return nombre;
    }

    private String buildTipoCafeButtonText(TipoCafeDTO tipoCafe) {
        String nombre = tipoCafe.getNombre() != null ? tipoCafe.getNombre() : I18n.t("sales.custom.card.cafe");

        if (tipoCafe.getSuplementoPrecio() != null
                && tipoCafe.getSuplementoPrecio().compareTo(BigDecimal.ZERO) > 0) {
            nombre += " (+" + moneyFormat.format(tipoCafe.getSuplementoPrecio()) + ")";
        }

        if (tipoCafe.isPorDefecto()) {
            nombre += " " + I18n.t("sales.custom.defaultTag");
        }

        return nombre;
    }

    private JComponent createEmptyState(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lbl.setFont(InformeUiTheme.FONT_BODY.deriveFont(Font.PLAIN, 16f));
        lbl.setIcon(TpvIconFactory.info(18, InformeUiTheme.TEXT_SECONDARY));
        lbl.setIconTextGap(8);

        JPanel wrapper = InformeUiTheme.createTransparentPanel(new BorderLayout());
        wrapper.add(lbl, BorderLayout.CENTER);
        return wrapper;
    }

    private Icon resolveCardIcon(CustomizationCard card, int size, Color color) {
        return switch (card) {
            case PREP, PREP_FOOD -> TpvIconFactory.settings(size, color);
            default -> TpvIconFactory.product(size, color);
        };
    }

    public void ensureValidCurrentCardForMode(enums.CustomizationMode mode) {
        if (mode == null) {
            return;
        }

        switch (mode) {
            case BEBIDA -> {
                boolean valid =
                        currentCard == CustomizationCard.CAFE ||
                        currentCard == CustomizationCard.SHOTS ||
                        currentCard == CustomizationCard.SYRUPS ||
                        currentCard == CustomizationCard.TOPPINGS ||
                        currentCard == CustomizationCard.MILK ||
                        currentCard == CustomizationCard.PREP;

                if (!valid) {
                    currentCard = CustomizationCard.CAFE;
                }
            }

            case COMIDA -> {
                boolean valid =
                        currentCard == CustomizationCard.OPCIONES_FOOD ||
                        currentCard == CustomizationCard.PREP_FOOD;

                if (!valid) {
                    currentCard = CustomizationCard.OPCIONES_FOOD;
                }
            }

            case VACIO -> {
                // no hacemos nada aquí
            }
        }

        showCard(currentCard);
    }
}
