package ui.ventas;

import enums.TipoServicio;
import ui.common.TecladoVirtualDialog;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel encargado de pedir el nombre del pedido y el tipo de servicio
 * antes de pasar a la pantalla de pago.
 */
public class NombrePedidoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public interface NombrePedidoListener {
        void onContinuar(String nombrePedido, TipoServicio tipoServicio);
        void onVolver();
    }

    private static final int MAX_NOMBRE_PEDIDO = 30;

    private final JTextField txtNombrePedido;

    private final JButton btnContinuar;
    private final JButton btnVolver;
    private final JButton btnTeclado;

    private final JToggleButton btnParaTomar;
    private final JToggleButton btnParaLlevar;
    private final ButtonGroup tipoServicioGroup;

    public NombrePedidoPanel(NombrePedidoListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("NombrePedidoListener no puede ser null");
        }

        setLayout(new BorderLayout());
        setBackground(InformeUiTheme.APP_BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(22, 24, 22, 24)
        ));

        card.add(buildHeader(), BorderLayout.NORTH);

        txtNombrePedido = new JTextField();
        configurarCampoNombre();

        btnTeclado = new JButton("⌨");
        InformeUiTheme.styleSecondaryButton(btnTeclado);
        btnTeclado.setPreferredSize(new Dimension(58, 48));
        btnTeclado.setToolTipText("Abrir teclado táctil");
        btnTeclado.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtNombrePedido,
                        "Teclado - Nombre del pedido",
                        MAX_NOMBRE_PEDIDO
                )
        );

        tipoServicioGroup = new ButtonGroup();

        btnParaTomar = new JToggleButton("Para tomar");
        btnParaLlevar = new JToggleButton("Para llevar");

        configurarToggleServicio(btnParaTomar);
        configurarToggleServicio(btnParaLlevar);

        tipoServicioGroup.add(btnParaTomar);
        tipoServicioGroup.add(btnParaLlevar);
        btnParaTomar.setSelected(true);
        actualizarEstiloTipoServicio();

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(buildNombreSection());
        center.add(Box.createVerticalStrut(18));
        center.add(buildServicioSection());

        card.add(center, BorderLayout.CENTER);

        btnVolver = new JButton("Volver");
        InformeUiTheme.styleSecondaryButton(btnVolver);
        btnVolver.setPreferredSize(new Dimension(140, 44));

        btnContinuar = new JButton("Continuar");
        InformeUiTheme.stylePrimaryButton(btnContinuar);
        btnContinuar.setPreferredSize(new Dimension(160, 44));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnVolver);
        actions.add(btnContinuar);

        card.add(actions, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        btnVolver.addActionListener(e -> listener.onVolver());

        btnContinuar.addActionListener(e ->
                listener.onContinuar(txtNombrePedido.getText(), getTipoServicio())
        );

        txtNombrePedido.addActionListener(e ->
                listener.onContinuar(txtNombrePedido.getText(), getTipoServicio())
        );

        btnParaTomar.addActionListener(e -> {
            actualizarEstiloTipoServicio();
        });

        btnParaLlevar.addActionListener(e -> {
            actualizarEstiloTipoServicio();
        });
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("NOMBRE DEL PEDIDO", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Aparecerá en el ticket del cliente y en preparación", SwingConstants.CENTER);
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);

        return header;
    }

    private JComponent buildNombreSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        JLabel label = InformeUiTheme.createFieldLabel("Nombre del pedido");

        JPanel inputWrapper = new JPanel(new BorderLayout(8, 0));
        inputWrapper.setOpaque(false);
        inputWrapper.add(txtNombrePedido, BorderLayout.CENTER);
        inputWrapper.add(btnTeclado, BorderLayout.EAST);

        panel.add(label, BorderLayout.NORTH);
        panel.add(inputWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildServicioSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel label = InformeUiTheme.createFieldLabel("Tipo de servicio");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setOpaque(false);
        buttons.setMaximumSize(new Dimension(420, 48));
        buttons.add(btnParaTomar);
        buttons.add(btnParaLlevar);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(buttons);

        panel.add(label, BorderLayout.NORTH);
        panel.add(wrapper, BorderLayout.CENTER);

        return panel;
    }

    private void configurarCampoNombre() {
        txtNombrePedido.setFont(new Font("SansSerif", Font.BOLD, 22));
        txtNombrePedido.setBackground(InformeUiTheme.CARD_BG_2);
        txtNombrePedido.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtNombrePedido.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtNombrePedido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        txtNombrePedido.setPreferredSize(new Dimension(500, 48));
    }

    private void configurarToggleServicio(JToggleButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(190, 48));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
    }

    private void actualizarEstiloTipoServicio() {
        aplicarEstiloToggle(btnParaTomar, btnParaTomar.isSelected());
        aplicarEstiloToggle(btnParaLlevar, btnParaLlevar.isSelected());
    }

    private void aplicarEstiloToggle(JToggleButton button, boolean selected) {
        if (selected) {
            button.setBackground(InformeUiTheme.STARBUCKS_GREEN);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
            button.setForeground(InformeUiTheme.TEXT_PRIMARY);
        }
    }

    public void setNombrePedido(String nombre) {
        txtNombrePedido.setText(nombre != null ? nombre : "");
    }

    public String getNombrePedido() {
        return txtNombrePedido.getText();
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        if (tipoServicio == TipoServicio.PARA_LLEVAR) {
            btnParaLlevar.setSelected(true);
        } else {
            btnParaTomar.setSelected(true);
        }

        actualizarEstiloTipoServicio();
    }

    public TipoServicio getTipoServicio() {
        if (btnParaLlevar.isSelected()) {
            return TipoServicio.PARA_LLEVAR;
        }

        return TipoServicio.PARA_TOMAR;
    }

    public void clear() {
        txtNombrePedido.setText("");
        setTipoServicio(TipoServicio.PARA_TOMAR);
    }

    public void requestFocusInField() {
        txtNombrePedido.requestFocusInWindow();
        txtNombrePedido.selectAll();
    }
}