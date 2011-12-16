package ndsr.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ndsr.beans.Stats;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author lkufel
 */
public class StatisticsFrame extends JFrame {

	private static final long serialVersionUID = -379577152202282273L;
	private static final int DEFAULT_INDENT = 60;

	private JPanel textPanel;
	private JPanel chartsPanel;
	private ChartPanel dayChartPanel;
	private ChartPanel weekChartPanel;
	private JTextArea txtrDayTextArea;
	private JTextArea txtrWeekTextArea;

	/** Creates new form ChartFrame */
	public StatisticsFrame(Stats stats) {
		initComponents(stats);
	}

	private void changePlot(JFreeChart chart) {
		CategoryPlot dayCategoryPlot = (CategoryPlot) chart.getPlot();

		dayCategoryPlot.setNoDataMessage("Not initialized yet.");
		CategoryItemRenderer renderer = dayCategoryPlot.getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesPaint(2, Color.yellow);
	}

	private void initComponents(Stats stats) {
		setTitle("Statistics");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		initTextPanel(contentPane, stats);
		initChartsPanel(contentPane, stats);

		refreshStats(stats);
		
		pack();
	}

	private void initTextPanel(Container contentPane, Stats stats) {
		// TEXT
		textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.LINE_AXIS));

		txtrDayTextArea = new JTextArea();
		txtrDayTextArea.setMargin(new Insets(0, DEFAULT_INDENT, 0, DEFAULT_INDENT));
		txtrDayTextArea.setEditable(false);
		txtrDayTextArea.setLineWrap(true);
		txtrDayTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textPanel.add(txtrDayTextArea);

		txtrWeekTextArea = new JTextArea();
		txtrWeekTextArea.setMargin(new Insets(0, DEFAULT_INDENT, 0, DEFAULT_INDENT));
		txtrWeekTextArea.setEditable(false);
		txtrWeekTextArea.setLineWrap(true);
		txtrWeekTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textPanel.add(txtrWeekTextArea);
		// ADD TEXT PANEL
		contentPane.add(textPanel);
	}

	private Container initChartsPanel(Container contentPane, Stats stats) {
		// CHARTS
		chartsPanel = new JPanel();
		chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.X_AXIS));

		JFreeChart dayChart = prepareDayChart(stats);
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

	private JFreeChart prepareDayChart(Stats stats) {
		DefaultCategoryDataset data = new DefaultCategoryDataset();

		if (stats != null) {
			double todayHours = stats.getTodayHours() + stats.getTodayMinutes() / 60.0;
			double remainingTodayHours = stats.getRemainingTodayHours() + stats.getRemainingTodayMinutes() / 60.0;
			data.addValue(todayHours, "Worked Hours", "Today");
			data.addValue(remainingTodayHours, "Remaining Hours", "Today");
		}

		JFreeChart dayChart = ChartFactory.createStackedBarChart3D("Today", null, "Hours", data,
				PlotOrientation.HORIZONTAL, true, true, false);

		changePlot(dayChart);

		return dayChart;
	}

	private JFreeChart prepareWeekChart(Stats stats) {
		DefaultCategoryDataset weekData = new DefaultCategoryDataset();

		if (stats != null) {
			double weekHours = stats.getWeekHours() + stats.getWeekMinutes() / 60.0;
			double weekTodayHours = stats.getRemainingWeekHours() + stats.getRemainingWeekMinutes() / 60.0;
			weekData.addValue(weekHours, "Worked Hours", "Week");
			weekData.addValue(weekTodayHours, "Remaining Hours", "Week");
		}
		JFreeChart weekChart = ChartFactory.createStackedBarChart3D("Week", null, "Hours", weekData,
				PlotOrientation.HORIZONTAL, true, true, false);

		changePlot(weekChart);

		return weekChart;
	}

	public void refreshStats(Stats stats) {
		String todayStats = "";
		String weekStats = "";
		if (stats != null) {
			todayStats = stats.toTodayString();
			weekStats = stats.toWeekString();
		}

		txtrDayTextArea.setText(todayStats);
		txtrWeekTextArea.setText(weekStats);
		
		dayChartPanel.setChart(prepareDayChart(stats));
		weekChartPanel.setChart(prepareWeekChart(stats));
	}
}
