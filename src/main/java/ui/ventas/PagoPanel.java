package ui.ventas;

import enums.TipoServicio;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Panel del flujo de pago.
 */
public class PagoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public interface PagoPanelListener {
        void onVolver();
        void onCobroEfectivo(BigDecimal importeRecibido);
        void onCobroEfectivoExacto();
        void onCobroTarjeta();
    }

    private final DecimalFormat moneyFormat;

    private BigDecimal totalActual = BigDecimal.ZERO;

    private final JLabel lblNombrePedido;
    private final JLabel lblTipoServicio;
    private final JLabel lblTotal;
    private final JTextField txtImporte;

    private final JButton btn7;
    private final JButton btn8;
    private final JButton btn9;
    private final JButton btn4;
    private final JButton btn5;
    private final JButton btn6;
    private final JButton btn1;
    private final JButton btn2;
    private final JButton btn3;
    private final JButton btnComa;
    private final JButton btn0;
    private final JButton btnBorrar;

    private final JButton btnLimpiar;
    private final JButton btnVolver;
    private final JButton btnTarjeta;
    private final JButton btnEfectivo;
    private final JButton btnEfectivoExacto;

    public PagoPanel(PagoPanelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("PagoPanelListener no puede ser null");
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        this.moneyFormat = new DecimalFormat("#,##0.00", symbols);
        this.moneyFormat.setParseBigDecimal(true);

        setLayout(new BorderLayout());
        setBackground(InformeUiTheme.APP_BG);
        setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(14, 14));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);

        JLabel lblTitulo = new JLabel("PAGO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JPanel resumen = new JPanel(new GridLayout(1, 3, 12, 0));
        resumen.setOpaque(false);

        lblNombrePedido = createInfoLabel("Pedido: Cliente");
        lblTipoServicio = createInfoLabel("Servicio: Para tomar");
        lblTotal = createTotalLabel("Total: 0,00 €");

        resumen.add(createInfoBox("Pedido", lblNombrePedido));
        resumen.add(createInfoBox("Servicio", lblTipoServicio));
        resumen.add(createInfoBox("Total", lblTotal));

        header.add(lblTitulo, BorderLayout.NORTH);
        header.add(resumen, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);

        txtImporte = new JTextField();
        txtImporte.setEditable(false);
        txtImporte.setFocusable(false);
        txtImporte.setHorizontalAlignment(SwingConstants.RIGHT);
        txtImporte.setFont(new Font("SansSerif", Font.BOLD, 30));
        txtImporte.setBackground(InformeUiTheme.CARD_BG_2);
        txtImporte.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtImporte.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtImporte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        txtImporte.setPreferredSize(new Dimension(300, 62));
        txtImporte.setText("");

        JPanel teclado = new JPanel(new GridLayout(4, 3, 8, 8));
        teclado.setOpaque(false);

        btn7 = createNumericButton("7");
        btn8 = createNumericButton("8");
        btn9 = createNumericButton("9");
        btn4 = createNumericButton("4");
        btn5 = createNumericButton("5");
        btn6 = createNumericButton("6");
        btn1 = createNumericButton("1");
        btn2 = createNumericButton("2");
        btn3 = createNumericButton("3");
        btnComa = createActionButton(",");
        btn0 = createNumericButton("0");
        btnBorrar = createActionButton("⌫");

        teclado.add(btn7);
        teclado.add(btn8);
        teclado.add(btn9);
        teclado.add(btn4);
        teclado.add(btn5);
        teclado.add(btn6);
        teclado.add(btn1);
        teclado.add(btn2);
        teclado.add(btn3);
        teclado.add(btnComa);
        teclado.add(btn0);
        teclado.add(btnBorrar);

        center.add(txtImporte, BorderLayout.NORTH);
        center.add(teclado, BorderLayout.CENTER);

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));

        btnLimpiar = createSecondaryActionButton("Limpiar");
        btnVolver = createSecondaryActionButton("Volver");
        btnTarjeta = createCardPaymentButton("Tarjeta");
        btnEfectivo = createPrimaryPaymentButton("Efectivo");
        btnEfectivoExacto = createPrimaryPaymentButton("Efectivo exacto");

        addActionButton(acciones, btnLimpiar);
        acciones.add(Box.createVerticalStrut(8));
        addActionButton(acciones, btnVolver);
        acciones.add(Box.createVerticalStrut(18));
        addActionButton(acciones, btnTarjeta);
        acciones.add(Box.createVerticalStrut(8));
        addActionButton(acciones, btnEfectivo);
        acciones.add(Box.createVerticalStrut(8));
        addActionButton(acciones, btnEfectivoExacto);

        card.add(header, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(acciones, BorderLayout.EAST);

        add(card, BorderLayout.CENTER);

        btn0.addActionListener(e -> appendDigit("0"));
        btn1.addActionListener(e -> appendDigit("1"));
        btn2.addActionListener(e -> appendDigit("2"));
        btn3.addActionListener(e -> appendDigit("3"));
        btn4.addActionListener(e -> appendDigit("4"));
        btn5.addActionListener(e -> appendDigit("5"));
        btn6.addActionListener(e -> appendDigit("6"));
        btn7.addActionListener(e -> appendDigit("7"));
        btn8.addActionListener(e -> appendDigit("8"));
        btn9.addActionListener(e -> appendDigit("9"));

        btnComa.addActionListener(e -> appendComma());
        btnBorrar.addActionListener(e -> borrarUltimoCaracter());
        btnLimpiar.addActionListener(e -> limpiarImporte());

        btnVolver.addActionListener(e -> listener.onVolver());

        btnTarjeta.addActionListener(e -> listener.onCobroTarjeta());

        btnEfectivo.addActionListener(e -> {
            BigDecimal importe = getImporte();
            listener.onCobroEfectivo(importe);
        });

        btnEfectivoExacto.addActionListener(e -> listener.onCobroEfectivoExacto());
    }

    private void addActionButton(JPanel panel, JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(button);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return label;
    }

    private JLabel createTotalLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        label.setForeground(InformeUiTheme.ACCENT_GOLD);
        return label;
    }

    private JPanel createInfoBox(String title, JLabel valueLabel) {
        JPanel box = new JPanel(new BorderLayout(0, 3));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        box.add(lblTitle, BorderLayout.NORTH);
        box.add(valueLabel, BorderLayout.CENTER);

        return box;
    }

    public void setData(String nombrePedido, TipoServicio tipoServicio, BigDecimal total) {
        String nombreFinal = (nombrePedido != null && !nombrePedido.isBlank())
                ? nombrePedido.trim()
                : "Cliente";

        String servicioFinal = (tipoServicio == TipoServicio.PARA_LLEVAR)
                ? "Para llevar"
                : "Para tomar";

        this.totalActual = (total != null) ? total : BigDecimal.ZERO;

        lblNombrePedido.setText(nombreFinal);
        lblTipoServicio.setText(servicioFinal);
        lblTotal.setText(formatMoney(this.totalActual) + " €");

        limpiarImporte();
    }

    public void clear() {
        this.totalActual = BigDecimal.ZERO;
        lblNombrePedido.setText("Cliente");
        lblTipoServicio.setText("Para tomar");
        lblTotal.setText("0,00 €");
        txtImporte.setText("");
    }

    public void setImporte(BigDecimal importe) {
        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            txtImporte.setText("");
            return;
        }

        txtImporte.setText(normalizeBigDecimalForDisplay(importe));
    }

    public BigDecimal getImporte() {
        return parseImporte(txtImporte.getText());
    }

    public void appendDigit(String digit) {
        if (digit == null || digit.isBlank()) {
            return;
        }

        String current = txtImporte.getText();

        if (current.contains(",")) {
            int indexComma = current.indexOf(",");
            int decimals = current.length() - indexComma - 1;

            if (decimals >= 2) {
                return;
            }
        }

        if ("0".equals(current) && !"0".equals(digit)) {
            txtImporte.setText(digit);
            return;
        }

        txtImporte.setText(current + digit);
    }

    public void appendComma() {
        String current = txtImporte.getText();

        if (current.contains(",")) {
            return;
        }

        if (current.isBlank()) {
            txtImporte.setText("0,");
            return;
        }

        txtImporte.setText(current + ",");
    }

    public void borrarUltimoCaracter() {
        String current = txtImporte.getText();

        if (current == null || current.isEmpty()) {
            return;
        }

        txtImporte.setText(current.substring(0, current.length() - 1));
    }

    public void limpiarImporte() {
        txtImporte.setText("");
    }

    public void requestFocusDefault() {
        btnEfectivo.requestFocusInWindow();
    }

    public BigDecimal getTotalActual() {
        return totalActual;
    }

    private JButton createNumericButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 21));
        button.setBackground(InformeUiTheme.CARD_BG_2);
        button.setForeground(InformeUiTheme.TEXT_PRIMARY);
        button.setPreferredSize(new Dimension(86, 58));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createActionButton(String text) {
        JButton button = createNumericButton(text);
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        button.setFont(new Font("SansSerif", Font.BOLD, 19));
        return button;
    }

    private JButton createSecondaryActionButton(String text) {
        JButton button = new JButton(text);
        InformeUiTheme.styleSecondaryButton(button);
        button.setMaximumSize(new Dimension(170, 42));
        button.setPreferredSize(new Dimension(170, 42));
        return button;
    }

    private JButton createPrimaryPaymentButton(String text) {
        JButton button = new JButton(text);
        InformeUiTheme.stylePrimaryButton(button);
        button.setMaximumSize(new Dimension(170, 48));
        button.setPreferredSize(new Dimension(170, 48));
        return button;
    }

    private JButton createCardPaymentButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(InformeUiTheme.ACCENT_GOLD);
        button.setForeground(new Color(25, 25, 25));
        button.setMaximumSize(new Dimension(170, 48));
        button.setPreferredSize(new Dimension(170, 48));
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private BigDecimal parseImporte(String text) {
        if (text == null || text.isBlank()) {
            return BigDecimal.ZERO;
        }

        String normalized = text.trim();

        if (normalized.endsWith(",")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (normalized.isBlank()) {
            return BigDecimal.ZERO;
        }

        normalized = normalized.replace(".", "");
        normalized = normalized.replace(",", ".");

        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String formatMoney(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return moneyFormat.format(safe);
    }

    private String normalizeBigDecimalForDisplay(BigDecimal amount) {
        return formatMoney(amount);
    }
}