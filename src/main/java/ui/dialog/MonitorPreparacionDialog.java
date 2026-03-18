package ui.dialog;

import service.AppServices;
import dtoS.ColaMonitorItemDTO;
import service.ColaImpresionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Monitor global de preparación.
 *
 * Flujo MVP:
 * - 3 estaciones fijas
 * - Cada estación muestra SOLO items pendientes del día
 * - Botón "Imprimir siguiente" consume el primero de la cola
 * - El detalle del item "impreso" se muestra en un JTextArea
 * - Auto-refresh por Timer
 *
 * Este diálogo NO depende de AppContext ni de sesión.
 */
public class MonitorPreparacionDialog extends JDialog {

    public static final int ESTACION_BEBIDAS_CALIENTES = ColaImpresionService.ESTACION_BEBIDAS_CALIENTES;
    public static final int ESTACION_BEBIDAS_FRIAS = ColaImpresionService.ESTACION_BEBIDAS_FRIAS;
    public static final int ESTACION_COMIDA = ColaImpresionService.ESTACION_COMIDA;

    private static final int REFRESH_MS = 2500;
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final AppServices appServices;
    private final ColaImpresionService colaImpresionService;
    private final Timer refreshTimer;

    private final StationPanel calientesPanel;
    private final StationPanel friasPanel;
    private final StationPanel comidaPanel;

    private final JButton btnRefresh = new JButton("Refrescar ahora");
    private final JButton btnClose = new JButton("Cerrar");

    public MonitorPreparacionDialog(Window owner, AppServices appServices) {
        super(owner, "Monitor de preparación", ModalityType.MODELESS);

        this.appServices = Objects.requireNonNull(appServices, "appServices no puede ser null");
        this.colaImpresionService =Objects.requireNonNull(appServices.colaImpresionService,"Cola impresion service no puede ser null");
                
       

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1500, 760);
        setMinimumSize(new Dimension(1320, 640));
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        calientesPanel = new StationPanel(
                ESTACION_BEBIDAS_CALIENTES,
                "BEBIDAS CALIENTES"
        );

        friasPanel = new StationPanel(
                ESTACION_BEBIDAS_FRIAS,
                "BEBIDAS FRÍAS"
        );

        comidaPanel = new StationPanel(
                ESTACION_COMIDA,
                "COMIDA"
        );

        root.add(buildNorthPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);

        wireEvents();

        refreshTimer = new Timer(REFRESH_MS, e -> refreshAllStations());
        refreshTimer.setRepeats(true);
        refreshTimer.start();

        refreshAllStations();
    }

    private JComponent buildNorthPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Monitor de preparación");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JTextArea info = new JTextArea(
                "Muestra únicamente items pendientes del día actual. "
                        + "Cada estación tiene su propia cola. "
                        + "El botón \"Imprimir siguiente\" consume el primer item pendiente, "
                        + "lo muestra en pantalla y lo marca como impreso/preparado."
        );
        info.setEditable(false);
        info.setOpaque(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);
        left.add(lblTitle, BorderLayout.NORTH);
        left.add(info, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(btnRefresh);
        right.add(btnClose);

        panel.add(left, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    private JComponent buildCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
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
            stationPanel.showError("Error al cargar cola: " + safeMessage(ex));
        }
    }

    private void printNextForStation(StationPanel stationPanel) {
        try {
            ColaMonitorItemDTO printed =
                    colaImpresionService.imprimirSiguiente(stationPanel.idEstacion);

            if (printed == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No hay items pendientes en " + stationPanel.stationName + ".",
                        "Sin pendientes",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refreshStation(stationPanel);
                return;
            }

            stationPanel.showPrintedItem(printed);
            refreshStation(stationPanel);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo imprimir el siguiente item.\n" + safeMessage(ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String safeMessage(Exception ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
    }

    private final class StationPanel extends JPanel {

        private final int idEstacion;
        private final String stationName;

        private final JLabel lblCount = new JLabel("Pendientes: 0");
        private final JButton btnPrintNext = new JButton("Imprimir siguiente");
        private final JButton btnClearPreview = new JButton("Limpiar detalle");

        private final DefaultListModel<ColaMonitorItemDTO> listModel = new DefaultListModel<>();
        private final JList<ColaMonitorItemDTO> lstItems = new JList<>(listModel);

        private final JTextArea txtPrinted = new JTextArea();

        StationPanel(int idEstacion, String stationName) {
            this.idEstacion = idEstacion;
            this.stationName = stationName;

            setLayout(new BorderLayout(8, 8));
            setBorder(new EmptyBorder(4, 4, 4, 4));

            add(buildHeader(), BorderLayout.NORTH);
            add(buildBody(), BorderLayout.CENTER);
            add(buildFooter(), BorderLayout.SOUTH);

            configureList();
            configureTextArea();
            wireLocalEvents();
        }

        private JComponent buildHeader() {
            JPanel panel = new JPanel(new BorderLayout(8, 8));
            panel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    stationName,
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 15)
            ));

            lblCount.setFont(new Font("Arial", Font.BOLD, 13));

            panel.add(lblCount, BorderLayout.WEST);
            panel.add(btnPrintNext, BorderLayout.EAST);
            return panel;
        }

        private JComponent buildBody() {
            JPanel body = new JPanel(new GridLayout(2, 1, 8, 8));

            JScrollPane spList = new JScrollPane(lstItems);
            spList.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(190, 190, 190)),
                    "Cola pendiente (hoy)",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 13)
            ));

            JScrollPane spText = new JScrollPane(txtPrinted);
            spText.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(190, 190, 190)),
                    "Último mini-ticket simulado",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 13)
            ));

            body.add(spList);
            body.add(spText);

            return body;
        }

        private JComponent buildFooter() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            panel.add(btnClearPreview);
            return panel;
        }

        private void configureList() {
            lstItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lstItems.setCellRenderer(new ColaItemRenderer());
            lstItems.setFont(new Font("Arial", Font.PLAIN, 13));
        }

        private void configureTextArea() {
            txtPrinted.setEditable(false);
            txtPrinted.setLineWrap(true);
            txtPrinted.setWrapStyleWord(true);
            txtPrinted.setFont(new Font("Consolas", Font.PLAIN, 13));
            txtPrinted.setBackground(Color.WHITE);
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

            lblCount.setText("Pendientes: " + safeRows.size());
            btnPrintNext.setEnabled(!safeRows.isEmpty());

            if (safeRows.isEmpty()) {
                if (txtPrinted.getText() == null || txtPrinted.getText().isBlank()) {
                    txtPrinted.setText("Sin items pendientes en esta estación.");
                }
            }
        }

        void showPrintedItem(ColaMonitorItemDTO item) {
            txtPrinted.setText(buildPrintedText(item));
            txtPrinted.setCaretPosition(0);
        }

        void showError(String message) {
            listModel.clear();
            lblCount.setText("Pendientes: error");
            btnPrintNext.setEnabled(false);
            txtPrinted.setText(message);
        }
    }

    private String buildPreviewText(ColaMonitorItemDTO item) {
        StringBuilder sb = new StringBuilder();

        sb.append("VISTA PREVIA DEL ITEM").append("\n");
        sb.append("----------------------------------------").append("\n");
        sb.append("Estación: ").append(item.getNombreEstacion()).append("\n");
        sb.append("Cola #: ").append(item.getIdCola()).append("\n");
        sb.append("Venta #: ").append(item.getIdVenta()).append("\n");
        sb.append("Hora cola: ").append(formatHora(item.getFechaCreacion())).append("\n");
        sb.append("\n");
        sb.append(item.getDetalleTexto() != null ? item.getDetalleTexto() : "(Sin detalle)");

        return sb.toString();
    }

    private String buildPrintedText(ColaMonitorItemDTO item) {
        StringBuilder sb = new StringBuilder();

        sb.append("MINI-TICKET / ETIQUETA").append("\n");
        sb.append("========================================").append("\n");
        sb.append("ESTACIÓN: ").append(item.getNombreEstacion()).append("\n");
        sb.append("COLA #: ").append(item.getIdCola()).append("\n");
        sb.append("VENTA #: ").append(item.getIdVenta()).append("\n");
        sb.append("HORA: ").append(LocalDateTime.now().format(HORA_FMT)).append("\n");
        sb.append("========================================").append("\n");
        sb.append(item.getDetalleTexto() != null ? item.getDetalleTexto() : "(Sin detalle)").append("\n");
        sb.append("========================================").append("\n");
        sb.append("IMPRESO Y MARCADO COMO PREPARADO");

        return sb.toString();
    }

    private String formatHora(LocalDateTime fecha) {
        return fecha != null ? fecha.format(HORA_FMT) : "--:--:--";
    }

    private static final class ColaItemRenderer extends JPanel implements ListCellRenderer<ColaMonitorItemDTO> {

        private final JLabel lblTop = new JLabel();
        private final JLabel lblBottom = new JLabel();

        ColaItemRenderer() {
            setLayout(new BorderLayout(0, 2));
            setBorder(new EmptyBorder(8, 8, 8, 8));

            lblTop.setFont(new Font("Arial", Font.BOLD, 13));
            lblBottom.setFont(new Font("Arial", Font.PLAIN, 12));

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

            lblTop.setText("#" + value.getIdCola() + " | Venta " + value.getIdVenta() + " | " + hora);
            lblBottom.setText(value.getResumenLista());

            if (isSelected) {
                setBackground(new Color(210, 230, 255));
            } else {
                setBackground(Color.WHITE);
            }

            return this;
        }
    }
}