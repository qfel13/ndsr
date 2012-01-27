package ndsr.gui.panels;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import ndsr.gui.TabbedSettingsFrame;
import ndsr.gui.WelcomeFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ReadyToUsePanel extends CardChildPanel {
	private static final long serialVersionUID = -7569711159094097905L;
	private JLabel configuredLabel;
	private JButton settingsButton;
	private JButton backButton;
	private JButton finishButton;
	
	private final WelcomeFrame welcomeFrame;
	private final TabbedSettingsFrame settings;
	
	public ReadyToUsePanel(WelcomeFrame w, TabbedSettingsFrame s) {
		welcomeFrame = w;
		settings = s;
		
		configuredLabel = new JLabel("<html>Nsdr was configured sucessfully<br>Now you can start using it with default settings by clicking on 'Start' or<br> change settings ussing 'Settings' button</html>");
		configuredLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		settingsButton = new JButton("Settings");
		settingsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showSettingsFrame();
			}
		});
		
		backButton = new JButton("Back");
		backButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		
		
		
		finishButton = new JButton("Finish and run Ndsr");
		finishButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				welcomeFrame.runNdsr();
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(backButton)
							.addPreferredGap(ComponentPlacement.RELATED, 250, Short.MAX_VALUE)
							.addComponent(finishButton))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(configuredLabel, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(settingsButton)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(configuredLabel, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(32)
							.addComponent(settingsButton)))
					.addPreferredGap(ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(backButton)
						.addComponent(finishButton))
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
	protected void showSettingsFrame() {
		settings.showFrame();
	}

	@Override
	public void refresh() {
		// do nothing
	}
}
