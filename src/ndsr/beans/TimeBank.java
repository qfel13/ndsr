package ndsr.beans;

import java.util.Calendar;
import java.util.Date;

import ndsr.Configuration;
import ndsr.calendar.CalendarHelper;

import com.google.api.services.calendar.model.Event;

public class TimeBank {
	private Week week;
	private long timeBankBalanceWithoutCurrent = 0;
	private java.util.Calendar timeBankResetTime = null;
	private Event timeBankResetEvent = null;
	
	public TimeBank() {
		init();
	}
	
	public void refresh() {
		init();
	}
	
	public void init() {
		week = new Week();
		week.init();
		CalendarHelper.setResetTime(this);
		timeBankBalanceWithoutCurrent = CalendarHelper.getTimeBank(timeBankResetTime);
	}
	
	public void newResetTime(Date date) {
		CalendarHelper.setResetTimeEvent(timeBankResetEvent, date);
		init();	
	}
	
	public long getBalance() {
		// TimeBank
		// days from week begin
		int tabIndex = (Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7;
		long timeBankBalance = timeBankBalanceWithoutCurrent;
		for (int q=0; q<tabIndex; q++) {
			if (q == 5 || week.getDay(q).isFreeDay()) {
				// Saturday doesn't count as working day - only add working time
				// Sundays are skipped here. Sunday is added below as getToday()
				if (week.getDay(q).getBegin().after(timeBankResetTime)) {
					timeBankBalance += week.getDay(q).getTime();
				}
				continue;
			}
			if (week.getDay(q).getBegin().after(timeBankResetTime)) {
				timeBankBalance += week.getDay(q).getTime() - Configuration.getInstance().getDailyWorkingTime(); 
			}
		}
		if (tabIndex >= 5) {
			// Saturday and Sunday
			timeBankBalance += Stats.getToday().getTime();
		} else {
			timeBankBalance += Stats.getToday().getOvertime();
		}

		return timeBankBalance;
	}

	public java.util.Calendar getTimeBankResetTime() {
		return timeBankResetTime;
	}

	public void setTimeBankResetTime(java.util.Calendar timeBankResetTime) {
		this.timeBankResetTime = timeBankResetTime;
	}
	
	public void setTimeBankResetTime(long millis) {
		this.timeBankResetTime = Calendar.getInstance();
		this.timeBankResetTime.setTimeInMillis(millis);
	}

	public Event getTimeBankResetEvent() {
		return timeBankResetEvent;
	}

	public void setTimeBankResetEvent(Event timeBankResetEvent) {
		this.timeBankResetEvent = timeBankResetEvent;
	}
}
