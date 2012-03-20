package ndsr.calendar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import ndsr.Configuration;
import ndsr.beans.Stats;

//import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

public class CalendarHelper {

	private static final Logger LOG = LoggerFactory.getLogger(CalendarHelper.class);

	// The clientId and clientSecret are copied from the API Access tab on
	// the Google APIs Console
	private String clientId = "931256229393.apps.googleusercontent.com";
	private String clientSecret = "QDin81EJRcCndmwpEV9iXftr";
	private String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
	private String scope = "https://www.googleapis.com/auth/calendar";
	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = new JacksonFactory();

	private Calendar calendarService = null;
	private boolean initialized = false;
	private Configuration configuration;
	
	private List<Event> weekEventList;
	private List<Event> todayEventList;
	
	private long counter = 0;

	private List<CalendarListEntry> calendarList;

	public CalendarHelper(Configuration configuration) {
		this.configuration = configuration;

		if (configuration.isInitialConfiguraionDone()) {
			String accessToken = configuration.getAccessToken();
			String refreshToken = configuration.getRefreshToken();
			try {
				initCalendarService(accessToken, refreshToken);
			} catch (IOException e) {
				initialized = false;
			}
		}
	}

	public void setCalendar(String calendarId) {
		try {
			configuration.setCalendarId(calendarId);
			configuration.setInitialConfiguraionDone(true);
			configuration.deleteOldProperties();
			configuration.writeConfiguration();
		} catch (IOException e) {
			LOG.error("writeConfiguration error", e);
		}
	}

	public String getAuthorizationUrl() {
		String authorizationUrl = new GoogleAuthorizationRequestUrl(clientId, redirectUrl, scope).build();
		LOG.debug("authorizationUrl = {}", authorizationUrl);
		return authorizationUrl;
	}

	public List<CalendarListEntry> getCalendarList() throws IOException {
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}

		calendarList = new ArrayList<CalendarListEntry>();

		CalendarList calendars = calendarService.calendarList().list().setMinAccessRole("writer").execute();
		++counter;

		while (true) {
			for (CalendarListEntry calendarListEntry : calendars.getItems()) {
				calendarList.add(calendarListEntry);
			}
			String pageToken = calendars.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				calendars = calendarService.calendarList().list().setPageToken(pageToken).execute();
				++counter;
			} else {
				break;
			}
		}

		return calendarList;
	}

	public List<Event> getEventList(String calendarId, String timeMin, String timeMax) throws IOException {
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}

		Events events = calendarService.events().list(calendarId).setTimeMin(timeMin).setTimeMax(timeMax).execute();
		++counter;

		List<Event> eventList = new ArrayList<Event>();

		while (true) {
			for (Event event : events.getItems()) {
				eventList.add(event);
			}

			String pageToken = events.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				events = calendarService.events().list(calendarId).setPageToken(pageToken).execute();
				++counter;
			} else {
				break;
			}
		}
		return eventList;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean initCalendarService(String accessToken, String refreshToken) throws FileNotFoundException, IOException {
		GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(accessToken, httpTransport, jsonFactory,
				clientId, clientSecret, refreshToken);

		calendarService = Calendar.builder(httpTransport, jsonFactory).setApplicationName("Ndsr")
				.setHttpRequestInitializer(accessProtectedResource).build();

		configuration.setAccessToken(accessToken);
		configuration.setRefreshToken(refreshToken);
		configuration.writeConfiguration();

		initialized = true;

		return initialized;
	}

	public boolean initCalendarService(String code) throws SocketTimeoutException, IOException {
		try {
			AccessTokenResponse response = new GoogleAuthorizationCodeGrant(httpTransport, jsonFactory, clientId, clientSecret, code,
					redirectUrl).execute();
			++counter;
			String accessToken = response.accessToken;
			String refreshToken = response.refreshToken;
			
			initialized = initCalendarService(accessToken, refreshToken);
		} catch (SocketTimeoutException e) {
			LOG.warn("SocketTimeoutException", e);
			initialized = false;
			throw e;
		} catch (IOException e) {
			LOG.warn("Cannot get code", e);
			initialized = false;
			throw e;
		}
		return initialized;
	}
	
	public String createNewCalendar(String summary, String description) throws IOException {
		LOG.debug("createEvent");
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}
		
		com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
		
		calendar.setSummary(summary);
		calendar.setDescription(description);

		com.google.api.services.calendar.model.Calendar createdCalendar = calendarService.calendars().insert(calendar).execute();
		
		return createdCalendar.getId();
	}

	private java.util.Calendar getTodayBegin() {
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
		calendar.set(java.util.Calendar.MINUTE, 0);
		calendar.set(java.util.Calendar.SECOND, 0);
		calendar.set(java.util.Calendar.MILLISECOND, 0);

		return calendar;
	}

	private java.util.Calendar getTodayEnd() {
		java.util.Calendar calendar = getTodayBegin();
		calendar.add(java.util.Calendar.HOUR, 24);
		return calendar;
	}

	private Event getLatestEvent(List<Event> eventList) {
		Event latestEntry = null;
		for (Event event : eventList) {
			String eventName = event.getSummary();
			String configuredEventName = configuration.getEventName();
			if (eventName.equals(configuredEventName)) {
				if (latestEntry != null) {
					if (laterThenLatest(event, latestEntry)) {
						latestEntry = event;
					}
				} else {
					latestEntry = event;
				}
			} else {
				LOG.debug("eventName: {} != configuredEventName: {}", eventName, configuredEventName);
			}
		}
		return latestEntry;
	}
	
	private boolean eventBeginAfter(Event event, java.util.Calendar time) {
		com.google.api.client.util.DateTime eventEndTime = event.getEnd().getDateTime();
		return eventEndTime.getValue() > time.getTimeInMillis();
	}
	
	private boolean eventBeginBefore(Event event, java.util.Calendar time) {
		com.google.api.client.util.DateTime eventEndTime = event.getEnd().getDateTime();
		return eventEndTime.getValue() < time.getTimeInMillis();
	}

	private boolean laterThenLatest(Event event, Event latest) {
		com.google.api.client.util.DateTime eventEndTime = event.getEnd().getDateTime();
		com.google.api.client.util.DateTime latestEndTime = latest.getEnd().getDateTime();
		return eventEndTime.getValue() > latestEndTime.getValue();
	}

	public List<Event> getTodayEvents() {
		List<Event> eventList = null;
		try {
			eventList = getEvents(getTodayBegin(), getTodayEnd());
		} catch (IOException e) {
			LOG.error("asda", e);
		}
		return eventList;
	}

	public Event getTodayLatestEvent() {
		return getLatestEvent(getTodayEvents());
	}
	
	public List<Event> getTodayEvents(List<Event> weekEventList) {
		List<Event> todayEvents = new ArrayList<Event>();
		for (Event event : weekEventList) {
			if (eventBeginAfter(event, getTodayBegin()) && eventBeginBefore(event, getTodayEnd())) {
				todayEvents.add(event);
			}
		}
		return todayEvents;
	}

	public Event createOrUpdate() throws IOException {
		weekEventList = getEvents(getWeekBegin(), getWeekEnd());
		todayEventList = getTodayEvents(weekEventList);

		Event eventForUpdate = getLatestEvent(todayEventList);

		if (eventForUpdate != null) {
			return updateEvent(eventForUpdate);
		} else {
			int minutesBeforeFirstEvent = configuration.getMinutesBeforeFirstEvent();
			LOG.debug("minutesBeforeFirstEvent = {}", minutesBeforeFirstEvent);
			return createEvent(minutesBeforeFirstEvent);
		}
	}

	public Event updateEvent(Event event) throws IOException {
		int eventMinutesAhead = configuration.getEventMinutesAhead();
		LOG.debug("eventMinutesAhead = {}", eventMinutesAhead);
		
		TimeZone timeZone = TimeZone.getDefault();
		
		java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
		cal.add(java.util.Calendar.MINUTE, eventMinutesAhead);
		DateTime endTime = new DateTime(cal.getTime(), timeZone);
		EventDateTime eventDateTime = new EventDateTime();
		eventDateTime.setDateTime(endTime);
		event.setEnd(eventDateTime);

		Event updated = calendarService.events().update(configuration.getCalendarId(), event.getId(), event).execute();
		++counter;
		LOG.debug("end: " + updated.getEnd());

		return updated;
	}

	public Event createEvent() throws IOException {
		return createEvent(0);
	}
	
	public Event createEvent(int minutesBeforeFirstEvent) throws IOException {
		LOG.debug("createEvent");
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}

		Event event = new Event();

		event.setSummary(configuration.getEventName());
		
		TimeZone timeZone = TimeZone.getDefault();
		
		java.util.Calendar cal = java.util.Calendar.getInstance(timeZone);
		if (minutesBeforeFirstEvent != 0) {
			cal.add(java.util.Calendar.MINUTE, -minutesBeforeFirstEvent);
		}
		DateTime start = new DateTime(cal.getTime(), timeZone);
		LOG.debug("start = {}", start);
		cal.add(java.util.Calendar.MINUTE, 10);
		DateTime end = new DateTime(cal.getTime(), timeZone);
		LOG.debug("end = {}", end);
		event.setStart(new EventDateTime().setDateTime(start));
		event.setEnd(new EventDateTime().setDateTime(end));

		String calendarId = configuration.getCalendarId();
		LOG.debug("calendarId = {}", calendarId);
		Event createdEvent = calendarService.events().insert(calendarId, event).execute();
		++counter;

		System.out.println(createdEvent.getId());

		return createdEvent;
	}

	private List<Event> getEvents(java.util.Calendar todayBegin, java.util.Calendar todayEnd) throws IOException {
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String timeMin = sdf.format(todayBegin.getTime());
		String timeMax = sdf.format(todayEnd.getTime());

		String calendarId = configuration.getCalendarId();
		LOG.debug("calendarId = {}", calendarId);
		Events events = calendarService.events().list(calendarId).setTimeMin(timeMin).setTimeMax(timeMax).execute();
		++counter;

		List<Event> eventList = new ArrayList<Event>();

		while (true) {
			if (events != null) {
				List<Event> items = events.getItems();
				if (items != null) {
					for (Event event : items) {
						eventList.add(event);
					}
				}
			}

			String pageToken = events.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				events = calendarService.events().list(calendarId).setPageToken(pageToken).execute();
				++counter;
			} else {
				break;
			}
		}
		return eventList;
	}

	public Stats getStats() throws IOException {
		Stats s = new Stats();
		getTodayStats(s);
		getWeekStats(s);
		return s;
	}
	
	private long getStart(Event event) {
		return event.getStart().getDateTime().getValue();
	}
	
	private long getEnd(Event event) {
		return event.getEnd().getDateTime().getValue();
	}
	
	private Stats getTodayStats(Stats stats) throws IOException {
		LOG.debug("Today event list size = {}", todayEventList.size());
		
		long milis = 0;
		for (Event event : todayEventList) {
			String title = event.getSummary();
			String eventName = configuration.getEventName();
			if (title != null && eventName != null && title.startsWith(eventName)) {
				milis += getEnd(event) - getStart(event);
			}
		}
		long sec = milis / 1000;
		long min = sec / 60;
		long hours = min / 60;
		long minutes = min % 60;

		stats.setToday(hours, minutes);

		return stats;
	}

	private java.util.Calendar getWeekBegin() {
		java.util.Calendar cal = getTodayBegin();
		int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
		cal.add(java.util.Calendar.DATE, -(dayOfWeek - 2));
		return cal;
	}

	private java.util.Calendar getWeekEnd() {
		java.util.Calendar cal = getWeekBegin();
		cal.add(java.util.Calendar.DATE, 7);
		return cal;
	}
	
	private Stats getWeekStats(Stats stats) throws IOException {
		LOG.debug("Week event list size = {}", weekEventList.size());
		
		long milis = 0;
		for (Event event : weekEventList) {
			String title = event.getSummary();
			String eventName = configuration.getEventName();
			if (title != null && eventName != null && title.startsWith(eventName)) {
				milis += getEnd(event) - getStart(event);
			}
		}
		long sec = milis / 1000;
		long min = sec / 60;
		long hours = min / 60;
		long minutes = min % 60;

		stats.setWeek(hours, minutes);

		return stats;
	}
	
	private int getWeekInYear(int year, int month, int day) throws ParseException {
		SimpleDateFormat sdf;
		java.util.Calendar cal;
		Date date;
		String sample = month+"/"+day+"/"+year;
		sdf = new SimpleDateFormat("MM/dd/yyyy");
		date = sdf.parse(sample);
		cal = java.util.Calendar.getInstance();
		cal.setTime(date);
		return cal.get(java.util.Calendar.WEEK_OF_YEAR);
	}
	
	public void getHistoryStats() {
		LOG.debug("getHistoryStats: entered");
		List<Event> eventList = null;
		String historyStartDateString = configuration.getHistoryStartDate();
		SimpleDateFormat sdf;
		java.util.Calendar historyStartDate = java.util.Calendar.getInstance();
		sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			historyStartDate.setTime(sdf.parse(historyStartDateString));
		} catch (ParseException e1) {
			LOG.error(e1.getMessage(), e1);
			return;
		}
		Map<String, Long> years = new HashMap<String, Long>();
		Map<String, Long> months = new HashMap<String, Long>();
		Map<String, Long> weeks = new HashMap<String, Long>();
		try {
			eventList = getEvents(historyStartDate, getTodayEnd());
			LOG.debug("getHistoryStats: got " + eventList.size() + " events.");
			for (Event event : eventList) {
				String summary = event.getSummary();
				String eventName = configuration.getEventName();
				long miliseconds = 0L;
				LOG.info("getHistoryStats: Event title: " + summary + ", name: " + eventName);
				if (summary != null && eventName != null && summary.startsWith(eventName)) {
					long eventEnd = getEnd(event);
					miliseconds = eventEnd - getStart(event);
					Date date = new Date(event.getEnd().getDateTime().getValue());
					LOG.info("getHistoryStats: event " + summary + " start:" + event.getStart().getDateTime().getValue() + ", end:" + event.getEnd().getDateTime().getValue() + ", miliseconds:" + miliseconds);
					String year = date.toLocaleString().substring(0, 4);
					String month = date.toLocaleString().substring(0, 7);
					String day = date.toLocaleString().substring(8, 10);
					LOG.debug("date:"+date.toLocaleString());
					LOG.debug("year:"+year+",month:"+month+",day:"+day);
					String week = null;
					try {
						week = year + "." + getWeekInYear(Integer.valueOf(year), Integer.valueOf(month.substring(5, 7)), Integer.valueOf(day));
					} catch (NumberFormatException e) {
						LOG.error(e.getMessage(), e);
						week = "0000.00";
					} catch (ParseException e) {
						LOG.error(e.getMessage(), e);
						week = "0000.00";
					}
					if (years.containsKey(year)) {
						years.put(year, years.get(year) + miliseconds);
					} else {
						years.put(year, miliseconds);
					}
					if (months.containsKey(month)) {
						months.put(month, months.get(month) + miliseconds);
					} else {
						months.put(month, miliseconds);
					}
					if (weeks.containsKey(week)) {
						weeks.put(week, weeks.get(week) + miliseconds);
					} else {
						weeks.put(week, miliseconds);
					}
				}
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		LOG.info("years: " + years.toString());
		LOG.info("months: " + months.toString());
		LOG.info("weeks: " + weeks.toString());
		LOG.debug("getHistoryStats: finished.");
	}
}
