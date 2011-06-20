package ndsr.chart;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author lkufel
 */
public class StatisticsFrame extends JFrame {

	private static final long serialVersionUID = -379577152202282273L;
	
	private JPanel textPanel;
	private JPanel chartsPanel;
	private ChartPanel dayChartPanel;
	private ChartPanel weekChartPanel;
	
	/** Creates new form ChartFrame */
	public StatisticsFrame(JFreeChart dayChart, JFreeChart weekChart) {
		initComponents(dayChart, weekChart);
	}

	private void initComponents(JFreeChart dayChart, JFreeChart weekChart) {
		setTitle("Statistics");
		textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		
		chartsPanel = new JPanel();
		chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.X_AXIS));
		
		// TEXT
		
		textPanel.add(new JLabel("Tutaj beda staty"));
		
		// CHARTS
		Dimension chartDimension = new Dimension(340, 210);
		dayChartPanel = new ChartPanel(dayChart);
		dayChartPanel.setPreferredSize(chartDimension);
		weekChartPanel = new ChartPanel(weekChart);
		weekChartPanel.setPreferredSize(chartDimension);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		chartsPanel.add(dayChartPanel);
		chartsPanel.add(weekChartPanel);
		
		// ADD TEXT
		contentPane.add(textPanel);
		// ADD CHARTS
		contentPane.add(chartsPanel);

		pack();
	}
}
