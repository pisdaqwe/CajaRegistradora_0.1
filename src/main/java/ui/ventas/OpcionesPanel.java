package ui.ventas;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OpcionesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// =========================================================
	// COLORES TPV CAFETERÍA / VERDE TIPO STARBUCKS
	// =========================================================
	private static final Color BG_MAIN = new Color(14, 48, 35);
	private static final Color BG_SECTION = new Color(20, 67, 47);
	private static final Color BG_BUTTON = new Color(0, 92, 62);
	private static final Color BG_BUTTON_ALT = new Color(28, 84, 62);
	private static final Color BG_BUTTON_ADMIN = new Color(57, 102, 74);
	private static final Color TEXT_MAIN = new Color(245, 245, 240);
	private static final Color TEXT_SOFT = new Color(212, 223, 216);
	private static final Color SEPARATOR = new Color(82, 129, 105);
	private static final Color BORDER = new Color(95, 145, 118);

	public interface OpcionesActionListener {
		void onDuplicarClicked();
		void onReimprimirClicked();
		void onSkuClicked();
		void onBuscarProductoClicked();
		void onDisponibilidadClicked();
		void onDescuentosClicked();
		void onUltimosTicketsClicked();
		void onDevolucionesClicked();
		void onMermaClicked();
		void onNuevoPedidoClicked();
		void onCerrarSesionClicked();
		void onVolverAdminClicked();
		void onVolverClicked();
	}

	private OpcionesActionListener actionListener;

	private JPanel adminSection;

	private JButton btnDuplicar;
	private JButton btnReimprimir;
	private JButton btnSku;
	private JButton btnBuscarProducto;
	private JButton btnDisponibilidad;
	private JButton btnDescuentos;
	private JButton btnUltimosTickets;
	private JButton btnVolver;
	private JButton btnDevoluciones;
	private JButton btnNuevoPedido;
	private JButton btnCerrarSesion;
	private JButton btnVolverAdmin;
	private JButton btnMerma;

	public OpcionesPanel() {
		setLayout(new BorderLayout());
		setBackground(BG_MAIN);
		setBorder(new EmptyBorder(16, 16, 16, 16));

		add(buildContent(), BorderLayout.CENTER);

		// De momento visible para poder diseñarlo todo
		setAdminMode(true);
	}

	public void setActionListener(OpcionesActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public void setAdminMode(boolean adminMode) {
		if (adminSection != null) {
			adminSection.setVisible(adminMode);
		}
		revalidate();
		repaint();
	}

	private JComponent buildContent() {
		JPanel root = new JPanel();
		root.setOpaque(false);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

		root.add(buildHeader());
		root.add(Box.createVerticalStrut(14));

		root.add(createSection(
				"ACCIONES DE TICKET",
				"Operaciones rápidas sobre el pedido actual",
				createButtonGrid(
						btnDuplicar = createPrimaryButton("DUPLICAR", this::fireDuplicarClicked),
						btnReimprimir = createPrimaryButton("REIMPRIMIR TICKET", this::fireReimprimirClicked)
				)
		));

		root.add(Box.createVerticalStrut(12));

		root.add(createSection(
				"BÚSQUEDA / ENTRADA",
				"Añadir productos por búsqueda o por código",
				createButtonGrid(
						btnSku = createPrimaryButton("SKU", this::fireSkuClicked),
						btnBuscarProducto = createPrimaryButton("BUSCAR PRODUCTO", this::fireBuscarProductoClicked)
				)
		));

		root.add(Box.createVerticalStrut(12));

		root.add(createSection(
				"CONSULTA",
				"Consulta rápida de disponibilidad y stock",
				createButtonGrid(
						btnDisponibilidad = createPrimaryButton("DISPONIBILIDAD", this::fireDisponibilidadClicked)
				)
		));

		root.add(Box.createVerticalStrut(12));

		root.add(createSection(
				"OPERACIONES",
				"Funciones especiales del TPV",
				createButtonGrid(
						btnDescuentos = createPrimaryButton("DESCUENTOS", this::fireDescuentosClicked),
						btnUltimosTickets = createPrimaryButton("ÚLTIMOS TICKETS", this::fireUltimosTicketsClicked)
				)
		));

		root.add(Box.createVerticalStrut(12));

		root.add(createSection(
				"FLUJO DE VENTA",
				"Acciones sobre el pedido actual y la sesión del usuario",
				createButtonGrid(
						btnNuevoPedido = createPrimaryButton("NUEVO PEDIDO", this::fireNuevoPedidoClicked),
						btnCerrarSesion = createPrimaryButton("CERRAR SESIÓN", this::fireCerrarSesionClicked)
				)
		));

		root.add(Box.createVerticalStrut(12));

		adminSection = createSection(
				"ADMINISTRACIÓN",
				"Opciones visibles para encargado / administrador",
				createButtonGrid(
						btnDevoluciones = createAdminButton("DEVOLUCIONES", this::fireDevolucionesClicked),
						btnMerma = createAdminButton("MERMA", this::fireMermaClicked),
						btnVolverAdmin = createAdminButton("VOLVER A ADMIN", this::fireVolverAdminClicked)
				)
		);
		root.add(adminSection);

		root.add(Box.createVerticalStrut(18));
		root.add(createBottomBar());

		JScrollPane scroll = new JScrollPane(root);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		return scroll;
	}

	private JComponent buildHeader() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("OPCIONES DEL PEDIDO");
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont(new Font("SansSerif", Font.BOLD, 28));
		title.setForeground(TEXT_MAIN);

		JLabel subtitle = new JLabel("Centro de operaciones rápidas del TPV");
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
		subtitle.setForeground(TEXT_SOFT);

		panel.add(title);
		panel.add(Box.createVerticalStrut(4));
		panel.add(subtitle);
		panel.add(Box.createVerticalStrut(10));
		panel.add(createSeparator());

		return panel;
	}

	private JPanel createSection(String title, String subtitle, JComponent content) {
		JPanel section = new JPanel(new BorderLayout(0, 10));
		section.setOpaque(true);
		section.setBackground(BG_SECTION);
		section.setBorder(createSectionBorder());

		JPanel header = new JPanel();
		header.setOpaque(false);
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
		lblTitle.setForeground(TEXT_MAIN);

		JLabel lblSubtitle = new JLabel(subtitle);
		lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblSubtitle.setForeground(TEXT_SOFT);

		header.add(lblTitle);
		header.add(Box.createVerticalStrut(2));
		header.add(lblSubtitle);
		header.add(Box.createVerticalStrut(8));
		header.add(createSeparator());

		section.add(header, BorderLayout.NORTH);
		section.add(content, BorderLayout.CENTER);

		return section;
	}

	private Border createSectionBorder() {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1, true),
				new EmptyBorder(12, 12, 12, 12)
		);
	}

	private JComponent createSeparator() {
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setForeground(SEPARATOR);
		separator.setBackground(SEPARATOR);
		return separator;
	}

	private JPanel createButtonGrid(JButton... buttons) {
	    int columnas = buttons.length == 1 ? 1 : 2;

	    JPanel panel = new JPanel(new GridLayout(0, columnas, 12, 12));
	    panel.setOpaque(false);

	    for (JButton button : buttons) {
	        panel.add(button);
	    }

	    return panel;
	}

	private JComponent createBottomBar() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);

		btnVolver = new JButton("VOLVER");
		btnVolver.addActionListener(e -> fireVolverClicked());
		styleBottomButton(btnVolver);

		panel.add(btnVolver, BorderLayout.CENTER);
		return panel;
	}

	private JButton createPrimaryButton(String text, Runnable action) {
		JButton button = new JButton(text);
		button.addActionListener(e -> action.run());
		stylePrimaryButton(button);
		return button;
	}

	private JButton createAdminButton(String text, Runnable action) {
		JButton button = new JButton(text);
		button.addActionListener(e -> action.run());
		styleAdminButton(button);
		return button;
	}

	private void stylePrimaryButton(JButton button) {
		button.setFocusPainted(false);
		button.setFont(new Font("SansSerif", Font.BOLD, 18));
		button.setForeground(TEXT_MAIN);
		button.setBackground(BG_BUTTON);
		button.setOpaque(true);
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(123, 170, 145), 1, true),
				new EmptyBorder(18, 12, 18, 12)
		));
		button.setPreferredSize(new Dimension(220, 82));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void styleAdminButton(JButton button) {
		button.setFocusPainted(false);
		button.setFont(new Font("SansSerif", Font.BOLD, 18));
		button.setForeground(TEXT_MAIN);
		button.setBackground(BG_BUTTON_ADMIN);
		button.setOpaque(true);
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(146, 183, 161), 1, true),
				new EmptyBorder(18, 12, 18, 12)
		));
		button.setPreferredSize(new Dimension(220, 82));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void styleBottomButton(JButton button) {
		button.setFocusPainted(false);
		button.setFont(new Font("SansSerif", Font.BOLD, 18));
		button.setForeground(TEXT_MAIN);
		button.setBackground(BG_BUTTON_ALT);
		button.setOpaque(true);
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(120, 162, 140), 1, true),
				new EmptyBorder(14, 12, 14, 12)
		));
		button.setPreferredSize(new Dimension(200, 54));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void fireDuplicarClicked() {
		if (actionListener != null) {
			actionListener.onDuplicarClicked();
		}
	}

	private void fireSkuClicked() {
		if (actionListener != null) {
			actionListener.onSkuClicked();
		}
	}

	private void fireBuscarProductoClicked() {
		if (actionListener != null) {
			actionListener.onBuscarProductoClicked();
		}
	}

	private void fireDisponibilidadClicked() {
		if (actionListener != null) {
			actionListener.onDisponibilidadClicked();
		}
	}

	

	private void fireDescuentosClicked() {
		if (actionListener != null) {
			actionListener.onDescuentosClicked();
		}
	}

	private void fireReimprimirClicked() {
		if (actionListener != null) {
			actionListener.onReimprimirClicked();
		}
	}

	private void fireUltimosTicketsClicked() {
		if (actionListener != null) {
			actionListener.onUltimosTicketsClicked();
		}
	}

	private void fireDevolucionesClicked() {
		if (actionListener != null) {
			actionListener.onDevolucionesClicked();
		}
	}

	private void fireMermaClicked() {
		if (actionListener != null) {
			actionListener.onMermaClicked();
		}
	}

	private void fireVolverAdminClicked() {
		if (actionListener != null) {
			actionListener.onVolverAdminClicked();
		}
	}

	private void fireVolverClicked() {
		if (actionListener != null) {
			actionListener.onVolverClicked();
		}
	}

	private void fireNuevoPedidoClicked() {
		if (actionListener != null) {
			actionListener.onNuevoPedidoClicked();
		}
	}

	private void fireCerrarSesionClicked() {
		if (actionListener != null) {
			actionListener.onCerrarSesionClicked();
		}
	}
}