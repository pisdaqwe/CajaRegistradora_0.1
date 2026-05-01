package ui.dialog;

import dtoS.TicketClienteDTO;
import dtoS.TicketDevolucionDTO;
import dtoS.TicketHoyRowDTO;
import service.AppServices;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketsHoyDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final String FILTER_ALL = "AMBOS";
    private static final String FILTER_SALES = "VENTAS";
    private static final String FILTER_REFUNDS = "DEVOLUCIONES";

    private final AppServices services;

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<FilterOption> cmbFiltroTipo;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnRecargar;
    private JButton btnAbrir;
    private JButton btnCerrar;

    private List<TicketHoyRowDTO> rows = new ArrayList<>();

    public TicketsHoyDialog(Window owner, AppServices services) {
        super(owner, I18n.t("ticketsToday.title"), ModalityType.APPLICATION_MODAL);

        if (services == null) {
            throw new IllegalArgumentException("AppServices no puede ser null");
        }

        this.services = services;

        buildUi();
        loadRowsByCurrentFilter();

        setMinimumSize(new Dimension(1120, 580));
        setPreferredSize(new Dimension(1180, 650));
        pack();
        setLocationRelativeTo(owner);
    }

    public void showDialog() {
        setVisible(true);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        JPanel top = InformeUiTheme.createTransparentPanel(new BorderLayout(10, 12));

        JPanel titlePanel = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 4));

        JLabel lblTitle = new JLabel(I18n.t("ticketsToday.header"));
        lblTitle.setIcon(TpvIconFactory.report(26, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(10);
        lblTitle.setFont(InformeUiTheme.FONT_TITLE);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(I18n.t("ticketsToday.subtitle"));
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.CENTER);
        top.add(titlePanel, BorderLayout.NORTH);

        JPanel filtersCard = InformeUiTheme.createCardPanel(new BorderLayout());
        JPanel filters = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel lblMostrar = InformeUiTheme.createFieldLabel(I18n.t("ticketsToday.show") + ":");

        cmbFiltroTipo = new JComboBox<>(new FilterOption[]{
                new FilterOption(FILTER_ALL, I18n.t("ticketsToday.filter.all")),
                new FilterOption(FILTER_SALES, I18n.t("ticketsToday.filter.sales")),
                new FilterOption(FILTER_REFUNDS, I18n.t("ticketsToday.filter.refunds"))
        });
        InformeUiTheme.styleCombo(cmbFiltroTipo);
        cmbFiltroTipo.setPreferredSize(new Dimension(180, 38));

        JLabel lblBuscar = InformeUiTheme.createFieldLabel(I18n.t("common.search") + ":");

        txtBuscar = new JTextField(22);
        InformeUiTheme.styleTextField(txtBuscar);
        txtBuscar.setPreferredSize(new Dimension(260, 38));

        JPanel buscarWrapper = InformeUiTheme.createTransparentPanel(new BorderLayout(6, 0));
        buscarWrapper.add(txtBuscar, BorderLayout.CENTER);

        JButton btnTecladoBuscar = new JButton("⌨");
        InformeUiTheme.styleSecondaryButton(btnTecladoBuscar);
        btnTecladoBuscar.setToolTipText(I18n.t("keyboard.open"));
        btnTecladoBuscar.setPreferredSize(new Dimension(54, 38));
        btnTecladoBuscar.addActionListener(e -> TecladoVirtualDialog.showAlfanumerico(
                this,
                txtBuscar,
                I18n.t("ticketsToday.keyboardTitle"),
                60
        ));
        buscarWrapper.add(btnTecladoBuscar, BorderLayout.EAST);

        btnBuscar = new JButton(I18n.t("common.search"));
        btnBuscar.setIcon(TpvIconFactory.search(18, Color.WHITE));
        btnBuscar.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnBuscar);

        btnRecargar = new JButton(I18n.t("common.refresh"));
        btnRecargar.setIcon(TpvIconFactory.refresh(18, InformeUiTheme.TEXT_PRIMARY));
        btnRecargar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnRecargar);

        cmbFiltroTipo.addActionListener(e -> loadRowsByCurrentFilter());
        btnBuscar.addActionListener(e -> searchRowsByCurrentFilter());
        btnRecargar.addActionListener(e -> {
            txtBuscar.setText("");
            loadRowsByCurrentFilter();
        });
        txtBuscar.addActionListener(e -> searchRowsByCurrentFilter());

        filters.add(lblMostrar);
        filters.add(cmbFiltroTipo);
        filters.add(Box.createHorizontalStrut(12));
        filters.add(lblBuscar);
        filters.add(buscarWrapper);
        filters.add(btnBuscar);
        filters.add(btnRecargar);

        filtersCard.add(filters, BorderLayout.CENTER);
        top.add(filtersCard, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{
                        I18n.t("ticketsToday.table.type"),
                        I18n.t("ticketsToday.table.id"),
                        I18n.t("ticketsToday.table.reference"),
                        I18n.t("ticketsToday.table.date"),
                        I18n.t("ticketsToday.table.order"),
                        I18n.t("ticketsToday.table.paymentRefund"),
                        I18n.t("ticketsToday.table.total"),
                        I18n.t("ticketsToday.table.employee")
                },
                0
        ) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        InformeUiTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    abrirRegistroSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        InformeUiTheme.styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = InformeUiTheme.createTransparentPanel(new BorderLayout());

        JLabel lblHint = new JLabel(I18n.t("ticketsToday.hint"));
        lblHint.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblHint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel actions = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        btnAbrir = new JButton(I18n.t("ticketsToday.open"));
        btnAbrir.setIcon(TpvIconFactory.eye(18, Color.WHITE));
        btnAbrir.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnAbrir);

        btnCerrar = new JButton(I18n.t("common.close"));
        btnCerrar.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnCerrar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnCerrar);

        btnAbrir.addActionListener(e -> abrirRegistroSeleccionado());
        btnCerrar.addActionListener(e -> dispose());

        actions.add(btnAbrir);
        actions.add(btnCerrar);

        bottom.add(lblHint, BorderLayout.WEST);
        bottom.add(actions, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadRowsByCurrentFilter() {
        try {
            String filtro = getFiltroActual();
            List<TicketHoyRowDTO> data = new ArrayList<>();

            if (FILTER_ALL.equals(filtro) || FILTER_SALES.equals(filtro)) {
                data.addAll(services.ticketClienteService.getTicketsHoy());
            }

            if (FILTER_ALL.equals(filtro) || FILTER_REFUNDS.equals(filtro)) {
                data.addAll(services.devolucionTicketService.getDevolucionesHoy());
            }

            sortRowsByFechaDesc(data);
            rows = data;
            reloadTable();

        } catch (Exception ex) {
            TpvDialogUtils.showError(this, I18n.t("ticketsToday.errorLoadTitle"), I18n.t("ticketsToday.errorLoad", ex.getMessage()));
        }
    }

    private void searchRowsByCurrentFilter() {
        try {
            String filtro = getFiltroActual();
            String query = txtBuscar.getText();

            List<TicketHoyRowDTO> data = new ArrayList<>();

            if (FILTER_ALL.equals(filtro) || FILTER_SALES.equals(filtro)) {
                data.addAll(services.ticketClienteService.searchTickets(query));
            }

            if (FILTER_ALL.equals(filtro) || FILTER_REFUNDS.equals(filtro)) {
                data.addAll(services.devolucionTicketService.searchDevoluciones(query));
            }

            sortRowsByFechaDesc(data);
            rows = data;
            reloadTable();

        } catch (Exception ex) {
            TpvDialogUtils.showError(this, I18n.t("ticketsToday.errorSearchTitle"), I18n.t("ticketsToday.errorSearch", ex.getMessage()));
        }
    }

    private String getFiltroActual() {
        Object selected = cmbFiltroTipo.getSelectedItem();
        if (selected instanceof FilterOption option) {
            return option.value();
        }
        return FILTER_ALL;
    }

    private void sortRowsByFechaDesc(List<TicketHoyRowDTO> data) {
        data.sort((a, b) -> {
            LocalDateTime fa = a != null ? a.getFechaGeneracion() : null;
            LocalDateTime fb = b != null ? b.getFechaGeneracion() : null;

            if (fa == null && fb == null) return 0;
            if (fa == null) return 1;
            if (fb == null) return -1;
            return fb.compareTo(fa);
        });
    }

    private void reloadTable() {
        tableModel.setRowCount(0);

        for (TicketHoyRowDTO row : rows) {
            tableModel.addRow(new Object[]{
                    formatTipo(row),
                    formatIdPrincipal(row),
                    formatReferencia(row),
                    formatFecha(row),
                    safe(row.getNombrePedido()),
                    formatMetodoPago(row.getMetodoPago()),
                    formatMoney(row.getTotal()) + " €",
                    safe(row.getNombreEmpleado())
            });
        }

        if (!rows.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    private void abrirRegistroSeleccionado() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow < 0) {
            TpvDialogUtils.showWarning(this, I18n.t("ticketsToday.openTitle"), I18n.t("ticketsToday.validation.selectRecord"));
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        if (modelRow < 0 || modelRow >= rows.size()) {
            return;
        }

        TicketHoyRowDTO row = rows.get(modelRow);

        try {
            if (row.isDevolucion()) {
                if (!row.hasIdDevolucion()) {
                    throw new IllegalStateException(I18n.t("ticketsToday.errorInvalidRefundRow"));
                }

                TicketDevolucionDTO ticket = services.devolucionTicketService.getTicketByDevolucion(row.getIdDevolucion());
                TicketDevolucionDialog dialog = new TicketDevolucionDialog(this, ticket);
                dialog.showDialog();
                return;
            }

            TicketClienteDTO ticket = services.ticketClienteService.getTicketByVenta(row.getIdVenta());
            TicketClienteDialog dialog = new TicketClienteDialog(this, ticket);
            dialog.showDialog();

        } catch (Exception e) {
            TpvDialogUtils.showError(this, I18n.t("common.error"), I18n.t("ticketsToday.errorOpen", e.getMessage()));
        }
    }

    private String formatTipo(TicketHoyRowDTO row) {
        if (row == null) return "";
        return row.isDevolucion() ? I18n.t("ticketsToday.type.refund") : I18n.t("ticketsToday.type.sale");
    }

    private String formatIdPrincipal(TicketHoyRowDTO row) {
        if (row == null) return "";
        if (row.isDevolucion() && row.hasIdDevolucion()) return String.valueOf(row.getIdDevolucion());
        return String.valueOf(row.getIdVenta());
    }

    private String formatReferencia(TicketHoyRowDTO row) {
        if (row == null) return "";
        if (row.isDevolucion() && row.hasIdVentaOriginal()) {
            return I18n.t("ticketsToday.reference.sale", row.getIdVentaOriginal());
        }
        return "-";
    }

    private String formatFecha(TicketHoyRowDTO row) {
        if (row == null || row.getFechaGeneracion() == null) return "";
        return row.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private String formatMetodoPago(String metodo) {
        if (metodo == null) return "";
        return switch (metodo.trim().toUpperCase()) {
            case "EFECTIVO" -> I18n.t("payment.cash");
            case "TARJETA" -> I18n.t("payment.card");
            case "VALE" -> I18n.t("payment.voucher");
            case "MIXTO" -> I18n.t("payment.mixed");
            default -> metodo;
        };
    }

    private String formatMoney(BigDecimal amount) {
        BigDecimal safe = amount != null ? amount : BigDecimal.ZERO;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(I18n.getCurrentLocale());
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(safe);
    }

    private String safe(String txt) {
        return txt == null ? "" : txt;
    }

    private record FilterOption(String value, String label) {
        @Override public String toString() { return label; }
    }
}
