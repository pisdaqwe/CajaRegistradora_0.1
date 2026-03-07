package ui.ventas;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import enums.CustomizationCard;

public class CustomizationCenterPanel  extends JPanel{

	
	 
	private static final long serialVersionUID = 1L;
	private final CardLayout cardLayout = new CardLayout();
	
	public CustomizationCenterPanel() {
		setLayout(cardLayout);
		setBackground( new Color(20,20,20));
		add(buildPlaceholderCard("SHOTS"), CustomizationCard.SHOTS.name());
        add(buildPlaceholderCard("SYRUPS"), CustomizationCard.SYRUPS.name());
        add(buildPlaceholderCard("TOPPINGS"), CustomizationCard.TOPPINGS.name());
        add(buildPlaceholderCard("MILK"), CustomizationCard.MILK.name());
        add(buildPlaceholderCard("PREP"), CustomizationCard.PREP.name());
		 
	}
	 public void showCard(CustomizationCard card) {
	        if (card == null) return;
	        cardLayout.show(this, card.name());
	    }
	private JComponent buildPlaceholderCard(String title ) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(20,20,20));
		panel.setBorder(new EmptyBorder(20,20,20,20));
		
		
		JLabel lblTitle = new JLabel(title,SwingConstants.LEFT);
		lblTitle.setFont(new Font("Monospaced",Font.BOLD,28));
		lblTitle.setForeground(Color.WHITE);
		JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setFont(new Font("Monospaced", Font.PLAIN, 16));
        txt.setBackground(new Color(30, 30, 30));
        txt.setForeground(new Color(220, 220, 220));
        txt.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        txt.setText("Placeholder de la card central: " + title + "\n\nAquí irán los botones reales de esta categoría.");

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(new JScrollPane(txt), BorderLayout.CENTER);

        return panel;
		
	}
	
	
	
	

}
