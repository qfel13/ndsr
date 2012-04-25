package ndsr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import ndsr.Configuration;
import ndsr.beans.Stats;
import ndsr.beans.Stats.StatType;
import ndsr.calendar.CalendarHelper;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.calendar.model.Event;

/**
 * @author lkufel
 */
public class StatisticsFrame extends JFrame {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsFrame.class);
	private static final long serialVersionUID = -379577152202282273L;

	private JPanel topPanel;
	private JPanel chartsPanel;
	private ChartPanel dayChartPanel;
	private ChartPanel weekChartPanel;
	private JTable table;
	private JButton navPrevious = new JButton("<<");
	private JButton navNext = new JButton(">>");
	private JLabel navTitle = new JLabel("");
	private Stats stats;
	private Stats currentWeekStats;
	private Configuration configuration;
	
	
	DefaultTableModel tableDataModel;

	/** Creates new form ChartFrame */
	public StatisticsFrame(Stats stats, Configuration configuration) {
		this.configuration = configuration;
		initComponents(stats);
	}

	private void changePlot(JFreeChart chart) {
		CategoryPlot dayCategoryPlot = (CategoryPlot) chart.getPlot();

		dayCategoryPlot.setNoDataMessage("Not initialized yet.");
		CategoryItemRenderer renderer = dayCategoryPlot.getRenderer();
		renderer.setSeriesPaint(0, new Color(48, 128, 20));
		renderer.setSeriesPaint(1, new Color(255, 215, 0));
		renderer.setSeriesPaint(2, new Color(238, 0, 0));
	}

	private void initComponents(Stats stats) {
		setTitle("Statistics");
		setMinimumSize(new Dimension(700, 320));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		initTable(contentPane, stats);
		initChartsPanel(contentPane, stats);

		refreshStats(stats);
		
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		setLocation(x, y);
	}
	
	private void initTable(Container contentPane, Stats stats) {
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		
		// Navigation bar
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
		subPanel.add(navPrevious, BorderLayout.WEST);
		subPanel.add(navTitle, BorderLayout.CENTER);
		subPanel.add(navNext, BorderLayout.EAST);
		topPanel.add(subPanel, BorderLayout.CENTER);
		
		// Table with data
		tableDataModel = new DefaultTableModel();
		table = new JTable(tableDataModel);
		table.setDefaultRenderer(Object.class, new StatisticsTableRenderer());
		topPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		topPanel.add(table, BorderLayout.CENTER);
		table.setShowHorizontalLines(false);
		table.setEnabled(false);
		contentPane.add(topPanel);
		
		// Initialize the buttons
		navPrevious.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				LOG.debug("Previous clicked");
				Calendar start = StatisticsFrame.this.stats.getWeekBegin();
				Calendar end = (Calendar)start.clone();
				start.add(Calendar.DAY_OF_MONTH, -7);
				Stats stats = new Stats(configuration);
				try {
					List<Event> eventList = CalendarHelper.getEvents(start, end);
					stats.setWeekDays(CalendarHelper.getWeekDays(eventList), start);
				} catch (IOException e) {
					LOG.error("Got IOException");
				}
				refreshStats(stats);
			}
		});
		
		navNext.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				LOG.debug("Next clicked");
				Calendar start = (Calendar)StatisticsFrame.this.stats.getWeekBegin().clone();
				start.add(Calendar.DAY_OF_MONTH, 7);
				Calendar end = (Calendar)start.clone();
				end.add(Calendar.DAY_OF_MONTH, 7);
				Stats stats = new Stats(configuration);
				List<Event> eventList;
				try {
					eventList = CalendarHelper.getEvents(start, end);
					stats.setWeekDays(CalendarHelper.getWeekDays(eventList), start);
				} catch (IOException e) {
					LOG.error("Got IOException");
					eventList = new ArrayList<Event>();
				}
				refreshStats(stats);
			}
		});
	}

	private Container initChartsPanel(Container contentPane, Stats stats) {
		// CHARTS
		chartsPanel = new JPanel();
		chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.X_AXIS));

		JFreeChart dayChart = prepareDayChart();
		JFreeChart weekChart = prepareWeekChart(stats);

		changePlot(dayChart);
		changePlot(weekChart);

		Dimension chartDimension = new Dimension(340, 210);
		dayChartPanel = new ChartPanel(dayChart);
		dayChartPanel.setPreferredSize(chartDimension);
		weekChartPanel = new ChartPanel(weekChart);
		weekChartPanel.setPreferredSize(chartDimension);

		chartsPanel.add(dayChartPanel);
		chartsPanel.add(weekChartPanel);
		// ADD CHARTS
		contentPane.add(chartsPanel);
		return contentPane;
	}

	private JFreeChart prepareDayChart() {
		DefaultCategoryDataset data = new DefaultCategoryDataset();

		if (currentWeekStats != null) {
			double todayHours = (double)currentWeekStats.getToday() / 3600000;
			double remainingTodayHours = (double)currentWeekStats.getRemainingToday() / 3600000;
			double overtimeTodayHours = (double)currentWeekStats.getOvertimeToday() / 3600000;
			if (overtimeTodayHours > 0 ) {
				todayHours = (double)currentWeekStats.oneDayTime / 3600000;
			}
			
			data.addValue(todayHours, "Worked Hours", "Today");
			data.addValue(remainingTodayHours, "Remaining Hours", "Today");
			data.addValue(overtimeTodayHours, "Overtime Hours", "Today");
		}

		JFreeChart dayChart = ChartFactory.createStackedBarChart3D("Today", null, "Hours", data,
				PlotOrientation.HORIZONTAL, true, true, false);

		changePlot(dayChart);

		return dayChart;
	}

	private JFreeChart prepareWeekChart(Stats stats) {
		DefaultCategoryDataset weekData = new DefaultCategoryDataset();

		if (stats != null) {
			double weekHours = (double)stats.getWeek() / 3600000;
			double remainingWeekHours = (double)stats.getRemainingWeek() / 3600000;
			double overtimeWeekHours = (double)stats.getOvertimeWeek() / 3600000;
			if (overtimeWeekHours > 0 ) {
				weekHours = (double)stats.oneWeekTime / 3600000;
			}
			
			weekData.addValue(weekHours, "Worked Hours", "Week");
			weekData.addValue(remainingWeekHours, "Remaining Hours", "Week");
			weekData.addValue(overtimeWeekHours, "Overtime Hours", "Week");
		}
		JFreeChart weekChart = ChartFactory.createStackedBarChart3D("Week", null, "Hours", weekData,
				PlotOrientation.HORIZONTAL, true, true, false);

		changePlot(weekChart);

		return weekChart;
	}

	public void refreshStats(Stats newStats) {
		if (newStats == null) {
			LOG.debug("Stats are null");
			return;
		}
		
		if (newStats.getToday() > 0) {
			this.currentWeekStats = newStats;
			this.stats = newStats;
		} 
		stats = newStats;
		
		Vector<String> cols = new Vector<String>();
		cols.add("");
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		data.add(new Vector<String>());
		data.add(new Vector<String>());
		data.add(new Vector<String>());
		// First column
		data.get(0).add("Working");
		data.get(1).add("Remaining");
		data.get(2).add("Overtime");
		int days = 7;
		if (stats.getWeekDays()[5] == 0 && stats.getWeekDays()[6] == 0) {
			// don't show empty Saturday and Sunday
			days = 5;
		}
		Calendar cal = (Calendar)stats.getWeekBegin().clone();
		if (stats.getWeekDays() != null) {
			for (int q=0; q<days; q++) {
				cols.add(DayOfWeek.values()[q].toString() + " (" +  cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + ")");
				data.get(0).add(Stats.getFormatedTime(stats.getWeekDays()[q], StatType.Normal, stats.oneDayTime));
				data.get(1).add(Stats.getFormatedTime(stats.getWeekDays()[q], StatType.Remaining, stats.oneDayTime));
				data.get(2).add(Stats.getFormatedTime(stats.getWeekDays()[q], StatType.Overtime, stats.oneDayTime));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		// Last column - week summary
		cols.add("Week");
		data.get(0).add(Stats.getFormatedTime(stats.getWeek(), StatType.Normal, stats.oneWeekTime));
		data.get(1).add(Stats.getFormatedTime(stats.getWeek(), StatType.Remaining, stats.oneWeekTime));
		data.get(2).add(Stats.getFormatedTime(stats.getWeek(), StatType.Overtime, stats.oneWeekTime));
		tableDataModel.setDataVector(data, cols);

		// Center header of table
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		// Create the week title 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		navTitle.setText("     " + sdf.format(stats.getWeekBegin().getTime()) + " - " + sdf.format(stats.getWeekEnd().getTime()) + "     ");

		dayChartPanel.setChart(prepareDayChart());
		weekChartPanel.setChart(prepareWeekChart(stats));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper classes
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	class StatisticsTableRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -5508577466216014240L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	    {
	        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if (column == 0) {
	        	setHorizontalAlignment(JLabel.RIGHT);
	        } else {
	        	setHorizontalAlignment(JLabel.CENTER);
	        }
	        setBackground(Color.WHITE);
	        Calendar cal = Calendar.getInstance();
	        Calendar weekBegin = stats.getWeekBegin();
	        Calendar weekEnd = (Calendar)stats.getWeekBegin().clone();
	        weekEnd.add(Calendar.DAY_OF_MONTH, 7);
	        if (weekBegin.before(cal) && weekEnd.after(cal)) {
	        	if ((cal.get(java.util.Calendar.DAY_OF_WEEK) - 2) % 7 == column - 1) {
	        		setBackground(new Color(135, 206, 250));
	        	}
	        }
	        if (column == table.getColumnCount() - 1) { 
	        		setBackground(Color.LIGHT_GRAY);
	        }
	        return this;
	    }
	}
	
	class NavigationTableRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 7815753150240542247L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	    {
	        setHorizontalAlignment(JLabel.CENTER);
			if (value instanceof JButton) {
				return (Component)value;
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    }
	}
	
	// Day of week - just for printing purposes
	enum DayOfWeek {
		Mon,
		Tue,
		Wed,
		Thu,
		Fri,
		Sat,
		Sun;
	}
}
