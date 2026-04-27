package ui.gestionempleado;

import app.AppContext;
import dtoS.EmpleadoDetalleDTO;
import dtoS.EmpleadoFiltroDTO;
import dtoS.EmpleadoRowDTO;
import model.Rol;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.dialog.EmpleadoFormDialog;
import ui.dialog.ResetPinEmpleadoDialog;
import ui.table.EmpleadosTableModel;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GestionEmpleadosFrame extends BaseTpvFrame {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private final Runnable onBack;
	private final AppServices services;

	private JTextField txtBuscar;
	private JComboBox<Object> cmbRol;
	private JComboBox<String> cmbEstado;
	private JCheckBox chkSoloFichados;
	private JCheckBox chkSoloCajaAbierta;

	private JTable tblEmpleados;
	private EmpleadosTableModel tableModel;

	private JButton btnNuevo;
	private JButton btnEditar;
	private JButton btnActivarDesactivar;
	private JButton btnResetPin;
	private JButton btnVerFichajes;

	private JLabel lblNombreValor;
	private JLabel lblUsuarioValor;
	private JLabel lblRolValor;
	private JLabel lblSucursalValor;
	private JLabel lblActivoValor;
	private JLabel lblFechaCreacionValor;
	private JLabel lblFichajeActualValor;
	private JLabel lblSesionCajaValor;
	private JLabel lblCajaActualValor;
	private JLabel lblUltimaActividadValor;
	private JTextArea txtObservaciones;

	private EmpleadoRowDTO empleadoSeleccionado;

	public GestionEmpleadosFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
		super("Gestión de Empleados", onLogoutNavigate, services);
		this.onBack = onBack;
		this.services = services;

		requireAuthenticatedOrExit();
		buildUI();
		cargarCombos();
		cargarTabla();
		refreshHeader();
	}

	private void buildUI() {
		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBorder(new EmptyBorder(16, 16, 16, 16));
		root.setBackground(InformeUiTheme.APP_BG);

		root.add(buildHeaderPanel(), BorderLayout.NORTH);
		root.add(buildCenterPanel(), BorderLayout.CENTER);
		root.add(buildBottomPanel(), BorderLayout.SOUTH);

		main.add(root, BorderLayout.CENTER);
	}

	private JPanel buildHeaderPanel() {
		JPanel wrapper = new JPanel(new BorderLayout(0, 10));
		wrapper.setOpaque(false);

		JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
		titlePanel.setOpaque(false);

		JLabel lblTitulo = new JLabel("Gestión de Empleados");
		lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
		lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

		JLabel lblSubtitulo = new JLabel(buildSubtitulo());
		lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
		lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

		titlePanel.add(lblTitulo);
		titlePanel.add(lblSubtitulo);

		JPanel filtros = InformeUiTheme.createCardPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		txtBuscar = new JTextField(22);
		InformeUiTheme.styleTextField(txtBuscar);

		cmbRol = new JComboBox<>();
		InformeUiTheme.styleCombo(cmbRol);

		cmbEstado = new JComboBox<>(new String[] { "Todos", "Activos", "Inactivos" });
		InformeUiTheme.styleCombo(cmbEstado);

		chkSoloFichados = new JCheckBox("Solo fichados");
		InformeUiTheme.styleCheckBox(chkSoloFichados);

		chkSoloCajaAbierta = new JCheckBox("Solo caja abierta");
		InformeUiTheme.styleCheckBox(chkSoloCajaAbierta);

		JButton btnRefrescar = new JButton("Refrescar");
		InformeUiTheme.styleSecondaryButton(btnRefrescar);

		btnNuevo = new JButton("Nuevo empleado");
		InformeUiTheme.stylePrimaryButton(btnNuevo);

		int x = 0;

		gbc.gridx = x++;
		gbc.gridy = 0;
		filtros.add(InformeUiTheme.createFieldLabel("Buscar:"), gbc);

		gbc.gridx = x++;
		gbc.weightx = 1.0;
		filtros.add(txtBuscar, gbc);

		gbc.gridx = x++;
		gbc.weightx = 0;
		filtros.add(InformeUiTheme.createFieldLabel("Rol:"), gbc);

		gbc.gridx = x++;
		filtros.add(cmbRol, gbc);

		gbc.gridx = x++;
		filtros.add(InformeUiTheme.createFieldLabel("Estado:"), gbc);

		gbc.gridx = x++;
		filtros.add(cmbEstado, gbc);

		gbc.gridx = x++;
		filtros.add(chkSoloFichados, gbc);

		gbc.gridx = x++;
		filtros.add(chkSoloCajaAbierta, gbc);

		gbc.gridx = x++;
		filtros.add(btnRefrescar, gbc);

		gbc.gridx = x;
		filtros.add(btnNuevo, gbc);

		btnRefrescar.addActionListener(e -> refrescarTodo());
		btnNuevo.addActionListener(e -> onNuevoEmpleado());

		wrapper.add(titlePanel, BorderLayout.NORTH);
		wrapper.add(filtros, BorderLayout.CENTER);

		return wrapper;
	}

	private JSplitPane buildCenterPanel() {
		tableModel = new EmpleadosTableModel();
		tblEmpleados = new JTable(tableModel);
		InformeUiTheme.styleTable(tblEmpleados);
		tblEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		tblEmpleados.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
			if (!e.getValueIsAdjusting()) {
				onEmpleadoSeleccionado();
			}
		});

		JPanel left = new JPanel(new BorderLayout(8, 8));
		left.setOpaque(false);
		left.add(new JScrollPane(tblEmpleados), BorderLayout.CENTER);
		InformeUiTheme.styleScrollPane((JScrollPane) left.getComponent(0));
		left.add(buildActionsPanel(), BorderLayout.SOUTH);

		JPanel right = buildDetailPanel();

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		split.setResizeWeight(0.70);
		split.setDividerLocation(930);
		split.setBorder(null);

		return split;
	}

	private JPanel buildActionsPanel() {
		JPanel panel = InformeUiTheme.createCardPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

		btnEditar = new JButton("Editar");
		btnActivarDesactivar = new JButton("Activar / Desactivar");
		btnResetPin = new JButton("Reset PIN");
		btnVerFichajes = new JButton("Ver fichajes");

		InformeUiTheme.styleSecondaryButton(btnEditar);
		InformeUiTheme.styleSecondaryButton(btnActivarDesactivar);
		InformeUiTheme.styleSecondaryButton(btnResetPin);
		InformeUiTheme.styleSecondaryButton(btnVerFichajes);

		btnEditar.addActionListener(e -> onEditarEmpleado());
		btnActivarDesactivar.addActionListener(e -> onActivarDesactivarEmpleado());
		btnResetPin.addActionListener(e -> onResetPinEmpleado());
		btnVerFichajes.addActionListener(e -> onVerFichajes());

		panel.add(btnEditar);
		panel.add(btnActivarDesactivar);
		panel.add(btnResetPin);
		panel.add(btnVerFichajes);

		return panel;
	}

	private JPanel buildDetailPanel() {
		JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

		JLabel title = InformeUiTheme.createSectionTitle("Detalle del empleado");
		panel.add(title, BorderLayout.NORTH);

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 8, 6, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		lblNombreValor = createValueLabel();
		lblUsuarioValor = createValueLabel();
		lblRolValor = createValueLabel();
		lblSucursalValor = createValueLabel();
		lblActivoValor = createValueLabel();
		lblFechaCreacionValor = createValueLabel();
		lblFichajeActualValor = createValueLabel();
		lblSesionCajaValor = createValueLabel();
		lblCajaActualValor = createValueLabel();
		lblUltimaActividadValor = createValueLabel();

		txtObservaciones = new JTextArea(5, 20);
		txtObservaciones.setEditable(false);
		txtObservaciones.setLineWrap(true);
		txtObservaciones.setWrapStyleWord(true);
		txtObservaciones.setBackground(InformeUiTheme.CARD_BG_2);
		txtObservaciones.setForeground(InformeUiTheme.TEXT_PRIMARY);
		txtObservaciones.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
		txtObservaciones.setBorder(InformeUiTheme.createInnerCardBorder());
		txtObservaciones.setFont(InformeUiTheme.FONT_BODY);

		int y = 0;
		addDetailRow(form, gbc, y++, "Nombre:", lblNombreValor);
		addDetailRow(form, gbc, y++, "Usuario:", lblUsuarioValor);
		addDetailRow(form, gbc, y++, "Rol:", lblRolValor);
		addDetailRow(form, gbc, y++, "Sucursal:", lblSucursalValor);
		addDetailRow(form, gbc, y++, "Activo:", lblActivoValor);
		addDetailRow(form, gbc, y++, "Fecha creación:", lblFechaCreacionValor);
		addDetailRow(form, gbc, y++, "Fichaje actual:", lblFichajeActualValor);
		addDetailRow(form, gbc, y++, "Sesión caja:", lblSesionCajaValor);
		addDetailRow(form, gbc, y++, "Caja actual:", lblCajaActualValor);
		addDetailRow(form, gbc, y++, "Última actividad:", lblUltimaActividadValor);

		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.gridwidth = 2;
		form.add(InformeUiTheme.createFieldLabel("Observaciones:"), gbc);

		gbc.gridy = y + 1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane scrollObs = new JScrollPane(txtObservaciones);
		InformeUiTheme.styleScrollPane(scrollObs);
		form.add(scrollObs, gbc);

		panel.add(form, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildBottomPanel() {
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		bottom.setOpaque(false);

		JButton btnVolver = new JButton("Volver");
		InformeUiTheme.styleSecondaryButton(btnVolver);
		btnVolver.addActionListener(e -> volver());

		JButton btnLogout = new JButton("Cerrar sesión");
		InformeUiTheme.styleDangerButton(btnLogout);
		btnLogout.addActionListener(e -> doLogout());

		bottom.add(btnVolver);
		bottom.add(btnLogout);

		return bottom;
	}

	private String buildSubtitulo() {
		if (AppContext.hasTerminalContext()) {
			return "Sucursal actual: " + AppContext.getIdSucursal();
		}
		return "Sin contexto de sucursal";
	}

	private JLabel createValueLabel() {
		JLabel lbl = new JLabel("-");
		lbl.setFont(InformeUiTheme.FONT_BODY);
		lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
		return lbl;
	}

	private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel valueLabel) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(InformeUiTheme.createFieldLabel(label), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		panel.add(valueLabel, gbc);
	}

	private void cargarCombos() {
		cmbRol.removeAllItems();
		cmbRol.addItem("Todos");

		List<Rol> roles = services.usuarioService.getRoles();
		for (Rol rol : roles) {
			cmbRol.addItem(rol);
		}
	}

	private void cargarTabla() {
		EmpleadoFiltroDTO filtro = leerFiltroActual();
		List<EmpleadoRowDTO> rows = services.usuarioService.buscarEmpleados(filtro);
		tableModel.setRows(rows);

		if (rows.isEmpty()) {
			empleadoSeleccionado = null;
			limpiarDetalle();
		}
	}

	private EmpleadoFiltroDTO leerFiltroActual() {
		EmpleadoFiltroDTO filtro = new EmpleadoFiltroDTO();
		filtro.setTextoBusqueda(txtBuscar.getText());

		Object rolSeleccionado = cmbRol.getSelectedItem();
		if (rolSeleccionado instanceof Rol rol) {
			filtro.setIdRol(rol.getIdRol());
		}

		String estado = (String) cmbEstado.getSelectedItem();
		if ("Activos".equalsIgnoreCase(estado)) {
			filtro.setSoloActivos(Boolean.TRUE);
		} else if ("Inactivos".equalsIgnoreCase(estado)) {
			filtro.setSoloActivos(Boolean.FALSE);
		} else {
			filtro.setSoloActivos(null);
		}

		if (AppContext.hasTerminalContext()) {
			filtro.setIdSucursal(AppContext.getIdSucursal());
		}

		filtro.setSoloConFichajeAbierto(chkSoloFichados.isSelected());
		filtro.setSoloConCajaAbierta(chkSoloCajaAbierta.isSelected());

		return filtro;
	}

	private void onEmpleadoSeleccionado() {
		int viewRow = tblEmpleados.getSelectedRow();
		if (viewRow < 0) {
			empleadoSeleccionado = null;
			limpiarDetalle();
			return;
		}

		int modelRow = tblEmpleados.convertRowIndexToModel(viewRow);
		empleadoSeleccionado = tableModel.getRow(modelRow);

		if (empleadoSeleccionado != null) {
			cargarDetalleEmpleado(empleadoSeleccionado.getIdUsuario());
		}
	}

	private void cargarDetalleEmpleado(int idUsuario) {
		Optional<EmpleadoDetalleDTO> opt = services.usuarioService.getDetalleEmpleado(idUsuario);

		if (opt.isEmpty()) {
			limpiarDetalle();
			return;
		}

		EmpleadoDetalleDTO d = opt.get();

		lblNombreValor.setText(safe(d.getNombre()));
		lblUsuarioValor.setText(safe(d.getUsuario()));
		lblRolValor.setText(safe(d.getNombreRol()));
		lblSucursalValor.setText(safe(d.getNombreSucursal()));
		lblActivoValor.setText(d.getEstadoActivoTexto());
		lblFechaCreacionValor.setText(formatDateTime(d.getFechaCreacion()));
		lblFichajeActualValor.setText(d.getFichajeActualTexto());
		lblSesionCajaValor.setText(d.getSesionCajaActualTexto());
		lblCajaActualValor.setText(d.getNombreCajaActualTexto());
		lblUltimaActividadValor.setText(formatDateTime(d.getUltimaActividad()));
		txtObservaciones.setText(safe(d.getObservacionesOperativas()));
	}

	private void onNuevoEmpleado() {
		EmpleadoFormDialog dialog = new EmpleadoFormDialog(this, services, null);
		if (dialog.showDialog()) {
			refrescarTodo();
		}
	}

	private void onEditarEmpleado() {
		if (!checkEmpleadoSeleccionado()) {
			return;
		}

		EmpleadoFormDialog dialog = new EmpleadoFormDialog(this, services, empleadoSeleccionado.getIdUsuario());
		if (dialog.showDialog()) {
			refrescarTodo();
		}
	}

	private void onActivarDesactivarEmpleado() {
		if (!checkEmpleadoSeleccionado()) {
			return;
		}

		boolean nuevoEstado = !empleadoSeleccionado.isActivo();
		String accion = nuevoEstado ? "activar" : "desactivar";

		int confirm = JOptionPane.showConfirmDialog(this,
				"¿Seguro que quieres " + accion + " al empleado seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			services.usuarioService.cambiarEstadoActivo(empleadoSeleccionado.getIdUsuario(), nuevoEstado,
					AppContext.getUsuarioId(), AppContext.getIdSucursal());
			JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.", "OK",
					JOptionPane.INFORMATION_MESSAGE);
			refrescarTodo();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo actualizar el estado",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onResetPinEmpleado() {
		if (!checkEmpleadoSeleccionado()) {
			return;
		}

		ResetPinEmpleadoDialog dialog = new ResetPinEmpleadoDialog(this, services, empleadoSeleccionado.getIdUsuario());
		if (dialog.showDialog()) {
			JOptionPane.showMessageDialog(this, "PIN actualizado correctamente.", "OK",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void onVerFichajes() {
		FichajesEmpleadosFrame frame = new FichajesEmpleadosFrame(onLogoutNavigate, () -> this.setVisible(true),
				services);
		frame.setVisible(true);
		this.setVisible(false);
	}

	private void refrescarTodo() {
		cargarTabla();

		if (empleadoSeleccionado != null) {
			cargarDetalleEmpleado(empleadoSeleccionado.getIdUsuario());
		}
	}

	private void limpiarDetalle() {
		lblNombreValor.setText("-");
		lblUsuarioValor.setText("-");
		lblRolValor.setText("-");
		lblSucursalValor.setText("-");
		lblActivoValor.setText("-");
		lblFechaCreacionValor.setText("-");
		lblFichajeActualValor.setText("-");
		lblSesionCajaValor.setText("-");
		lblCajaActualValor.setText("-");
		lblUltimaActividadValor.setText("-");
		txtObservaciones.setText("");
	}

	private boolean checkEmpleadoSeleccionado() {
		if (empleadoSeleccionado == null) {
			JOptionPane.showMessageDialog(this, "Selecciona primero un empleado.", "Sin selección",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	private String formatDateTime(java.time.LocalDateTime value) {
		return value == null ? "-" : value.format(DATE_TIME_FORMATTER);
	}

	private String safe(String value) {
		return value == null || value.trim().isEmpty() ? "-" : value;
	}

	private void volver() {
		safeDispose();
		if (onBack != null) {
			onBack.run();
		}
	}
}