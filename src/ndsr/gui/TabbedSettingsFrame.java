/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TabbedSettingsFrame.java
 *
 * Created on Jan 11, 2011, 2:26:20 PM
 */

package ndsr.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

import ndsr.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import javax.swing.SwingConstants;

/**
 * @author lkufel
 */
public class TabbedSettingsFrame extends JFrame {

	private static final Logger log = LoggerFactory.getLogger(TabbedSettingsFrame.class);

	private static final long serialVersionUID = -2075547569254525343L;

	private Configuration configuration;

	// Account Panel
	private JPanel googleAccountPanel = new JPanel(/* "Google account" */);

	// Labels
	private JLabel usernameLabel = new JLabel("Username");
	private JLabel passwordLabel = new JLabel("Password");
	private JLabel urlLabel = new JLabel("Calendar URL");
	// Fields
	private JTextField usernameText = new JTextField();
	private JPasswordField passwordText = new JPasswordField();
	private JTextField urlText = new JTextField();

	// Event Panel
	private JPanel eventsPanel = new JPanel(/* "Events */);
	// Labels
	private JLabel sleepTimeLabel = new JLabel("Sleep time (in minutes)");
	private JLabel idleTimeLabel = new JLabel("Idle time (in minutes)");
	private JLabel eventNameLabel = new JLabel("Calendar event name");
	private JLabel lastIdleTimeThresholdLabel = new JLabel("New event after idle (in minutes)");
	private JLabel minutesBeforeFirstLabel = new JLabel("Minutes before first event");
	private JLabel eventMinutesAheadLabel = new JLabel("Current event minutes ahead");
	// Fields
	private JTextField sleepTimeText = new JTextField();
	private JTextField idleTimeText = new JTextField();
	private JTextField eventNameText = new JTextField();
	private JTextField lastIdleTimeThresholdText = new JTextField();
	private JTextField minutesBeforeFirstText = new JTextField();
	private JTextField eventMinutesAheadText = new JTextField();

	// Connection Panel
	private JPanel connectionPanel = new JPanel(/* "Connection" */);
	// Labels
	private JLabel httpProxyHostLabel = new JLabel("Http proxy host");
	private JLabel httpProxyPortLabel = new JLabel("Http proxy port");
	private JLabel httpsProxyHostLabel = new JLabel("Https proxy host");
	private JLabel httpsProxyPortLabel = new JLabel("Https proxy port");
	// Fields
	private JTextField httpProxyHostText = new JTextField();
	private JTextField httpProxyPortText = new JTextField();
	private JTextField httpsProxyHostText = new JTextField();
	private JTextField httpsProxyPortText = new JTextField();

	// Icons Panel
	private JPanel iconsPanel = new JPanel(/* "Icons" */);
	// Labels
	private JLabel iconNormalLabel = new JLabel("Normal icon location");
	private JLabel iconInactiveLabel = new JLabel("Inactive icon location");
	// Fields
	private JTextField iconNormalText = new JTextField();
	private JTextField iconInactiveText = new JTextField();
	// Buttons
	private JButton iconNormalBrowseButton = new JButton("...");
	private JButton iconInactiveBrowseButton = new JButton("...");

	// Other Panel
	private JPanel otherPanel = new JPanel(/* "Other" */);
	// Labels
	private JLabel workIpRegExpLabel = new JLabel("Work IP regular expression");
	// Fields
	private JTextField workIpRegExpText = new JTextField();

	// Settings Buttons
	private JButton cancelButton = new JButton();
	private JButton okButton = new JButton();

	private JTabbedPane settingsTabbedPanel = new JTabbedPane();

	/** Creates new form TabbedSettingsFrame */
	public TabbedSettingsFrame() {
		initComponents();
	}

	public TabbedSettingsFrame(Configuration configuration) {

		this.configuration = configuration;
		initComponents();
		setTextsFromConfiguration();
	}

	private void setTextsFromConfiguration() {
		usernameText.setText(configuration.getUser());
		passwordText.setText(configuration.getPasswd());
		urlText.setText(configuration.getUrl());

		httpProxyHostText.setText(configuration.getHttpProxyHost());
		httpProxyPortText.setText(configuration.getHttpProxyPort());
		httpsProxyHostText.setText(configuration.getHttpsProxyHost());
		httpsProxyPortText.setText(configuration.getHttpsProxyPort());

		sleepTimeText.setText("" + configuration.getSleepTime());
		idleTimeText.setText("" + configuration.getIdleTime());
		eventNameText.setText(configuration.getEventName());
		lastIdleTimeThresholdText.setText("" + configuration.getLastIdleTimeThreshold());

		workIpRegExpText.setText(configuration.getWorkIpRegExp());
		iconNormalText.setText(configuration.getNormalIconLocation());
		iconInactiveText.setText(configuration.getInactiveIconLocation());
	}

	private void initComponents() {
		// JFrame settings
		this.setTitle("Settings");
		this.setMinimumSize(new Dimension(400, 250));
		this.setResizable(false);

		// Account Layout
		GroupLayout gl_googleAccountPanel = new GroupLayout(googleAccountPanel);
		googleAccountPanel.setLayout(gl_googleAccountPanel);
		gl_googleAccountPanel.setHorizontalGroup(gl_googleAccountPanel.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				gl_googleAccountPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_googleAccountPanel
										.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(usernameLabel, GroupLayout.Alignment.LEADING,
												GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
										.addComponent(passwordLabel, GroupLayout.Alignment.LEADING,
												GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
										.addComponent(urlLabel, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(
								gl_googleAccountPanel
										.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(urlText, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
										.addComponent(passwordText, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
										.addComponent(usernameText, GroupLayout.Alignment.TRAILING,
												GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)).addContainerGap()));
		gl_googleAccountPanel.setVerticalGroup(gl_googleAccountPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						gl_googleAccountPanel
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_googleAccountPanel.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(usernameLabel).addComponent(usernameText))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										gl_googleAccountPanel
												.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(passwordLabel)
												.addComponent(passwordText, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										gl_googleAccountPanel
												.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(urlLabel)
												.addComponent(urlText, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(53, 53, 53)));

		// Events Layout
		GroupLayout gl_eventsPanel = new GroupLayout(eventsPanel);
		gl_eventsPanel.setHorizontalGroup(gl_eventsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_eventsPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.LEADING)
										.addComponent(eventMinutesAheadLabel, Alignment.TRAILING,
												GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
										.addComponent(minutesBeforeFirstLabel, GroupLayout.DEFAULT_SIZE, 164,
												Short.MAX_VALUE)
										.addComponent(idleTimeLabel, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
										.addComponent(eventNameLabel, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
										.addComponent(sleepTimeLabel, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
										.addComponent(lastIdleTimeThresholdLabel, GroupLayout.DEFAULT_SIZE, 164,
												Short.MAX_VALUE))
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_eventsPanel
														.createSequentialGroup()
														.addPreferredGap(ComponentPlacement.UNRELATED)
														.addGroup(
																gl_eventsPanel
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(minutesBeforeFirstText,
																				GroupLayout.DEFAULT_SIZE, 206,
																				Short.MAX_VALUE)
																		.addComponent(sleepTimeText,
																				Alignment.TRAILING,
																				GroupLayout.DEFAULT_SIZE, 206,
																				Short.MAX_VALUE)
																		.addComponent(idleTimeText, Alignment.TRAILING,
																				GroupLayout.DEFAULT_SIZE, 206,
																				Short.MAX_VALUE)
																		.addComponent(eventNameText,
																				Alignment.TRAILING,
																				GroupLayout.DEFAULT_SIZE, 206,
																				Short.MAX_VALUE)
																		.addComponent(lastIdleTimeThresholdText,
																				GroupLayout.DEFAULT_SIZE, 206,
																				Short.MAX_VALUE)))
										.addGroup(
												Alignment.TRAILING,
												gl_eventsPanel
														.createSequentialGroup()
														.addGap(10)
														.addComponent(eventMinutesAheadText,
																GroupLayout.PREFERRED_SIZE, 206,
																GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		gl_eventsPanel.setVerticalGroup(gl_eventsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_eventsPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.BASELINE, false)
										.addComponent(sleepTimeLabel)
										.addComponent(sleepTimeText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(idleTimeText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(idleTimeLabel))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.TRAILING)
										.addComponent(eventNameLabel)
										.addComponent(eventNameText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(lastIdleTimeThresholdText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lastIdleTimeThresholdLabel))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(minutesBeforeFirstLabel)
										.addComponent(minutesBeforeFirstText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_eventsPanel
										.createParallelGroup(Alignment.LEADING)
										.addComponent(eventMinutesAheadText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(eventMinutesAheadLabel)).addGap(29)));
		eventsPanel.setLayout(gl_eventsPanel);

		// Connection Layout
		GroupLayout gl_connectionPanel = new GroupLayout(connectionPanel);
		gl_connectionPanel.setHorizontalGroup(gl_connectionPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_connectionPanel
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_connectionPanel
												.createParallelGroup(Alignment.LEADING, false)
												.addComponent(httpsProxyPortLabel, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(httpsProxyHostLabel, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(httpProxyPortLabel, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(httpProxyHostLabel, GroupLayout.DEFAULT_SIZE, 101,
														Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(
										gl_connectionPanel
												.createParallelGroup(Alignment.LEADING)
												.addComponent(httpsProxyHostText, GroupLayout.DEFAULT_SIZE, 240,
														Short.MAX_VALUE)
												.addComponent(httpProxyPortText, GroupLayout.DEFAULT_SIZE, 240,
														Short.MAX_VALUE)
												.addComponent(httpProxyHostText, Alignment.TRAILING,
														GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
												.addComponent(httpsProxyPortText, Alignment.TRAILING,
														GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
								.addContainerGap()));
		gl_connectionPanel.setVerticalGroup(gl_connectionPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_connectionPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_connectionPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(httpProxyHostLabel)
										.addComponent(httpProxyHostText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_connectionPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(httpProxyPortLabel)
										.addComponent(httpProxyPortText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_connectionPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(httpsProxyHostLabel)
										.addComponent(httpsProxyHostText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_connectionPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(httpsProxyPortLabel)
										.addComponent(httpsProxyPortText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(43, Short.MAX_VALUE)));
		connectionPanel.setLayout(gl_connectionPanel);

		// Icons Layout
		JLabel lblWarningChangesIn = new JLabel("Warning: changes in this tab requires restart");
		lblWarningChangesIn.setHorizontalAlignment(SwingConstants.CENTER);
		lblWarningChangesIn.setForeground(Color.RED);

		GroupLayout gl_iconsPanel = new GroupLayout(iconsPanel);
		gl_iconsPanel.setHorizontalGroup(gl_iconsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_iconsPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_iconsPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_iconsPanel
														.createSequentialGroup()
														.addGroup(
																gl_iconsPanel
																		.createParallelGroup(Alignment.LEADING, false)
																		.addComponent(iconInactiveLabel,
																				Alignment.TRAILING,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(iconNormalLabel,
																				Alignment.TRAILING,
																				GroupLayout.DEFAULT_SIZE, 112,
																				Short.MAX_VALUE))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(
																gl_iconsPanel
																		.createParallelGroup(Alignment.TRAILING)
																		.addComponent(iconNormalText,
																				GroupLayout.DEFAULT_SIZE, 233,
																				Short.MAX_VALUE)
																		.addComponent(iconInactiveText,
																				GroupLayout.DEFAULT_SIZE, 233,
																				Short.MAX_VALUE))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(
																gl_iconsPanel
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(iconInactiveBrowseButton,
																				Alignment.TRAILING,
																				GroupLayout.PREFERRED_SIZE, 25,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(iconNormalBrowseButton,
																				Alignment.TRAILING,
																				GroupLayout.PREFERRED_SIZE, 25,
																				GroupLayout.PREFERRED_SIZE)))
										.addComponent(lblWarningChangesIn, GroupLayout.DEFAULT_SIZE, 380,
												Short.MAX_VALUE)).addContainerGap()));
		gl_iconsPanel
				.setVerticalGroup(gl_iconsPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_iconsPanel
										.createSequentialGroup()
										.addGap(5)
										.addComponent(lblWarningChangesIn, GroupLayout.PREFERRED_SIZE, 20,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(
												gl_iconsPanel
														.createParallelGroup(Alignment.BASELINE)
														.addComponent(iconNormalText, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(iconNormalBrowseButton)
														.addComponent(iconNormalLabel))
										.addGroup(
												gl_iconsPanel
														.createParallelGroup(Alignment.LEADING)
														.addGroup(
																gl_iconsPanel.createSequentialGroup().addGap(9)
																		.addComponent(iconInactiveLabel))
														.addGroup(
																gl_iconsPanel
																		.createSequentialGroup()
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addGroup(
																				gl_iconsPanel
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addGroup(
																								gl_iconsPanel
																										.createSequentialGroup()
																										.addGap(1)
																										.addComponent(
																												iconInactiveText,
																												GroupLayout.PREFERRED_SIZE,
																												GroupLayout.DEFAULT_SIZE,
																												GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								iconInactiveBrowseButton))))
										.addContainerGap(69, Short.MAX_VALUE)));
		iconsPanel.setLayout(gl_iconsPanel);

		iconNormalBrowseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				iconNormalMouseClicked(event);
			}
		});
		iconInactiveBrowseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				iconInactiveMouseClicked(event);
			}
		});

		// Other Layout
		GroupLayout gl_otherPanel = new GroupLayout(otherPanel);
		otherPanel.setLayout(gl_otherPanel);
		gl_otherPanel.setHorizontalGroup(gl_otherPanel.createParallelGroup(Alignment.TRAILING).addGroup(
				Alignment.LEADING,
				gl_otherPanel.createSequentialGroup().addContainerGap()
						.addComponent(workIpRegExpLabel, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(workIpRegExpText, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE).addGap(11)));
		gl_otherPanel.setVerticalGroup(gl_otherPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_otherPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_otherPanel
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(workIpRegExpLabel)
										.addComponent(workIpRegExpText, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(108, Short.MAX_VALUE)));

		settingsTabbedPanel.addTab("Google account", googleAccountPanel);
		settingsTabbedPanel.addTab("Events", eventsPanel);
		settingsTabbedPanel.addTab("Connection", connectionPanel);
		settingsTabbedPanel.addTab("Icons", null, iconsPanel, null);
		settingsTabbedPanel.addTab("Other", otherPanel);

		cancelButton.setText("Cancel");
		cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				cancelButtonMouseClicked(evt);
			}
		});

		okButton.setText("OK");
		okButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				okButtonMouseClicked(evt);
			}
		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(settingsTabbedPanel, GroupLayout.Alignment.LEADING)
										.addGroup(
												layout.createSequentialGroup().addComponent(cancelButton)
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(okButton))).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(settingsTabbedPanel, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)).addContainerGap()));

		settingsTabbedPanel.getAccessibleContext().setAccessibleName("Google account");

		pack();
	}

	private void okButtonMouseClicked(MouseEvent event) {
		try {
			configuration.setUser(usernameText.getText());
			configuration.setPasswd(new String(passwordText.getPassword()));
			configuration.setUrl(urlText.getText());

			configuration.setHttpProxyHost(httpProxyHostText.getText());
			configuration.setHttpProxyPort(httpProxyPortText.getText());
			configuration.setHttpsProxyHost(httpsProxyHostText.getText());
			configuration.setHttpsProxyPort(httpsProxyPortText.getText());

			configuration.setSleepTime(sleepTimeText.getText());
			configuration.setIdleTime(idleTimeText.getText());
			configuration.setEventName(eventNameText.getText());
			configuration.setLastIdleTimeThreshold(lastIdleTimeThresholdText.getText());

			configuration.setWorkIpRegExp(workIpRegExpText.getText());
			configuration.setNormalIconLocation(iconNormalText.getText());
			configuration.setInactiveIconLocation(iconInactiveText.getText());

			configuration.writeConfiguration("passwd.properties");
		} catch (FileNotFoundException ex) {
			log.error("Configuration property file not found", ex);
		} catch (IOException ex) {
			log.error("Problem with writting to configuration property file", ex);
		}
		setVisible(false);
	}

	private void cancelButtonMouseClicked(MouseEvent event) {
		setTextsFromConfiguration();
		setVisible(false);
	}

	private void handleFileSelection(JTextField textField) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "gif", "png");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			String absolutePath = selectedFile.getAbsolutePath();
			log.debug("absolutePath: {}", absolutePath);
			textField.setText(absolutePath);
		}
	}

	private void iconNormalMouseClicked(MouseEvent event) {
		handleFileSelection(iconNormalText);
	}

	private void iconInactiveMouseClicked(MouseEvent event) {
		handleFileSelection(iconInactiveText);
	}
}
