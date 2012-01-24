package ndsr;

import java.io.File;

import javax.swing.UIManager;

import ndsr.gui.TabbedSettingsFrame;
import ndsr.gui.WelcomeFrame;

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

		configuration = new Configuration(development);
		calendarHelper = new CalendarHelper(configuration);

		if (systemLookAndFeel) {
			setDefaulfLookAndFeel();
		}
		
		settings = new TabbedSettingsFrame(configuration);

		if (!multipleInstances) {
			if (!InstanceLocker.lockInstance()) {
				LOG.error("Duplicate ndsr instance, exiting. (Could not lock file)");
				System.exit(1);
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
