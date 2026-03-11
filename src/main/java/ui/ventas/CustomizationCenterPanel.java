package ui.ventas;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoCustomizationDTO;
import enums.CustomizationCard;

import javax.swing.BorderFactory;
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
import java.awt.Component;
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
 * RESPONSABILIDAD:
 * - Vive dentro de CARD_CUSTOM de VentasCenterPanel.
 * - Contiene un CardLayout interno.
 * - Cada card representa una categoría de customización:
 *   SHOTS, SYRUPS, TOPPINGS, MILK, PREP.
 *
 * IMPORTANTE:
 * - NO toca TicketSession directamente.
 * - Recibe ProductoCustomizationDTO desde fuera.
 * - Emite callbacks al pulsar opciones.
 */
public class CustomizationCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final CardLayout cardLayout = new CardLayout();
    private CustomizationCard currentCard = CustomizationCard.MILK;

    /**
     * Guardamos el grid de cada card para reconstruirlo dinámicamente.
     */
    private final Map<CustomizationCard, JPanel> gridByCard = new EnumMap<>(CustomizationCard.class);

    /**
     * Datos actualmente cargados.
     */
    private ProductoCustomizationDTO currentData =
            new ProductoCustomizationDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    /**
     * Listener hacia fuera.
     */
    private CustomizationActionListener actionListener;

    private final DecimalFormat moneyFormat = new DecimalFormat("0.00 €");

    public interface CustomizationActionListener {
        void onExtraClicked(ExtraDTO extra);
        void onPersonalizacionClicked(PersonalizacionDTO personalizacion);
        void onAskMeClicked();
    }

    public CustomizationCenterPanel() {
        setLayout(cardLayout);
        setBackground(new Color(20, 20, 20));

        add(buildEmptyDynamicCard("SHOTS", CustomizationCard.SHOTS), CustomizationCard.SHOTS.name());
        add(buildEmptyDynamicCard("SYRUPS", CustomizationCard.SYRUPS), CustomizationCard.SYRUPS.name());
        add(buildEmptyDynamicCard("TOPPINGS", CustomizationCard.TOPPINGS), CustomizationCard.TOPPINGS.name());
        add(buildEmptyDynamicCard("MILK", CustomizationCard.MILK), CustomizationCard.MILK.name());
        add(buildEmptyDynamicCard("PREP", CustomizationCard.PREP), CustomizationCard.PREP.name());
        add(buildEmptyDynamicCard("PREP_FOOD", CustomizationCard.PREP_FOOD),CustomizationCard.PREP_FOOD.name());
        add(buildEmptyDynamicCard("OPCIONES_FOOD", CustomizationCard.OPCIONES_FOOD),CustomizationCard.OPCIONES_FOOD.name());
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
        if (card == null) return;
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
                new ArrayList<>()
        );

        rebuildAllCards();

        // Mantener también aquí la card actual
        showCard(currentCard);
    }

    // =========================================================
    // CONSTRUCCIÓN BASE DE CARDS
    // =========================================================

    private JComponent buildEmptyDynamicCard(String title, CustomizationCard card) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(new Color(20, 20, 20));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);

        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setOpaque(false);

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(20, 20, 20));
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
        rebuildExtrasCard(CustomizationCard.SHOTS, filterExtrasByCard(CustomizationCard.SHOTS));
        rebuildExtrasCard(CustomizationCard.SYRUPS, filterExtrasByCard(CustomizationCard.SYRUPS));
        rebuildExtrasCard(CustomizationCard.TOPPINGS, filterExtrasByCard(CustomizationCard.TOPPINGS));
        rebuildExtrasCard(CustomizationCard.MILK, filterExtrasByCard(CustomizationCard.MILK));

        rebuildExtrasCard(CustomizationCard.OPCIONES_FOOD, filterFoodExtras());

        List<PersonalizacionDTO> preps = filterPreps();
        rebuildPrepCard(CustomizationCard.PREP, preps);
        rebuildPrepCard(CustomizationCard.PREP_FOOD, preps);
    }

    private void rebuildExtrasCard(CustomizationCard card, List<ExtraDTO> extras) {
        JPanel grid = gridByCard.get(card);
        if (grid == null) return;

        grid.removeAll();

        if (extras.isEmpty()) {
            grid.add(createEmptyState("No hay opciones disponibles"));
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
        if (grid == null) return;

        grid.removeAll();

        // Botón fijo Ask Me siempre visible en PREP y PREP_FOOD
        grid.add(createAskMeButton());

        if (preps.isEmpty()) {
            grid.add(createEmptyState("No hay opciones disponibles"));
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

    private List<ExtraDTO> filterExtrasByCard(CustomizationCard card) {
        List<ExtraDTO> result = new ArrayList<>();

        for (ExtraDTO extra : currentData.getExtras()) {
            if (extra == null || extra.getTipo() == null) continue;

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
            if (p == null || p.getTipo() == null) continue;

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
            if (extra == null || extra.getTipo() == null) continue;

            String tipo = normalize(extra.getTipo());
            if ("FOOD_EXTRA".equals(tipo)) {
                result.add(extra);
            }
        }

        return result;
    }

    private boolean matchesExtraCard(CustomizationCard card, String tipo) {
        return switch (card) {
            case SHOTS -> "SHOT".equals(tipo) || "SHOTS".equals(tipo);
            case SYRUPS -> "SYRUP".equals(tipo) || "SYRUPS".equals(tipo);
            case TOPPINGS -> "TOPPING".equals(tipo) || "TOPPINGS".equals(tipo);
            case MILK -> "MILK".equals(tipo);
            case PREP -> false;
            case PREP_FOOD ->false;
            case OPCIONES_FOOD ->false;
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
        JButton b = new JButton("ASK ME");
        styleOptionButton(b);

        b.addActionListener(e -> fireAskMeClicked());

        return b;
    }

    private JButton createExtraButton(ExtraDTO extra) {
        JButton b = new JButton(buildButtonText(extra.getNombre(), extra.getPrecio()));
        styleOptionButton(b);

        b.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onExtraClicked(extra);
            }
        });

        return b;
    }

    private JButton createPersonalizacionButton(PersonalizacionDTO p) {
        JButton b = new JButton(buildButtonText(p.getNombre(), p.getPrecio()));
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
        b.setFont(new Font("Monospaced", Font.BOLD, 15));
        b.setBackground(new Color(255, 210, 0));
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
    }

    private String buildButtonText(String nombre, BigDecimal precio) {
        if (precio != null && precio.compareTo(BigDecimal.ZERO) > 0) {
            return nombre + " (+" + moneyFormat.format(precio) + ")";
        }
        return nombre;
    }

    private JComponent createEmptyState(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(new Color(210, 210, 210));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(lbl, BorderLayout.CENTER);
        return wrapper;
    }
    public void ensureValidCurrentCardForMode(enums.CustomizationMode mode) {
        if (mode == null) return;

        switch (mode) {
            case BEBIDA -> {
                boolean valid =
                        currentCard == CustomizationCard.SHOTS ||
                        currentCard == CustomizationCard.SYRUPS ||
                        currentCard == CustomizationCard.TOPPINGS ||
                        currentCard == CustomizationCard.MILK ||
                        currentCard == CustomizationCard.PREP;

                if (!valid) {
                    currentCard = CustomizationCard.MILK;
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