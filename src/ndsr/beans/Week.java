package ndsr.beans;

import java.util.Calendar;
import java.util.List;

import ndsr.Configuration;
import ndsr.calendar.CalendarHelper;
import ndsr.enums.StatType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.calendar.model.Event;

public class Week {
	private static final Logger LOG = LoggerFactory.getLogger(Week.class);
	private Calendar weekBegin;
	// Week days days[0]=Monday
	private Day days[] = new Day[7];

	public Week() {
		this(Calendar.getInstance());
	}
	
	public Week(final Calendar cal) {
		super();
		this.weekBegin = (Calendar)cal.clone();
		this.weekBegin.set(java.util.Calendar.HOUR_OF_DAY, 0);
		this.weekBegin.set(java.util.Calendar.MINUTE, 0);
		this.weekBegin.set(java.util.Calendar.SECOND, 0);
		this.weekBegin.set(java.util.Calendar.MILLISECOND, 0);
		this.weekBegin.add(java.util.Calendar.DATE, -((this.weekBegin.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7));
		Calendar tmp = (Calendar)this.weekBegin.clone();
	    for(int i = 0; i < 7; i++) {
	    	days[i] = new Day(tmp);
	    	tmp.add(Calendar.DATE, 1);
	    }
	}
	
	public Calendar getWeekBegin() {
		return weekBegin;
	}
	
	public Calendar getWeekEnd() {
		Calendar end = (Calendar)weekBegin.clone();
		end.add(Calendar.DATE, 7);
		return end;
	}
	
	public long getTime() {
		long week = 0;
		for(int i = 0; i < 7; i++) {
			week += days[i].getTime();
	    }
		return week;
	}
	
	public long getRemaining() {
		long remain = Configuration.getInstance().getWeeklyWorkingTime() - getTime() - getVacation();
		return remain < 0 ? 0 : remain;
	}
	
	public long getOvertime() {
		long remain = getTime() + getVacation() - Configuration.getInstance().getWeeklyWorkingTime();
		return remain < 0 ? 0 : remain;
	}
	
	public long getVacation() {
		long week = 0;
		// Weekend is not counted as vacation
		for(int i = 0; i < 5; i++) {
			if (days[i].isFreeDay()) {
				week += Configuration.getInstance().getDailyWorkingTime();
			}
	    }
		return week;
	}
	
	public void refresh() {
		init();
	}
	
	public Day getDayByTime(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return days[(cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7];
	}
	
	public Day getDay(int i) {
		if (i<0 || i > 6) {
			return null;
		}
		return days[i];
	}
	
	public void init() {
		// create new days table
		Calendar tmp = (Calendar)weekBegin.clone();
	    for(int i = 0; i < 7; i++) {
	    	days[i] = new Day(tmp);
	    	tmp.add(Calendar.DATE, 1);
	    }
	    
		List<Event> events = CalendarHelper.getEventsNoThrow(getWeekBegin(), getWeekEnd());
		String workingEvent = Configuration.getInstance().getEventName();
		String vacationEvent = Configuration.getInstance().getVacationEventPrefix();
		String otherHoliday = Configuration.getInstance().getPubHolEventPrefix();
		
		for (Event event : events) {
			if (event.getSummary() == null) {
				// ignore events with no title
				continue;
			}

			if (event.getSummary().startsWith(workingEvent)) {
				Day day = getDayByTime(CalendarHelper.getEnd(event));
				day.addTime(CalendarHelper.getEventTime(event));
			} else if (event.getSummary().startsWith(vacationEvent) || event.getSummary().startsWith(otherHoliday)) {
				boolean isVacationEvent = event.getSummary().startsWith(vacationEvent);
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				start.setTimeInMillis(CalendarHelper.getStart(event));
				end.setTimeInMillis(CalendarHelper.getEnd(event));
				while (start.getTimeInMillis() < end.getTimeInMillis()) {
					if (start.before(weekBegin)) {
						start.add(Calendar.DATE, 1);
						continue;
					}
					LOG.debug("start={}, end={}", start.getTime(), end.getTime());
					if (isVacationEvent) {
						getDayByTime(start.getTimeInMillis()).setVacation(event.getSummary());
					} else {
						getDayByTime(start.getTimeInMillis()).setOtherHoliday(event.getSummary());
					}
					start.add(Calendar.DATE, 1);
				}		
			} 
		}
	}
	
	public void setPrevWeek() {
		weekBegin.add(Calendar.DATE, -7);
		init();
	}
	
	public void setNextWeek() {
		weekBegin.add(Calendar.DATE, +7);
		init();
	}
	
	public boolean workedDuringWeek() {
		return days[5].getTime() > 0 || days[6].getTime() > 0;
	}
	
	public boolean isCurrent() {
		Calendar cal = Calendar.getInstance();
		return cal.after(getWeekBegin()) && cal.before(getWeekEnd());
	}

	@Override
	public int hashCode() {
		return weekBegin.get(Calendar.WEEK_OF_YEAR);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Week other = (Week) obj;
		return weekBegin.get(Calendar.WEEK_OF_YEAR) == other.weekBegin.get(Calendar.WEEK_OF_YEAR) 
				&& weekBegin.get(Calendar.YEAR) == other.weekBegin.get(Calendar.YEAR);
	}
	
	public String toString(StatType type) {
		switch (type) {
		case WORK: return Stats.getFormatedTime(getTime());
		case REMAIN: return Stats.getFormatedTime(getRemaining());
		case OVERTIME: return Stats.getFormatedTime(getOvertime());
		}
		return "";
	}
}
