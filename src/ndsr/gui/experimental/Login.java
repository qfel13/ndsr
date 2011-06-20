package ndsr.gui.experimental;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 *
 * @author lkufel
 */
public class Login extends JPanel {
	private static final long serialVersionUID = -4111414154254147350L;
	private Desktop desktop;
	private URI createURI;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JButton loginButton;
	private JLabel loginLabel;
	private JTextField loginText;
	private JLabel passwordLabel;
	private JPasswordField passwordText;
	private JLabel registerLink;
	private JCheckBox rememberMe;
		
	/** Creates new form Login */
	public Login() {
		initComponents();
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.BROWSE)) {
			} else {
				try {
					createURI = new URI("https://www.google.com/accounts/NewAccount");
				} catch (URISyntaxException ex) {
					// TODO:
				}
			}
		}
	}

	private void initComponents() {

		loginLabel = new JLabel();
		jLabel2 = new JLabel();
		loginButton = new JButton();
		rememberMe = new JCheckBox();
		loginText = new JTextField();
		passwordLabel = new JLabel();
		jLabel1 = new JLabel();
		passwordText = new JPasswordField();
		registerLink = new JLabel();

		loginLabel.setText("Login");

		jLabel2.setText("Don't have Google account?");

		loginButton.setText("Login");

		rememberMe.setText("Remember Me");
		rememberMe.setEnabled(false);

		passwordLabel.setText("Password");

		jLabel1.setText("Use Google account credentials to log in");

		registerLink.setText("<html><a href=\"\">Register now</a></html>");
		registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				registerLinkMouseClicked(evt);
			}
		});

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGap(0, 233, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addComponent(rememberMe)
						.addGap(18, 63, Short.MAX_VALUE)
						.addComponent(loginButton))
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(passwordLabel)
							.addComponent(loginLabel))
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(passwordText, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
							.addComponent(loginText, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)))
					.addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jLabel2)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(registerLink, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGap(0, 137, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jLabel1)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(loginLabel)
					.addComponent(loginText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(passwordLabel)
					.addComponent(passwordText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE, false)
					.addComponent(loginButton)
					.addComponent(rememberMe))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jLabel2)
					.addComponent(registerLink, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap())
		);
	}

	private void registerLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registerLinkMouseClicked
		try {
			desktop.browse(createURI);
		} catch (IOException ex) {
			// TODO:
		}
	}



}
