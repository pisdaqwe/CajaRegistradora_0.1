package ui.ventas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import enums.TipoServicio;

/**
 * Panel del flujo de pago.
 *
 * Responsabilidad:
 * - Mostrar nombre del pedido
 * - Mostrar tipo de servicio (para tomar / para llevar)
 * - Mostrar total
 * - Permitir introducir importe recibido mediante teclado numérico
 * - Emitir callbacks cuando el usuario quiera cobrar en efectivo,
 *   cobrar en efectivo exacto o volver atrás
 *
 * NO debe:
 * - tocar TicketSession
 * - tocar CobroSession
 * - persistir en BD
 * - decidir el flujo global de navegación
 *
 * La coordinación de pantallas y la lógica de negocio
 * se hará desde VentasFrame.
 */
public class PagoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Listener de eventos del panel.
     */
    public interface PagoPanelListener {
        void onVolver();
        void onCobroEfectivo(BigDecimal importeRecibido);
        void onCobroEfectivoExacto();
    }

    // =====================================================
    // COLORES BASE
    // =====================================================

    private static final Color COLOR_FONDO = new Color(245, 239, 230);
    private static final Color COLOR_PANEL = new Color(255, 252, 247);
    private static final Color COLOR_TITULO = new Color(54, 69, 55);
    private static final Color COLOR_SUBTITULO = new Color(95, 88, 78);
    private static final Color COLOR_BORDE = new Color(210, 200, 188);

    private static final Color COLOR_DISPLAY = Color.WHITE;
    private static final Color COLOR_TEXTO_DISPLAY = new Color(45, 45, 45);

    private static final Color COLOR_BOTON_NUM = new Color(241, 234, 224);
    private static final Color COLOR_BOTON_NUM_TEXTO = new Color(45, 45, 45);

    private static final Color COLOR_BOTON_ACCION = new Color(120, 90, 65);
    private static final Color COLOR_BOTON_ACCION_TEXTO = Color.WHITE;

    private static final Color COLOR_BOTON_PAGO = new Color(0, 92, 62);
    private static final Color COLOR_BOTON_PAGO_TEXTO = Color.WHITE;

    // =====================================================
    // FORMATEO
    // =====================================================

    private final DecimalFormat moneyFormat;

    // =====================================================
    // ESTADO VISUAL
    // =====================================================

    private BigDecimal totalActual = BigDecimal.ZERO;

    // =====================================================
    // COMPONENTES
    // =====================================================

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
    private final JButton btnEfectivo;
    private final JButton btnEfectivoExacto;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PagoPanel(PagoPanelListener listener) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        this.moneyFormat = new DecimalFormat("#,##0.00", symbols);
        this.moneyFormat.setParseBigDecimal(true);

        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel card = new JPanel(new BorderLayout(20, 20));
        card.setBackground(COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));

        // -------------------------------------------------
        // CABECERA
        // -------------------------------------------------
        JPanel pnlHeader = new JPanel();
        pnlHeader.setOpaque(false);
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Pago");
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TITULO);

        lblNombrePedido = new JLabel("Pedido: Cliente");
        lblNombrePedido.setAlignmentX(CENTER_ALIGNMENT);
        lblNombrePedido.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblNombrePedido.setForeground(COLOR_SUBTITULO);

        lblTipoServicio = new JLabel("Servicio: Para tomar");
        lblTipoServicio.setAlignmentX(CENTER_ALIGNMENT);
        lblTipoServicio.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTipoServicio.setForeground(COLOR_SUBTITULO);

        lblTotal = new JLabel("Total: 0,00 €");
        lblTotal.setAlignmentX(CENTER_ALIGNMENT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotal.setForeground(COLOR_TITULO);

        pnlHeader.add(lblTitulo);
        pnlHeader.add(Box.createVerticalStrut(10));
        pnlHeader.add(lblNombrePedido);
        pnlHeader.add(Box.createVerticalStrut(8));
        pnlHeader.add(lblTipoServicio);
        pnlHeader.add(Box.createVerticalStrut(8));
        pnlHeader.add(lblTotal);

        // -------------------------------------------------
        // CENTRO: DISPLAY + TECLADO
        // -------------------------------------------------
        JPanel pnlCentro = new JPanel(new BorderLayout(18, 18));
        pnlCentro.setOpaque(false);

        txtImporte = new JTextField();
        txtImporte.setEditable(false);
        txtImporte.setHorizontalAlignment(SwingConstants.RIGHT);
        txtImporte.setFont(new Font("SansSerif", Font.BOLD, 32));
        txtImporte.setBackground(COLOR_DISPLAY);
        txtImporte.setForeground(COLOR_TEXTO_DISPLAY);
        txtImporte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        txtImporte.setPreferredSize(new Dimension(300, 70));
        txtImporte.setText("");

        JPanel pnlTeclado = new JPanel(new GridLayout(4, 3, 10, 10));
        pnlTeclado.setOpaque(false);

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

        pnlTeclado.add(btn7);
        pnlTeclado.add(btn8);
        pnlTeclado.add(btn9);

        pnlTeclado.add(btn4);
        pnlTeclado.add(btn5);
        pnlTeclado.add(btn6);

        pnlTeclado.add(btn1);
        pnlTeclado.add(btn2);
        pnlTeclado.add(btn3);

        pnlTeclado.add(btnComa);
        pnlTeclado.add(btn0);
        pnlTeclado.add(btnBorrar);

        pnlCentro.add(txtImporte, BorderLayout.NORTH);
        pnlCentro.add(pnlTeclado, BorderLayout.CENTER);

        // -------------------------------------------------
        // DERECHA: ACCIONES
        // -------------------------------------------------
        JPanel pnlAcciones = new JPanel();
        pnlAcciones.setOpaque(false);
        pnlAcciones.setLayout(new BoxLayout(pnlAcciones, BoxLayout.Y_AXIS));

        btnLimpiar = createSecondaryActionButton("Limpiar");
        btnVolver = createSecondaryActionButton("Volver");
        btnEfectivo = createPrimaryPaymentButton("Efectivo");
        btnEfectivoExacto = createPrimaryPaymentButton("Efectivo exacto");

        btnLimpiar.setAlignmentX(CENTER_ALIGNMENT);
        btnVolver.setAlignmentX(CENTER_ALIGNMENT);
        btnEfectivo.setAlignmentX(CENTER_ALIGNMENT);
        btnEfectivoExacto.setAlignmentX(CENTER_ALIGNMENT);

        pnlAcciones.add(btnLimpiar);
        pnlAcciones.add(Box.createVerticalStrut(12));
        pnlAcciones.add(btnVolver);
        pnlAcciones.add(Box.createVerticalStrut(24));
        pnlAcciones.add(btnEfectivo);
        pnlAcciones.add(Box.createVerticalStrut(12));
        pnlAcciones.add(btnEfectivoExacto);

        card.add(pnlHeader, BorderLayout.NORTH);
        card.add(pnlCentro, BorderLayout.CENTER);
        card.add(pnlAcciones, BorderLayout.EAST);

        add(card, BorderLayout.CENTER);

        // -------------------------------------------------
        // EVENTOS DEL TECLADO
        // -------------------------------------------------
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

        // -------------------------------------------------
        // EVENTOS DE NAVEGACIÓN / COBRO
        // -------------------------------------------------
        btnVolver.addActionListener(e -> listener.onVolver());

        btnEfectivo.addActionListener(e -> {
            BigDecimal importe = getImporte();
            listener.onCobroEfectivo(importe);
        });

        btnEfectivoExacto.addActionListener(e -> listener.onCobroEfectivoExacto());
    }

    // =====================================================
    // API PÚBLICA
    // =====================================================

    /**
     * Carga los datos visibles del panel al entrar en la pantalla de pago.
     */
    public void setData(String nombrePedido, TipoServicio tipoServicio, BigDecimal total) {
        String nombreFinal = (nombrePedido != null && !nombrePedido.isBlank())
                ? nombrePedido.trim()
                : "Cliente";

        String servicioFinal = (tipoServicio == TipoServicio.PARA_LLEVAR)
                ? "Para llevar"
                : "Para tomar";

        this.totalActual = (total != null) ? total : BigDecimal.ZERO;

        lblNombrePedido.setText("Pedido: " + nombreFinal);
        lblTipoServicio.setText("Servicio: " + servicioFinal);
        lblTotal.setText("Total: " + formatMoney(this.totalActual) + " €");

        limpiarImporte();
    }

    /**
     * Limpia completamente el panel.
     */
    public void clear() {
        this.totalActual = BigDecimal.ZERO;
        lblNombrePedido.setText("Pedido: Cliente");
        lblTipoServicio.setText("Servicio: Para tomar");
        lblTotal.setText("Total: 0,00 €");
        txtImporte.setText("");
    }

    /**
     * Permite cargar manualmente un importe en el display.
     */
    public void setImporte(BigDecimal importe) {
        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            txtImporte.setText("");
            return;
        }

        txtImporte.setText(normalizeBigDecimalForDisplay(importe));
    }

    /**
     * Devuelve el importe introducido en el display.
     */
    public BigDecimal getImporte() {
        return parseImporte(txtImporte.getText());
    }

    /**
     * Añade un dígito al display.
     *
     * Regla:
     * - si ya hay coma, solo permite 2 decimales
     */
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

        txtImporte.setText(current + digit);
    }

    /**
     * Añade coma decimal al display.
     *
     * Reglas:
     * - si está vacío, pone "0,"
     * - solo se permite una coma
     */
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

    /**
     * Borra el último carácter.
     */
    public void borrarUltimoCaracter() {
        String current = txtImporte.getText();
        if (current == null || current.isEmpty()) {
            return;
        }

        txtImporte.setText(current.substring(0, current.length() - 1));
    }

    /**
     * Limpia el importe del display.
     */
    public void limpiarImporte() {
        txtImporte.setText("");
    }

    /**
     * Permite mover el foco a una acción por defecto.
     */
    public void requestFocusDefault() {
        btnEfectivo.requestFocusInWindow();
    }

    /**
     * Devuelve el total actualmente cargado.
     */
    public BigDecimal getTotalActual() {
        return totalActual;
    }

    // =====================================================
    // HELPERS PRIVADOS
    // =====================================================

    private JButton createNumericButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setBackground(COLOR_BOTON_NUM);
        button.setForeground(COLOR_BOTON_NUM_TEXTO);
        button.setPreferredSize(new Dimension(90, 70));
        return button;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBackground(COLOR_BOTON_ACCION);
        button.setForeground(COLOR_BOTON_ACCION_TEXTO);
        button.setPreferredSize(new Dimension(90, 70));
        return button;
    }

    private JButton createSecondaryActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(COLOR_BOTON_ACCION);
        button.setForeground(COLOR_BOTON_ACCION_TEXTO);
        button.setMaximumSize(new Dimension(180, 48));
        button.setPreferredSize(new Dimension(180, 48));
        return button;
    }

    private JButton createPrimaryPaymentButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setBackground(COLOR_BOTON_PAGO);
        button.setForeground(COLOR_BOTON_PAGO_TEXTO);
        button.setMaximumSize(new Dimension(180, 58));
        button.setPreferredSize(new Dimension(180, 58));
        return button;
    }

    /**
     * Convierte el texto del display a BigDecimal.
     */
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

    /**
     * Formatea importes monetarios para labels.
     */
    private String formatMoney(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return moneyFormat.format(safe);
    }

    /**
     * Convierte un BigDecimal a texto de display con coma decimal, sin símbolo €.
     */
    private String normalizeBigDecimalForDisplay(BigDecimal amount) {
        return formatMoney(amount);
    }
}
