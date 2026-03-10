package ui.ventas;

import model.TicketRow;
import model.TicketSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TicketPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final TicketSession ticketSession;
    private final Runnable onSelectionChanged;
    private boolean syncingSelection = false;

    private final DefaultListModel<TicketRow> model = new DefaultListModel<>();
    private final JList<TicketRow> list = new JList<>(model);

    public TicketPanel(TicketSession ticketSession, Runnable onSelectionChanged) {
        this.ticketSession = ticketSession;
        this.onSelectionChanged = onSelectionChanged;

        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildList(), BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("TICKET");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        return title;
    }

    private JComponent buildList() {
        list.setCellRenderer(new TicketRowRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(new Color(30, 30, 30));
        list.setFixedCellHeight(40); // táctil / legible
        list.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        list.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            if (syncingSelection) return;

            int idx = list.getSelectedIndex();
            ticketSession.setSelectedFlatIndex(idx);

            if (onSelectionChanged != null) {
                onSelectionChanged.run();
            }
        });

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // =========================================================
    // API pública del panel
    // =========================================================

    public void refreshFromTicket() {
        model.clear();

        List<TicketRow> rows = ticketSession.buildRows();
        for (TicketRow r : rows) {
            model.addElement(r);
        }

        syncSelectionFromSession();
    }

    public void syncSelectionFromSession() {
        syncingSelection = true;
        try {
            int idx = ticketSession.getSelectedFlatIndex();
            if (idx >= 0 && idx < model.size()) {
                list.setSelectedIndex(idx);
                list.ensureIndexIsVisible(idx);
            } else {
                list.clearSelection();
            }
        } finally {
            syncingSelection = false;
        }
    }

    public JList<TicketRow> getList() {
        return list;
    }
}