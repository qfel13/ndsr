package ndsr;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import ndsr.beans.Stats;

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

		while (true) {
			for (CalendarListEntry calendarListEntry : calendars.getItems()) {
				calendarList.add(calendarListEntry);
			}
			String pageToken = calendars.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				calendars = calendarService.calendarList().list().setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		return calendarList;
	}

	public List<Event> getEventList(String calendarId, String timeMin, String timeMax) throws IOException {
		// 2009-07-06T08:30:00+02:00
		// String timeMin = "2011-12-20T00:00:00+01:00";
		// String timeMax = "2011-12-21T00:00:00+01:00";
		// String calendarId = "m17oihki8uvmjo45495ddvions@group.calendar.google.com";

		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}

		Events events = calendarService.events().list(calendarId).setTimeMin(timeMin).setTimeMax(timeMax).execute();

		List<Event> eventList = new ArrayList<Event>();

		while (true) {
			for (Event event : events.getItems()) {
				eventList.add(event);
			}

			String pageToken = events.getNextPageToken();
			if (pageToken != null && !pageToken.isEmpty()) {
				events = calendarService.events().list(calendarId).setPageToken(pageToken).execute();
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

	public Event createOrUpdate() throws IOException {
		List<Event> eventList = getEvents(getTodayBegin(), getTodayEnd());

		Event eventForUpdate = getLatestEvent(eventList);

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
		java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));
		cal.add(java.util.Calendar.MINUTE, eventMinutesAhead);
		DateTime endTime = new DateTime(cal.getTime(), TimeZone.getTimeZone("Europe/Warsaw"));
		EventDateTime eventDateTime = new EventDateTime();
		eventDateTime.setDateTime(endTime);
		event.setEnd(eventDateTime);

		Event updated = calendarService.events().update(configuration.getCalendarId(), event.getId(), event).execute();
		LOG.debug("end: " + updated.getEnd());

		return updated;
	}

	public Event createNewEvent() throws IOException {
		return createEvent(0);
	}
	
	public Event createEvent(int minutesBeforeFirstEvent) throws IOException {
		LOG.debug("createEvent");
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}

		Event event = new Event();

		event.setSummary(configuration.getEventName());
		java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("Europe/Warsaw"));
		if (minutesBeforeFirstEvent != 0) {
			cal.add(java.util.Calendar.MINUTE, -minutesBeforeFirstEvent);
		}
		DateTime start = new DateTime(cal.getTime(), TimeZone.getTimeZone("UTC"));
		LOG.debug("start = {}", start);
		cal.add(java.util.Calendar.MINUTE, 10);
		DateTime end = new DateTime(cal.getTime(), TimeZone.getTimeZone("UTC"));
		LOG.debug("end = {}", end);
		event.setStart(new EventDateTime().setDateTime(start).setTimeZone("UTC"));
		event.setEnd(new EventDateTime().setDateTime(end).setTimeZone("UTC"));

		String calendarId = configuration.getCalendarId();
		LOG.debug("calendarId = {}", calendarId);
		Event createdEvent = calendarService.events().insert(calendarId, event).execute();

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
		Events events = calendarService.events().list(calendarId).setTimeMin(timeMin).setTimeMax(timeMax)/* .setQ("praca") */.execute();

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
		List<Event> eventList = getEvents(getTodayBegin(), getTodayEnd());
		
		LOG.debug("Today event list size = {}", eventList.size());
		
		long milis = 0;
		for (Event event : eventList) {
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
		List<Event> eventList = getEvents(getWeekBegin(), getWeekEnd());
		
		LOG.debug("Week event list size = {}", eventList.size());
		
		long milis = 0;
		for (Event event : eventList) {
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
}
