package ndsr.gui.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import ndsr.Configuration;
import ndsr.Ndsr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NdsrTrayIcon implements MouseListener {
	private static final Logger LOG = LoggerFactory.getLogger(NdsrTrayIcon.class);

	private Ndsr ndsr;
	private Configuration configuration;

	private TrayIcon trayIcon = null;
	private PopupMenu trayPopupMenu = null;

	// POPUP MENU ITEMS
	private MenuItem statisticsItem = null;
	private MenuItem calendarItem = null;
	private MenuItem outOfWorkItem = null;
	private MenuItem logsItem;
	private MenuItem settingsItem = null;
	private MenuItem helpItem = null;
	private MenuItem aboutItem = null;
	private MenuItem exitItem = null;
	// END OF POPUP MENU ITEMS

	// IMAGES FOR TRAY ICON
	private Image image;
	private Image grayImage;
	private boolean gray = false;
	private String version;

	public NdsrTrayIcon(Ndsr n, Configuration c, String v) throws RuntimeException {
		ndsr = n;
		configuration = c;
		version = v;
		
		if (SystemTray.isSupported()) {
			LOG.info("System Tray is supported");
			SystemTray tray = SystemTray.getSystemTray();
	
			loadIcons();
			trayPopupMenu = buildPopupMenu();
	
			LOG.debug("Creating Tray icon");
			trayIcon = new TrayIcon(image, "Initializing ...", trayPopupMenu);
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(this);
			try {
				LOG.debug("Adding Tray icon to Tray");
				tray.add(trayIcon);
			} catch (AWTException e) {
				LOG.error("TrayIcon could not be added. " + e.getMessage());
				LOG.info("Trying to work without tray.");
				return;
			}
		} else {
			LOG.error("System Tray is NOT supported");
			LOG.info("Trying to work without tray.");
			// TODO: throw exception and show message and exit
			return;
		}
	}

	public void useGrayIcon() {
		if (!gray) {
			trayIcon.setImage(grayImage);
			gray = true;
		}
	}

	public void useNormalIcon() {
		if (gray) {
			trayIcon.setImage(image);
			gray = false;
		}
	}

	public void loadIcons() {
		String iconPath = configuration.getNormalIconLocation();
		String grayIconPath = configuration.getInactiveIconLocation();

		LOG.debug("iconPath = {}, grayIconPath = {}", iconPath, grayIconPath);

		File iconFile = new File(iconPath);
		File grayIconFile = new File(grayIconPath);
		LOG.debug("Checking tray icon file ...");
		if (iconFile.exists() && grayIconFile.exists()) {
			LOG.debug("Tray icon file found");
			image = Toolkit.getDefaultToolkit().getImage(iconPath);
			grayImage = Toolkit.getDefaultToolkit().getImage(grayIconPath);
		} else {
			LOG.error("Tray icon file NOT found. Try to work without tray.");
			LOG.info("Trying to work without tray.");
			return;
		}
	}

	public void reloadIcons() {
		loadIcons();
		if (gray) {
			trayIcon.setImage(grayImage);
		} else {
			trayIcon.setImage(image);
		}
	}

	public void setToolTip(String tooltip) {
		trayIcon.setToolTip(tooltip);
	}

	private PopupMenu buildPopupMenu() {
		LOG.debug("Creating popup manu");
		trayPopupMenu = new PopupMenu();

		createStatisticsItem();
		createCalendarItem();
		createOutOfWorkItem();
		createLogsItem();
		createSettingsItem();
		createHelpItem();
		createAboutItem();
		createExitItem();

		LOG.debug("Adding statistics menu item");
		trayPopupMenu.add(statisticsItem);
		LOG.debug("Adding calendar menu item");
		trayPopupMenu.add(calendarItem);

		trayPopupMenu.addSeparator();

		LOG.debug("Adding Go out of work menu item");
		trayPopupMenu.add(outOfWorkItem);

		trayPopupMenu.addSeparator();

		LOG.debug("Adding logs menu item");
		trayPopupMenu.add(logsItem);
		
		LOG.debug("Adding settings menu item");
		trayPopupMenu.add(settingsItem);

		trayPopupMenu.addSeparator();
		
		LOG.debug("Adding help menu item");
		trayPopupMenu.add(helpItem);

		LOG.debug("Adding about menu item");
		trayPopupMenu.add(aboutItem);
		
		trayPopupMenu.addSeparator();
		
		LOG.debug("Adding exit menu item");
		trayPopupMenu.add(exitItem);

		return trayPopupMenu;
	}

	private void createStatisticsItem() {
		LOG.debug("Creating statistics menu item");
		// STATISTICS
		ActionListener statisticsListener = new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				ndsr.showStatistics();
			}
		};
		statisticsItem = new MenuItem("Statistics");
		statisticsItem.addActionListener(statisticsListener);
	}

	private void createCalendarItem() {
		LOG.debug("Creating calendar menu item");
		// CALENDAR
		ActionListener calendarListener = new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				ndsr.showCalendar();
			}
		};
		calendarItem = new MenuItem("Go to Calendar");
		calendarItem.addActionListener(calendarListener);
	}

	private void createOutOfWorkItem() {
		// OUT OF WORK
		ActionListener outOfWorkListener = new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				ndsr.showOutOfWork();
			}
		};
	
		outOfWorkItem = new MenuItem("Go out of work");
		outOfWorkItem.addActionListener(outOfWorkListener);
	}

	private void createLogsItem() {
		LOG.debug("Creating logs menu item");
		// LOGS
		ActionListener logsListener = new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				ndsr.showLogs();
			}
		};
		logsItem = new MenuItem("Logs");
		logsItem.addActionListener(logsListener);
	}

	private void createSettingsItem() {
		// SETTING
		LOG.debug("Creating settings menu item");
		ActionListener settingsListener = new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				ndsr.showSettings();
			}
		};
		settingsItem = new MenuItem("Settings");
		settingsItem.addActionListener(settingsListener);
	}
	
	private void createHelpItem() {
		// HELP
		LOG.debug("Creating about menu item");
		ActionListener helpListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ndsr.showAbout();
			}
		};
		
		helpItem = new MenuItem("Help");
		helpItem.addActionListener(helpListener);
	}

	private void createAboutItem() {
		// ABOUT
		LOG.debug("Creating about menu item");
		ActionListener aboutListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ndsr.showAbout();
			}
		};
		
		String aboutLabel = "About";
		if (version != null) {
			aboutLabel += " v" + version;
		}
		aboutItem = new MenuItem(aboutLabel);
		aboutItem.addActionListener(aboutListener);
	}
	
	private void createExitItem() {
		// EXIT
		LOG.debug("Creating exit menu item");
		ActionListener exitListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LOG.debug("Exiting...");
				System.exit(0);
			}
		};
		exitItem = new MenuItem("Exit");
		exitItem.addActionListener(exitListener);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		int count = event.getClickCount();
		if (count == 2) {
			LOG.debug("opening stats after 2 click");
			ndsr.showStatistics();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
