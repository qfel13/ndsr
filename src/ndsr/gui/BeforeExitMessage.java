package ndsr.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeforeExitMessage extends JDialog {
	private static final long serialVersionUID = 3856080904385293475L;

	private static final Logger LOG = LoggerFactory.getLogger(BeforeExitMessage.class);

	public BeforeExitMessage(String message) {
		this("Error", message);
	}
	
	public BeforeExitMessage(String title, String message) {
		final JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);

		setTitle(title);
		setContentPane(optionPane);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {

				LOG.info("exit");
				System.exit(1);
			}
		});
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				if (isVisible() && (e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					setVisible(false);
					LOG.info("exit");
					System.exit(1);
				}
			}
		});
		
		pack();

		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		// Move the window
		setLocation(x, y);

		setVisible(true);
	}
}
