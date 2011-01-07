package ndsr;

import com.google.gdata.util.ServiceException;
import java.awt.AWTException;
import java.awt.Color;
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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
//import org.apache.log4j.Logger;
import ndsr.idle.IdleTime;
import ndsr.idle.LinuxIdleTime;
import ndsr.idle.WindowsIdleTime;
import org.apache.log4j.PropertyConfigurator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lkufel
 */
public class Tray {

	private static final Logger log = LoggerFactory.getLogger(Tray.class);
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private TrayIcon trayIcon = null;
	private PopupMenu popup = null;
	private MenuItem detailsItem = null;
	private MenuItem settingsItem = null;
	private MenuItem caritasItem = null;
	private MenuItem exitItem = null;
	private Image image;
	private Stats stats;
	private SettingsFrame settingsFrame;
	private CalendarHandler calendarHandler;
	private IdleTime idleTime;
	private static String os = "";

	public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException {
		PropertyConfigurator.configure("log4j.properties");
		new Tray().run();
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
		settingsFrame = new SettingsFrame(configuration, calendarHandler);

		String statsStr;
		Boolean running = true;
		int lastIdleSec = 0;

		do {
			int idleTimeInSec = configuration.getIdleTimeInSec();
			int idleSec = idleTime.getIdleTime();
			if (idleSec < idleTimeInSec) {
				log.debug("NOT IDLE");

				String workIpRegExp = configuration.getWorkIpRegExp();

				if (workIpRegExp == null || isIpFromWork(workIpRegExp)) {
					log.debug("At Work");

					int lastIdleTimeThreshold = configuration.getLastIdleTimeThresholdInSec();
					log.debug("lastIdleSec = {} lastIdleTimeThreshold = {}", lastIdleSec, lastIdleTimeThreshold);
					if (lastIdleSec > lastIdleTimeThreshold) {
						log.debug("CREATE NEW EVENT: {}", calendarHandler.createNewEvent());
					} else {
						log.debug("CREATE OR UPDATE: {}", calendarHandler.createOrUpdate());
					}

					try {
						stats = calendarHandler.getStats();
						statsStr = stats.toString();
					} catch (IOException ex) {
						statsStr = "io exception" + ex.getMessage();
					} catch (ServiceException ex) {
						statsStr = "service exception" + ex.getMessage();
					}
					trayIcon.setToolTip(statsStr);
				} else {
					log.debug("Not at work");
					trayIcon.setToolTip("Not at work");
				}
			} else {
				log.debug("IDLE: System is idle for more then {} min", idleTimeInSec / 60);
			}
			lastIdleSec = idleSec;
			long sleepTime = configuration.getSleepTimeInMili();
			log.debug("Sleep for {} min == {} millis", configuration.getSleepTime(), sleepTime);
			Thread.sleep(sleepTime);
		} while (running);
	}

	private void initTrayIcon() throws RuntimeException {
		if (SystemTray.isSupported()) {
			log.info("System Tray is supported");
			SystemTray tray = SystemTray.getSystemTray();
			String iconPath = "icon" + FILE_SEPARATOR;
			if (os.equals("linux")) {
				iconPath += "no_linux.png";
			} else {
				iconPath += "no.png";
			}
			System.out.println("iconPath = " + iconPath);

			File iconFile = new File(iconPath);
			log.debug("Checking tray icon file ...");
			if (iconFile.exists()) {
				log.debug("Tray icon file found");
				image = Toolkit.getDefaultToolkit().getImage(iconPath);
			} else {
				log.error("Tray icon file NOT found. Try to work without tray.");
				log.info("Trying to work without tray.");
				return;
			}
			log.debug("Creating popup manu");
			popup = new PopupMenu();

			log.debug("Creating details menu item");
			// DETAILS
			ActionListener detailsListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showDetails();
				}
			};
			detailsItem = new MenuItem("Show details");
			detailsItem.addActionListener(detailsListener);
			
			log.debug("Adding details menu item");
			popup.add(detailsItem);

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

			log.debug("Creating caritas menu item");
			ActionListener caritasListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showCaritasDialog();
				}
			};
			caritasItem = new MenuItem("Caritas");
			caritasItem.addActionListener(caritasListener);
			log.debug("Adding caritas menu item");
			popup.add(caritasItem);

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

	private void showCaritasDialog() {
		JOptionPane.showMessageDialog(null, "Nie bądź kurwa Caritasem!",
				"Nie bądź kurwa Caritasem!", JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(Toolkit.getDefaultToolkit().getImage("icon" + FILE_SEPARATOR + "caritas.png")));
	}

	private void showDetails() {

		DefaultCategoryDataset data = new DefaultCategoryDataset();

		if (stats != null) {
			double todayHours = stats.getTodayHours() + stats.getTodayMinutes() / 60.0;
			double remainingTodayHours = stats.getRemainingTodayHours() + stats.getRemainingTodayMinutes() / 60.0;
			double weekHours = stats.getWeekHours() + stats.getWeekMinutes() / 60.0;
			double weekTodayHours = stats.getRemainingWeekHours() + stats.getRemainingWeekMinutes() / 60.0;

			data.addValue(todayHours, "Worked Hours", "Today");
			data.addValue(remainingTodayHours, "Remaining Hours", "Today");
			data.addValue(weekHours, "Worked Hours", "Week");
			data.addValue(weekTodayHours, "Remaining Hours", "Week");
		}

		JFreeChart localJFreeChart = ChartFactory.createStackedBarChart3D("Today and Week", null, "Hours", data, PlotOrientation.HORIZONTAL, true, true, false);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart.getPlot();

		localCategoryPlot.setNoDataMessage("Not initialized yet.");
		StackedBarRenderer3D renderer2 = (StackedBarRenderer3D) localCategoryPlot.getRenderer();
		renderer2.setSeriesPaint(0, Color.green);
		renderer2.setSeriesPaint(1, Color.yellow);
		renderer2.setSeriesPaint(2, Color.red);

		ChartFrame frame = new ChartFrame("Hours", localJFreeChart);
		frame.pack();
		log.debug("close Operation = {}", frame.getDefaultCloseOperation());
		frame.setVisible(true);
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
