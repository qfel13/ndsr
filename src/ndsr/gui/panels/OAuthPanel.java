package ndsr.gui.panels;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ndsr.calendar.CalendarHelper;
import ndsr.gui.TabbedSettingsFrame;
import ndsr.gui.WelcomeFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthPanel extends CardChildPanel {
	private static final String AUTHORIZATION_CODE_NOT_CORRECT = "Authorization code is not correct. " +
			"Cannot exchange code for OAuth tokens.";
	private static final String SOCKET_TIMEOUT = "Cannot connect to authorization server. Check your connection settings.";
	private static final long serialVersionUID = 5699146507497562643L;
	private static final Logger LOG = LoggerFactory.getLogger(OAuthPanel.class);
	private JTextField tokenField;
	private JButton browseButton;
	private JLabel urlLabel;
	private JButton nextButton;
	private JButton exitButton;
	private JButton settingsButton;
	private JLabel instructionsLabel;
	private final WelcomeFrame welcomeFrame;
	private final CalendarHelper calendarHelper;
	private final TabbedSettingsFrame settings;
	private JLabel warningLabel;

	public OAuthPanel(WelcomeFrame w, CalendarHelper c, TabbedSettingsFrame s) {
		welcomeFrame = w;
		calendarHelper = c;
		settings = s;
		settings.enableOnlyConnectionTab();

		setSize(new Dimension(450, 300));

		exitButton = new JButton("Exit");
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LOG.debug("Exiting without google accunt authorization");
				System.exit(0);
			}
		});
		exitButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					LOG.debug("Exiting without google accunt authorization");
					System.exit(0);
				}
			}
		});

		settingsButton = new JButton("Settings");
		settingsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showConnectionSettings();
			}
		});
		settingsButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					showConnectionSettings();
				}
			}
		});
		
		nextButton = new JButton("Next");
		nextButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				goNext();
			}
		});
		nextButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					goNext();
				}
			}
		});
		nextButton.setEnabled(false);

		instructionsLabel = new JLabel("<html><ol>" + "<li>If you are using proxy click Settings and configure it (if not skip this step)</li>"
				+ "<li>Go to url shown below by clicking browse</li>"
				+ "<li>Login with your google account (if you are not logged in yet)</li>" + "<li>Click 'Allow access' button </li>"
				+ "<li>Copy shown code and paste it to text field below</li>" + "<li>Click next</li>" + "</ol></html>");

		final String authorizationUrl = calendarHelper != null ? calendarHelper.getAuthorizationUrl() : "";

		browseButton = new JButton("Browse");
		browseButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					browse(authorizationUrl);
				}
			}
		});
		browseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				browse(authorizationUrl);
			}
		});

		urlLabel = new JLabel();
		int length = authorizationUrl.length();
		urlLabel.setText(length > 75 ? authorizationUrl.substring(0, 75) + " ..." : authorizationUrl);

		tokenField = new JTextField();
		tokenField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				check();
			}

			public void removeUpdate(DocumentEvent e) {
				check();
			}

			public void insertUpdate(DocumentEvent e) {
				check();
			}

			public void check() {
				if (!tokenField.getText().isEmpty()) {
					nextButton.setEnabled(true);
				} else {
					nextButton.setEnabled(false);
				}
				warningLabel.setText("");
			}
		});

		tokenField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					LOG.debug("keyPressed");
					goNext();
				}
			}
		});

		warningLabel = new JLabel("");
		warningLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		warningLabel.setForeground(Color.RED);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(warningLabel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(instructionsLabel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(urlLabel, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(browseButton))
						.addComponent(tokenField, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(exitButton)
							.addPreferredGap(ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
							.addComponent(settingsButton)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(nextButton)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(instructionsLabel, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(browseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(urlLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tokenField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(warningLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(nextButton)
						.addComponent(exitButton)
						.addComponent(settingsButton))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

	private void goNext() {
		changeNextButtonTextAndDisable();
		warningLabel.setText("");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					calendarHelper.initCalendarService(tokenField.getText());
					LOG.info("Code was good calendar service is initialized");
					settings.enableAllTabs();
					welcomeFrame.showNextPanel();
				} catch (SocketTimeoutException e) {
					LOG.info(SOCKET_TIMEOUT);
					warningLabel.setText(SOCKET_TIMEOUT);
					settingsButton.requestFocusInWindow();
				} catch (IOException e) {
					LOG.info(AUTHORIZATION_CODE_NOT_CORRECT);
					warningLabel.setText(AUTHORIZATION_CODE_NOT_CORRECT);
					
				}
				restoreNextButtonTextAndEnable();
			}
		}).start();
	}
	
	private void changeNextButtonTextAndDisable() {
		nextButton.setText("Wait...");
		nextButton.setEnabled(false);
	}
	
	private void restoreNextButtonTextAndEnable() {
		nextButton.setText("Next");
		nextButton.setEnabled(true);
		nextButton.requestFocusInWindow();
	}
	
	private void showConnectionSettings() {
		settings.showFrame();
	}

	@Override
	public void refresh() {
		LOG.debug("nothing to do");
	}

	private void browse(final String authorizationUrl) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				// Point or redirect your user to the authorizationUrl.
				desktop.browse(new URI(authorizationUrl));
				tokenField.requestFocusInWindow();
			} catch (IOException ex) {
				// FIXME:
			} catch (URISyntaxException ex) {
				// FIXME:
			}
		} else {
			// TODO
		}
	}
}
