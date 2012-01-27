package ndsr.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutFrame extends JFrame {
	private static final long serialVersionUID = -7229712466057928754L;
	
	private static final Logger LOG = LoggerFactory.getLogger(AboutFrame.class);

	private JButton okButton;

	private JPanel contributorsPanel;

	public AboutFrame(String version) {
		setResizable(false);
		setTitle("About Ndsr");
		
		okButton = new JButton("OK");
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});
		
		contributorsPanel = new JPanel();
		if (version == null) {
			version = "unknown";
		}
		String createdBy = "Ndsr (version: " + version + ") was created by:";
		
		LOG.info("createdBy = {}", createdBy);
		
		TitledBorder contributorsBorder = BorderFactory.createTitledBorder(createdBy);
		contributorsBorder.setTitleFont(new Font("Tahoma", Font.BOLD, 10));

	    contributorsBorder.setTitleJustification(TitledBorder.LEFT);
	    contributorsPanel.setBorder(contributorsBorder);
		
//		JPanel donatePanel = new JPanel();
//		TitledBorder BuyMeABeerBorder = BorderFactory.createTitledBorder("Buy me a Beer");
//		BuyMeABeerBorder.setTitleFont(new Font("Tahoma", Font.BOLD, 10));

//		BuyMeABeerBorder.setTitleJustification(TitledBorder.LEFT);
//		donatePanel.setBorder(BuyMeABeerBorder);
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(193)
							.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(contributorsPanel, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							/*.addComponent(donatePanel, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)*/))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(contributorsPanel, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(donatePanel, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(okButton)
					.addContainerGap())
		);
		
		JLabel lblNewLabel = new JLabel("Łukasz Kufel (qfel13)");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel label = new JLabel("Adam Różewicki (admroz)");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel label_1 = new JLabel("Mikołaj Sosna (gitmik)");
		label_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		GroupLayout gl_panel = new GroupLayout(contributorsPanel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(147, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(146))
						.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(10)
									.addComponent(label_1))
								.addComponent(label))
							.addGap(131))))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label_1)
					.addContainerGap(12, Short.MAX_VALUE))
		);
		contributorsPanel.setLayout(gl_panel);
		getContentPane().setLayout(groupLayout);
				
		pack();
		
		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		 
		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		 
		// Move the window
		setLocation(x, y);
	}
}
