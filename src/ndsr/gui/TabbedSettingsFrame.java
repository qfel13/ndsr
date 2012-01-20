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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import ndsr.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private JLabel usernameLabel = new JLabel("Username"); // FIXME
	private JLabel passwordLabel = new JLabel("Password"); // FIXME
	private JLabel urlLabel = new JLabel("Calendar URL");  // FIXME
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
	private JLabel inactiveTimeStartLabel = new JLabel("Inactive time start");
	private JLabel inactiveTimeEndLabel = new JLabel("Inactive time end");
	// Fields
	private JTextField sleepTimeText = new JTextField();
	private JTextField idleTimeText = new JTextField();
	private JTextField eventNameText = new JTextField();
	private JTextField lastIdleTimeThresholdText = new JTextField();
	private JTextField minutesBeforeFirstText = new JTextField();
	private JTextField eventMinutesAheadText = new JTextField();
	private JTextField inactiveTimeStartText = new JTextField();
	private JTextField inactiveTimeEndText = new JTextField();

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
	// Checkboxes
	JCheckBox runNdsrAtStartUpChkbox = new JCheckBox("Run Ndsr at system startup (current user only)");
	
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
		initRunNdsrAtStartUpChkbox();
	}

	private void initRunNdsrAtStartUpChkbox() {
		try {
			runNdsrAtStartUpChkbox.setEnabled(System.getProperty("os.name").toLowerCase().contains("windows"));
			// run ndsr at startup, now supported only on windows
			if(System.getProperty("os.name").toLowerCase().contains("windows")) {
				
				// get application path
				String ndsrExecPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");
				if (ndsrExecPath.startsWith("/")) {
					ndsrExecPath = ndsrExecPath.replaceFirst("/", "");
				}
				String ndsrDirPath = (String) ndsrExecPath.subSequence(0,ndsrExecPath.lastIndexOf("/"));
				// check if vbs scripts exist
				File getStartupDirVBS = new File(ndsrDirPath + "/scripts/getWindowsUserStartUpDirectoryPath.vbs");
				File addLnkVBS = new File(ndsrDirPath + "/scripts/addShortcutToStartUpWindows.vbs");
				if (getStartupDirVBS.exists() && addLnkVBS.exists()) {
					log.debug("Executing {}/scripts/getWindowsUserStartUpDirectoryPath.vbs script", ndsrDirPath);
					// get user startup path using vbscript
					String[] cmd = new String[] {"wscript.exe", ndsrDirPath+"/scripts/getWindowsUserStartUpDirectoryPath.vbs"};
					Runtime.getRuntime().exec(cmd);
					// read startup path from file
					FileInputStream fstream = new FileInputStream(System.getProperty("java.io.tmpdir") + "/" + "ndsrStartUpPath.txt");
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String startupPath = br.readLine();
					log.debug("Got StartUp dir path: {}", startupPath);
					String ndsrShortcutName = "ndsr.lnk";
					// check if ndsr.lnk exist in startup
					File ndsrLnk = new File(startupPath + "\\" + ndsrShortcutName);
					log.debug("Shortcut link ({}) exists: {}",ndsrLnk.getAbsolutePath(), ndsrLnk.exists());
					runNdsrAtStartUpChkbox.setSelected(ndsrLnk.exists());
				} else {
					log.error("vbs scripts does not exist in scripts directory, disabling 'run ndsr at system start up' feature.");
					runNdsrAtStartUpChkbox.setEnabled(false);
				}
			}
		} catch(IOException ex) {
			log.error("Failed to initialize run ndsr at system startup checkbox state.", ex);
		}
	}

	private void setTextsFromConfiguration() {
		
		httpProxyHostText.setText(configuration.getHttpProxyHost());
		httpProxyPortText.setText(configuration.getHttpProxyPort());
		httpsProxyHostText.setText(configuration.getHttpsProxyHost());
		httpsProxyPortText.setText(configuration.getHttpsProxyPort());

		sleepTimeText.setText("" + configuration.getSleepTime());
		idleTimeText.setText("" + configuration.getIdleTime());
		eventNameText.setText(configuration.getEventName());
		lastIdleTimeThresholdText.setText("" + configuration.getLastIdleTimeThreshold());
		minutesBeforeFirstText.setText("" + configuration.getMinutesBeforeFirstEvent());
		eventMinutesAheadText.setText("" + configuration.getEventMinutesAhead());
		inactiveTimeStartText.setText(configuration.getInactiveTimeStart());
		inactiveTimeEndText.setText(configuration.getInactiveTimeEnd());
		

		workIpRegExpText.setText(configuration.getWorkIpRegExp());
		iconNormalText.setText(configuration.getNormalIconLocation());
		iconInactiveText.setText(configuration.getInactiveIconLocation());
	}

	private void initComponents() {
		// JFrame settings
		this.setTitle("Settings");
		this.setMinimumSize(new Dimension(400, 225));

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
		gl_eventsPanel.setHorizontalGroup(
			gl_eventsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_eventsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(inactiveTimeEndLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(inactiveTimeStartLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(eventMinutesAheadLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(minutesBeforeFirstLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(idleTimeLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(eventNameLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(sleepTimeLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(lastIdleTimeThresholdLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(inactiveTimeStartText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(eventMinutesAheadText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(minutesBeforeFirstText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(sleepTimeText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(idleTimeText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(eventNameText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(lastIdleTimeThresholdText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(inactiveTimeEndText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_eventsPanel.setVerticalGroup(
			gl_eventsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_eventsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.BASELINE, false)
						.addComponent(sleepTimeLabel)
						.addComponent(sleepTimeText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(idleTimeText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(idleTimeLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(eventNameLabel)
						.addComponent(eventNameText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lastIdleTimeThresholdText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lastIdleTimeThresholdLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(minutesBeforeFirstLabel)
						.addComponent(minutesBeforeFirstText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(eventMinutesAheadLabel)
						.addComponent(eventMinutesAheadText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(inactiveTimeStartText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(inactiveTimeStartLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_eventsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(inactiveTimeEndLabel)
						.addComponent(inactiveTimeEndText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(43))
		);
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
		gl_otherPanel.setHorizontalGroup(
			gl_otherPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_otherPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_otherPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_otherPanel.createSequentialGroup()
							.addGap(6)
							.addComponent(workIpRegExpLabel, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(workIpRegExpText, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
							.addGap(11))
						.addGroup(gl_otherPanel.createSequentialGroup()
							.addComponent(runNdsrAtStartUpChkbox)
							.addContainerGap(241, Short.MAX_VALUE))))
		);
		gl_otherPanel.setVerticalGroup(
			gl_otherPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_otherPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_otherPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(workIpRegExpText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(workIpRegExpLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(runNdsrAtStartUpChkbox)
					.addContainerGap(129, Short.MAX_VALUE))
		);
		otherPanel.setLayout(gl_otherPanel);

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
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(settingsTabbedPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
					.addContainerGap(395, Short.MAX_VALUE)
					.addComponent(cancelButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(okButton)
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(settingsTabbedPanel, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addGap(5))
		);
		getContentPane().setLayout(layout);

		settingsTabbedPanel.getAccessibleContext().setAccessibleName("Google account");

		pack();
	}

	private void okButtonMouseClicked(MouseEvent event) {
		try {
			configuration.setHttpProxyHost(httpProxyHostText.getText());
			configuration.setHttpProxyPort(httpProxyPortText.getText());
			configuration.setHttpsProxyHost(httpsProxyHostText.getText());
			configuration.setHttpsProxyPort(httpsProxyPortText.getText());

			configuration.setSleepTime(sleepTimeText.getText());
			configuration.setIdleTime(idleTimeText.getText());
			configuration.setEventName(eventNameText.getText());
			configuration.setLastIdleTimeThreshold(lastIdleTimeThresholdText.getText());
			configuration.setMinutesBeforeFirstEvent(minutesBeforeFirstText.getText());
			configuration.setEventMinutesAhead(eventMinutesAheadText.getText());
			try {
				configuration.setInactiveTimeStart(inactiveTimeStartText.getText());
				configuration.setInactiveTimeEnd(inactiveTimeEndText.getText());
			} catch (IllegalArgumentException e) {
				log.debug("zly format czasu", e);
			}

			configuration.setWorkIpRegExp(workIpRegExpText.getText());
			configuration.setNormalIconLocation(iconNormalText.getText());
			configuration.setInactiveIconLocation(iconInactiveText.getText());

			configuration.writeConfiguration();
			
			checkStartupLink();
		} catch (FileNotFoundException ex) {
			log.error("Configuration property file not found", ex);
		} catch (IOException ex) {
			log.error("Problem with writting to configuration property file", ex);
		}
		setVisible(false);
	}

	private void checkStartupLink() throws IOException, FileNotFoundException {
		// run ndsr at startup, now supported only on windows
		if(System.getProperty("os.name").toLowerCase().contains("windows") && runNdsrAtStartUpChkbox.isEnabled()) {
			// get application path
			String ndsrExecPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");
			if (ndsrExecPath.startsWith("/")) {
				ndsrExecPath = ndsrExecPath.replaceFirst("/", "");
			}
			String ndsrDirPath = (String) ndsrExecPath.subSequence(0,ndsrExecPath.lastIndexOf("/"));
			// ndsr starter filename needs to be hardcoded 
//				String ndsrExecFilename = (String) ndsrExecPath.subSequence(ndsrExecPath.lastIndexOf("/")+1, ndsrExecPath.length());
			log.debug("Executing {}/scripts/getWindowsUserStartUpDirectoryPath.vbs script", ndsrDirPath);
			// get user startup path using vbscript
			String[] cmd1 = new String[] {"wscript.exe", ndsrDirPath+"/scripts/getWindowsUserStartUpDirectoryPath.vbs"};
			Runtime.getRuntime().exec(cmd1);
			// read startup path from file
			FileInputStream fstream = new FileInputStream(System.getProperty("java.io.tmpdir") + "/" + "ndsrStartUpPath.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String startupPath = br.readLine();
			String ndsrShortcutName = "ndsr.lnk";
			File ndsrLnk = new File(startupPath + "\\" + ndsrShortcutName);
			if (runNdsrAtStartUpChkbox.isEnabled() && runNdsrAtStartUpChkbox.isSelected()) {
				// create startup lnk
				if (!ndsrLnk.exists()) {
					// ndsr.exe is hardcoded since we are running the jar not the exe starter so we cannot get exe file name  
					String[] cmd2 = new String[] {"wscript.exe", ndsrDirPath+"/scripts/addShortcutToStartUpWindows.vbs", ndsrDirPath, "ndsr.exe", ndsrShortcutName};
					log.debug("Executing {}/scripts/addShortcutToStartUpWindows.vbs script", ndsrDirPath);
					Runtime.getRuntime().exec(cmd2);
				} else {
					log.debug("Startup lnk file already exists, skipping  creation.");
				}
			} else if(runNdsrAtStartUpChkbox.isEnabled()) {
				// remove lnk, only if it exists
				if (ndsrLnk.exists()) {
					log.debug("Removing ndsr shortcut from startup dir: {}", ndsrLnk.getAbsolutePath());
					ndsrLnk.delete();
				} else {
					log.debug("Startup lnk file ({}) does not exist,nothing to delete.", ndsrLnk.getAbsolutePath());
				}
			}
		}
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

	public void showFrame() {
		initRunNdsrAtStartUpChkbox();
		
		setVisible(true);
	}
}
