package ndsr.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class ConnectionSettingsFrame extends JFrame {
	private static final long serialVersionUID = -1572904349012369401L;

	private ConnectionSettingsPanel connectionSettingsPanel;
	
	public ConnectionSettingsFrame() {
		connectionSettingsPanel = new ConnectionSettingsPanel();
		
		getContentPane().add(connectionSettingsPanel, BorderLayout.CENTER);
	}
}
