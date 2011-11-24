package ndsr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lkufel
 */
public class Configuration {
	private Properties properties;
	private File propertiesFile;

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	// General 
	private static final String USER = "user";
	private static final String PASSWD = "passwd";
	private static final String URL = "url";
	// Event
	private static final String SLEEP_TIME = "sleepTime";
	private static final String IDLE_TIME = "idleTime";
	private static final String EVENT_NAME = "eventName";
	private static final String LAST_IDLE_TIME_THRESHOLD = "lastIdleTimeThreshold";
	// Connection
	private static final String HTTPS_PROXY_HOST = "https.proxyHost";
	private static final String HTTPS_PROXY_PORT = "https.proxyPort";
	private static final String HTTP_PROXY_HOST = "http.proxyHost";
	private static final String HTTP_PROXY_PORT = "http.proxyPort";
	// Other
	private static final String WORK_IP_REG_EXP = "workIpRegExp";
	private static final String NORMAL_ICON_LOCATION = "normalIconLocation";
	private static final String INACTIVE_ICON_LOCATION = "inactiveIconLocation";

	// DEFAULT VALUES
	private static final int DEFAULT_IDLE_TIME = 10;
	private static final int DEFAULT_SLEEP_TIME = 5;
	private static final int DEFAULT_LAST_IDLE_TIME_THRESHOLD = 60;
	private static final String DEFAULT_NORMAL_WIN_ICON_LOCATION = "icon/no.png";
	private static final String DEFAULT_INACTIVE_WIN_ICON_LOCATION = "icon/no_gray.png";
	private static final String DEFAULT_NORMAL_LINUX_ICON_LOCATION = "icon/no_linux.png";
	private static final String DEFAULT_INACTIVE_LINUX_ICON_LOCATION = "icon/no_gray_linux.png";

	private final String os = System.getProperty("os.name").toLowerCase();
	
	public String getUser() {
		return properties.getProperty(USER);
	}

	public void setUser(String user) {
		properties.setProperty(USER, user);
	}

	public String getPasswd() {
		return properties.getProperty(PASSWD);
	}

	public void setPasswd(String passwd) {
		properties.setProperty(PASSWD, passwd);
	}

	public String getUrl() {
		return properties.getProperty(URL);
	}

	public void setUrl(String url) {
		properties.setProperty(URL, url);
	}

	public int getSleepTime() {
		return parseOrDefault(properties.getProperty(SLEEP_TIME), DEFAULT_SLEEP_TIME);
	}

	public long getSleepTimeInMili() {
		return parseOrDefault(properties.getProperty(SLEEP_TIME), DEFAULT_SLEEP_TIME) * 60 * 1000;
	}

	public void setSleepTime(int sleepTime) {
		properties.setProperty(SLEEP_TIME, "" + sleepTime);
	}

	public void setSleepTime(String sleepTimeStr) {
		properties.setProperty(SLEEP_TIME, "" + parseOrDefault(sleepTimeStr, DEFAULT_SLEEP_TIME));
	}

	public int getIdleTime() {
		return parseOrDefault(properties.getProperty(IDLE_TIME), DEFAULT_IDLE_TIME);
	}

	public int getIdleTimeInSec() {
		return getIdleTime() * 60;
	}

	public void setIdleTime(int idleTime) {
		properties.setProperty(IDLE_TIME, "" + idleTime);
	}

	public void setIdleTime(String idleTimeStr) {
		properties.setProperty(IDLE_TIME, "" + parseOrDefault(idleTimeStr, DEFAULT_IDLE_TIME));
	}

	public String getHttpProxyHost() {
		return properties.getProperty(HTTPS_PROXY_HOST);
	}

	public void setHttpProxyHost(String httpProxyHost) {
		System.setProperty(HTTP_PROXY_HOST, httpProxyHost);
		properties.setProperty(HTTP_PROXY_HOST, httpProxyHost);
	}

	public String getHttpProxyPort() {
		return properties.getProperty(HTTPS_PROXY_PORT);
	}

	public void setHttpProxyPort(String httpProxyPort) {
		System.setProperty(HTTP_PROXY_PORT, httpProxyPort);
		properties.setProperty(HTTP_PROXY_PORT, httpProxyPort);
	}

	public String getHttpsProxyHost() {
		return properties.getProperty(HTTPS_PROXY_HOST);
	}

	public void setHttpsProxyHost(String httpsProxyHost) {
		System.setProperty(HTTPS_PROXY_HOST, httpsProxyHost);
		properties.setProperty(HTTPS_PROXY_HOST, httpsProxyHost);
	}

	public String getHttpsProxyPort() {
		return properties.getProperty(HTTPS_PROXY_PORT);
	}

	public void setHttpsProxyPort(String httpsProxyPort) {
		System.setProperty(HTTPS_PROXY_PORT, httpsProxyPort);
		properties.setProperty(HTTPS_PROXY_PORT, httpsProxyPort);
	}

	public String getEventName() {
		String eventName = properties.getProperty(EVENT_NAME);
		if (eventName == null || eventName.isEmpty()) {
			eventName = "Praca";
		}
		return eventName;
	}

	public void setEventName(String eventName) {
		properties.setProperty(EVENT_NAME, eventName);
	}

	public String getWorkIpRegExp() {
		return properties.getProperty(WORK_IP_REG_EXP);
	}

	public void setWorkIpRegExp(String workIpRegExp) {
		properties.setProperty(WORK_IP_REG_EXP, workIpRegExp);
	}

	public int getLastIdleTimeThreshold() {
		// in minutes
		return parseOrDefault(properties.getProperty(LAST_IDLE_TIME_THRESHOLD), DEFAULT_LAST_IDLE_TIME_THRESHOLD);
	}

	public int getLastIdleTimeThresholdInSec() {
		return getLastIdleTimeThreshold() * 60;
	}

	public void setLastIdleTimeThreshold(String lastIdleTimeThreshold) {
		properties.setProperty(LAST_IDLE_TIME_THRESHOLD,
				"" + parseOrDefault(lastIdleTimeThreshold, DEFAULT_LAST_IDLE_TIME_THRESHOLD));
	}

	public String getNormalIconLocation() {
		String iconLocation = properties.getProperty(NORMAL_ICON_LOCATION);
		if (iconLocation == null || iconLocation.isEmpty()) {
			iconLocation = os.equals("linux") ? DEFAULT_NORMAL_LINUX_ICON_LOCATION
					: DEFAULT_NORMAL_WIN_ICON_LOCATION;
		}
		return iconLocation;
	}

	public void setNormalIconLocation(String normalIconLocation) {
		properties.setProperty(NORMAL_ICON_LOCATION, normalIconLocation);
	}
	
	public String getInactiveIconLocation() {
		String iconLocation = properties.getProperty(INACTIVE_ICON_LOCATION);
		if (iconLocation == null || iconLocation.isEmpty()) {
			iconLocation = os.equals("linux") ? DEFAULT_INACTIVE_LINUX_ICON_LOCATION
					: DEFAULT_INACTIVE_WIN_ICON_LOCATION;
		}
		return iconLocation;
	}

	public void setInactiveIconLocation(String inactiveIconLocation) {
		properties.setProperty(INACTIVE_ICON_LOCATION, inactiveIconLocation);
	}
	
	public void readConfiguration(String filename) throws FileNotFoundException, IOException {
		readConfiguration(new File(filename));
	}

	public void readConfiguration(File file) throws FileNotFoundException, IOException {
		propertiesFile = file;

		properties = new Properties();
		properties.load(new FileInputStream(file));

		setHttpProxyHost(properties.getProperty(HTTP_PROXY_HOST));
		setHttpProxyPort(properties.getProperty(HTTP_PROXY_PORT));
		setHttpsProxyHost(properties.getProperty(HTTPS_PROXY_HOST));
		setHttpsProxyPort(properties.getProperty(HTTPS_PROXY_PORT));
	}

	public void writeConfiguration(String filename) throws FileNotFoundException, IOException {
		log.debug("user = {}", this.getUser());

		FileWriter fileWriter = new FileWriter(propertiesFile);
		try {
			properties.store(fileWriter, "Please do NOT edit this file unless you know what you are doing.");
			fileWriter.flush();
		} finally {
			fileWriter.close();
		}
	}

	private int parseOrDefault(String value, int defaultValue) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
