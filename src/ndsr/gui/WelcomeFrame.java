package ndsr.gui;

import java.awt.BorderLayout;

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
	
	final Main main;

	public WelcomeFrame(Main m, CalendarHelper calendarHelper) {
		main = m;
		
		LOG.debug("WelcomeFrame init start");
		setTitle("Welcome screen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		oauthPanel = new OAuthPanel(this, calendarHelper);
		calendarChoosePanel = new CalendarChoosePanel(this, calendarHelper);
		readyToUsePanel = new ReadyToUsePanel(this);
		
		cardPanel = new CardPanel(this);
		
		cardPanel.addPanel(oauthPanel);
		cardPanel.addPanel(calendarChoosePanel);
		cardPanel.addPanel(readyToUsePanel);

		getContentPane().add(cardPanel, BorderLayout.CENTER);

		pack();

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
