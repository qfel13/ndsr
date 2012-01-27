package ndsr;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.UIManager;

import ndsr.calendar.CalendarHelper;
import ndsr.gui.BeforeExitMessage;
import ndsr.gui.TabbedSettingsFrame;
import ndsr.gui.WelcomeFrame;
import ndsr.utils.InstanceLocker;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of Ndsr application.
 * 
 * @author lkufel
 */
public class Main {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	private CalendarHelper calendarHelper;
	private Configuration configuration;
	private TabbedSettingsFrame settings;

	private WelcomeFrame welcomeFrame;
	private Ndsr ndsr;

	// command line flags
	private boolean systemLookAndFeel = true;
	private boolean development = false;
	private boolean forceInitialConfiguration = false;
	private boolean multipleInstances = false;

	public static void main(String[] args) {
		new Main().run(args);
	}

	/**
	 * Initialization method for Ndsr application.
	 * 
	 * @param args parameters from command line.
	 */
	public void run(String[] args) {
		setLog4jConfiguration();
		parseArguments(args);

		Enumeration<URL> resources;
		try {
			resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				URL nextElement = resources.nextElement();
				LOG.debug("URL = {}", nextElement);
				Manifest manifest = new Manifest(nextElement.openStream());
				// check that this is your manifest and do what you need or get the next one
				Attributes mainAttributes = manifest.getMainAttributes();
				if ("ndsr.Main".equals(mainAttributes.get(new Attributes.Name("Main-Class")))) {
					LOG.debug("{}", mainAttributes.get(new Attributes.Name("Implementation-Version")));
				}	
//				if ("ndsr.Main".equals(mainAttributes.get("Main-Class"))) {
//					LOG.debug("{}", mainAttributes.get(new Attributes.Name("Implementation-Version")));
//				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(1);
		configuration = new Configuration(development);
		calendarHelper = new CalendarHelper(configuration);

		if (systemLookAndFeel) {
			setDefaulfLookAndFeel();
		}

		settings = new TabbedSettingsFrame(configuration);

		if (!multipleInstances) {
			if (!InstanceLocker.lockInstance()) {
				LOG.error("Duplicate ndsr instance, exiting. (Could not lock file)");
				if (development) {
					System.exit(1);
				} else {
					new BeforeExitMessage("Duplicate ndsr instance, exiting. \n(Could not lock file)");
					return;
				}
			} else {
				LOG.info("Starting program instance.");
			}
		}

		if (forceInitialConfiguration || !configuration.isInitialConfiguraionDone()) {
			welcomeFrame = new WelcomeFrame(this, calendarHelper, settings);
			welcomeFrame.setVisible(true);
		} else {
			createNdsrAndRun();
		}
	}

	public void createNdsrAndRun() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ndsr = new Ndsr();
				ndsr.run(configuration, calendarHelper, settings);
			}
		}).start();
	}

	/**
	 * Sets system look and feel for java application.
	 */
	private void setDefaulfLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.debug("cannot set system look and feel");
		}
	}

	/**
	 * Used in development because log4j.properties is not listed in classpath.
	 */
	private void setLog4jConfiguration() {
		String filePath = "log4j.properties";
		File file = new File(filePath);
		if (file.exists()) {
			PropertyConfigurator.configure(filePath);
		}
	}

	/**
	 * Sets flags used during initialization.
	 */
	private void parseArguments(String[] args) {
		for (String arg : args) {
			if (arg.equals("--development") || arg.equals("-d")) {
				development = true;
			} else if (arg.equals("--java-look-and-feel") || arg.equals("-j")) {
				systemLookAndFeel = false;
			} else if (arg.equals("--initial-configuration") || arg.equals("-c")) {
				forceInitialConfiguration = true;
			} else if (arg.equals("--allow-multiple-instances") || arg.equals("-m")) {
				multipleInstances = true;
			}
		}
	}
}
