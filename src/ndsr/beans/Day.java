package ndsr.beans;

import java.util.Calendar;
import java.util.List;

import com.google.api.services.calendar.model.Event;

import ndsr.Configuration;
import ndsr.calendar.CalendarHelper;
import ndsr.enums.StatType;

public class Day {
	private final Calendar cal;
	String title = "";
	boolean vacation = false;
	boolean otherHoliday = false;
	// in miliseconds
	long workingTime = 0;

	public Day() {
		this(Calendar.getInstance());
	}
	
	public Day(final Calendar cal) {
		this.cal = (Calendar)cal.clone();
		this.cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		this.cal.set(java.util.Calendar.MINUTE, 0);
		this.cal.set(java.util.Calendar.SECOND, 0);
		this.cal.set(java.util.Calendar.MILLISECOND, 0);
	}

	public void addTime(long milis) {
		workingTime += milis;
	}
	
	public long getTime() {
		return workingTime;
	}
	
	public long getRemaining() {
		if (isFreeDay()) {
			return 0;
		}
		long remain = Configuration.getInstance().getDailyWorkingTime() - workingTime;
		return remain < 0 ? 0 : remain;
	}
	
	public long getOvertime() {
		if (isFreeDay()) {
			return 0;
		}
		long remain = workingTime - Configuration.getInstance().getDailyWorkingTime();
		return remain < 0 ? 0 : remain;
	}

	public boolean isVacation() {
		return vacation;
	}

	public void setVacation(String title) {
		this.vacation = true;
		this.title = title;
	}

	public boolean isOtherHoliday() {
		return otherHoliday;
	}

	public void setOtherHoliday(String title) {
		this.otherHoliday = true;
		this.title = title;
	}
	
	public boolean isFreeDay() {
		return vacation || otherHoliday;
	}

	public Calendar getBegin() {
		return cal;
	}
	
	public Calendar getEnd() {
		Calendar end = (Calendar)cal.clone();
		end.add(Calendar.DATE, 1);
		return end;
	}
	
	public String getTitle() {
		return title;
	}

	public void refresh() {
		init();
	}
	
	public void init() {
		workingTime = 0;
		List<Event> events = CalendarHelper.getEventsNoThrow(getBegin(), getEnd());
		String workingEvent = Configuration.getInstance().getEventName();
		String vacationEvent = Configuration.getInstance().getVacationEventPrefix();
		String otherHoliday = Configuration.getInstance().getPubHolEventPrefix();
		
		for (Event event : events) {
			if (event.getSummary() == null) {
				// ignore events with no title
				continue;
			}

			if (event.getSummary().startsWith(workingEvent)) {
				addTime(CalendarHelper.getEventTime(event));
			} else if (event.getSummary().startsWith(vacationEvent)) {
				setVacation(event.getSummary());
			} else if (event.getSummary().startsWith(otherHoliday)) {
				setOtherHoliday(event.getSummary());
			}
		}
	}
	
	
	public boolean isToday() {
		Calendar cal = Calendar.getInstance();
		return cal.after(getBegin()) && cal.before(getEnd());
	}

	@Override
	public int hashCode() {
		return cal.get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Day other = (Day) obj;
		
		return cal.get(Calendar.DAY_OF_YEAR) == other.cal.get(Calendar.DAY_OF_YEAR) 
				&& cal.get(Calendar.YEAR) == other.cal.get(Calendar.YEAR);
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
