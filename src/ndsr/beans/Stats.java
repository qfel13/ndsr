package ndsr.beans;

/**
 * @author lkufel
 */
public class Stats {
	private int weekHoursWithoutVacation = 40;
	private int standardOneDayHours = 8;
	
	private long todayHours;
	private long todayMinutes;
	private long weekHours;
	private long weekMinutes;

	private long remainingTodayHours;
	private long remainingTodayMinutes;
	private long remainingWeekHours;
	private long remainingWeekMinutes;

	private long overtimeTodayHours;
	private long overtimeTodayMinutes;
	private long overtimeWeekHours;
	private long overtimeWeekMinutes;

	public void setWeekHoursWithoutVacation(int withoutVacation) {
		weekHoursWithoutVacation = withoutVacation;
	}
	
	public void substractDaysFromWeek(int days) {
		weekHoursWithoutVacation -= days * 8;
	}

	public void setToday(long todayHours, long todayMinutes) {
		this.todayMinutes = todayMinutes;
		this.todayHours = todayHours;

		if (todayHours < standardOneDayHours || (todayHours == standardOneDayHours && todayMinutes == 0)) {
			remainingTodayMinutes = todayMinutes == 0 ? 0 : 60 - todayMinutes;
			remainingTodayHours = (todayMinutes > 0 ? -1 : 0) + standardOneDayHours - todayHours;
		} else {
			remainingTodayMinutes = 0;
			remainingTodayHours = 0;
			overtimeTodayHours = todayHours - standardOneDayHours;
			overtimeTodayMinutes = todayMinutes;
		}
	}

	public void setWeek(long weekHours, long weekMinutes) {
		this.weekMinutes = weekMinutes;
		this.weekHours = weekHours;
		
		if (weekHours < weekHoursWithoutVacation || (weekHours == weekHoursWithoutVacation && weekMinutes == 0)) {
			remainingWeekMinutes = weekMinutes == 0 ? 0 : 60 - weekMinutes;
			remainingWeekHours = (weekMinutes > 0 ? -1 : 0) + weekHoursWithoutVacation - weekHours;
		} else {
			remainingWeekMinutes = 0;
			remainingWeekHours = 0;
			overtimeWeekHours = weekHours - weekHoursWithoutVacation;
			overtimeWeekMinutes = weekMinutes;
		}
	}

	public long getRemainingTodayHours() {
		return remainingTodayHours;
	}

	public long getRemainingTodayMinutes() {
		return remainingTodayMinutes;
	}

	public long getRemainingWeekHours() {
		return remainingWeekHours;
	}

	public long getRemainingWeekMinutes() {
		return remainingWeekMinutes;
	}

	public long getTodayHours() {
		return todayHours;
	}

	public long getTodayMinutes() {
		return todayMinutes;
	}

	public long getWeekHours() {
		return weekHours;
	}

	public long getWeekMinutes() {
		return weekMinutes;
	}

	public long getOvertimeTodayHours() {
		return overtimeTodayHours;
	}

	public long getOvertimeTodayMinutes() {
		return overtimeTodayMinutes;
	}

	public long getOvertimeWeekHours() {
		return overtimeWeekHours;
	}

	public long getOvertimeWeekMinutes() {
		return overtimeWeekMinutes;
	}

	public String toWeekString() {
		StringBuilder s = new StringBuilder();

		s.append("Week\n");
		s.append(getTwoDigits(weekHours)).append(":").append(getTwoDigits(weekMinutes)).append(" - worked\n");
		s.append(getTwoDigits(remainingWeekHours)).append(":").append(getTwoDigits(remainingWeekMinutes)).append(" - remaining\n");
		s.append(getTwoDigits(overtimeWeekHours)).append(":").append(getTwoDigits(overtimeWeekMinutes)).append(" - overtime");

		return s.toString();
	}

	public String toTodayString() {
		StringBuilder s = new StringBuilder();

		s.append("Today\n");
		s.append(getTwoDigits(todayHours)).append(":").append(getTwoDigits(todayMinutes)).append(" - worked\n");
		s.append(getTwoDigits(remainingTodayHours)).append(":").append(getTwoDigits(remainingTodayMinutes)).append(" - remaining\n");
		s.append(getTwoDigits(overtimeTodayHours)).append(":").append(getTwoDigits(overtimeTodayMinutes)).append(" - overtime\n");

		return s.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		// if (Main.getOs().equals("linux")) {
		// s.append("Today[");
		// s.append(getTwoDigits(todayHours)).append(":").append(getTwoDigits(todayMinutes)).append(" <> ");
		// s.append(getTwoDigits(remainingTodayHours)).append(":").append(getTwoDigits(remainingTodayMinutes))
		// .append("] Week[");
		// s.append(getTwoDigits(weekHours)).append(":").append(getTwoDigits(weekMinutes)).append(" <> ");
		// s.append(getTwoDigits(remainingWeekHours)).append(":").append(getTwoDigits(remainingWeekMinutes))
		// .append("]");
		// } else {
		s.append("Today\n");
		s.append(getTwoDigits(todayHours)).append(":").append(getTwoDigits(todayMinutes)).append(" - worked\n");
		s.append(getTwoDigits(remainingTodayHours)).append(":").append(getTwoDigits(remainingTodayMinutes)).append(" - remaining\n");
		s.append(getTwoDigits(overtimeTodayHours)).append(":").append(getTwoDigits(overtimeTodayMinutes)).append(" - overtime\n");
		s.append("Week\n");
		s.append(getTwoDigits(weekHours)).append(":").append(getTwoDigits(weekMinutes)).append(" - worked\n");
		s.append(getTwoDigits(remainingWeekHours)).append(":").append(getTwoDigits(remainingWeekMinutes)).append(" - remaining\n");
		s.append(getTwoDigits(overtimeWeekHours)).append(":").append(getTwoDigits(overtimeWeekMinutes)).append(" - overtime");
		// }
		return s.toString();
	}

	private String getTwoDigits(long number) {
		return number < 10 ? "0" + number : "" + number;
	}
}
