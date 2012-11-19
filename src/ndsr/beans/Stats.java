package ndsr.beans;

import java.util.Calendar;

import ndsr.Configuration;
import ndsr.enums.StatType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author lkufel
 */
public class Stats {
	private static final Logger LOG = LoggerFactory.getLogger(Stats.class);
	private static Stats instance; 
	
	// in miliseconds
	private static Day today = new Day(Calendar.getInstance());
	private static Week week = new Week(Calendar.getInstance());
	private static TimeBank timeBank = new TimeBank();
	
	private Calendar weekBegin;
	private Calendar weekEnd;
	
	public static Stats get() {
		if (instance == null) {
			instance = new Stats(null);
		}
		return instance;
	}
	
	public Stats(Configuration configuration) {
		super();
		instance = this;
	}

	public Calendar getWeekBegin() {
		return weekBegin;
	}

	public Calendar getWeekEnd() {
		return weekEnd;
	}
	
	public static Week getWeek() {
		return week;
	}
	
	public static void setWeek(Week _week) {
		week = _week;
	}

	public static Day getToday() {
		return today;
	}
	
	public static void checkToday() {
		if (!getToday().isToday()) {
			LOG.info("Date change detected.");
			setToday(new Day());
			getToday().init();			
			getTimeBank().refresh();
		} else {
			getToday().refresh();
		}
	}
	
	public static void setToday(Day day) {
		today = day;
	}

	public static TimeBank getTimeBank() {
		return timeBank;
	}

	public static void setTimeBank(TimeBank timeBank) {
		Stats.timeBank = timeBank;
	}

	public static String getFormatedTime(long time) {
		long h = 0; 
		long m = 0;

		h = time / 3600000;
		m = (time / 60000) % 60;
		
		if (time < 0 && h == 0) {
			return String.format("-%02d:%02d", h, Math.abs(m));
		}
		return String.format("%02d:%02d", h, Math.abs(m));
	}

	public static String toWeekString() {
		return String.format("Week\n %s- worked\n %s - remaining\n %s - overtime\n",
				week.toString(StatType.WORK), week.toString(StatType.REMAIN), week.toString(StatType.OVERTIME));
	}

	public static String toTodayString() {
		return String.format("Today\n %s- worked\n %s - remaining\n %s - overtime\n",
				today.toString(StatType.WORK), today.toString(StatType.REMAIN), today.toString(StatType.OVERTIME));
	}

	public static String getString() {
		return toTodayString() + toWeekString();
	}

}
