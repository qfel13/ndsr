/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package run;

import com.google.gdata.data.calendar.CalendarEntry;
import java.io.File;
import java.net.InetAddress;
import java.util.List;
import ndsr.CalendarHandler;
import ndsr.Configuration;
import ndsr.gui.CalendarChooser;

/**
 *
 * @author lkufel
 */
public class Main {

	public Main() {
	}
	public static void main(String[] args) {
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			InetAddress[] ips = InetAddress.getAllByName("localhost");
			for (InetAddress ip : ips) {
				System.out.println("localhost IP: " + ip.getHostAddress());
			}
			System.out.println("IP: " + thisIp.getHostAddress());
			System.out.println(thisIp.getHostAddress().matches("^8.*$"));
//			System.out.println("127.0.0.1".matches("127.0.0.1"));

			Configuration config = new Configuration();
			config.readConfiguration("C:\\Program Files\\ndsr\\passwd.properties");
//
			CalendarHandler handler = new CalendarHandler(config);
//			List<CalendarEntry> calendars = handler.getCalendars();
//			for (CalendarEntry calendar : calendars) {
//				System.out.println("title = " + calendar.getTitle().getPlainText());
//				System.out.println("summary = " + calendar.getSummary().getPlainText());
////				System.out.println("hidden = " + calendar.getHidden().getValue());
////				System.out.println("color = " + calendar.getColor().getValue());
////				System.out.println("locations.size = " + calendar.getLocations().size());
////				System.out.println("locations.get(0) = " + calendar.getLocations().get(0).getValueString());
////				System.out.println("timezone = " + calendar.getTimeZone().getValue());
//				System.out.println("=============================");
//			}
//			CalendarEntry cal = handler.createNewCalendar("nowy kalendarz", "ciekawe summary");
//			System.out.println("cal = " + cal.getTitle().getPlainText());
//
//			calendars = handler.getCalendars();
//			for (CalendarEntry calendar : calendars) {
//				System.out.println("title = " + calendar.getTitle().getPlainText());
//				System.out.println("summary = " + calendar.getSummary().getPlainText());
//				System.out.println("=============================");
//			}

			CalendarChooser calendarChooser = new CalendarChooser(config, handler);
			calendarChooser.setVisible(true);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
