package ndsr.beans;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ndsr.Configuration;


/**
 * @author lkufel
 */
public class Stats {
	SimpleDateFormat weekIdFormat = new SimpleDateFormat("ddMMyy");
	
	// in miliseconds
	public final long oneDayTime;
	public final long oneWeekTime;
	
	private Calendar weekBegin;
	private Calendar weekEnd;
	private long[] weekDays;
	private String weekId;
	
	private long today;
	private long week;

	public Stats(Configuration configuration) {
		super();
		oneDayTime = configuration.getDailyWorkingTime();
		oneWeekTime = configuration.getWeeklyWorkingTime();
	}

	public void setToday(long milis) {
		today = milis;
	}
	
	public long getToday() {
		return today;
	}
	
	public long getRemainingToday() {
		long remain = oneDayTime - today;
		return remain < 0 ? 0 : remain;
	}
	
	public long getOvertimeToday() {
		long remain = today - oneDayTime;
		return remain < 0 ? 0 : remain;
	}

	public static long getHours(long time) {
		return time / 3600000;
	}

	public static long getMinutes(long time) {
		return (time / 60000) % 60;
	}

	public void setWeek(long milis) {
		this.week = milis;
	}

	public long getWeek() {
		return week;
	}
	
	public long getRemainingWeek() {
		long remain = oneWeekTime - week;
		return remain < 0 ? 0 : remain;
	}
	
	public long getOvertimeWeek() {
		long remain = week - oneWeekTime;
		return remain < 0 ? 0 : remain;
	}

	public long[] getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(long[] weekDays, Calendar weekBegin) {
		this.weekDays = weekDays;
		this.weekBegin = weekBegin;
		this.weekId = weekIdFormat.format(weekBegin.getTime());
		this.weekEnd = (Calendar)weekBegin.clone();
		this.weekEnd.add(Calendar.DAY_OF_MONTH, 7);
		this.weekEnd.add(Calendar.SECOND, -1);
		if (this.week == 0) {
			for (int q=0; q<weekDays.length; q++) {
				this.week += weekDays[q];
			}
		}
	}

	public String getWeekId() {
		return weekId;
	}

	public Calendar getWeekBegin() {
		return weekBegin;
	}

	public Calendar getWeekEnd() {
		return weekEnd;
	}
	
	public static String getFormatedTime(long time, StatType type, long limit) {
		long h = 0; 
		long m = 0;
		switch (type) {
		case Normal:
			h = time / 3600000;
			m = (time / 60000) % 60;
			break;
		case Remaining:
			h = (limit - time < 0 ? 0 : limit - time) / 3600000;
			m = ((limit - time < 0 ? 0 : limit - time) / 60000) % 60;
			break;
		case Overtime:
			h = (time - limit < 0 ? 0 : time - limit) / 3600000;
			m = ((time - limit < 0 ? 0 : time - limit) / 60000) % 60;
		}

		return String.format("%02d:%02d", h, m);
	}

	public String toWeekString() {
		return String.format("Week\n %02d:%02d- worked\n %02d:%02d - remaining\n %02d:%02d - overtime\n", 
				getHours(week), getMinutes(week),
				getHours(getRemainingWeek()), getMinutes(getRemainingWeek()),
				getHours(getOvertimeWeek()), getMinutes(getOvertimeWeek()));
	}

	public String toTodayString() {
		return String.format("Today\n %02d:%02d- worked\n %02d:%02d - remaining\n %02d:%02d - overtime\n", 
				getHours(today), getMinutes(today),
				getHours(getRemainingToday()), getMinutes(getRemainingToday()),
				getHours(getOvertimeToday()), getMinutes(getOvertimeToday()));
	}

	@Override
	public String toString() {
		return toTodayString() + toWeekString();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Helper classes
	///////////////////////////////////////////////////////////////////////////////////////
	
	public enum StatType {
		Normal,
		Remaining,
		Overtime;
	};
}
