package ui.common;

import app.AppContext;
import dtoS.EmpleadoFiltroDTO;
import dtoS.EmpleadoRowDTO;
import service.AppServices;
import ui.dialog.EmpleadoFormDialog;
import ui.dialog.ResetPinEmpleadoDialog;
import ui.gestionempleado.FichajesEmpleadosFrame;
import ui.gestionempleado.GestionEmpleadosFrame;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class EmpleadoMenuFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices services;

    public EmpleadoMenuFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super("Gestión de Empleados", onLogoutNavigate, services);
        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Empleados");
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(buildSubtitulo());
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        top.add(lblTitulo, BorderLayout.NORTH);
        top.add(lblSubtitulo, BorderLayout.SOUTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        JButton btnListado = createBigButton(
                "Listado / Gestión",
                "Ver empleados, detalle, editar, activar/desactivar y abrir la gestión principal."
        );
        JButton btnAlta = createBigButton(
                "Alta de empleado",
                "Crear un nuevo empleado con sus datos básicos, rol y PIN."
        );
        JButton btnReset = createBigButton(
                "Reset PIN",
                "Seleccionar un empleado y asignarle un nuevo PIN."
        );
        JButton btnFichajes = createBigButton(
                "Fichajes",
                "Consultar entradas, salidas, estados y observaciones."
        );

        btnListado.addActionListener(e -> openGestionEmpleados());
        btnAlta.addActionListener(e -> openAltaEmpleado());
        btnReset.addActionListener(e -> openResetPin());
        btnFichajes.addActionListener(e -> openFichajes());

        grid.add(btnListado);
        grid.add(btnAlta);
        grid.add(btnReset);
        grid.add(btnFichajes);

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

        root.add(top, BorderLayout.NORTH);
        root.add(grid, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private String buildSubtitulo() {
        if (AppContext.hasTerminalContext()) {
            return "Sucursal actual: " + AppContext.getIdSucursal();
        }
        return "Sin contexto de sucursal";
    }

    private JButton createBigButton(String titulo, String descripcion) {
        String html = "<html><div style='text-align:left;'>"
                + "<div style='font-size:18px; font-weight:bold; color:#F5F2EB; margin-bottom:8px;'>"
                + titulo
                + "</div>"
                + "<div style='font-size:12px; color:#BDC8C2; width:220px;'>"
                + descripcion
                + "</div>"
                + "</div></html>";

        JButton b = new JButton(html);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setVerticalAlignment(SwingConstants.TOP);
        b.setFont(InformeUiTheme.FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        b.setForeground(InformeUiTheme.TEXT_PRIMARY);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void openGestionEmpleados() {
        GestionEmpleadosFrame frame = new GestionEmpleadosFrame(onLogoutNavigate, () -> this.setVisible(true), services);
        frame.setVisible(true);
        this.setVisible(false);
    }

    private void openAltaEmpleado() {
        EmpleadoFormDialog dialog = new EmpleadoFormDialog(this, services, null);
        if (dialog.showDialog()) {
            TpvDialogUtils.showInfo(
                    this,
                    "Empleado creado",
                    "Empleado creado correctamente."
            );
        }
    }

    private void openResetPin() {
        EmpleadoRowDTO empleado = seleccionarEmpleadoParaAccion();
        if (empleado == null) {
            return;
        }

        ResetPinEmpleadoDialog dialog = new ResetPinEmpleadoDialog(this, services, empleado.getIdUsuario());
        if (dialog.showDialog()) {
            TpvDialogUtils.showInfo(
                    this,
                    "PIN actualizado",
                    "PIN actualizado correctamente."
            );
        }
    }

    private void openFichajes() {
        FichajesEmpleadosFrame frame = new FichajesEmpleadosFrame(onLogoutNavigate, () -> this.setVisible(true), services);
        frame.setVisible(true);
        this.setVisible(false);
    }

    private EmpleadoRowDTO seleccionarEmpleadoParaAccion() {
        EmpleadoFiltroDTO filtro = new EmpleadoFiltroDTO();

        if (AppContext.hasTerminalContext()) {
            filtro.setIdSucursal(AppContext.getIdSucursal());
        }

        List<EmpleadoRowDTO> empleados = services.usuarioService.buscarEmpleados(filtro);
        if (empleados.isEmpty()) {
            TpvDialogUtils.showWarning(
                    this,
                    "Sin empleados",
                    "No hay empleados disponibles."
            );
            return null;
        }

        return TpvDialogUtils.showSelection(
                this,
                "Elegir empleado",
                "Selecciona el empleado sobre el que quieres realizar la acción.",
                empleados
        );
    }

    private void volver() {
        safeDispose();
        if (onBack != null) {
            onBack.run();
        }
    }
}