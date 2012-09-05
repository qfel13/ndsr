package ndsr.calendar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ndsr.Configuration;
import ndsr.beans.Day;
import ndsr.beans.Stats;
import ndsr.beans.TimeBank;
import ndsr.beans.Week;

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
	private static final SimpleDateFormat ALL_DAY_EVENT_SDF = new SimpleDateFormat("yyyy-MM-dd");
	private static final String RESET_TIMEBANK_EVENT_NAME = "NDSR ResetTimeBankEvent";

	// The clientId and clientSecret are copied from the API Access tab on
	// the Google APIs Console
	private String clientId = "931256229393.apps.googleusercontent.com";
	private String clientSecret = "QDin81EJRcCndmwpEV9iXftr";
	private String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
	private String scope = "https://www.googleapis.com/auth/calendar";
	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = new JacksonFactory();

	private static Calendar calendarService = null;
	private boolean initialized = false;
	private static Configuration configuration;

	private List<CalendarListEntry> calendarList;

	public CalendarHelper(Configuration configuration) {
		CalendarHelper.configuration = configuration;

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

	public Event createOrUpdate() throws IOException {
		Day day = new Day();
		Event eventForUpdate = getLatestEvent(getEvents(day.getBegin(), day.getEnd()));

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
		System.out.println(createdEvent.getId());

		return createdEvent;
	}
	
	public static Week getWeek() {
		return getWeek(java.util.Calendar.getInstance());
	}
	
	public static Week getWeek(java.util.Calendar date) {
		Week week = new Week(date);
		
		return week;
	}

	public static List<Event> getEvents(java.util.Calendar begin, java.util.Calendar end) throws IOException {
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}
		LOG.debug("Getting events from " + begin.getTime() + " till " + end.getTime());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String timeMin = sdf.format(begin.getTime());
		String timeMax = sdf.format(end.getTime());

		String calendarId = configuration.getCalendarId();
		LOG.debug("calendarId = {}", calendarId);
		Events events = calendarService.events().list(calendarId).setTimeMin(timeMin).setTimeMax(timeMax).execute();
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
	
	public static List<Event> getEventsNoThrow(java.util.Calendar begin, java.util.Calendar end) {
		try {
			java.util.Calendar b = (java.util.Calendar)begin.clone();
			List<Event> eventList = null;
			// If it is more the year - get each year and sum them
			if (end.getTimeInMillis() - b.getTimeInMillis() > 365l * 24l * 3600l * 1000l) {
				java.util.Calendar b2 = (java.util.Calendar)b.clone();
				b.add(java.util.Calendar.YEAR, 1);
				if (eventList == null) {
					eventList = new ArrayList<Event>();
				}
				eventList.addAll(getEvents(b2, b));
			}
			if (eventList == null) {
				eventList = getEvents(b, end);
			} else {
				eventList.addAll(getEvents(b, end));
			}
			return eventList;
		} catch (IOException e) {
			LOG.error("getEventsNoThrow: got exception: " + e);
		}
		return new ArrayList<Event>();
	}
	
	public static void setResetTime(TimeBank timeBank) {
		LOG.debug("getResetTime begin");
		java.util.Calendar end = java.util.Calendar.getInstance();
		long resetTime = 0;
		java.util.Calendar begin = new Day().getBegin();
		begin.set(java.util.Calendar.DAY_OF_YEAR, 1);
		List<Event> events = getEventsNoThrow(begin, end);
		LOG.debug("events.size=" + events.size());
		if (events.isEmpty()) {
			// could be begin of year - get previous year
			end = (java.util.Calendar)begin.clone();
			begin.add(java.util.Calendar.YEAR, -1);
			events = getEventsNoThrow(begin, end);
			LOG.debug("events.size=" + events.size());
			if (events.isEmpty()) {
				// no event - return begin of week
				timeBank.setTimeBankResetTime(new Week(java.util.Calendar.getInstance()).getWeekBegin());
				return;
			}
		} 

		Event resetEvent = null;
		while (!events.isEmpty()) {
			for (Event event : events) {
				String title = event.getSummary();
				if (title != null && title.equals(RESET_TIMEBANK_EVENT_NAME)) {
					long eventEnd = getStart(event);
					if (resetTime < eventEnd) {
						resetTime = eventEnd;
						resetEvent = event;
					}
				}
			}
			if (resetTime != 0) {
				timeBank.setTimeBankResetTime(resetTime);
				timeBank.setTimeBankResetEvent(resetEvent);
				return;
			}
			end = (java.util.Calendar)begin.clone();
			begin.add(java.util.Calendar.YEAR, -1);
			events = getEventsNoThrow(begin, end);
			LOG.debug("events.size=" + events.size());
		}
		
		timeBank.setTimeBankResetTime(end);
	}
	
	// From stackoverflow
	// http://stackoverflow.com/questions/4600034/calculate-number-of-weekdays-between-two-dates-in-java
	static long workingDays(long start, long end){
	    //Ignore argument check

		java.util.Calendar c1 = java.util.Calendar.getInstance();
	    c1.setTimeInMillis(start);
	    int w1 = c1.get(java.util.Calendar.DAY_OF_WEEK);
	    c1.add(java.util.Calendar.DAY_OF_WEEK, -w1 + 1);

	    java.util.Calendar c2 = java.util.Calendar.getInstance();
	    c2.setTimeInMillis(end);
	    int w2 = c2.get(java.util.Calendar.DAY_OF_WEEK);
	    c2.add(java.util.Calendar.DAY_OF_WEEK, -w2 + 1);

	    //end Saturday to start Saturday 
	    long days = (c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24);
	    long daysWithoutSunday = days-(days*2/7);

	    if (w1 == java.util.Calendar.SUNDAY) {
	        w1 = java.util.Calendar.MONDAY;
	    }
	    if (w2 == java.util.Calendar.SUNDAY) {
	        w2 = java.util.Calendar.MONDAY;
	    }
	    return daysWithoutSunday-w1+w2;
	}
	
	public static long getTimeBank(java.util.Calendar timeBankResetTime) {
		Week week = new Week(java.util.Calendar.getInstance());
		LOG.debug("Reset time: " + timeBankResetTime.getTime().toString());
		if (timeBankResetTime.getTimeInMillis() == week.getWeekBegin().getTimeInMillis()) {
			return 0;
		}
		long totalWorkedTime = 0;
		String workingEvent = Configuration.getInstance().getEventName();
		String vacationEvent = Configuration.getInstance().getVacationEventPrefix();
		String otherHoliday = Configuration.getInstance().getPubHolEventPrefix();
		
		for (Event event : getEventsNoThrow(timeBankResetTime, week.getWeekBegin())) {
			if (event.getSummary().startsWith(workingEvent)) {
				totalWorkedTime += getEventTime(event);
			} else if (event.getSummary().startsWith(vacationEvent) || event.getSummary().startsWith(otherHoliday)) {
				long start = Math.max(getStart(event),timeBankResetTime.getTimeInMillis());
				long end = Math.min(getEnd(event), week.getWeekBegin().getTimeInMillis());
				totalWorkedTime += workingDays(start, end) * Configuration.getInstance().getDailyWorkingTime();
			}
		}
		long days = ((week.getWeekBegin().getTimeInMillis() - timeBankResetTime.getTimeInMillis()) / (24 * 3600000));
		LOG.debug("Days from reset to beginOfWeek: " + days);
		long weeks = days / 7;
		long remainDays = days % 7;
		LOG.debug("Weeks from reset to beginOfWeek: " + weeks + ", remain days: " + remainDays);
		// Need information what the days are
		
		if (remainDays > 2) {
			remainDays -= 2;
		} else {
			remainDays = 0;
		}
		// Total working time
		long totalPossibleWorkingTime = (weeks * configuration.getWeeklyWorkingTime()) + (remainDays * configuration.getDailyWorkingTime());
		long timeBankBalanceWithoutCurrent = totalWorkedTime - totalPossibleWorkingTime;
		LOG.debug("worked=" + (totalWorkedTime / 3600000) + ":" + (totalWorkedTime - ((totalWorkedTime / 3600000) * 3600000)) / 60000 +
				", possWorking=" + (totalPossibleWorkingTime / 3600000) + ":" + (totalPossibleWorkingTime - ((totalPossibleWorkingTime / 3600000) * 3600000)) / 60000 +
				", bankBallance=" + (timeBankBalanceWithoutCurrent / 3600000) + ":" + (timeBankBalanceWithoutCurrent - ((timeBankBalanceWithoutCurrent / 3600000) * 3600000)) / 60000); 
		return timeBankBalanceWithoutCurrent;
	}
	
	public static void setResetTimeEvent(Event prevEvent, Date newDate) {
		LOG.debug("setResetTimeEvent");
		if (calendarService == null) {
			throw new IllegalStateException("Calendar service is not initialized");
		}
		
		Event event = new Event();
		event.setSummary(RESET_TIMEBANK_EVENT_NAME);
		event.setStart(new EventDateTime().setDate(ALL_DAY_EVENT_SDF.format(newDate)));
		// add a day
		newDate.setTime(newDate.getTime() + (24l * 3600000l));
		event.setEnd(new EventDateTime().setDate(ALL_DAY_EVENT_SDF.format(newDate)));

		String calendarId = configuration.getCalendarId();
		
		try {
			if (prevEvent != null) {
				calendarService.events().delete(calendarId, prevEvent.getId()).execute();
			}
			calendarService.events().insert(calendarId, event).execute();
		} catch (IOException e) {
			LOG.error("Cannot create resettimebank event", e);
		}
	}
	
	public Stats getStats() throws IOException {
		Day day = new Day();
		day.init();
		Stats.setToday(day);
		Stats.getWeek().refresh();
		return null;
	}

	private static long parseDate(String date) {
		Date d = new Date(0);
		try {
			d = ALL_DAY_EVENT_SDF.parse(date);
		} catch (ParseException e) {
			LOG.error("Parsing date '" + date + "' failed: ", e);
		}
		return d.getTime();
	}

	public static long getStart(Event event) {
		if (event.getStart().getDate() != null) {
			// all-day event
			return parseDate(event.getStart().getDate());
		}
		return event.getStart().getDateTime().getValue();
	}
	
	public static long getEnd(Event event) {
		if (event.getEnd().getDate() != null) {
			// all-day event
			return parseDate(event.getEnd().getDate());
		}
		return event.getEnd().getDateTime().getValue();
	}
	
	public static long getEventTime(Event event) {
		return getEnd(event) - getStart(event);
	}
}
