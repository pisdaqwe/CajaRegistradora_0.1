package ui.ventas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import enums.TipoServicio;

/**
 * Panel encargado de pedir el nombre del pedido y el tipo de servicio
 * antes de pasar a la pantalla de pago.
 *
 * Responsabilidad:
 * - Mostrar el campo de nombre
 * - Permitir elegir entre "Para tomar" y "Para llevar"
 * - Emitir eventos al exterior mediante listener
 *
 * NO debe:
 * - tocar TicketSession
 * - tocar CobroSession
 * - decidir navegación global
 * - persistir nada
 *
 * La navegación la coordinará VentasFrame.
 */
public class NombrePedidoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Listener de eventos del panel.
     */
    public interface NombrePedidoListener {
        void onContinuar(String nombrePedido, TipoServicio tipoServicio);
        void onVolver();
    }

    // =====================================================
    // COLORES BASE
    // =====================================================

    private static final Color COLOR_FONDO = new Color(245, 239, 230);
    private static final Color COLOR_PANEL = new Color(255, 252, 247);
    private static final Color COLOR_TITULO = new Color(54, 69, 55);
    private static final Color COLOR_SUBTITULO = new Color(95, 88, 78);
    private static final Color COLOR_BOTON_PRINCIPAL = new Color(0, 92, 62);
    private static final Color COLOR_BOTON_SECUNDARIO = new Color(120, 90, 65);
    private static final Color COLOR_TEXTO_BLANCO = Color.WHITE;
    private static final Color COLOR_CAMPO = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(210, 200, 188);

    // =====================================================
    // COMPONENTES
    // =====================================================

    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;
    private final JTextField txtNombrePedido;

    private final JButton btnContinuar;
    private final JButton btnVolver;

    private final JToggleButton btnParaTomar;
    private final JToggleButton btnParaLlevar;
    private final ButtonGroup tipoServicioGroup;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public NombrePedidoPanel(NombrePedidoListener listener) {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // -------------------------------------------------
        // Card central
        // -------------------------------------------------
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // -------------------------------------------------
        // Título
        // -------------------------------------------------
        lblTitulo = new JLabel("Nombre para el pedido");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TITULO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        lblSubtitulo = new JLabel("Este nombre aparecerá en el ticket del cliente y en preparación");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblSubtitulo.setForeground(COLOR_SUBTITULO);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        // -------------------------------------------------
        // Campo de nombre
        // -------------------------------------------------
        txtNombrePedido = new JTextField();
        txtNombrePedido.setFont(new Font("SansSerif", Font.BOLD, 24));
        txtNombrePedido.setBackground(COLOR_CAMPO);
        txtNombrePedido.setForeground(Color.DARK_GRAY);
        txtNombrePedido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        txtNombrePedido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        txtNombrePedido.setPreferredSize(new Dimension(500, 58));

        // -------------------------------------------------
        // Botones de tipo de servicio
        // -------------------------------------------------
        tipoServicioGroup = new ButtonGroup();

        btnParaTomar = new JToggleButton("Para tomar");
        styleToggleButton(btnParaTomar);

        btnParaLlevar = new JToggleButton("Para llevar");
        styleToggleButton(btnParaLlevar);

        tipoServicioGroup.add(btnParaTomar);
        tipoServicioGroup.add(btnParaLlevar);

        // Valor por defecto
        btnParaTomar.setSelected(true);

        JPanel pnlBotonesServicio = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        pnlBotonesServicio.setOpaque(false);
        pnlBotonesServicio.add(btnParaTomar);
        pnlBotonesServicio.add(btnParaLlevar);

        // -------------------------------------------------
        // Botones de navegación
        // -------------------------------------------------
        btnVolver = new JButton("Volver");
        btnVolver.setFocusPainted(false);
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnVolver.setBackground(COLOR_BOTON_SECUNDARIO);
        btnVolver.setForeground(COLOR_TEXTO_BLANCO);
        btnVolver.setPreferredSize(new Dimension(150, 46));

        btnContinuar = new JButton("Continuar");
        btnContinuar.setFocusPainted(false);
        btnContinuar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnContinuar.setBackground(COLOR_BOTON_PRINCIPAL);
        btnContinuar.setForeground(COLOR_TEXTO_BLANCO);
        btnContinuar.setPreferredSize(new Dimension(170, 46));

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnVolver);
        pnlBotones.add(btnContinuar);

        // -------------------------------------------------
        // Montaje visual
        // -------------------------------------------------
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblSubtitulo);
        card.add(Box.createVerticalStrut(25));
        card.add(txtNombrePedido);
        card.add(Box.createVerticalStrut(20));
        card.add(pnlBotonesServicio);
        card.add(Box.createVerticalStrut(25));
        card.add(pnlBotones);

        add(card, BorderLayout.CENTER);

        // -------------------------------------------------
        // Eventos
        // -------------------------------------------------
        btnVolver.addActionListener(e -> listener.onVolver());

        btnContinuar.addActionListener(e ->
                listener.onContinuar(txtNombrePedido.getText(), getTipoServicio())
        );

        // ENTER en el campo = continuar
        txtNombrePedido.addActionListener(e ->
                listener.onContinuar(txtNombrePedido.getText(), getTipoServicio())
        );

        // Opcional: si quieres reaccionar al cambio visual, aquí van ambos
        btnParaTomar.addActionListener(e -> onTipoServicioChanged());
        btnParaLlevar.addActionListener(e -> onTipoServicioChanged());
    }

    // =====================================================
    // HELPERS VISUALES
    // =====================================================

    private void styleToggleButton(JToggleButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(COLOR_BOTON_PRINCIPAL);
        button.setForeground(COLOR_TEXTO_BLANCO);
        button.setPreferredSize(new Dimension(170, 46));
    }

    /**
     * Hook por si más adelante quieres cambiar colores, texto o comportamiento
     * al alternar entre "Para tomar" y "Para llevar".
     *
     * De momento no hace nada porque el ButtonGroup ya gestiona la exclusividad.
     */
    private void onTipoServicioChanged() {
        // No-op por ahora
    }

    // =====================================================
    // API PÚBLICA
    // =====================================================

    /**
     * Carga un nombre en el campo.
     */
    public void setNombrePedido(String nombre) {
        txtNombrePedido.setText(nombre != null ? nombre : "");
    }

    /**
     * Devuelve el texto actual del campo.
     */
    public String getNombrePedido() {
        return txtNombrePedido.getText();
    }

    /**
     * Marca visualmente el tipo de servicio.
     */
    public void setTipoServicio(TipoServicio tipoServicio) {
        if (tipoServicio == TipoServicio.PARA_LLEVAR) {
            btnParaLlevar.setSelected(true);
        } else {
            btnParaTomar.setSelected(true);
        }
    }

    /**
     * Devuelve el tipo de servicio seleccionado actualmente.
     */
    public TipoServicio getTipoServicio() {
        if (btnParaLlevar.isSelected()) {
            return TipoServicio.PARA_LLEVAR;
        }
        return TipoServicio.PARA_TOMAR;
    }

    /**
     * Limpia el panel y deja el servicio por defecto en "Para tomar".
     */
    public void clear() {
        txtNombrePedido.setText("");
        setTipoServicio(TipoServicio.PARA_TOMAR);
    }

    /**
     * Pone el foco en el campo y selecciona su contenido.
     */
    public void requestFocusInField() {
        txtNombrePedido.requestFocusInWindow();
        txtNombrePedido.selectAll();
    }
}
