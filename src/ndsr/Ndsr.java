package ndsr;

import java.awt.Desktop;
import java.awt.Desktop.Action;
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
import ndsr.calendar.CalendarHelper;
import ndsr.gui.AboutFrame;
import ndsr.gui.OutOfWorkFrame;
import ndsr.gui.StatisticsFrame;
import ndsr.gui.TabbedSettingsFrame;
import ndsr.gui.tray.NdsrTrayIcon;
import ndsr.idle.IdleTime;
import ndsr.idle.LinuxIdleTime;
import ndsr.idle.WindowsIdleTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lkufel
 */
public class Ndsr {

	private static final Logger LOG = LoggerFactory.getLogger(Ndsr.class);

	private Stats stats;
	private TabbedSettingsFrame settingsFrame;
	private StatisticsFrame statisticsFrame;
	private OutOfWorkFrame outOfWorkFrame;
	private CalendarHelper calendarHelper;
	private AboutFrame aboutFrame;
	private Configuration configuration;
	private NdsrTrayIcon ndsrTrayIcon;
	private IdleTime idleTime;
	private boolean work = true;
	private static String os = ""; // FIXME: remove this

	private Boolean running = false;

	private void init() {
		os = System.getProperty("os.name").toLowerCase();

		ndsrTrayIcon = new NdsrTrayIcon(this, configuration);
		initIdleTime();

		statisticsFrame = new StatisticsFrame(stats);
		outOfWorkFrame = new OutOfWorkFrame(this);
		aboutFrame = new AboutFrame();
	}

	public void run(Configuration configuration, CalendarHelper calendarHelper, TabbedSettingsFrame settings) {
		this.configuration = configuration;
		this.calendarHelper = calendarHelper;
		this.settingsFrame = settings;
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
					LOG.debug("NOT IDLE. idleSec = {}, idleTimeInSec = {}", idleSec, idleTimeInSec);

					if (isAtWork()) {
						LOG.debug("At Work");
						try {
							int lastIdleTimeThreshold = configuration.getLastIdleTimeThresholdInSec();
							LOG.debug("lastIdleSec = {} lastIdleTimeThreshold = {}", lastIdleSec, lastIdleTimeThreshold);
							if (lastIdleSec > lastIdleTimeThreshold) {
								LOG.debug("CREATE NEW EVENT: {}", calendarHelper.createNewEvent());
							} else {
								LOG.debug("CREATE OR UPDATE: {}", calendarHelper.createOrUpdate());
							}

							stats = calendarHelper.getStats();
							if (stats != null) {
								statsStr = stats.toString();
							}
						} catch (IOException ex) {
							statsStr = "io exception";
							LOG.error(statsStr, ex);
						} catch (Exception ex) {
							statsStr = "exception";
							LOG.error(statsStr, ex);
						}

						if (statsStr != null) {
							ndsrTrayIcon.setToolTip(statsStr);
						}
						ndsrTrayIcon.useNormalIcon();
					} else {
						LOG.debug("Not at work");
						if (statsStr == null) {
							ndsrTrayIcon.setToolTip("Not at work");
						}
						ndsrTrayIcon.useGrayIcon();
					}
				} else {
					LOG.debug("IDLE: System is idle for more then {} min", idleTimeInSec / 60);
				}
				lastIdleSec = idleSec;
				long sleepTime = configuration.getSleepTimeInMili();
				LOG.debug("Sleep for {} min == {} millis", configuration.getSleepTime(), sleepTime);
				Thread.sleep(sleepTime);
			} catch (Throwable t) {
				LOG.error("Throwable ", t);
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

	

	

	public void setWork(boolean newWork) {
		work = newWork;
	}

	private boolean isIpFromWork() throws SocketException {
		String workIpRegExp = configuration.getWorkIpRegExp();

		if (workIpRegExp == null || workIpRegExp.isEmpty()) {
			return true;
		}

		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

		if (interfaces == null) {
			LOG.debug("interfaces == null");
			return false;
		}

		while (interfaces.hasMoreElements()) {
			NetworkInterface nif = interfaces.nextElement();
			if (nif != null) {
				List<InterfaceAddress> interfaceAddresses = nif.getInterfaceAddresses();
				LOG.debug("nif.toString() = {}", nif.toString());
				byte[] mac = nif.getHardwareAddress();
				if (mac != null) {
					StringBuilder macBuilder = new StringBuilder(18);
					for (byte b : mac) {
						if (macBuilder.length() > 0)
							macBuilder.append(':');
						macBuilder.append(String.format("%02x", b));
					}
					macBuilder.toString();
					LOG.debug("nif.getHardwareAddress() = {}", macBuilder.toString());
				}
				Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
				LOG.debug("inetAddresses.hasMoreElements = {}", inetAddresses.hasMoreElements());

				LOG.debug("nif.getMTU() = {}", nif.getMTU());
				LOG.debug("nif.isLoopback() = {}", nif.isLoopback());
				LOG.debug("nif.isPointToPoint() = {}", nif.isPointToPoint());
				LOG.debug("nif.isUp() = {}", nif.isUp());
				LOG.debug("nif.isVirtual() = {}", nif.isVirtual());
				LOG.debug("nif.supportsMulticast() = {}", nif.supportsMulticast());

				if (interfaceAddresses == null) {
					LOG.debug("interfaceAddresses == null");
					return false;
				}

				for (InterfaceAddress interfaceAddress : interfaceAddresses) {
					if (interfaceAddress == null) {
						LOG.debug("interfaceAddress == null");
						continue;
					}
					InetAddress inetAddress = interfaceAddress.getAddress();
					if (inetAddress == null) {
						LOG.debug("inetAddress == null");
						continue;
					}
					String ip = inetAddress.getHostAddress();
					LOG.debug("ip = {} workIpRegExp = {}", ip, workIpRegExp);
					if (ip != null && ip.matches(workIpRegExp)) {
						LOG.debug("{} matches to {}", ip, workIpRegExp);
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
			LOG.error("Unsupported operating system: {}", os);
			System.exit(1);
		}
	}

//	public static String getOs() {
//		return os;
//	}

	public void showSettings() {
		LOG.debug("showSettings");
		settingsFrame.showFrame();
	}
	
	public void showOutOfWork() {
		LOG.debug("showOutOfWork");
		outOfWorkFrame.showWindow();
		work = false;
	}

	public void showCalendar() {
		LOG.debug("showCalendar");
		if (Desktop.isDesktopSupported()) {
			Desktop d = Desktop.getDesktop();
			if (d.isSupported(Action.BROWSE)) {
				try {
					d.browse(new URI("https://www.google.com/calendar/render"));
				} catch (IOException e) {
					LOG.error("d.browse failed", e);
				} catch (URISyntaxException e) {
					LOG.error("URI syntax", e);
				}
			}
		}
	}
	
	public void showStatistics() {
		LOG.debug("showStatistics");
		statisticsFrame.refreshStats(stats);
		statisticsFrame.setVisible(true);
	}

	public void showLogs() {
		String jar = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File dir = new File(jar.substring(0, jar.lastIndexOf("/")).replaceAll("%20", " ").concat("/logs"));
		LOG.debug("jar = {}, dir = {}", jar, dir);
		if (Desktop.isDesktopSupported()) {
			Desktop d = Desktop.getDesktop();
			if (d.isSupported(Action.OPEN)) {
				try {
					if (dir.exists()) {
						d.open(dir);
					} else {
						JOptionPane.showMessageDialog(null, "Logs dir does not exist!\n" + dir.toString(),
								"Logs dir does not exist!", JOptionPane.ERROR_MESSAGE);
						LOG.error("Logs dir does not exist");
					}
				} catch (IOException e1) {
					LOG.error("d.open failed", e1);
				}
			}
		}
	}

	public void showAbout() {
		aboutFrame.setVisible(true);
	}
}

