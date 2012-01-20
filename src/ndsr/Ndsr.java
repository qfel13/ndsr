package ndsr;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Image;
import java.awt.Menu;
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
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;

import ndsr.beans.Stats;
import ndsr.gui.OutOfWorkFrame;
import ndsr.gui.StatisticsFrame;
import ndsr.gui.TabbedSettingsFrame;
import ndsr.idle.IdleTime;
import ndsr.idle.LinuxIdleTime;
import ndsr.idle.WindowsIdleTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lkufel
 */
public class Ndsr implements MouseListener {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	private TrayIcon trayIcon = null;
	private PopupMenu trayPopupMenu = null;

	// POPUP MENU ITEMS
	private MenuItem statisticsItem = null;
	private MenuItem calendarItem = null;
	private MenuItem logsItem;
	private MenuItem settingsItem = null;
	private Menu moreItem = null;
	// OTHER OPTIONS MENU ITEMS
	private MenuItem outOfWorkItem = null;
	private MenuItem exitItem = null;
	// END OF POPUP MENU ITEMS

	// IMAGES FOR TRAY ICON
	private Image image;
	private Image grayImage;

	private boolean grayIcon = false;
	private Stats stats;
	private TabbedSettingsFrame settingsFrame;
	private StatisticsFrame statisticsFrame;
	private OutOfWorkFrame outOfWorkFrame;
	private CalendarHelper calendarHelper;
	private Configuration configuration;
	private IdleTime idleTime;
	private boolean work = true;
	private static String os = ""; // FIXME: remove this

	private Boolean running = false;

	private void init() {
		os = System.getProperty("os.name").toLowerCase();

		initTrayIcon(configuration);
		initIdleTime();

		settingsFrame = new TabbedSettingsFrame(configuration);
		statisticsFrame = new StatisticsFrame(stats);
		outOfWorkFrame = new OutOfWorkFrame(this);
	}

	public void run(Configuration configuration, CalendarHelper calendarHelper) {
		this.configuration = configuration;
		this.calendarHelper = calendarHelper;
		
		init();

		runMainLoop();
	}

	private void runMainLoop() {
		String statsStr = null;
		running  = true;
		int lastIdleSec = 0;

		do {
			try {
				int idleTimeInSec = configuration.getIdleTimeInSec();
				int idleSec = idleTime.getIdleTime();
				if (idleSec < idleTimeInSec) {
					log.debug("NOT IDLE. idleSec = {}, idleTimeInSec = {}", idleSec, idleTimeInSec);

					if (isAtWork()) {
						log.debug("At Work");
						try {
							int lastIdleTimeThreshold = configuration.getLastIdleTimeThresholdInSec();
							log.debug("lastIdleSec = {} lastIdleTimeThreshold = {}", lastIdleSec, lastIdleTimeThreshold);
							if (lastIdleSec > lastIdleTimeThreshold) {
								log.debug("CREATE NEW EVENT: {}", calendarHelper.createNewEvent());
							} else {
								log.debug("CREATE OR UPDATE: {}", calendarHelper.createOrUpdate());
							}

							stats = calendarHelper.getStats(); // FIXME
							statsStr = stats.toString();
						} catch (IOException ex) {
							statsStr = "io exception";
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
		} while (running );
	}

	private boolean isAtWork() throws SocketException {
		return (work && isIpFromWork());
	}

	/*private boolean isInactiveTime() {
		try {
			int inactiveTimeStartHour = configuration.getInactiveTimeStartHour();
			int inactiveTimeStartMinute = configuration.getInactiveTimeStartMinute();
			int inactiveTimeEndHour = configuration.getInactiveTimeEndHour();
			int inactiveTimeEndMinute = configuration.getInactiveTimeEndMinute();

			Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));
			Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));
			Calendar end = Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));

			start.set(Calendar.HOUR_OF_DAY, inactiveTimeStartHour);
			start.set(Calendar.MINUTE, inactiveTimeStartMinute);

			end.set(Calendar.HOUR_OF_DAY, inactiveTimeEndHour);
			end.set(Calendar.MINUTE, inactiveTimeEndMinute);

			if (end.before(start)) {
				log.debug("end before start - adding one day");
				end.add(Calendar.DATE, 1);
			}

			log.debug("now.after(start) = {}", now.after(start));
			log.debug("now.before(end) = {}", now.before(end));
			return (now.after(start) && now.before(end));
		} catch (IllegalStateException e) {
			return false;
		}
	}*/

	private void initTrayIcon(Configuration configuration) throws RuntimeException {
		if (SystemTray.isSupported()) {
			log.info("System Tray is supported");
			SystemTray tray = SystemTray.getSystemTray();

			String iconPath = configuration.getNormalIconLocation();
			String grayIconPath = configuration.getInactiveIconLocation();

			log.debug("iconPath = {}, grayIconPath = {}", iconPath, grayIconPath);

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
			trayPopupMenu = new PopupMenu();

			log.debug("Creating statistics menu item");
			// STATISTICS
			ActionListener statisticsListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showStatistics();
				}
			};
			statisticsItem = new MenuItem("Statistics");
			statisticsItem.addActionListener(statisticsListener);

			log.debug("Adding statistics menu item");
			trayPopupMenu.add(statisticsItem);

			log.debug("Creating calendar menu item");
			// CALENDAR
			ActionListener calendarListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showCalendar();
				}
			};
			calendarItem = new MenuItem("Go to Calendar");
			calendarItem.addActionListener(calendarListener);

			log.debug("Adding calendar menu item");
			trayPopupMenu.add(calendarItem);

			trayPopupMenu.addSeparator();

			// MORE
			moreItem = new Menu("More");

			log.debug("Creating logs menu item");
			// LOGS
			ActionListener logsListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showLogs();
				}
			};
			logsItem = new MenuItem("Logs");
			logsItem.addActionListener(logsListener);

			log.debug("Adding logs menu item");
			moreItem.add(logsItem);

			// SETTING
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
			moreItem.add(settingsItem);

			moreItem.addSeparator();

			// OUT OF WORK
			ActionListener outOfWorkListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showOutOfWork();
				}
			};

			outOfWorkItem = new MenuItem("Go out of work");
			outOfWorkItem.addActionListener(outOfWorkListener);
			log.debug("Adding Go out of work menu item");
			moreItem.add(outOfWorkItem);

			log.debug("Adding More menu item");
			trayPopupMenu.add(moreItem);

			trayPopupMenu.addSeparator();

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
			trayPopupMenu.add(exitItem);

			log.debug("Creating Tray icon");
			trayIcon = new TrayIcon(image, "Initializing ...", trayPopupMenu);
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(this);
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

	private void showStatistics() {
		log.debug("showStatistics");
		statisticsFrame.refreshStats(stats);
		statisticsFrame.setVisible(true);
	}

	private void showLogs() {
		String jar = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File dir = new File(jar.substring(0, jar.lastIndexOf("/")).replaceAll("%20", " ").concat("/logs"));
		log.debug("jar = {}, dir = {}", jar, dir);
		if (Desktop.isDesktopSupported()) {
			Desktop d = Desktop.getDesktop();
			if (d.isSupported(Action.OPEN)) {
				try {
					if (dir.exists()) {
						d.open(dir);
					} else {
						JOptionPane.showMessageDialog(null, "Logs dir does not exist!\n" + dir.toString(),
								"Logs dir does not exist!", JOptionPane.ERROR_MESSAGE);
						log.error("Logs dir does not exist");
					}
				} catch (IOException e1) {
					log.error("d.open failed", e1);
				}
			}
		}
	}

	private void showSettings() {
		log.debug("showSettings");
		settingsFrame.showFrame();
	}

	public void setWork(boolean newWork) {
		work = newWork;
	}

	private void showOutOfWork() {
		log.debug("showOutOfWork");
		outOfWorkFrame.showWindow();
		work = false;
	}

	private void showCalendar() {
		log.debug("showCalendar");
		if (Desktop.isDesktopSupported()) {
			Desktop d = Desktop.getDesktop();
			if (d.isSupported(Action.BROWSE)) {
				try {
					d.browse(new URI("https://www.google.com/calendar/render"));
				} catch (IOException e) {
					log.error("d.browse failed", e);
				} catch (URISyntaxException e) {
					log.error("URI syntax", e);
				}
			}
		}
	}

	private boolean isIpFromWork() throws SocketException {
		String workIpRegExp = configuration.getWorkIpRegExp();

		if (workIpRegExp == null || workIpRegExp.isEmpty()) {
			return true;
		}

		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		if (interfaces == null) {
			log.debug("interfaces == null");
			return false;
		}

		while (interfaces.hasMoreElements()) {
			NetworkInterface nif = interfaces.nextElement();
			if (nif != null) {
				List<InterfaceAddress> interfaceAddresses = nif.getInterfaceAddresses();
				log.debug("nif.toString() = {}", nif.toString());
				byte[] mac = nif.getHardwareAddress();
				if (mac != null) {
					StringBuilder macBuilder = new StringBuilder(18);
					for (byte b : mac) {
						if (macBuilder.length() > 0)
							macBuilder.append(':');
						macBuilder.append(String.format("%02x", b));
					}
					macBuilder.toString();
					log.debug("nif.getHardwareAddress() = {}", macBuilder.toString());
				}
				Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
				log.debug("inetAddresses.hasMoreElements = {}", inetAddresses.hasMoreElements());

				log.debug("nif.getMTU() = {}", nif.getMTU());
				log.debug("nif.isLoopback() = {}", nif.isLoopback());
				log.debug("nif.isPointToPoint() = {}", nif.isPointToPoint());
				log.debug("nif.isUp() = {}", nif.isUp());
				log.debug("nif.isVirtual() = {}", nif.isVirtual());
				log.debug("nif.supportsMulticast() = {}", nif.supportsMulticast());

				if (interfaceAddresses == null) {
					log.debug("interfaceAddresses == null");
					return false;
				}

				for (InterfaceAddress interfaceAddress : interfaceAddresses) {
					if (interfaceAddress == null) {
						log.debug("interfaceAddress == null");
						continue;
					}
					InetAddress inetAddress = interfaceAddress.getAddress();
					if (inetAddress == null) {
						log.debug("inetAddress == null");
						continue;
					}
					String ip = inetAddress.getHostAddress();
					log.debug("ip = {} workIpRegExp = {}", ip, workIpRegExp);
					if (ip != null && ip.matches(workIpRegExp)) {
						log.debug("{} matches to {}", ip, workIpRegExp);
						return true;
					}
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

	// TODO: move this mouse code to seperate listener
	@Override
	public void mouseClicked(MouseEvent event) {
		int count = event.getClickCount();
		log.debug("mouseClick {}", count);
		if (count == 2) {
			showStatistics();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}

