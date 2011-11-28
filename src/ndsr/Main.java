package ndsr;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Desktop.Action;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileLock;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
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
public class Main implements MouseListener {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	private TrayIcon trayIcon = null;
	private PopupMenu popup = null;
	private MenuItem statisticsItem = null;
	private MenuItem settingsItem = null;
	private MenuItem calendarItem = null;
	private MenuItem exitItem = null;
	private Image image;
	private Image grayImage;
	private boolean grayIcon = false;
	private Stats stats;
	private TabbedSettingsFrame settingsFrame;
	private StatisticsFrame statisticsFrame;
	private CalendarHandler calendarHandler;
	private IdleTime idleTime;
	private MenuItem logsItem;
	private static String os = "";
	private static final String ndsrInstanceLockFileName = System.getProperty("java.io.tmpdir") + "ndsr.lck";

	public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException {
		PropertyConfigurator.configure("log4j.properties");
		// detect if ndsr is already running.
		if (!lockInstance(ndsrInstanceLockFileName)) {
			log.info("Duplicate ndsr instance, exitting. (Could not lock file {})", ndsrInstanceLockFileName);
			return;
		} else {
			log.info("Starting program instance.");
		}

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

		File f = new File("passwd.properties");
		File fDevelopment = new File("C:\\Program Files\\ndsr\\passwd.properties");
		File fDevelopment2 = new File("/home/adro/ndsr/passwd.properties");
		boolean noConfiguration = false;
		if (f.exists()) {
			configuration.readConfiguration(f);
		} else if (fDevelopment.exists()) {
			configuration.readConfiguration(fDevelopment);
		} else if (fDevelopment2.exists()) {
			configuration.readConfiguration(fDevelopment2);
		} else {
			noConfiguration = true;
		}

		initTrayIcon(configuration);
		initIdleTime();

		if (noConfiguration) {
			trayIcon.displayMessage("No configuration found", "No configuration found", TrayIcon.MessageType.ERROR);
		}

		calendarHandler = new CalendarHandler(configuration);
		settingsFrame = new TabbedSettingsFrame(configuration);
		
		statisticsFrame = new StatisticsFrame(stats);

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
					if (workIpRegExp == null || workIpRegExp.isEmpty() || isIpFromWork(workIpRegExp)) {
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
			popup = new PopupMenu();

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
			popup.add(statisticsItem);

			popup.addSeparator();

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
			popup.add(calendarItem);

			popup.addSeparator();

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
			popup.add(logsItem);

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
			popup.add(settingsItem);

			// popup.addSeparator();
			//
			// Menu otherOptions = new Menu("More");
			//
			// MenuItem a = new MenuItem("a");
			// MenuItem b = new MenuItem("a");
			//
			// otherOptions.add(a);
			// otherOptions.add(b);
			//
			// popup.add(otherOptions);

			popup.addSeparator();

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
		settingsFrame.setVisible(true);
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

	private boolean isIpFromWork(String workIpRegExp) throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		if (interfaces == null) {
			return false;
		}

		while (interfaces.hasMoreElements()) {
			NetworkInterface nif = interfaces.nextElement();
			if (nif != null) {
				List<InterfaceAddress> interfaceAddresses = nif.getInterfaceAddresses();

				if (interfaceAddresses == null) {
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
	
	private static boolean lockInstance(final String lockFile) {
	    try {
	        final File file = new File(lockFile);
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (Exception e) {
	                        log.error("Unable to remove lock file: " + lockFile, e);
	                    }
	                }
	            });
	            return true;
	        }
	    } catch (Exception e) {
	        log.error("Unable to create and/or lock file: " + lockFile, e);
	    }
	    return false;
	}

}
