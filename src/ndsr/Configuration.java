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
	private static final String DEFAULT_PROPERTIES_FILENAME = "passwd.properties";
	private Properties properties;
	private File propertiesFile;

	private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

	// General 
	private static final String USER = "user";
	private static final String PASSWD = "passwd";
	private static final String URL = "url";
	// Oauth
	private static final String INITIAL_CONFIGURATION_DONE = "oauthConfigured";
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String CALENDAR_ID = "calendarId";
	// Event
	private static final String SLEEP_TIME = "sleepTime";
	private static final String IDLE_TIME = "idleTime";
	private static final String EVENT_NAME = "eventName";
	private static final String VACATION_EVENT_PREFIX = "vacationEventName";
	private static final String PUB_HOLI_EVENT_PREFIX = "publicHolidayEventName";
	private static final String LAST_IDLE_TIME_THRESHOLD = "lastIdleTimeThreshold";
	private static final String MINUTES_BEFORE_FIRST_EVENT = "minutesBeforeFirst";
	private static final String EVENT_MINUTES_AHEAD = "eventMinutesAhead";
	private static final String INACTIVE_TIME_START_HOUR = "inactiveTimeStartHour";
	private static final String INACTIVE_TIME_START_MINUTE = "inactiveTimeStartMinute";
	private static final String INACTIVE_TIME_END_HOUR = "inactiveTimeEndHour";
	private static final String INACTIVE_TIME_END_MINUTE = "inactiveTimeEndMinute";
	// Connection
	private static final String HTTP_PROXY_HOST = "http.proxyHost";
	private static final String HTTP_PROXY_PORT = "http.proxyPort";
	private static final String HTTP_PROXY_USE_FOR_ALL = "httpProxtUseForAll";
	private static final String HTTPS_PROXY_HOST = "https.proxyHost";
	private static final String HTTPS_PROXY_PORT = "https.proxyPort";
	// Other
	private static final String WORK_IP_REG_EXP = "workIpRegExp";
	private static final String NORMAL_ICON_LOCATION = "normalIconLocation";
	private static final String INACTIVE_ICON_LOCATION = "inactiveIconLocation";
	private static final String HISTORY_START_DATE = "historyStartDate";
	// Working time
	private static final String DAILY_WORKING_TIME = "dailyWorkingTime";
	private static final String WEEKLY_WORKING_TIME = "weeklyWorkingTime";
	
	//
	private static final String COUNTER = "counter";

	// DEFAULT VALUES
	private static final int DEFAULT_IDLE_TIME = 10;
	private static final int DEFAULT_SLEEP_TIME = 5;
	private static final int DEFAULT_LAST_IDLE_TIME_THRESHOLD = 60;
	private static final int DEFAULT_MINUTES_BEFORE_FIRST_EVENT = 10;
	private static final int DEFAULT_EVENT_MINUTES_AHEAD = 5;
	private static final String DEFAULT_EVENT_NAME = "Work";
	private static final String DEFAULT_VACATION_EVENT_PRAFIX = "Vacation";
	private static final String DEFAULT_PUB_HOLI_EVENT_PREFIX = "Public holiday";
	private static final String DEFAULT_NORMAL_ICON_LOCATION = "";
	private static final String DEFAULT_INACTIVE_ICON_LOCATION = "";

	private static final String[] STANDARD_CONF_FILES = { Configuration.DEFAULT_PROPERTIES_FILENAME };
	private static final String[] DEVELOPMENT_CONF_FILES = {"C:\\Program Files\\ndsr\\passwd.properties",
			"/home/adro/ndsr/passwd.properties", "c:\\Program Files\\NdsrTest\\passwd.properties" };
	
	private boolean initilized = false;
	private static Configuration instance = null;
	
	public static Configuration init(boolean development) {
		if (instance == null) {
			instance = new Configuration(development);
		}
		return instance;
	}
	
	public static Configuration getInstance() {
		return instance;
	}
	
	private Configuration(boolean development) {
		properties = new Properties();
		
		if (development) {
			for (String conf : DEVELOPMENT_CONF_FILES) {
				File f = new File(conf);
				if (f.exists()) {
					try {
						readConfiguration(f);
					} catch (FileNotFoundException e) {
						continue;
					} catch (IOException e) {
						continue;
					}
					initilized = true;
					break;
				}
			}
		}
		for (String conf : STANDARD_CONF_FILES) {
			File f = new File(conf);
			if (f.exists()) {
				try {
					readConfiguration(f);
				} catch (FileNotFoundException e) {
					continue;
				} catch (IOException e) {
					continue;
				}
				initilized = true;
				break;
			}
		}
	}
	
	public boolean isInitialized() {
		return initilized;
	}
	
	public String getAccessToken() {
		return properties.getProperty(ACCESS_TOKEN);
	}

	public void setAccessToken(String accessToken) {
		properties.setProperty(ACCESS_TOKEN, accessToken);
	}

	public String getRefreshToken() {
		return properties.getProperty(REFRESH_TOKEN);
	}

	public void setRefreshToken(String refreshToken) {
		properties.setProperty(REFRESH_TOKEN, refreshToken);
	}

	public String getCalendarId() {
		return properties.getProperty(CALENDAR_ID);
	}

	public void setCalendarId(String calendarId) {
		properties.setProperty(CALENDAR_ID, calendarId);
	}

	public int getSleepTime() {
		return parseIntOrDefault(properties.getProperty(SLEEP_TIME), DEFAULT_SLEEP_TIME);
	}

	public long getSleepTimeInMili() {
		return parseIntOrDefault(properties.getProperty(SLEEP_TIME), DEFAULT_SLEEP_TIME) * 60 * 1000;
	}

	public void setSleepTime(int sleepTime) {
		properties.setProperty(SLEEP_TIME, "" + sleepTime);
	}

	public void setSleepTime(String sleepTimeStr) {
		properties.setProperty(SLEEP_TIME, "" + parseIntOrDefault(sleepTimeStr, DEFAULT_SLEEP_TIME));
	}

	public int getIdleTime() {
		return parseIntOrDefault(properties.getProperty(IDLE_TIME), DEFAULT_IDLE_TIME);
	}

	public int getIdleTimeInSec() {
		return getIdleTime() * 60;
	}

	public void setIdleTime(int idleTime) {
		properties.setProperty(IDLE_TIME, "" + idleTime);
	}

	public void setIdleTime(String idleTimeStr) {
		properties.setProperty(IDLE_TIME, "" + parseIntOrDefault(idleTimeStr, DEFAULT_IDLE_TIME));
	}

	public String getEventName() {
		return properties.getProperty(EVENT_NAME, DEFAULT_EVENT_NAME);
	}

	public void setEventName(String eventName) {
		properties.setProperty(EVENT_NAME, eventName);
	}
	
	public String getVacationEventPrefix() {
		return properties.getProperty(VACATION_EVENT_PREFIX, DEFAULT_VACATION_EVENT_PRAFIX);
	}

	public void setVacationEventPrefix(String eventName) {
		properties.setProperty(VACATION_EVENT_PREFIX, eventName);
	}
	
	public String getPubHolEventPrefix() {
		return properties.getProperty(PUB_HOLI_EVENT_PREFIX, DEFAULT_PUB_HOLI_EVENT_PREFIX);
	}

	public void setPubHolEventPrefix(String eventName) {
		properties.setProperty(PUB_HOLI_EVENT_PREFIX, eventName);
	}

	public int getLastIdleTimeThresholdInSec() {
		return getLastIdleTimeThreshold() * 60;
	}

	public void setLastIdleTimeThreshold(String lastIdleTimeThreshold) {
		properties.setProperty(LAST_IDLE_TIME_THRESHOLD, "" + parseIntOrDefault(lastIdleTimeThreshold, DEFAULT_LAST_IDLE_TIME_THRESHOLD));
	}
	
	public int getLastIdleTimeThreshold() {
		// in minutes
		return parseIntOrDefault(properties.getProperty(LAST_IDLE_TIME_THRESHOLD), DEFAULT_LAST_IDLE_TIME_THRESHOLD);
	}
	
	public int getMinutesBeforeFirstEvent() {
		return parseIntOrDefault(properties.getProperty(MINUTES_BEFORE_FIRST_EVENT), DEFAULT_MINUTES_BEFORE_FIRST_EVENT);
	}
	
	public void setMinutesBeforeFirstEvent(String minutesBeforeFirstEvent) {
		properties.setProperty(MINUTES_BEFORE_FIRST_EVENT, "" + parseIntOrDefault(minutesBeforeFirstEvent, 
				DEFAULT_MINUTES_BEFORE_FIRST_EVENT));
	}
	
	public int getEventMinutesAhead() {
		return parseIntOrDefault(properties.getProperty(EVENT_MINUTES_AHEAD), DEFAULT_EVENT_MINUTES_AHEAD);
	}
	
	public void setEventMinutesAhead(String eventMinutesAhead) {
		properties.setProperty(EVENT_MINUTES_AHEAD, "" + parseIntOrDefault(eventMinutesAhead, DEFAULT_EVENT_MINUTES_AHEAD));
	}
	
	public long parseTimeString(String time) {
		try {
			int hour = getHour(time, true);
			int min = getMinute(time);
			return (hour * 3600000) + (min * 60000); 
		} catch (Exception e) {
			LOG.error("Cannot parse time string ='" + time + "'. Exception is: ", e);
		}
		return 0;
	}
	
	private int getHour(String time, boolean allowMoreThenDay) {
		int colonIndex = time.indexOf(":");
		if (colonIndex == -1) {
			throw new IllegalArgumentException("Time should contain colon");
		}
		String hourStr = time.substring(0, colonIndex);
		try {
			int hour = Integer.valueOf(hourStr, 10);
			
			if (hour < 0 || (!allowMoreThenDay && hour > 23)) {
				throw new IllegalArgumentException("Hour should be between 0 and 23");
			}
			
			return hour;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Hour should be integer");
		}
	}
	
	private int getMinute(String time) {
		int colonIndex = time.indexOf(":");
		if (colonIndex == -1) {
			throw new IllegalArgumentException("Time should contain colon");
		}
		String minuteStr = time.substring(colonIndex + 1);
		try {
			Integer minute = Integer.valueOf(minuteStr, 10);
			
			if (minute < 0 || minute > 59) {
				throw new IllegalArgumentException("Minute should be between 0 and 59");
			}
			
			return minute;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Minute should be integer");
		}
	}
	
	public String getInactiveTimeStart() {
		try {
			return getInactiveTimeStartHour() + ":" + getInactiveTimeStartMinute();
		} catch (IllegalStateException e) {
			return "";
		}
	}
	
	public void setInactiveTimeStart(String inactiveTimeStart) {
		if (!inactiveTimeStart.isEmpty()) {
			int inactiveTimeStartHour = getHour(inactiveTimeStart, false);
			int inactiveTimeStartMinute = getMinute(inactiveTimeStart);
			
			LOG.debug("inactiveTimeStartHour = {}", inactiveTimeStartHour);
			LOG.debug("inactiveTimeStartMinute = {}", inactiveTimeStartMinute);
			
			properties.setProperty(INACTIVE_TIME_START_HOUR, "" + inactiveTimeStartHour);
			properties.setProperty(INACTIVE_TIME_START_MINUTE, "" + inactiveTimeStartMinute);
		} else {
			properties.setProperty(INACTIVE_TIME_START_HOUR, "");
			properties.setProperty(INACTIVE_TIME_START_MINUTE, "");
		}
	}
	
	public int getInactiveTimeStartHour() {
		return parseOrThrow(properties.getProperty(INACTIVE_TIME_START_HOUR));
	}
	
	public void setInactiveTimeStartHour(String inactiveTimeStartHour) {
		properties.setProperty(INACTIVE_TIME_START_HOUR, inactiveTimeStartHour);
	}
	
	public int getInactiveTimeStartMinute() {
		return parseOrThrow(properties.getProperty(INACTIVE_TIME_START_MINUTE));
	}
	
	public void setInactiveTimeStartMinute(String inactiveTimeStartHour) {
		properties.setProperty(INACTIVE_TIME_START_MINUTE, inactiveTimeStartHour);
	}
	
	public String getInactiveTimeEnd() {
		try {
			return getInactiveTimeEndHour() + ":" + getInactiveTimeEndMinute();
		} catch (IllegalStateException e) {
			return "";
		}
	}
	
	public void setInactiveTimeEnd(String inactiveTimeEnd) {
		if (!inactiveTimeEnd.isEmpty()) {
			int inactiveTimeEndHour = getHour(inactiveTimeEnd, false);
			int inactiveTimeEndMinute = getMinute(inactiveTimeEnd);
			
			LOG.debug("inactiveTimeEndHour = {}", inactiveTimeEndHour);
			LOG.debug("inactiveTimeEndMinute = {}", inactiveTimeEndMinute);
			
			properties.setProperty(INACTIVE_TIME_END_HOUR, "" + inactiveTimeEndHour);
			properties.setProperty(INACTIVE_TIME_END_MINUTE, "" + inactiveTimeEndMinute);
		} else {
			properties.setProperty(INACTIVE_TIME_END_HOUR, "");
			properties.setProperty(INACTIVE_TIME_END_MINUTE, "");
		}
	}

	public int getInactiveTimeEndHour() {
		return parseOrThrow(properties.getProperty(INACTIVE_TIME_END_HOUR));
	}
	
	public void setInactiveTimeEndHour(String inactiveTimeEndHour) {
		properties.setProperty(INACTIVE_TIME_END_HOUR, inactiveTimeEndHour);
	}
	
	public int getInactiveTimeEndMinute() {
		return parseOrThrow(properties.getProperty(INACTIVE_TIME_END_MINUTE));
	}
	
	public void setInactiveTimeEndMinute(String inactiveTimeEndHour) {
		properties.setProperty(INACTIVE_TIME_END_MINUTE, inactiveTimeEndHour);
	}

	public String getHttpProxyHost() {
		return properties.getProperty(HTTP_PROXY_HOST);
	}
	
	public void setHttpProxyHost(String httpProxyHost) {
		System.setProperty(HTTP_PROXY_HOST, httpProxyHost);
		properties.setProperty(HTTP_PROXY_HOST, httpProxyHost);
	}

	public int getHttpProxyPort() {
		return parseIntOrDefault(properties.getProperty(HTTP_PROXY_PORT), 0);
	}

	public void setHttpProxyPort(String httpProxyPort) {
		System.setProperty(HTTP_PROXY_PORT, httpProxyPort);
		properties.setProperty(HTTP_PROXY_PORT, httpProxyPort);
	}
	
	public boolean isHttpProxyUseForAll() {
		return Boolean.valueOf(properties.getProperty(HTTP_PROXY_USE_FOR_ALL));
	}
	
	public void setHttpProxyUseForAll(boolean useForAll) {
		properties.setProperty(HTTP_PROXY_USE_FOR_ALL, String.valueOf(useForAll));
	}

	public String getHttpsProxyHost() {
		return properties.getProperty(HTTPS_PROXY_HOST);
	}

	public void setHttpsProxyHost(String httpsProxyHost) {
		if (httpsProxyHost != null) {
			System.setProperty(HTTPS_PROXY_HOST, httpsProxyHost);
			properties.setProperty(HTTPS_PROXY_HOST, httpsProxyHost);
		}
	}

	public int getHttpsProxyPort() {
		return parseIntOrDefault(properties.getProperty(HTTPS_PROXY_PORT), 0);
	}

	public void setHttpsProxyPort(String httpsProxyPort) {
		if (httpsProxyPort != null) {
			System.setProperty(HTTPS_PROXY_PORT, httpsProxyPort);
			properties.setProperty(HTTPS_PROXY_PORT, httpsProxyPort);
		}
	}

	public String getWorkIpRegExp() {
		return properties.getProperty(WORK_IP_REG_EXP);
	}

	public void setWorkIpRegExp(String workIpRegExp) {
		properties.setProperty(WORK_IP_REG_EXP, workIpRegExp);
	}

	public String getNormalIconLocation() {
		String iconLocation = properties.getProperty(NORMAL_ICON_LOCATION);
		if (iconLocation == null) {
			iconLocation = DEFAULT_NORMAL_ICON_LOCATION;
		}
		return iconLocation;
	}

	public void setNormalIconLocation(String normalIconLocation) {
		properties.setProperty(NORMAL_ICON_LOCATION, normalIconLocation);
	}
	
	public String getInactiveIconLocation() {
		String iconLocation = properties.getProperty(INACTIVE_ICON_LOCATION);
		if (iconLocation == null) {
			iconLocation = DEFAULT_INACTIVE_ICON_LOCATION;
		}
		return iconLocation;
	}

	public void setInactiveIconLocation(String inactiveIconLocation) {
		properties.setProperty(INACTIVE_ICON_LOCATION, inactiveIconLocation);
	}
	
	public void setInitialConfiguraionDone(boolean configured) {
		properties.setProperty(INITIAL_CONFIGURATION_DONE, String.valueOf(configured));
	}
	
	public boolean isInitialConfiguraionDone() {
		return Boolean.valueOf(properties.getProperty(INITIAL_CONFIGURATION_DONE));
	}
	
	public void setCounter(long counter) {
		properties.setProperty(COUNTER, String.valueOf(counter));
	}
	
	public long getCounter() {
		return parseLongOrDefault(properties.getProperty(COUNTER), 0);
	}
	
	public String getHistoryStartDate() {
		return properties.getProperty(HISTORY_START_DATE, "01/01/2012");
	}
	
	public void setHistoryStartDate(String value) {
		properties.setProperty(HISTORY_START_DATE, value);
	}
	
	public long getDailyWorkingTime() {
		return parseLongOrDefault(properties.getProperty(DAILY_WORKING_TIME), 28800000); // default 8 hours
	}
	
	public String getDailyWorkingTimeString() {
		long time = getDailyWorkingTime();
		return String.format("%02d:%02d", (time / 3600000), (time / 60000) % 60);
	}
	
	public void setDailyWorkingTime(long value) {
		properties.setProperty(DAILY_WORKING_TIME, String.valueOf(value));
	}
	
	public long getWeeklyWorkingTime() {
		return parseLongOrDefault(properties.getProperty(WEEKLY_WORKING_TIME), 144000000); // default 40 hours
	}
	
	public String getWeeklyWorkingTimeString() {
		long time = getWeeklyWorkingTime();
		return String.format("%02d:%02d", (time / 3600000), (time / 60000) % 60);
	}
	
	public void setWeeklyWorkingTime(long value) {
		properties.setProperty(WEEKLY_WORKING_TIME, String.valueOf(value));
	}

	public void readConfiguration(String filename) throws FileNotFoundException, IOException {
		readConfiguration(new File(filename));
	}

	public void readConfiguration(File file) throws FileNotFoundException, IOException {
		propertiesFile = file;

		properties.load(new FileInputStream(file));

		setHttpProxyHost(properties.getProperty(HTTP_PROXY_HOST));
		setHttpProxyPort(properties.getProperty(HTTP_PROXY_PORT));
		setHttpsProxyHost(properties.getProperty(HTTPS_PROXY_HOST));
		setHttpsProxyPort(properties.getProperty(HTTPS_PROXY_PORT));
	}
	
	public void deleteOldProperties() {
		properties.remove(USER);
		properties.remove(PASSWD);
		properties.remove(URL);
	}

	public void writeConfiguration() throws FileNotFoundException, IOException {
		if (!initilized) { 
			String filename = DEFAULT_PROPERTIES_FILENAME;
			LOG.debug("Writing properties to file: {}", filename);
			propertiesFile = new File(filename);
		}

		FileWriter fileWriter = new FileWriter(propertiesFile);
		try {
			properties.store(fileWriter, "Please do NOT edit this file unless you know what you are doing.");
			fileWriter.flush();
		} finally {
			fileWriter.close();
		}
	}

	private int parseIntOrDefault(String value, int defaultValue) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	private long parseLongOrDefault(String value, long defaultValue) {
		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	private int parseOrThrow(String value) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			LOG.debug("value = {}", value);
			throw new IllegalStateException(e);
		}
	}
}
