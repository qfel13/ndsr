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

import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import ndsr.Configuration;
import ndsr.trash.SettingsFrame;

/**
 *
 * @author lkufel
 */
public class TabbedSettingsFrame extends JFrame {

	private static final long serialVersionUID = -2075547569254525343L;
	
	private Configuration configuration;
	
	private JButton cancelButton;
    private JPanel connectionPanel;
    private JLabel eventNameLabel;
    private JTextField eventNameText;
    private JPanel eventsPanel;
    private JPanel googleAccountPanel;
    private JLabel httpProxyHostLabel;
    private JTextField httpProxyHostText;
    private JLabel httpProxyPortLabel;
    private JTextField httpProxyPortText;
    private JLabel httpsProxyHostLabel;
    private JTextField httpsProxyHostText;
    private JLabel httpsProxyPortLabel;
    private JTextField httpsProxyPortText;
    private JLabel idleTimeLabel;
    private JTextField idleTimeText;
    private JTabbedPane jTabbedPane1;
    private JLabel lastIdleTimeThresholdLabel;
    private JTextField lastIdleTimeThresholdText;
    private JButton okButton;
    private JPanel otherPanel;
    private JLabel passwordLabel;
    private JPasswordField passwordText;
    private JLabel sleepTimeLabel;
    private JTextField sleepTimeText;
    private JLabel urlLabel;
    private JTextField urlText;
    private JLabel usernameLabel;
    private JTextField usernameText;
    private JLabel workIpRegExpLabel;
    private JTextField workIpRegExpText;

    /** Creates new form TabbedSettingsFrame */
    public TabbedSettingsFrame() {
        initComponents();
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
	}

	public TabbedSettingsFrame(Configuration configuration) {

		this.configuration = configuration;
        initComponents();
        setTextsFromConfiguration();
	}

    private void initComponents() {

        jTabbedPane1 = new JTabbedPane();
        googleAccountPanel = new JPanel();
        urlText = new JTextField();
        passwordLabel = new JLabel();
        usernameLabel = new JLabel();
        usernameText = new JTextField();
        passwordText = new JPasswordField();
        urlLabel = new JLabel();
        eventsPanel = new JPanel();
        lastIdleTimeThresholdText = new JTextField();
        lastIdleTimeThresholdLabel = new JLabel();
        eventNameText = new JTextField();
        eventNameLabel = new JLabel();
        idleTimeLabel = new JLabel();
        idleTimeText = new JTextField();
        sleepTimeLabel = new JLabel();
        sleepTimeText = new JTextField();
        connectionPanel = new JPanel();
        httpProxyPortText = new JTextField();
        httpProxyHostText = new JTextField();
        httpsProxyHostText = new JTextField();
        httpsProxyPortLabel = new JLabel();
        httpsProxyPortText = new JTextField();
        httpProxyHostLabel = new JLabel();
        httpProxyPortLabel = new JLabel();
        httpsProxyHostLabel = new JLabel();
        otherPanel = new JPanel();
        workIpRegExpLabel = new JLabel();
        workIpRegExpText = new JTextField();
        cancelButton = new JButton();
        okButton = new JButton();

        setTitle("Settings");
        setResizable(false);

        passwordLabel.setText("password");

        usernameLabel.setText("username");

        urlLabel.setText("calendar url");

        GroupLayout googleAccountPanelLayout = new GroupLayout(googleAccountPanel);
        googleAccountPanel.setLayout(googleAccountPanelLayout);
        googleAccountPanelLayout.setHorizontalGroup(
            googleAccountPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(googleAccountPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(googleAccountPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(usernameLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                    .addComponent(passwordLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                    .addComponent(urlLabel, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(googleAccountPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(urlText, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .addComponent(passwordText, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .addComponent(usernameText, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                .addContainerGap())
        );
        googleAccountPanelLayout.setVerticalGroup(
            googleAccountPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(googleAccountPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(googleAccountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameText))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(googleAccountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(googleAccountPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(urlLabel)
                    .addComponent(urlText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
        );

        jTabbedPane1.addTab("General", googleAccountPanel);
        lastIdleTimeThresholdLabel.setText("new event after idle (in minutes)");
        eventNameLabel.setText("event name");
        idleTimeLabel.setText("idleTime (in minutes)");
        sleepTimeLabel.setText("sleepTime (in minutes)");

        GroupLayout eventsPanelLayout = new GroupLayout(eventsPanel);
        eventsPanel.setLayout(eventsPanelLayout);
        eventsPanelLayout.setHorizontalGroup(
            eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(eventsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(sleepTimeLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(idleTimeLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(lastIdleTimeThresholdLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(eventNameLabel, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(sleepTimeText, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(idleTimeText, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(lastIdleTimeThresholdText, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(eventNameText, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addContainerGap())
        );
        eventsPanelLayout.setVerticalGroup(
            eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(eventsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE, false)
                    .addComponent(sleepTimeLabel)
                    .addComponent(sleepTimeText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(idleTimeText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(idleTimeLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(eventNameLabel)
                    .addComponent(eventNameText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(eventsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lastIdleTimeThresholdText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastIdleTimeThresholdLabel))
                .addGap(27, 27, 27))
        );

        jTabbedPane1.addTab("Events", eventsPanel);
        httpsProxyPortLabel.setText("https proxy port");
        httpProxyHostLabel.setText("http proxy host");
        httpProxyPortLabel.setText("http proxy port");
        httpsProxyHostLabel.setText("https proxy host");

        GroupLayout connectionPanelLayout = new GroupLayout(connectionPanel);
        connectionPanel.setLayout(connectionPanelLayout);
        connectionPanelLayout.setHorizontalGroup(
            connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(connectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(httpsProxyPortLabel, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(httpsProxyHostLabel, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(httpProxyPortLabel, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(httpProxyHostLabel, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(httpsProxyHostText, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(httpProxyPortText, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(httpsProxyPortText, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(httpProxyHostText, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                .addContainerGap())
        );
        connectionPanelLayout.setVerticalGroup(
            connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(connectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(httpProxyHostLabel)
                    .addComponent(httpProxyHostText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(httpProxyPortLabel)
                    .addComponent(httpProxyPortText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(httpsProxyHostLabel)
                    .addComponent(httpsProxyHostText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connectionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(httpsProxyPortLabel)
                    .addComponent(httpsProxyPortText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Connection", connectionPanel);
        workIpRegExpLabel.setText("work IP regular expression");

        GroupLayout otherPanelLayout = new GroupLayout(otherPanel);
        otherPanel.setLayout(otherPanelLayout);
        otherPanelLayout.setHorizontalGroup(
            otherPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, otherPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(workIpRegExpLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(workIpRegExpText, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        otherPanelLayout.setVerticalGroup(
            otherPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(otherPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(otherPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(workIpRegExpLabel)
                    .addComponent(workIpRegExpText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(105, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Other", otherPanel);

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
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("General");

        pack();
    }

	private void okButtonMouseClicked(MouseEvent evt) {
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

            configuration.writeConfiguration("passwd.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SettingsFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SettingsFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(false);
	}

	private void cancelButtonMouseClicked(MouseEvent evt) {
		setTextsFromConfiguration();
		setVisible(false);
	}
}
