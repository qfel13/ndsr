package ndsr.trash;

import java.util.Calendar;

public class DayOfYearToDayOfWeekExample {

	public static void main(String[] args) {
		// Create a calendar with year and day of year.
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 2);

		// See the full information of the calendar object.
		System.out.println(calendar.getTime().toString());

		// Get the weekday and print it
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println("Weekday: " + weekday);
	}
}
