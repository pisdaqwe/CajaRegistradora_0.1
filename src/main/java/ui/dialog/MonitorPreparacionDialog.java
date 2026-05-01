package ui.dialog;

import app.AppContext;
import dtoS.ColaMonitorItemDTO;
import service.AppServices;
import service.ColaImpresionService;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MonitorPreparacionDialog extends JDialog {

    private static final String CODIGO_BEBIDAS_CALIENTES = "BEBIDAS_CALIENTES";
    private static final String CODIGO_BEBIDAS_FRIAS = "BEBIDAS_FRIAS";
    private static final String CODIGO_COMIDA = "COMIDA";

    private static final int REFRESH_MS = 2500;
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final AppServices appServices;
    private final ColaImpresionService colaImpresionService;
    private final Timer refreshTimer;

    private final int idSucursalActual;

    private final StationPanel calientesPanel;
    private final StationPanel friasPanel;
    private final StationPanel comidaPanel;

    private final JButton btnRefresh = new JButton(I18n.t("common.refresh"));
    private final JButton btnClose = new JButton(I18n.t("common.close"));

    public MonitorPreparacionDialog(Window owner, AppServices appServices) {
        super(owner, I18n.t("prepMonitor.title"), ModalityType.MODELESS);

        this.appServices = Objects.requireNonNull(appServices, "appServices no puede ser null");
        this.colaImpresionService = Objects.requireNonNull(
                appServices.colaImpresionService,
                "colaImpresionService no puede ser null"
        );
        this.idSucursalActual = AppContext.getIdSucursal();

        int idEstacionCalientes = colaImpresionService
                .requireIdEstacionByCodigoYSucursal(CODIGO_BEBIDAS_CALIENTES, idSucursalActual);

        int idEstacionFrias = colaImpresionService
                .requireIdEstacionByCodigoYSucursal(CODIGO_BEBIDAS_FRIAS, idSucursalActual);

        int idEstacionComida = colaImpresionService
                .requireIdEstacionByCodigoYSucursal(CODIGO_COMIDA, idSucursalActual);

        calientesPanel = new StationPanel(idEstacionCalientes, I18n.t("prepMonitor.station.hotDrinks"));
        friasPanel = new StationPanel(idEstacionFrias, I18n.t("prepMonitor.station.coldDrinks"));
        comidaPanel = new StationPanel(idEstacionComida, I18n.t("prepMonitor.station.food"));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1520, 820);
        setMinimumSize(new Dimension(1360, 700));
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        root.add(buildNorthPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);

        wireEvents();

        refreshTimer = new Timer(REFRESH_MS, e -> refreshAllStations());
        refreshTimer.setRepeats(true);
        refreshTimer.start();

        refreshAllStations();
    }

    private JComponent buildNorthPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);

        JPanel leftCard = InformeUiTheme.createCardPanel(new BorderLayout(0, 8));

        JLabel lblTitle = new JLabel(I18n.t("prepMonitor.header"));
        lblTitle.setFont(InformeUiTheme.FONT_TITLE);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(I18n.t("prepMonitor.subtitleHtml", idSucursalActual));
        lblSub.setFont(InformeUiTheme.FONT_BODY);
        lblSub.setForeground(InformeUiTheme.TEXT_SECONDARY);

        leftCard.add(lblTitle, BorderLayout.NORTH);
        leftCard.add(lblSub, BorderLayout.CENTER);

        JPanel rightCard = InformeUiTheme.createCardPanel(new BorderLayout(0, 10));
        rightCard.setPreferredSize(new Dimension(250, 100));

        JLabel lblInfo = new JLabel(I18n.t("prepMonitor.actions"));
        lblInfo.setFont(InformeUiTheme.FONT_SECTION);
        lblInfo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);

        InformeUiTheme.styleSecondaryButton(btnRefresh);
        InformeUiTheme.stylePrimaryButton(btnClose);
        btnRefresh.setIcon(TpvIconFactory.refresh(18, InformeUiTheme.TEXT_PRIMARY));
        btnClose.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));

        buttons.add(btnRefresh);
        buttons.add(btnClose);

        rightCard.add(lblInfo, BorderLayout.NORTH);
        rightCard.add(buttons, BorderLayout.CENTER);

        panel.add(leftCard, BorderLayout.CENTER);
        panel.add(rightCard, BorderLayout.EAST);

        return panel;
    }

    private JComponent buildCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setOpaque(false);

        panel.add(calientesPanel);
        panel.add(friasPanel);
        panel.add(comidaPanel);

        return panel;
    }

    private void wireEvents() {
        btnRefresh.addActionListener(e -> refreshAllStations());
        btnClose.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stopRefreshTimer();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                stopRefreshTimer();
            }
        });
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    private void refreshAllStations() {
        refreshStation(calientesPanel);
        refreshStation(friasPanel);
        refreshStation(comidaPanel);
    }

    private void refreshStation(StationPanel stationPanel) {
        try {
            List<ColaMonitorItemDTO> rows =
                    colaImpresionService.getPendientesHoyByEstacion(stationPanel.idEstacion);

            stationPanel.setItems(rows);

        } catch (Exception ex) {
            stationPanel.showError(I18n.t("prepMonitor.errorQueue", safeMessage(ex)));
        }
    }

    private void printNextForStation(StationPanel stationPanel) {
        try {
            ColaMonitorItemDTO printed =
                    colaImpresionService.imprimirSiguiente(stationPanel.idEstacion);

            if (printed == null) {
            	TpvDialogUtils.showInfo(
            	        this,
            	        I18n.t("prepMonitor.noPendingTitle"),
            	        I18n.t("prepMonitor.noPendingMessage", stationPanel.stationName)
            	);
                refreshStation(stationPanel);
                return;
            }

            stationPanel.showPrintedItem(printed);
            refreshStation(stationPanel);

        } catch (Exception ex) {
        	TpvDialogUtils.showError(
        	        this,
        	        I18n.t("common.error"),
        	        I18n.t("prepMonitor.errorPrint", safeMessage(ex))
        	);
        }
    }

    private String safeMessage(Exception ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
    }

    private String buildPreviewText(ColaMonitorItemDTO item) {
        StringBuilder sb = new StringBuilder();

        sb.append(I18n.t("prepMonitor.preview.title")).append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append(I18n.t("prepMonitor.preview.queueTime")).append(": ").append(formatHora(item.getFechaCreacion())).append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append(item.getDetalleTexto() != null ? item.getDetalleTexto() : I18n.t("prepMonitor.preview.noDetail"));

        return sb.toString();
    }

    private String buildPrintedText(ColaMonitorItemDTO item) {
        StringBuilder sb = new StringBuilder();

        sb.append(I18n.t("prepMonitor.printed.title")).append("\n");
        sb.append("========================================").append("\n");
        sb.append(I18n.t("prepMonitor.printed.station")).append(": ").append(item.getNombreEstacion()).append("\n");
        sb.append(I18n.t("prepMonitor.printed.queue")).append(": ").append(item.getIdCola()).append("\n");
        sb.append(I18n.t("prepMonitor.printed.sale")).append(": ").append(item.getIdVenta()).append("\n");
        sb.append(I18n.t("prepMonitor.printed.time")).append(": ").append(LocalDateTime.now().format(HORA_FMT)).append("\n");
        sb.append("========================================").append("\n");
        sb.append(item.getDetalleTexto() != null ? item.getDetalleTexto() : I18n.t("prepMonitor.preview.noDetail")).append("\n");
        sb.append("========================================").append("\n");
        sb.append(I18n.t("prepMonitor.printed.done"));

        return sb.toString();
    }

    private String formatHora(LocalDateTime fecha) {
        return fecha != null ? fecha.format(HORA_FMT) : "--:--:--";
    }

    private final class StationPanel extends JPanel {

        private final int idEstacion;
        private final String stationName;

        private final JLabel lblCount = new JLabel(I18n.t("prepMonitor.pending", 0));
        private final JButton btnPrintNext = new JButton(I18n.t("prepMonitor.printNext"));
        private final JButton btnClearPreview = new JButton(I18n.t("prepMonitor.clearDetail"));

        private final DefaultListModel<ColaMonitorItemDTO> listModel = new DefaultListModel<>();
        private final JList<ColaMonitorItemDTO> lstItems = new JList<>(listModel);

        private final JTextArea txtPrinted = new JTextArea();

        StationPanel(int idEstacion, String stationName) {
            this.idEstacion = idEstacion;
            this.stationName = stationName;

            setLayout(new BorderLayout(10, 10));
            setOpaque(true);
            setBackground(InformeUiTheme.CARD_BG);
            setBorder(InformeUiTheme.createCardBorder());

            add(buildHeader(), BorderLayout.NORTH);
            add(buildBody(), BorderLayout.CENTER);
            add(buildFooter(), BorderLayout.SOUTH);

            configureList();
            configureTextArea();
            wireLocalEvents();
        }

        private JComponent buildHeader() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setOpaque(false);

            JPanel titleBox = new JPanel();
            titleBox.setOpaque(false);
            titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

            JLabel lblTitle = new JLabel(stationName);
            lblTitle.setFont(InformeUiTheme.FONT_SECTION);
            lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

            lblCount.setFont(InformeUiTheme.FONT_LABEL);
            lblCount.setForeground(InformeUiTheme.TEXT_SECONDARY);

            titleBox.add(lblTitle);
            titleBox.add(Box.createVerticalStrut(4));
            titleBox.add(lblCount);

            InformeUiTheme.stylePrimaryButton(btnPrintNext);
            btnPrintNext.setIcon(TpvIconFactory.check(18, InformeUiTheme.TEXT_PRIMARY));

            panel.add(titleBox, BorderLayout.CENTER);
            panel.add(btnPrintNext, BorderLayout.EAST);

            return panel;
        }

        private JComponent buildBody() {
            JPanel body = new JPanel(new GridLayout(2, 1, 10, 10));
            body.setOpaque(false);

            JScrollPane spList = new JScrollPane(lstItems);
            spList.setBorder(createSectionBorder(I18n.t("prepMonitor.queueSection")));
            spList.getViewport().setBackground(InformeUiTheme.CARD_BG_2);

            JScrollPane spText = new JScrollPane(txtPrinted);
            spText.setBorder(createSectionBorder(I18n.t("prepMonitor.lastMiniTicket")));
            spText.getViewport().setBackground(InformeUiTheme.CARD_BG_2);

            body.add(spList);
            body.add(spText);

            return body;
        }

        private JComponent buildFooter() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            panel.setOpaque(false);

            InformeUiTheme.styleSecondaryButton(btnClearPreview);
            btnClearPreview.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
            panel.add(btnClearPreview);

            return panel;
        }

        private Border createSectionBorder(String title) {
            return BorderFactory.createTitledBorder(
                    new CompoundBorder(
                            new LineBorder(InformeUiTheme.BORDER, 1, true),
                            new EmptyBorder(4, 4, 4, 4)
                    ),
                    title
            );
        }

        private void configureList() {
            lstItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lstItems.setCellRenderer(new ColaItemRenderer());
            lstItems.setFont(InformeUiTheme.FONT_BODY);
            lstItems.setBackground(InformeUiTheme.CARD_BG_2);
            lstItems.setForeground(InformeUiTheme.TEXT_PRIMARY);
            lstItems.setFixedCellHeight(-1);
        }

        private void configureTextArea() {
            txtPrinted.setEditable(false);
            txtPrinted.setLineWrap(true);
            txtPrinted.setWrapStyleWord(true);
            txtPrinted.setFont(new Font("Consolas", Font.PLAIN, 13));
            txtPrinted.setBackground(InformeUiTheme.CARD_BG_2);
            txtPrinted.setForeground(InformeUiTheme.TEXT_PRIMARY);
            txtPrinted.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
            txtPrinted.setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        private void wireLocalEvents() {
            btnPrintNext.addActionListener(e -> printNextForStation(this));

            btnClearPreview.addActionListener(e -> txtPrinted.setText(""));

            lstItems.addListSelectionListener(e -> {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ColaMonitorItemDTO selected = lstItems.getSelectedValue();
                if (selected != null) {
                    txtPrinted.setText(buildPreviewText(selected));
                    txtPrinted.setCaretPosition(0);
                }
            });
        }

        void setItems(List<ColaMonitorItemDTO> rows) {
            listModel.clear();

            List<ColaMonitorItemDTO> safeRows = rows != null ? rows : Collections.emptyList();
            for (ColaMonitorItemDTO row : safeRows) {
                listModel.addElement(row);
            }

            lblCount.setText(I18n.t("prepMonitor.pending", safeRows.size()));
            btnPrintNext.setEnabled(!safeRows.isEmpty());

            if (safeRows.isEmpty()) {
                if (txtPrinted.getText() == null || txtPrinted.getText().isBlank()) {
                    txtPrinted.setText(I18n.t("prepMonitor.emptyStation"));
                }
            }
        }

        void showPrintedItem(ColaMonitorItemDTO item) {
            txtPrinted.setText(buildPrintedText(item));
            txtPrinted.setCaretPosition(0);
        }

        void showError(String message) {
            listModel.clear();
            lblCount.setText(I18n.t("prepMonitor.pendingError"));
            btnPrintNext.setEnabled(false);
            txtPrinted.setText(message);
        }
    }

    private static final class ColaItemRenderer extends JPanel implements ListCellRenderer<ColaMonitorItemDTO> {

        private final JLabel lblTop = new JLabel();
        private final JLabel lblBottom = new JLabel();

        ColaItemRenderer() {
            setLayout(new BorderLayout(0, 4));
            setBorder(new EmptyBorder(8, 8, 8, 8));
            setOpaque(true);

            lblTop.setFont(InformeUiTheme.FONT_LABEL);
            lblBottom.setFont(InformeUiTheme.FONT_BODY);

            add(lblTop, BorderLayout.NORTH);
            add(lblBottom, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ColaMonitorItemDTO> list,
                ColaMonitorItemDTO value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            if (value == null) {
                lblTop.setText("");
                lblBottom.setText("");
                return this;
            }

            String hora = value.getFechaCreacion() != null
                    ? value.getFechaCreacion().format(HORA_FMT)
                    : "--:--:--";

            lblTop.setText("#" + value.getIdCola() + " | " + I18n.t("prepMonitor.renderer.sale") + " " + value.getIdVenta() + " | " + hora);
            lblBottom.setText(value.getResumenLista());

            if (isSelected) {
                setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
                lblTop.setForeground(InformeUiTheme.TEXT_PRIMARY);
                lblBottom.setForeground(InformeUiTheme.TEXT_PRIMARY);
            } else {
                setBackground(InformeUiTheme.CARD_BG_2);
                lblTop.setForeground(InformeUiTheme.TEXT_PRIMARY);
                lblBottom.setForeground(InformeUiTheme.TEXT_SECONDARY);
            }

            return this;
        }
    }
}