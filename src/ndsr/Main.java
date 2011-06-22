package ndsr;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.UIManager;

import ndsr.beans.Stats;
import ndsr.chart.StatisticsFrame;
import ndsr.gui.TabbedSettingsFrame;
import ndsr.idle.IdleTime;
import ndsr.idle.LinuxIdleTime;
import ndsr.idle.WindowsIdleTime;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * @author lkufel
 */
public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private TrayIcon trayIcon = null;
	private PopupMenu popup = null;
	private MenuItem statisticsItem = null;
	private MenuItem settingsItem = null;
//	private MenuItem caritasItem = null;
	private MenuItem exitItem = null;
	private Image image;
	private Image grayImage;
	private boolean grayIcon = false;
	private Stats stats;
	private TabbedSettingsFrame settingsFrame;
	private CalendarHandler calendarHandler;
	private IdleTime idleTime;
	private static String os = "";

	public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException {
		PropertyConfigurator.configure("log4j.properties");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error("Error setting native LAF: ", e);
		}

		new Main().run();
	}

	public void run() throws InterruptedException, FileNotFoundException, IOException {
		Configuration configuration = new Configuration();

		os = System.getProperty("os.name").toLowerCase();

		initTrayIcon();
		initIdleTime();

		File f = new File("passwd.properties");
		File fDevelopment = new File("C:\\Program Files\\ndsr\\passwd.properties");
		File fDevelopment2 = new File("/home/adro/ndsr/passwd.properties");
		if (f.exists()) {
			configuration.readConfiguration(f);
		} else if (fDevelopment.exists()) {
			configuration.readConfiguration(fDevelopment);
		} else if (fDevelopment2.exists()) {
			configuration.readConfiguration(fDevelopment2);
		} else {
			trayIcon.displayMessage("No configuration found", "No configuration found", TrayIcon.MessageType.ERROR);
		}

		calendarHandler = new CalendarHandler(configuration);
		settingsFrame = new TabbedSettingsFrame(configuration);

		String statsStr = null;
		Boolean running = true;
		int lastIdleSec = 0;

		do {
			try {
				int idleTimeInSec = configuration.getIdleTimeInSec();
				int idleSec = idleTime.getIdleTime();
				if (idleSec < idleTimeInSec) {
					log.debug("NOT IDLE. idleSec = {}, idleTimeInSec = {}", idleSec, idleTimeInSec);

					String workIpRegExp = configuration.getWorkIpRegExp();

					if (workIpRegExp == null || workIpRegExp == "" || isIpFromWork(workIpRegExp)) {
						log.debug("At Work");
						try {
							int lastIdleTimeThreshold = configuration.getLastIdleTimeThresholdInSec();
							log.debug("lastIdleSec = {} lastIdleTimeThreshold = {}", lastIdleSec, lastIdleTimeThreshold);
							if (lastIdleSec > lastIdleTimeThreshold) {
								log.debug("CREATE NEW EVENT: {}", calendarHandler.createNewEvent());
							} else {
								log.debug("CREATE OR UPDATE: {}", calendarHandler.createOrUpdate());
							}

							stats = calendarHandler.getStats();
							statsStr = stats.toString();
						} catch (IOException ex) {
							statsStr = "io exception";
							log.error(statsStr, ex);
						} catch (AuthenticationException ex) {
							statsStr = "Authentication Exception";
							log.error(statsStr, ex);
							calendarHandler.authenticate(configuration);
						} catch (ServiceException ex) {
							statsStr = "service exception";
							log.error(statsStr, ex);
						} catch (Exception ex) {
							statsStr = "exception";
							log.error(statsStr, ex);
						}

						if (statsStr != null) {
							trayIcon.setToolTip(statsStr);
						}
						if (grayIcon) {
							trayIcon.setImage(image);
							grayIcon = false;
						}
					} else {
						log.debug("Not at work");
						if (!grayIcon) {
							if (statsStr == null) {
								trayIcon.setToolTip("Not at work");
							}
							trayIcon.setImage(grayImage);
							grayIcon = true;
						}
					}
				} else {
					log.debug("IDLE: System is idle for more then {} min", idleTimeInSec / 60);
				}
				lastIdleSec = idleSec;
				long sleepTime = configuration.getSleepTimeInMili();
				log.debug("Sleep for {} min == {} millis", configuration.getSleepTime(), sleepTime);
				Thread.sleep(sleepTime);
			} catch (Throwable t) {
				log.error("Throwable ", t);
			}
		} while (running);
	}

	private void initTrayIcon() throws RuntimeException {
		if (SystemTray.isSupported()) {
			log.info("System Tray is supported");
			SystemTray tray = SystemTray.getSystemTray();
			String iconPath = "icon" + FILE_SEPARATOR;
			String grayIconPath = "icon" + FILE_SEPARATOR;
			// TODO: add configuration, allow user to choose appropriate file
			if (os.equals("linux")) {
				iconPath += "no_linux.png";
				grayIconPath += "no_gray_linux.png";
			} else {
				iconPath += "no.png";
				grayIconPath += "no_gray.png";
			}
			log.debug("iconPath = {}", iconPath);

			File iconFile = new File(iconPath);
			File grayIconFile = new File(grayIconPath);
			log.debug("Checking tray icon file ...");
			if (iconFile.exists() && grayIconFile.exists()) {
				log.debug("Tray icon file found");
				image = Toolkit.getDefaultToolkit().getImage(iconPath);
				grayImage = Toolkit.getDefaultToolkit().getImage(grayIconPath);
			} else {
				log.error("Tray icon file NOT found. Try to work without tray.");
				log.info("Trying to work without tray.");
				return;
			}
			log.debug("Creating popup manu");
			popup = new PopupMenu();

			log.debug("Creating statistics menu item");
			// DETAILS
			ActionListener statisticsListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showStatistics();
				}
			};
			statisticsItem = new MenuItem("Statistics");
			statisticsItem.addActionListener(statisticsListener);

			log.debug("Adding statistics menu item");
			popup.add(statisticsItem);

			log.debug("Creating settings menu item");
			ActionListener settingsListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showSettings();
				}
			};
			settingsItem = new MenuItem("Settings");
			settingsItem.addActionListener(settingsListener);
			log.debug("Adding settings menu item");
			popup.add(settingsItem);

			/*log.debug("Creating caritas menu item");
			ActionListener caritasListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showCaritasDialog();
				}
			};
			caritasItem = new MenuItem("Caritas");
			caritasItem.addActionListener(caritasListener);
			log.debug("Adding caritas menu item");
			popup.add(caritasItem);*/

			log.debug("Creating exit menu item");
			ActionListener exitListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					log.debug("Exiting...");
					System.exit(0);
				}
			};
			exitItem = new MenuItem("Exit");
			exitItem.addActionListener(exitListener);
			log.debug("Adding exit menu item");
			popup.add(exitItem);

			log.debug("Creating Tray icon");
			trayIcon = new TrayIcon(image, "Initializing ...", popup);
			trayIcon.setImageAutoSize(true);
			try {
				log.debug("Adding Tray icon to Tray");
				tray.add(trayIcon);
			} catch (AWTException e) {
				log.error("TrayIcon could not be added. " + e.getMessage());
				log.info("Trying to work without tray.");
				return;
			}
		} else {
			log.error("System Tray is NOT supported");
			log.info("Trying to work without tray.");
			return;
		}
	}

//	private void showCaritasDialog() {
//		JOptionPane.showMessageDialog(null, "Nie bądź kurwa Caritasem!", "Nie bądź kurwa Caritasem!",
//				JOptionPane.INFORMATION_MESSAGE,
//				new ImageIcon(Toolkit.getDefaultToolkit().getImage("icon" + FILE_SEPARATOR + "caritas.png")));
//	}

	private void showStatistics() {
		StatisticsFrame statisticsFrame = new StatisticsFrame(stats);
		log.debug("close Operation = {}", statisticsFrame.getDefaultCloseOperation());
		statisticsFrame.setVisible(true);
	}
	
	

	private void showSettings() {
		log.debug("Settings");
		settingsFrame.setVisible(true);
	}

	private boolean isIpFromWork(String workIpRegExp) throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		while (interfaces.hasMoreElements()) {
			NetworkInterface nif = interfaces.nextElement();
			List<InterfaceAddress> interfaceAddresses = nif.getInterfaceAddresses();
			for (InterfaceAddress interfaceAddress : interfaceAddresses) {
				String ip = interfaceAddress.getAddress().getHostAddress();
				log.debug("ip = {} workIpRegExp = {}", ip, workIpRegExp);
				if (ip.matches(workIpRegExp)) {
					log.debug("{} matches to {}", ip, workIpRegExp);
					return true;
				}
			}
		}
		return false;
	}

	private void initIdleTime() {
		if (os.equals("linux")) {
			idleTime = new LinuxIdleTime();
		} else if (os.startsWith("windows")) {
			idleTime = new WindowsIdleTime();
		} else {
			log.error("Unsupported operating system: {}", os);
			System.exit(1);
		}
	}

	public static String getOs() {
		return os;
	}
}
