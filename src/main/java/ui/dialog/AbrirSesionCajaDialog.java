package ui.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dtoS.CajaEstadoDTO;
import model.Caja;
import service.AppServices;

import java.awt.*;

public class AbrirSesionCajaDialog extends JDialog {
    private JTable tablaEmpleados;
    private JPanel panelCajas;
    private JTextField txtImporteCustom;
    private JLabel lblImporteSeleccionado;
    private ButtonGroup grupoCajas;
    private CajaEstadoDTO cajaSeleccionada;
    private AppServices services;
	
	public AbrirSesionCajaDialog (Window owner,AppServices services ) {
		super(owner,"Abrir sesion de Caja",ModalityType.APPLICATION_MODAL); 
		this.services = services;
		buildUI();
		setSize(900,650);
		setLocationRelativeTo(owner);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
	}
	public void buildUI() {
		JPanel root  = new JPanel(new BorderLayout(16,16));
		root.setBorder(new EmptyBorder(16,16,16,16));
		root.setBackground(new Color(25,25,25));
		setContentPane(root);
		
		
		
		
	}
	private JComponent buildHeader() {
		JLabel titulo = new JLabel("Abrir sesion de Caja");
		JLabel title = new JLabel("ABRIR SESIÓN DE CAJA");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        return title;
		
		
	}
	private JComponent buildCneter() {
		JPanel center = new  JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setOpaque(false);
		center.add(buildEmpleadoPanel());
		center.add(Box.createVerticalStrut(12));
		center.add(buildCajasPanel());
		center.add(Box.createVerticalStrut(12));
		center.add(buildImportePanel());
		return center;
		
	}
	
	private JComponent  buildEmpleadoPanel() {
		JPanel panel = new JPanel(new BorderLayout(8,8));
		panel.setBorder(BorderFactory.createTitledBorder("Empleados Fichados"));
		panel.setOpaque(true);
		
		JTextField txtBuscar = new JTextField();
		panel.add(txtBuscar,BorderLayout.NORTH);
		
		tablaEmpleados = new JTable();
		JScrollPane pane = new JScrollPane(tablaEmpleados);
		panel.add(pane,BorderLayout.CENTER);
		
		pane.setPreferredSize(new Dimension(800,180));
		return panel;
		
	}
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

    private JComponent createCajaPlaceholder(String nombre) {
        JButton btn = new JButton(nombre);
        btn.setPreferredSize(new Dimension(160, 60));
        btn.setEnabled(false);
        return btn;
    }

    // =====================================================
    // IMPORTE
    // =====================================================

    private JComponent buildImportePanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Importe inicial"));
        panel.setOpaque(false);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        botones.setOpaque(false);

        botones.add(createImporteButton("100 €"));
        botones.add(createImporteButton("200 €"));
        botones.add(createImporteButton("250 €"));
        botones.add(createImporteButton("300 €"));

        JPanel custom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        custom.setOpaque(false);

        txtImporteCustom = new JTextField(10);
        custom.add(new JLabel("Importe personalizado:"));
        custom.add(txtImporteCustom);

        lblImporteSeleccionado = new JLabel("Importe seleccionado: —");
        lblImporteSeleccionado.setForeground(Color.LIGHT_GRAY);

        panel.add(botones);
        panel.add(custom);
        panel.add(lblImporteSeleccionado);

        return panel;
    }

    private JButton createImporteButton(String text) {
        JButton b = new JButton(text);
        return b;
    }

    // =====================================================
    // FOOTER
    // =====================================================

    private JComponent buildFooter() {

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JButton btnConfirmar = new JButton("Abrir sesión de caja");
        btnConfirmar.setEnabled(false); // luego se habilita con validaciones

        footer.add(btnCancelar);
        footer.add(btnConfirmar);

        return footer;
    }
    //=================================================
	//Carga de cajas 
    //=================================================
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
    private void actualizarEstadoConfirmacion() {

//        boolean puedeConfirmar =
//                cajaSeleccionada != null
//                && empleadoSeleccionado != null
//                && importeSeleccionado != null;
//
//        btnConfirmar.setEnabled(puedeConfirmar);
    }

    
	
}
