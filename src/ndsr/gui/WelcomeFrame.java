package ndsr.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import ndsr.CalendarHelper;
import ndsr.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WelcomeFrame extends JFrame {

	private static final Logger LOG = LoggerFactory.getLogger(WelcomeFrame.class);
	private static final long serialVersionUID = 6408504844845430094L;

	private CardPanel cardPanel;
	private OAuthPanel oauthPanel;
	private CalendarChoosePanel calendarChoosePanel;
	private ReadyToUsePanel readyToUsePanel;
	
	private final Main main;
	private final TabbedSettingsFrame settings;

	public WelcomeFrame(Main m, CalendarHelper calendarHelper, TabbedSettingsFrame s) {
		main = m;
		settings = s;
		
		LOG.debug("WelcomeFrame init start");
		setTitle("Welcome screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		oauthPanel = new OAuthPanel(this, calendarHelper, settings);
		calendarChoosePanel = new CalendarChoosePanel(this, calendarHelper);
		readyToUsePanel = new ReadyToUsePanel(this, settings);
		
		cardPanel = new CardPanel(this);
		
		cardPanel.addPanel(oauthPanel);
		cardPanel.addPanel(calendarChoosePanel);
		cardPanel.addPanel(readyToUsePanel);

		getContentPane().add(cardPanel, BorderLayout.CENTER);

		pack();

		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		 
		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		 
		// Move the window
		setLocation(x, y);
		
		// SwingUtilities.invokeLater(arg0);
		LOG.debug("WelcomeFrame init end");
	}
	
	public void showNextPanel() {
		LOG.debug("showNextPanel");
		cardPanel.next();
	}
	
	public void showPreviousPanel() {
		cardPanel.previous();
	}

	public void runNdsr() {
		setVisible(false);
		main.createNdsrAndRun();
	}
}
