package ui.dialog;

import dtoS.StockExtraDisponibilidadDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditarDisponibilidadExtraDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // COLORES
    // =========================================================
    private static final Color BG_MAIN = new Color(14, 48, 35);
    private static final Color BG_PANEL = new Color(20, 67, 47);
    private static final Color BG_HEADER = new Color(0, 92, 62);
    private static final Color BORDER = new Color(95, 145, 118);
    private static final Color TEXT_MAIN = new Color(245, 245, 240);
    private static final Color TEXT_SOFT = new Color(212, 223, 216);
    private static final Color TEXT_DARK = new Color(30, 40, 35);

    private static final int WIDTH = 640;
    private static final int HEIGHT = 380;

    // =========================================================
    // ESTADO
    // =========================================================
    private final StockExtraDisponibilidadDTO extra;
    private EditarDisponibilidadExtraDialogResult result =
            EditarDisponibilidadExtraDialogResult.cancelled();

    // =========================================================
    // UI
    // =========================================================
    private JLabel lblNombreExtra;
    private JLabel lblTipoExtra;
    private JCheckBox chkDisponible;

    public EditarDisponibilidadExtraDialog(JFrame owner, StockExtraDisponibilidadDTO extra) {
        super(owner, "Editar disponibilidad de extra", true);
        this.extra = extra;

        initDialog();
        initComponents();
        buildLayout();
        loadInitialData();
    }

    private void initDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        lblNombreExtra = createValueLabel();
        lblTipoExtra = createValueLabel();

        chkDisponible = new JCheckBox("Disponible en esta sucursal");
        chkDisponible.setOpaque(false);
        chkDisponible.setFont(new Font("SansSerif", Font.BOLD, 18));
        chkDisponible.setForeground(TEXT_MAIN);
        chkDisponible.setFocusPainted(false);
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("EDITAR EXTRA");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Activa o desactiva el extra en la sucursal actual");
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SOFT);

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblSubtitle);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(createLabel("Extra:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(lblNombreExtra, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(createLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(lblTipoExtra, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(chkDisponible, gbc);

        return panel;
    }

    private JComponent buildBottomBar() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setOpaque(false);

        JButton btnCancelar = createActionButton("CANCELAR");
        JButton btnGuardar = createActionButton("GUARDAR");

        btnCancelar.addActionListener(e -> cancel());
        btnGuardar.addActionListener(e -> accept());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    private void loadInitialData() {
        lblNombreExtra.setText(extra.getNombreExtra());
        lblTipoExtra.setText(extra.getTipoExtra());
        chkDisponible.setSelected(extra.isDisponible());
    }

    private void accept() {
        result = new EditarDisponibilidadExtraDialogResult(true, chkDisponible.isSelected());
        dispose();
    }

    private void cancel() {
        result = EditarDisponibilidadExtraDialogResult.cancelled();
        dispose();
    }

    public EditarDisponibilidadExtraDialogResult showDialog() {
        setVisible(true);
        return result;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(TEXT_MAIN);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(TEXT_SOFT);
        return label;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(TEXT_MAIN);
        button.setBackground(BG_HEADER);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(14, 12, 14, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}