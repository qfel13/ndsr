package ndsr.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
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

import ndsr.CalendarHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthPanel extends CardChildPanel {
	private static final String AUTHORIZATION_CODE_NOT_CORRECT = "Authorization code is not correct. Cannot exchange code for OAuth tokens.";
	private static final long serialVersionUID = 5699146507497562643L;
	private static final Logger LOG = LoggerFactory.getLogger(OAuthPanel.class);
	private JTextField tokenField;
	private JButton browseButton;
	private JLabel urlLabel;
	private JButton nextButton;
	private JButton exitButton;
	private JLabel instructionsLabel;
	private final WelcomeFrame welcomeFrame;
	private final CalendarHelper calendarHelper;
	private JLabel warningLabel;

	public OAuthPanel(WelcomeFrame w, CalendarHelper c) {
		welcomeFrame = w;
		calendarHelper = c;

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

		instructionsLabel = new JLabel("<html><ol>" + "<li>Go to url shown below by clicking browse</li>"
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
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
						groupLayout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										groupLayout
												.createParallelGroup(Alignment.LEADING)
												.addComponent(warningLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430,
														Short.MAX_VALUE)
												.addComponent(instructionsLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430,
														Short.MAX_VALUE)
												.addGroup(
														Alignment.TRAILING,
														groupLayout.createSequentialGroup()
																.addComponent(urlLabel, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
																.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(browseButton))
												.addComponent(tokenField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430,
														Short.MAX_VALUE)
												.addGroup(
														Alignment.TRAILING,
														groupLayout.createSequentialGroup().addComponent(exitButton)
																.addPreferredGap(ComponentPlacement.RELATED, 324, Short.MAX_VALUE)
																.addComponent(nextButton))).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(instructionsLabel, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(browseButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(urlLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tokenField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(warningLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addGap(18)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(nextButton).addComponent(exitButton))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		setLayout(groupLayout);
	}

	protected void goNext() {
		boolean success = calendarHelper.initCalendarService(tokenField.getText());
		if (!success) {
			LOG.info(AUTHORIZATION_CODE_NOT_CORRECT);
			warningLabel.setText(AUTHORIZATION_CODE_NOT_CORRECT);
		} else {
			LOG.info("Code was good calendar service is initialized");
			welcomeFrame.showNextPanel();
		}
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
