package ndsr.gui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class ConnectionSettingsPanel extends JPanel {
	private static final long serialVersionUID = 2824534457641508814L;
	// Labels
	private JLabel httpProxyHostLabel = new JLabel("HTTP Proxy:");
	private JLabel httpProxyPortLabel = new JLabel("Port:");
	private JLabel httpsProxyHostLabel = new JLabel("HTTPS Proxy:");
	private JLabel httpsProxyPortLabel = new JLabel("Port:");
	// Fields
	private JTextField httpProxyHostText = new JTextField();
	private JSpinner httpProxyPortSpinner = new JSpinner();
	private JTextField httpsProxyHostText = new JTextField();
	private JSpinner httpsProxyPortSpinner = new JSpinner();
	private JCheckBox useForAllCheckbox = new JCheckBox("Use this proxy server to all protocols");
	
	public ConnectionSettingsPanel() {
		httpProxyPortSpinner.setModel(new SpinnerNumberModel(0, 0, 65535, 1));
		httpProxyPortSpinner.setEditor(new JSpinner.NumberEditor(httpProxyPortSpinner, "####0"));
		
		httpsProxyPortSpinner.setModel(new SpinnerNumberModel(0, 0, 65535, 1));
		httpsProxyPortSpinner.setEditor(new JSpinner.NumberEditor(httpsProxyPortSpinner, "####0"));
		
		// Connection Layout
		GroupLayout gl_connectionPanel = new GroupLayout(this);
		gl_connectionPanel.setHorizontalGroup(
			gl_connectionPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_connectionPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_connectionPanel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(httpsProxyHostLabel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
						.addComponent(httpProxyHostLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_connectionPanel.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(Alignment.LEADING, gl_connectionPanel.createSequentialGroup()
							.addComponent(httpProxyHostText, GroupLayout.PREFERRED_SIZE, 204, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(httpProxyPortLabel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(httpProxyPortSpinner, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_connectionPanel.createSequentialGroup()
							.addComponent(httpsProxyHostText, GroupLayout.PREFERRED_SIZE, 204, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(httpsProxyPortLabel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(httpsProxyPortSpinner, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
						.addComponent(useForAllCheckbox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap(14, Short.MAX_VALUE))
		);
		gl_connectionPanel.setVerticalGroup(
			gl_connectionPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_connectionPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_connectionPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(httpProxyHostLabel)
						.addComponent(httpProxyHostText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(httpProxyPortLabel)
						.addComponent(httpProxyPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(7)
					.addComponent(useForAllCheckbox)
					.addGap(1)
					.addGroup(gl_connectionPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(httpsProxyHostLabel)
						.addComponent(httpsProxyHostText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(httpsProxyPortLabel)
						.addComponent(httpsProxyPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(218, Short.MAX_VALUE))
		);
		gl_connectionPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {httpProxyHostLabel, httpsProxyHostLabel});
		gl_connectionPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {httpProxyPortSpinner, httpsProxyPortSpinner});
		gl_connectionPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {httpProxyPortLabel, httpsProxyPortLabel});
		gl_connectionPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {httpProxyHostText, httpsProxyHostText});

		useForAllCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				toggleOtherProtocols();
			}
		});
		setLayout(gl_connectionPanel);
	}

	private void toggleOtherProtocols() {
		boolean enable = !useForAllCheckbox.isSelected();
		httpsProxyHostText.setEnabled(enable);
		httpsProxyPortSpinner.setEnabled(enable);
	}
	
	public String getHttpProxyHost() {
		return httpProxyHostText.getText();
	}

	public void setHttpProxyHost(String httpProxyHost) {
		httpProxyHostText.setText(httpProxyHost);
	}

	public String getHttpProxyPort() {
		return String.valueOf(httpProxyPortSpinner.getValue());
	}

	public void setHttpProxyPort(Number httpProxyPort) {
		httpProxyPortSpinner.setValue(httpProxyPort);
	}

	public String getHttpsProxyHost() {
		return httpsProxyHostText.getText();
	}

	public void setHttpsProxyHost(String httpsProxyHost) {
		httpsProxyHostText.setText(httpsProxyHost);
	}

	public String getHttpsProxyPort() {
		return String.valueOf(httpsProxyPortSpinner.getValue());
	}

	public void setHttpsProxyPort(Number httpsProxyPort) {
		httpsProxyPortSpinner.setValue(httpsProxyPort);
	}
	
	public boolean isUseForAll() {
		return useForAllCheckbox.isSelected();
	}
	public void setUseForAll(boolean selected) {
		useForAllCheckbox.setSelected(selected);
	}
}
