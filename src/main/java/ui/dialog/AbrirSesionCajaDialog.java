package ui.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import dtoS.CajaEstadoDTO;
import dtoS.FichajeActivoDTO;
import service.AppServices;
import ui.table.EmpleadosFichadosTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class AbrirSesionCajaDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panelCajas;
	private ButtonGroup grupoCajas;

	private JTextField txtImporteCustom;
	private JLabel lblImporteSeleccionado;
	private ButtonGroup grupoImportes;
	private BigDecimal importeSeleccionado;

	private JLabel lblEmpleadoSleccionado;
	private JTable tablaEmpleados;
	TableRowSorter<EmpleadosFichadosTableModel> sorter ;

	private EmpleadosFichadosTableModel tableModel;
	private FichajeActivoDTO empleadoSeleccionado;
	private JButton btnConfirmar;
	JButton btnCancelar ;

	private CajaEstadoDTO cajaSeleccionada;
	private AppServices services;

	public AbrirSesionCajaDialog(Window owner, AppServices services) {
		super(owner, "Abrir sesion de Caja", ModalityType.APPLICATION_MODAL);
		this.services = services;
		buildUI();
		cargarEmpleadosFichados();
		cargarCajas();
		setSize(900, 650);
		setLocationRelativeTo(owner);

	}

	public void buildUI() {
		JPanel root = new JPanel(new BorderLayout(16, 16));
		root.setBorder(new EmptyBorder(16, 16, 16, 16));
		root.setBackground(new Color(25, 25, 25));
		setContentPane(root);
		root.add(buildHeader(), BorderLayout.NORTH);
		root.add(buildCneter(), BorderLayout.CENTER);
		root.add(buildFooter(), BorderLayout.SOUTH);

	}
	// ===================================================
	// HEADER,FOOTER,CENTER
	// ===================================================

	private JComponent buildHeader() {
		JLabel titulo = new JLabel("Abrir sesion de Caja");
		JLabel title = new JLabel("ABRIR SESIÓN DE CAJA");
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		return title;

	}

	private JComponent buildCneter() {
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setOpaque(false);
		center.add(buildEmpleadoPanel());
		center.add(Box.createVerticalStrut(12));
		center.add(buildCajasPanel());
		center.add(Box.createVerticalStrut(12));
		center.add(buildImportePanel());
		return center;

	}

	private JComponent buildFooter() {

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
		footer.setOpaque(false);

		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(e -> dispose());

		btnConfirmar = new JButton("Abrir sesión de caja");
		btnConfirmar.setEnabled(false); // luego se habilita con validaciones
		btnConfirmar.addActionListener(e->confirmarAperturaCaja());
		footer.add(btnCancelar);
		footer.add(btnConfirmar);

		return footer;
	}
	// ===================================================
	// HEADER,FOOTER,CENTER
	// ===================================================

	// ===================================================
	// EMPLEADO
	// ===================================================
	private JComponent buildEmpleadoPanel() {

	    JPanel panel = new JPanel(new BorderLayout(8, 8));
	    panel.setBorder(BorderFactory.createTitledBorder("Empleados fichados"));
	    panel.setOpaque(true);

	    JTextField txtBuscar = new JTextField();
	    panel.add(txtBuscar, BorderLayout.NORTH);

	    tableModel = new EmpleadosFichadosTableModel();
	    tablaEmpleados = new JTable(tableModel);

	    tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    tablaEmpleados.setRowHeight(24);
	    tablaEmpleados.setFillsViewportHeight(true);
	    tablaEmpleados.setEnabled(true);

	    // SORTER
	    sorter = new TableRowSorter<>(tableModel);
	    tablaEmpleados.setRowSorter(sorter);

	    // LISTENER SELECCIÓN CORRECTO (con conversión vista → modelo)
	    tablaEmpleados.getSelectionModel().addListSelectionListener(e -> {

	        if (e.getValueIsAdjusting()) return;

	        int filaVista = tablaEmpleados.getSelectedRow();

	        if (filaVista >= 0) {
	            int filaModelo = tablaEmpleados.convertRowIndexToModel(filaVista);
	            empleadoSeleccionado = tableModel.getEmpleadoAt(filaModelo);
	        } else {
	            empleadoSeleccionado = null;
	        }

	        actualizarEstadoConfirmacion();
	    });

	    // FILTRO DEL TEXTFIELD
	    txtBuscar.getDocument().addDocumentListener(new DocumentListener() {

	        @Override
	        public void insertUpdate(DocumentEvent e) {
				filtrar();
	        }

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	            filtrar();
	        }

	        @Override
	        public void changedUpdate(DocumentEvent e) {
	            filtrar();
	        }

	        private void filtrar() {
	            String texto = txtBuscar.getText().trim().toLowerCase();

	            if (texto.isEmpty()) {
	                sorter.setRowFilter(null);
	                return;
	            }

	            sorter.setRowFilter(new RowFilter<EmpleadosFichadosTableModel, Integer>() {
	                @Override
	                public boolean include(Entry<? extends EmpleadosFichadosTableModel, ? extends Integer> entry) {
	                    String nombre = entry.getStringValue(0);
	                    return nombre != null && nombre.toLowerCase().contains(texto);
	                }
	            });
	        }

	    });

	    JScrollPane scroll = new JScrollPane(tablaEmpleados);
	    panel.add(scroll, BorderLayout.CENTER);

	    panel.setPreferredSize(new Dimension(800, 180));

	    return panel;
	}


	private void cargarEmpleadosFichados() {
		tableModel.setDatos(services.fichajeService.findFichajesActivos());
	}
	// ===================================================
	// EMPLEADO
	// ===================================================

	// ===================================================
	// CAJA
	// ===================================================
	private JComponent buildCajasPanel() {

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBorder(BorderFactory.createTitledBorder("Cajas"));
		wrapper.setOpaque(false);

		panelCajas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
		panelCajas.setOpaque(false);

		// Placeholder visual
		panelCajas.add(createCajaPlaceholder("Caja 1"));
		panelCajas.add(createCajaPlaceholder("Caja 2"));
		panelCajas.add(createCajaPlaceholder("Terraza"));
		panelCajas.add(createCajaPlaceholder("Foodtruck"));

		wrapper.add(panelCajas, BorderLayout.CENTER);
		wrapper.setPreferredSize(new Dimension(800, 150));

		return wrapper;
	}

	private void cargarCajas() {

		panelCajas.removeAll();
		grupoCajas = new ButtonGroup();
		cajaSeleccionada = null;

		for (CajaEstadoDTO caja : services.sesionCajaService.getEstadoCajas()) {

			JToggleButton btn = new JToggleButton();
			btn.setPreferredSize(new Dimension(170, 75));
			btn.setFocusPainted(false);
			btn.setForeground(Color.WHITE);

			String texto = "<html><center><b>" + caja.getNombreCaja() + "</b><br/>";

			if (!caja.isOperativa()) {
				// ⚫ NO OPERATIVA
				texto += "Fuera de servicio</center></html>";
				btn.setBackground(Color.GRAY);
				btn.setEnabled(false);

			} else if (caja.isOcupada()) {
				// 🔴 OCUPADA
				texto += "Ocupada<br/>(" + caja.getEmpleadoAsignado() + ")</center></html>";
				btn.setBackground(new Color(170, 60, 60));
				btn.setEnabled(false);

			} else {
				// 🟢 DISPONIBLE
				texto += "Disponible</center></html>";
				btn.setBackground(new Color(60, 140, 90));
				btn.setEnabled(true);

				btn.addActionListener(e -> {
					cajaSeleccionada = caja;
					actualizarEstadoConfirmacion();
				});
			}

			btn.setText(texto);

			grupoCajas.add(btn);
			panelCajas.add(btn);
		}

		panelCajas.revalidate();
		panelCajas.repaint();
	}

	private JComponent createCajaPlaceholder(String nombre) {
		JButton btn = new JButton(nombre);
		btn.setPreferredSize(new Dimension(160, 60));
		btn.setEnabled(false);
		return btn;
	}
	// ===================================================
	// CAJA
	// ===================================================

	// =====================================================
	// IMPORTE
	// =====================================================

	private JComponent buildImportePanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Importe inicial"));
		panel.setOpaque(false);

		grupoImportes = new ButtonGroup();

		JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
		botones.setOpaque(false);

		botones.add(createImporteButton(new BigDecimal(100)));
		botones.add(createImporteButton(new BigDecimal(200)));
		botones.add(createImporteButton(new BigDecimal(250)));
		botones.add(createImporteButton(new BigDecimal(300)));

		JPanel custom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
		custom.setOpaque(false);

		txtImporteCustom = new JTextField(10);
		txtImporteCustom.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				onImporteCustomChanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				onImporteCustomChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				onImporteCustomChanged();
			}
		});

		custom.add(new JLabel("Importe personalizado:"));
		custom.add(txtImporteCustom);

		lblImporteSeleccionado = new JLabel("Importe seleccionado: —");
		lblImporteSeleccionado.setForeground(Color.LIGHT_GRAY);

		panel.add(botones);
		panel.add(custom);
		panel.add(lblImporteSeleccionado);

		return panel;
	}

	private JToggleButton createImporteButton(BigDecimal importeP) {
		JToggleButton b = new JToggleButton(importeP + " € ");
		grupoImportes.add(b);
		b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onClikImporteButon(importeP);
			}
		});

		return b;
	}

	private void onClikImporteButon(BigDecimal importe) {
		importeSeleccionado = importe;
		txtImporteCustom.setText("");
		lblImporteSeleccionado.setText("Importe seleccionado: " + importe + " €");
		actualizarEstadoConfirmacion();
	}

	public void onImporteCustomChanged() {
		String texto = txtImporteCustom.getText().trim();

		if (texto.isEmpty()) {
			importeSeleccionado = null;
			lblImporteSeleccionado.setText("Importe seleccionado: -");
			actualizarEstadoConfirmacion();
			return;

		}
		try {
			BigDecimal valor = new BigDecimal(texto);
			if (valor.compareTo(BigDecimal.ZERO) <= 0) {
				throw new NumberFormatException();
			}
			grupoImportes.clearSelection();
			importeSeleccionado = valor;
			lblImporteSeleccionado.setText("ImporteSleccionado: " + valor + " €");

		} catch (NumberFormatException e) {
			importeSeleccionado = null;
			lblImporteSeleccionado.setText("Importe selecccionado: -");
		}
		actualizarEstadoConfirmacion();
	}

	// =====================================================
	// IMPORTE FIN
	// =====================================================

	private void actualizarEstadoConfirmacion() {

		boolean puedeConfirmar = cajaSeleccionada != null && empleadoSeleccionado != null
				&& importeSeleccionado != null;

		btnConfirmar.setEnabled(puedeConfirmar);
	}

	// =====================================================
	// APERTURA CAJA
	// =====================================================
	private void confirmarAperturaCaja() {
		try {
			services.cajaFacade.abrirSesionCaja(empleadoSeleccionado, cajaSeleccionada, importeSeleccionado);
			JOptionPane.showMessageDialog(this, "Sesión de caja abierta Correctamente", "Caja abierta",
					JOptionPane.INFORMATION_MESSAGE);
			dispose();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al abrir sesión de caja",
					JOptionPane.ERROR_MESSAGE);
		}

	}

}
