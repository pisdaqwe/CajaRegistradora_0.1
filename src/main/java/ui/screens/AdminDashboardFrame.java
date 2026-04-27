package ui.screens;

import app.AppContext;
import model.Usuario;
import service.AppServices;
import service.SesionCajaService;
import dao.SesionCajaDao;
import dtoS.SesionCajaRefDTO;
import dao.CajaDao;
import ui.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dashboard de Manager (ADMIN / TECNICO). - Navegación principal - Acceso a
 * gestión - Acceso a caja
 */
public class AdminDashboardFrame extends BaseTpvFrame {

	private final Runnable onLogoutNavigate;

	private final AppServices services;

	// UI
	private JButton btnNuevoPedido;

	public AdminDashboardFrame(Runnable onLogoutNavigate, AppServices services) {
		super("Panel de Administración", onLogoutNavigate,services);
		this.onLogoutNavigate = onLogoutNavigate;

		this.services = services;

		requireAuthenticatedOrExit();

		buildUI();
		refreshHeader();
		refreshNuevoPedidoVisibility();
	}

	// =====================================================
	// UI
	// =====================================================

	private void buildUI() {

		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBorder(new EmptyBorder(16, 16, 16, 16));
		root.setBackground(new Color(20, 20, 20));

		// ---------- GRID CENTRAL ----------
		JPanel grid = new JPanel(new GridLayout(2, 3, 25, 25));
		grid.setOpaque(false);

		JButton btnInformes = createBigButton("Informes");
		JButton btnEmpleados = createBigButton("Gestión Empleados");
		JButton btnConfig = createBigButton("Configuración Sistema");
		JButton btnAuditoria = createBigButton("Auditoría");
		JButton btnHerramientas = createBigButton("Herramientas Técnico");
		JButton btnGestionCaja = createBigButton("Gestión Caja");

		btnInformes.addActionListener(e -> onInformes());
		btnEmpleados.addActionListener(e -> onEmpleados());
		btnConfig.addActionListener(e -> onConfig());
		btnAuditoria.addActionListener(e -> onAuditoria());
		btnHerramientas.addActionListener(e -> onHerramientas());
		btnGestionCaja.addActionListener(e -> onGestionCaja());

		aplicarPermisos(btnEmpleados, btnConfig);

		grid.add(btnInformes);
		grid.add(btnEmpleados);
		grid.add(btnConfig);
		grid.add(btnAuditoria);
		grid.add(btnHerramientas);
		grid.add(btnGestionCaja);

		root.add(grid, BorderLayout.CENTER);

		// ---------- PANEL INFERIOR ----------
		root.add(buildBottomPanel(), BorderLayout.SOUTH);

		main.add(root, BorderLayout.CENTER);
	}

	private JPanel buildBottomPanel() {

		JPanel bottom = new JPanel(new BorderLayout(12, 12));
		bottom.setOpaque(false);

		btnNuevoPedido = createPrimaryButton("Nuevo Pedido");
		JButton btnLogout = createDangerButton("Cerrar sesión");

		btnNuevoPedido.addActionListener(e -> onNuevoPedido());
		btnLogout.addActionListener(e -> doLogout());

		bottom.add(btnNuevoPedido, BorderLayout.CENTER);
		bottom.add(btnLogout, BorderLayout.EAST);

		return bottom;
	}

	// =====================================================
	// LÓGICA
	// =====================================================

	private void refreshNuevoPedidoVisibility() {
		boolean tieneCajaAsignada = 
				services.sesionCajaService.
				findSesionAbiertaByUsuarioActual().
				isPresent();

		btnNuevoPedido.setVisible(tieneCajaAsignada);
	}

	private void aplicarPermisos(JButton btnEmpleados, JButton btnConfig) {
		Usuario u = AppContext.getUsuario();
		String rol = (u.getRol() != null && u.getRol().getNombre() != null) ? u.getRol().getNombre().toUpperCase() : "";

		if ("TECNICO".equals(rol)) {
			btnEmpleados.setEnabled(false);
			btnConfig.setEnabled(false);
		}
	}

	// =====================================================
	// ACCIONES
	// =====================================================

	private void onNuevoPedido() {
	    try {
	        int idUsuario = AppContext.getUsuarioId();

	        // 1) Gate: exige sesión abierta y trae el ref (idSesion, idCaja, idSucursal, nombreCaja)
	        SesionCajaRefDTO ref = services.sesionCajaService
	                .requireSesionAbiertaPorUsuario(idUsuario);

	        // 2) Guardar en AppContext (lo usará VentasFrame)
	        AppContext.setSesionCajaActual(ref);

	        // 3) Abrir VentasFrame
	        // (de momento placeholder si aún no existe)
	        JOptionPane.showMessageDialog(this,
	                "OK: Sesión asignada -> " + ref.getNombreCaja()
	                        + " (sesión " + ref.getIdSesion() + ").\n"
	                        + "Siguiente: abrir VentasFrame.",
	                "Gate OK", JOptionPane.INFORMATION_MESSAGE);

	       
	         VentasFrame frame = new VentasFrame(onLogoutNavigate, () -> this.setVisible(true), services);
	         frame.setVisible(true);
	         this.setVisible(false);

	    } catch (Exception ex) {
	        JOptionPane.showMessageDialog(this, ex.getMessage(),
	                "No se puede abrir Ventas", JOptionPane.WARNING_MESSAGE);
	    }
	    refreshNuevoPedidoVisibility();
	}

	private void onGestionCaja() {
		GestionCajaFrame frame = new GestionCajaFrame(onLogoutNavigate, () -> this.setVisible(true), services,()->refreshNuevoPedidoVisibility());
		frame.setVisible(true);
		this.setVisible(false);

	}

	private void onInformes() {
		this.setVisible(false);
		InformesMenuFrame frame = new InformesMenuFrame(onLogoutNavigate, () -> this.setVisible(true),services);
		frame.setVisible(true);
	}

	private void onEmpleados() {
		
		 EmpleadoMenuFrame frame = new EmpleadoMenuFrame(onLogoutNavigate, () -> this.setVisible(true),services );
		    frame.setVisible(true);
		    setVisible(false);
	}

	private void onConfig() {

		this.setVisible(false);
		ConfiguracionMenuFrame frame = new ConfiguracionMenuFrame(onLogoutNavigate, () -> this.setVisible(true),services);
		frame.setVisible(true);
	}

	private void onAuditoria() {
		this.setVisible(false);
		AuditoriaMenuFrame frame = new AuditoriaMenuFrame(onLogoutNavigate, () -> this.setVisible(true),services);
		frame.setVisible(true);
	}

	private void onHerramientas() {
		 SistemaTecnicoFrame frame = new SistemaTecnicoFrame(appServices, () -> {
		        AdminDashboardFrame dashboard = new AdminDashboardFrame(onLogoutNavigate, appServices);
		        dashboard.setExtendedState(JFrame.MAXIMIZED_BOTH);
		        dashboard.setVisible(true);
		    });

		    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		    frame.setVisible(true);
		    safeDispose();
	}

	// =====================================================
	// BOTONES
	// =====================================================

	private JButton createBigButton(String text) {
		JButton b = new JButton(text);
		b.setFont(new Font("Arial", Font.BOLD, 18));
		b.setFocusPainted(false);
		b.setBackground(new Color(30, 120, 90));
		b.setForeground(Color.WHITE);
		b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
		return b;
	}

	private JButton createPrimaryButton(String text) {
		JButton b = new JButton(text);
		b.setFont(new Font("Arial", Font.BOLD, 20));
		b.setFocusPainted(false);
		b.setBackground(new Color(50, 160, 100));
		b.setForeground(Color.WHITE);
		b.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		return b;
	}

	private JButton createDangerButton(String text) {
		JButton b = new JButton(text);
		b.setFont(new Font("Arial", Font.BOLD, 18));
		b.setFocusPainted(false);
		b.setBackground(new Color(170, 50, 50));
		b.setForeground(Color.WHITE);
		b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
		return b;
	}
}