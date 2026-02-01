package ui.dialog;

import java.awt.*;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Diálogo modal para el conteo de efectivo de una caja.
 *
 * Responsabilidad ÚNICA: - Permitir introducir un importe decimal válido (máx.
 * 2 decimales) - Devolver el importe como BigDecimal
 *
 * NO conoce: - cajas - sesiones - empleados - lógica de negocio
 *
 * Cancelar → devuelve null Aceptar → devuelve BigDecimal
 */
public class ConteoCajaDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// ===============================
	// Componentes UI
	// ===============================
	private JTextField txtCantidadContada;

	// ===============================
	// Resultado del diálogo
	// ===============================
	private BigDecimal importeSeleccionado; // null si se cancela

	// ===============================
	// CONSTRUCTOR
	// ===============================
	public ConteoCajaDialog(Window owner) {
		super(owner, "Conteo de Caja", ModalityType.APPLICATION_MODAL);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(380, 600);
		setResizable(false);
		setLocationRelativeTo(owner);

		buildUI();
	}

	// ===============================
	// CONSTRUCCIÓN UI
	// ===============================
	private void buildUI() {

		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(root);

		// -------- CABECERA --------
		JLabel lblTitulo = new JLabel("CONTEO DE CAJA", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 22));
		root.add(lblTitulo, BorderLayout.NORTH);

		// -------- CUERPO --------
		JPanel panelCentral = new JPanel();
		panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

		// Campo de importe con filtro de dinero
		txtCantidadContada = new JTextField();
		txtCantidadContada.setBorder(BorderFactory.createTitledBorder("Efectivo contado (€)"));
		txtCantidadContada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
		txtCantidadContada.setFont(new Font("Monospaced", Font.BOLD, 28));
		txtCantidadContada.setHorizontalAlignment(JTextField.RIGHT);

		// Filtro: solo números y máximo 2 decimales
		((AbstractDocument) txtCantidadContada.getDocument()).setDocumentFilter(new MoneyDocumentFilter());

		panelCentral.add(txtCantidadContada);
		panelCentral.add(Box.createVerticalStrut(20));

		// -------- TECLADO NUMÉRICO --------
		JPanel keypad = new JPanel(new GridLayout(4, 3, 8, 8));

		String[] keys = { "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "←" };

		for (String key : keys) {
			keypad.add(createKeypadButton(key));
		}

		panelCentral.add(keypad);
		root.add(panelCentral, BorderLayout.CENTER);

		// -------- PIE --------
		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
		panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));

		JButton btnCancelar = new JButton("CANCELAR");
		JButton btnAceptar = new JButton("ACEPTAR");

		btnCancelar.setFont(new Font("Monospaced", Font.BOLD, 18));
		btnAceptar.setFont(new Font("Monospaced", Font.BOLD, 18));

		btnCancelar.setBackground(new Color(220, 180, 180));
		btnAceptar.setBackground(new Color(180, 220, 180));

		btnCancelar.addActionListener(e -> dispose());
		btnAceptar.addActionListener(e -> procesarAceptar());

		panelBotones.add(btnCancelar);
		panelBotones.add(btnAceptar);

		root.add(panelBotones, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(btnAceptar);

		SwingUtilities.invokeLater(() -> txtCantidadContada.requestFocusInWindow());
	}

	// ===============================
	// BOTONES DEL TECLADO
	// ===============================
	private JButton createKeypadButton(String label) {

		JButton b = new JButton(label);
		b.setFont(new Font("Monospaced", Font.BOLD, 24));
		b.setFocusable(false);

		b.addActionListener(e -> {

			if ("←".equals(label)) {
				// Backspace
				int len = txtCantidadContada.getText().length();
				if (len > 0) {
					try {
						txtCantidadContada.getDocument().remove(len - 1, 1);
					} catch (Exception ignored) {
					}
				}
				return;
			}

			if (".".equals(label) && txtCantidadContada.getText().isEmpty()) {
				// UX: si pulsa "." al inicio → "0."
				txtCantidadContada.setText("0.");
				return;
			}

			txtCantidadContada.replaceSelection(label);
		});

		return b;
	}

	// ===============================
	// ACEPTAR
	// ===============================
	private void procesarAceptar() {

		String texto = txtCantidadContada.getText().trim();

		// No permitir vacío o solo "."
		if (texto.isEmpty() || ".".equals(texto)) {
			JOptionPane.showMessageDialog(this, "Debe introducir un importe válido", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			BigDecimal valor = new BigDecimal(texto);

			// Seguridad extra
			if (valor.compareTo(BigDecimal.ZERO) < 0) {
				throw new NumberFormatException();
			}

			importeSeleccionado = valor;
			dispose();

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Importe no válido (máx. 2 decimales)", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// ===============================
	// API PÚBLICA
	// ===============================

	/**
	 * Muestra el diálogo y devuelve el importe contado.
	 *
	 * @return BigDecimal si se acepta, null si se cancela
	 */
	public BigDecimal showDialog() {
		setVisible(true);
		return importeSeleccionado;
	}

	// ===============================
	// FILTRO DE DOCUMENTO (DINERO)
	// ===============================
	private static class MoneyDocumentFilter extends DocumentFilter {

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {

			if (isValid(fb.getDocument().getText(0, fb.getDocument().getLength()),
						string, 
						offset)) {
				super.insertString(fb, offset, string, attr);
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {

			if (isValid(fb.getDocument().getText(0, fb.getDocument().getLength()), 
						text, 
						offset)) {
				super.replace(fb, offset, length, text, attrs);
			}
		}

		/**
		 * Valida: - solo números - un solo punto - máximo 2 decimales
		 */
		private boolean isValid(String currentText, String newText, int offset) {

			if (!newText.matches("[0-9.]*"))
				return false;

			String result = new StringBuilder(currentText).insert(offset, newText).toString();

			// Solo un punto
			if (result.chars().filter(c -> c == '.').count() > 1)
				return false;

			// Máx. 2 decimales
			return result.matches("\\d*(\\.\\d{0,2})?");
		}
	}
}
