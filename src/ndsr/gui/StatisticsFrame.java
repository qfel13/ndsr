package ndsr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import ndsr.enums.StatType;

import org.jbundle.thin.base.screen.jcalendarbutton.JCalendarButton;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lkufel
 */
public class StatisticsFrame extends JFrame {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsFrame.class);
	private static final long serialVersionUID = -379577152202282273L;
	private static final int WINDOW_WIDTH = 600;
	private static final int WINDOW_HEIGHT = 300;
	private static final int CHART_WIDTH = 300;
	private static final int CHART_HEIGHT = 170;
	
	private JPanel chartsPanel;
	private JPanel optionsPanel;
	private ChartPanel dayChartPanel;
	private ChartPanel weekChartPanel;
	private JTable table;
	private JButton navPrevious = new JButton("<<");
	private JButton navNext = new JButton(">>");
	private JLabel navTitle = new JLabel("");
	private JLabel timeBankBalance;
	
	DefaultTableModel tableDataModel;
	private JLabel lastResetTime;
	private JCalendarButton calButton;

	/** Creates new form ChartFrame */
	public StatisticsFrame() {
		initComponents();
	}

	private void changePlot(JFreeChart chart) {
		CategoryPlot dayCategoryPlot = (CategoryPlot) chart.getPlot();

		dayCategoryPlot.setNoDataMessage("Not initialized yet.");
		CategoryItemRenderer renderer = dayCategoryPlot.getRenderer();
		renderer.setSeriesPaint(0, new Color(48, 128, 20));
		renderer.setSeriesPaint(1, new Color(255, 215, 0));
		renderer.setSeriesPaint(2, new Color(238, 0, 0));
	}

	private void initComponents() {
		setTitle("Statistics");
		setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
		contentPane.add(statsPanel);
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		
		initTable(statsPanel);
		initChartsPanel(statsPanel);
		initOptionsPanel();

		refresh();
		
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		setLocation(x, y);
	}
	
	private void initTable(Container contentPane) {
		
		// Navigation bar
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));
		subPanel.add(navPrevious, BorderLayout.WEST);
		subPanel.add(navTitle, BorderLayout.CENTER);
		subPanel.add(navNext, BorderLayout.EAST);
		contentPane.add(subPanel, BorderLayout.CENTER);
		
		// Table with data
		tableDataModel = new DefaultTableModel();
		table = new JTable(tableDataModel);
		table.setDefaultRenderer(Object.class, new StatisticsTableRenderer());
		contentPane.add(table.getTableHeader(), BorderLayout.PAGE_START);
		contentPane.add(table, BorderLayout.CENTER);
		table.setShowHorizontalLines(false);
		table.setEnabled(false);
		
		// Initialize the buttons
		navPrevious.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				LOG.debug("Previous clicked");
				Stats.getWeek().setPrevWeek();
				refresh();
			}
		});
		
		navNext.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				LOG.debug("Next clicked");
				Stats.getWeek().setNextWeek();
				refresh();
			}
		});
	}

	private Container initChartsPanel(Container contentPane) {
		// CHARTS
		chartsPanel = new JPanel();
		chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.X_AXIS));

		JFreeChart dayChart = prepareDayChart();
		JFreeChart weekChart = prepareWeekChart();

		Dimension chartDimension = new Dimension(CHART_WIDTH, CHART_HEIGHT);
		dayChartPanel = new ChartPanel(dayChart);
		dayChartPanel.setPreferredSize(chartDimension);
		weekChartPanel = new ChartPanel(weekChart);
		weekChartPanel.setPreferredSize(chartDimension);

		chartsPanel.add(dayChartPanel);
		chartsPanel.add(weekChartPanel);
		chartsPanel.add(optionsPanel);
		// ADD CHARTS
		contentPane.add(chartsPanel);
		return contentPane;
	}
	
	private JPanel getCenterBox(Container cont) {
		JPanel centerBox = new JPanel();
		centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.X_AXIS));
		centerBox.setBackground(Color.WHITE);
		
		centerBox.add(Box.createHorizontalGlue());
		centerBox.add(cont);
		centerBox.add(Box.createHorizontalGlue());
		return centerBox;
	}
	
	private void initOptionsPanel() {
		JPanel timeBankPanel = new JPanel();
		timeBankPanel.setLayout(new BoxLayout(timeBankPanel, BoxLayout.Y_AXIS));
		timeBankPanel.setBorder(BorderFactory.createTitledBorder("Time bank"));
		timeBankPanel.setBackground(Color.WHITE);
		
		timeBankBalance = new JLabel("+10:40");
		timeBankBalance.setFont(new Font("Serif", Font.BOLD, 20));
		
		timeBankPanel.add(Box.createVerticalGlue());
		timeBankPanel.add(getCenterBox(timeBankBalance));
		timeBankPanel.add(Box.createVerticalGlue());
		lastResetTime = new JLabel();
		JPanel resetTimeBox = new JPanel();
		resetTimeBox.setLayout(new BoxLayout(resetTimeBox, BoxLayout.X_AXIS));
		resetTimeBox.setBackground(Color.WHITE);
		resetTimeBox.add(lastResetTime, BorderLayout.WEST);
		calButton = new JCalendarButton();
		calButton.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getNewValue() instanceof Date) {
					Stats.getTimeBank().newResetTime((Date)arg0.getNewValue());
					refresh();
				}
			}
		});
		resetTimeBox.add(calButton);
		timeBankPanel.add(resetTimeBox);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		buttonsPanel.setBackground(Color.WHITE);

		// Initialize the buttons
		final JButton refreshB = new JButton("Refresh");
		refreshB.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				refreshB.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Stats.checkToday();
				Stats.getToday().refresh();
				Stats.getWeek().refresh();
				Stats.getTimeBank().refresh();
				refresh();
				refreshB.setCursor(Cursor.getDefaultCursor());
			}
		});
		
		buttonsPanel.add(getCenterBox(refreshB));
		
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBackground(Color.WHITE);
		optionsPanel.add(timeBankPanel);
		optionsPanel.add(Box.createVerticalGlue());
		
		optionsPanel.add(buttonsPanel);
	}

	private JFreeChart prepareDayChart() {
		DefaultCategoryDataset data = new DefaultCategoryDataset();

		double todayHours = (double)Stats.getToday().getTime() / 3600000;
		double remainingTodayHours = (double)Stats.getToday().getRemaining() / 3600000;
		double overtimeTodayHours = (double)Stats.getToday().getOvertime() / 3600000;
		if (overtimeTodayHours > 0 ) {
			todayHours = (double)Configuration.getInstance().getDailyWorkingTime() / 3600000;
		}

		data.addValue(todayHours, "Worked", "Today");
		data.addValue(remainingTodayHours, "Remaining", "Today");
		if (overtimeTodayHours > 0) {
			data.addValue(overtimeTodayHours, "Overtime", "Today");
		}

		JFreeChart dayChart = ChartFactory.createStackedBarChart3D("Today", null, null	, data,
				PlotOrientation.HORIZONTAL, true, true, false);

		changePlot(dayChart);

		return dayChart;
	}

	private JFreeChart prepareWeekChart() {
		DefaultCategoryDataset weekData = new DefaultCategoryDataset();

		double weekHours = (double)Stats.getWeek().getTime() / 3600000;
		double remainingWeekHours = (double)Stats.getWeek().getRemaining() / 3600000;
		double overtimeWeekHours = (double)Stats.getWeek().getOvertime() / 3600000;
		double vacationWeekHours = (double)Stats.getWeek().getVacation() / 3600000;
		
		if (Stats.getWeek().getOvertime() > 0 ) {
			weekHours = (double)Configuration.getInstance().getWeeklyWorkingTime() / 3600000;
			weekHours -= vacationWeekHours;
		}

		if  (vacationWeekHours > 0 ) { 
			weekData.addValue(vacationWeekHours, "Vacation", "Week");
		}
		if (weekHours > 0) {
			weekData.addValue(weekHours, "Worked", "Week");
		}
		if  (remainingWeekHours > 0 ) {
			weekData.addValue(remainingWeekHours, "Remaining", "Week");
		}
		if  (overtimeWeekHours > 0 ) {
			weekData.addValue(overtimeWeekHours, "Overtime", "Week");
		} 

		JFreeChart weekChart = ChartFactory.createStackedBarChart3D("Week", null, null, weekData,
				PlotOrientation.HORIZONTAL, true, true, false);

		CategoryPlot dayCategoryPlot = (CategoryPlot) weekChart.getPlot();

		dayCategoryPlot.setNoDataMessage("Not initialized yet.");
		CategoryItemRenderer renderer = dayCategoryPlot.getRenderer();
		int i = 0;
		if  (vacationWeekHours > 0 ) { 
			renderer.setSeriesPaint(i++, new Color(135, 206, 250));
		}
		if (weekHours > 0) {
			renderer.setSeriesPaint(i++, new Color(48, 128, 20));
		}
		if  (remainingWeekHours > 0 ) {
			renderer.setSeriesPaint(i++, new Color(255, 215, 0));
		}
		if  (overtimeWeekHours > 0 ) {
			renderer.setSeriesPaint(i++, new Color(238, 0, 0));
		} 
		
		return weekChart;
	}

	public void refresh() {
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
		if (!Stats.getWeek().workedDuringWeek()) {
			// don't show empty Saturday and Sunday
			days = 5;
		}
		Calendar cal = (Calendar)Stats.getWeek().getWeekBegin().clone();
		for (int q=0; q<days; q++) {
			cols.add(DayOfWeek.values()[q].toString() + " (" +  cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + ")");
			if (Stats.getWeek().getDay(q).isFreeDay() && Stats.getWeek().getDay(q).getTime() == 0) {
				data.get(0).add(Stats.getWeek().getDay(q).getTitle());
				data.get(1).add("");
				data.get(2).add("");
			} else {
				data.get(0).add(Stats.getWeek().getDay(q).toString(StatType.WORK));
				data.get(1).add(Stats.getWeek().getDay(q).toString(StatType.REMAIN));
				data.get(2).add(Stats.getWeek().getDay(q).toString(StatType.OVERTIME));
			}
			cal.add(Calendar.DATE, 1);
		}
		// Last column - week summary
		cols.add("Week");
		data.get(0).add(Stats.getWeek().toString(StatType.WORK));
		data.get(1).add(Stats.getWeek().toString(StatType.REMAIN));
		data.get(2).add(Stats.getWeek().toString(StatType.OVERTIME));
		tableDataModel.setDataVector(data, cols);

		// Center header of table
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		// Create the week title 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		navTitle.setText("     " + sdf.format(Stats.getWeek().getWeekBegin().getTime()) + " - " + sdf.format(Stats.getWeek().getWeekEnd().getTime()) + "     ");

		dayChartPanel.setChart(prepareDayChart());
		weekChartPanel.setChart(prepareWeekChart());
		
		// TimeBank
		long balance = Stats.getTimeBank().getBalance();
		// reset seconds
		balance = (balance / 60000) * 60000;
		timeBankBalance.setText(Stats.getFormatedTime(balance));
		if (balance > 0) {
			timeBankBalance.setForeground(new Color(48, 128, 20));
		} else if (balance < 0) {
			timeBankBalance.setForeground(Color.RED);
		} else {
			timeBankBalance.setForeground(Color.BLACK);
		}
		
		lastResetTime.setText("Last reset: " + new SimpleDateFormat("dd-MM-yyyy").format(Stats.getTimeBank().getTimeBankResetTime().getTime()));
		calButton.setTargetDate(Stats.getTimeBank().getTimeBankResetTime().getTime());
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
	        if (column > 0 && column < table.getColumnCount() - 1) {
	        	if (Stats.getWeek().getDay(column - 1).isVacation()) {
	        		setBackground(new Color(135, 206, 250));
	        	}
	        	if (Stats.getWeek().getDay(column - 1).isOtherHoliday()) {
	        		setBackground(new Color(135, 206, 250));
	        	}
	        }
	        if (Stats.getWeek().isCurrent()) {
	        	if ((Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7 == column - 1) {
	        		setBackground(Color.ORANGE);
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
