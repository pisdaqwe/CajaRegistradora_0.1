package ui.ventas;

import enums.ModoOperacion;
import model.TicketSession;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class BottomBarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final TicketSession ticketSession;
    private final ModoOperacion modoOperacion;

    private final Runnable onCobrar;
    private final Runnable onCancelar;
    private final Runnable onOpciones;
    private final Runnable onDescuentos;
    private final Runnable onEliminar;

    private final JLabel lblTotal = new JLabel(I18n.t("sales.bottom.total", "0.00"), SwingConstants.LEFT);
    private final JButton btnCobrar = new JButton(I18n.t("sales.bottom.charge"));
    private final JButton btnCancelar = new JButton(I18n.t("common.cancel"));
    private final JButton btnOpciones = new JButton(I18n.t("sales.bottom.options"));
    private final JButton btnDescuentos = new JButton(I18n.t("sales.bottom.discounts"));
    private final JButton btnEliminar = new JButton(I18n.t("common.delete"));

    public BottomBarPanel(
            TicketSession ticketSession,
            Runnable onCobrar,
            Runnable onCancelar,
            Runnable onOpciones,
            Runnable onDescuentos,
            Runnable onEliminar
    ) {
        this(ticketSession, ModoOperacion.VENTA, onCobrar, onCancelar, onOpciones, onDescuentos, onEliminar);
    }

    public BottomBarPanel(
            TicketSession ticketSession,
            ModoOperacion modoOperacion,
            Runnable onCobrar,
            Runnable onCancelar,
            Runnable onOpciones,
            Runnable onDescuentos,
            Runnable onEliminar
    ) {
        this.ticketSession = ticketSession;
        this.modoOperacion = modoOperacion != null ? modoOperacion : ModoOperacion.VENTA;
        this.onCobrar = onCobrar;
        this.onCancelar = onCancelar;
        this.onOpciones = onOpciones;
        this.onDescuentos = onDescuentos;
        this.onEliminar = onEliminar;

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        lblTotal.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblTotal.setForeground(InformeUiTheme.ACCENT_GOLD);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(lblTotal);

        JPanel right = new JPanel(new GridLayout(1, 5, 10, 10));
        right.setOpaque(false);

        btnDescuentos.setIcon(TpvIconFactory.product(18, InformeUiTheme.TEXT_PRIMARY));
        btnOpciones.setIcon(TpvIconFactory.settings(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCobrar.setIcon(TpvIconFactory.creditCard(18, Color.WHITE));
        btnEliminar.setIcon(TpvIconFactory.cancel(18, Color.WHITE));

        styleBtn(btnDescuentos);
        styleBtn(btnOpciones);
        styleBtn(btnCancelar);
        stylePrimaryBtn(btnCobrar);
        styleDangerBtn(btnEliminar);

        btnCobrar.addActionListener(e -> {
            if (onCobrar != null) onCobrar.run();
        });
        btnCancelar.addActionListener(e -> {
            if (onCancelar != null) onCancelar.run();
        });
        btnOpciones.addActionListener(e -> {
            if (onOpciones != null) onOpciones.run();
        });
        btnDescuentos.addActionListener(e -> {
            if (onDescuentos != null) onDescuentos.run();
        });
        btnEliminar.addActionListener(e -> {
            if (onEliminar != null) onEliminar.run();
        });

        right.add(btnDescuentos);
        right.add(btnOpciones);
        right.add(btnCancelar);
        right.add(btnCobrar);
        right.add(btnEliminar);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        applyModeVisuals();
        refresh();
    }

    private void applyModeVisuals() {
        if (modoOperacion == ModoOperacion.MERMA) {
            btnCobrar.setText(I18n.t("sales.bottom.registerWaste"));
            btnCobrar.setIcon(TpvIconFactory.warning(18, Color.WHITE));
            btnCancelar.setText(I18n.t("sales.bottom.cancelWaste"));
            btnDescuentos.setVisible(false);
        } else {
            btnCobrar.setText(I18n.t("sales.bottom.charge"));
            btnCobrar.setIcon(TpvIconFactory.creditCard(18, Color.WHITE));
            btnCancelar.setText(I18n.t("common.cancel"));
            btnDescuentos.setVisible(true);
        }
    }

    public void refresh() {
        BigDecimal total = ticketSession.getTotal();
        lblTotal.setText(I18n.t("sales.bottom.total", total.toPlainString()));

        boolean hasItems = !ticketSession.isEmpty();

        btnCobrar.setEnabled(hasItems);
        btnCancelar.setEnabled(hasItems || modoOperacion == ModoOperacion.MERMA);
        btnEliminar.setEnabled(hasItems);
        btnDescuentos.setEnabled(hasItems && modoOperacion != ModoOperacion.MERMA);

        revalidate();
        repaint();
    }

    private void styleBtn(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
        b.setBackground(InformeUiTheme.ACCENT_GOLD);
        b.setForeground(Color.BLACK);
        b.setIconTextGap(8);
        b.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void stylePrimaryBtn(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 18));
        b.setBackground(InformeUiTheme.STARBUCKS_GREEN);
        b.setForeground(Color.WHITE);
        b.setIconTextGap(8);
        b.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerBtn(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
        b.setBackground(InformeUiTheme.DANGER);
        b.setForeground(Color.WHITE);
        b.setIconTextGap(8);
        b.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
