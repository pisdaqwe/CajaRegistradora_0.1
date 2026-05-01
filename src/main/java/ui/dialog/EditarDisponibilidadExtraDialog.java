package ui.dialog;

import dtoS.StockExtraDisponibilidadDTO;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import java.awt.*;

public class EditarDisponibilidadExtraDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 640;
    private static final int HEIGHT = 380;

    private final StockExtraDisponibilidadDTO extra;
    private EditarDisponibilidadExtraDialogResult result = EditarDisponibilidadExtraDialogResult.cancelled();

    private JLabel lblNombreExtra;
    private JLabel lblTipoExtra;
    private JCheckBox chkDisponible;

    public EditarDisponibilidadExtraDialog(JFrame owner, StockExtraDisponibilidadDTO extra) {
        super(owner, I18n.t("availability.extraEdit.title"), true);
        this.extra = extra;

        initDialog();
        initComponents();
        buildLayout();
        loadInitialData();
    }

    private void initDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(560, 330));
        setResizable(true);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        lblNombreExtra = createValueLabel();
        lblTipoExtra = createValueLabel();

        chkDisponible = new JCheckBox(I18n.t("availability.extraEdit.availableHere"));
        InformeUiTheme.styleCheckBox(chkDisponible);
        chkDisponible.setFont(InformeUiTheme.FONT_BODY.deriveFont(Font.BOLD, 18f));
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 4));

        JLabel lblTitle = new JLabel(I18n.t("availability.extraEdit.header"), SwingConstants.CENTER);
        lblTitle.setIcon(TpvIconFactory.product(22, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(10);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION.deriveFont(22f));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(I18n.t("availability.extraEdit.subtitle"), SwingConstants.CENTER);
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSubtitle, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormRow(panel, gbc, row++, I18n.t("availability.extraEdit.extra") + ":", lblNombreExtra);
        addFormRow(panel, gbc, row++, I18n.t("availability.extraEdit.type") + ":", lblTipoExtra);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(chkDisponible, gbc);
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(value, gbc);
    }

    private JComponent buildBottomBar() {
        JPanel panel = InformeUiTheme.createTransparentPanel(new GridLayout(1, 2, 12, 12));

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnCancelar);

        JButton btnGuardar = new JButton(I18n.t("common.save"));
        btnGuardar.setIcon(TpvIconFactory.save(18, Color.WHITE));
        btnGuardar.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnGuardar);

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
        return InformeUiTheme.createFieldLabel(text);
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setFont(InformeUiTheme.FONT_BODY);
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return label;
    }
}
