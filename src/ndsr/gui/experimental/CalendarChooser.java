package ndsr.gui.experimental;

import java.io.IOException;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import ndsr.CalendarHandler;
import ndsr.Configuration;

import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.util.ServiceException;

/**
 *
 * @author lkufel
 */
public class CalendarChooser extends JFrame {

	private static final long serialVersionUID = -7787914416668235397L;

	private JComboBox calendarsCombobox;
	private JButton jButton1;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JRadioButton jRadioButton1;
	private JRadioButton jRadioButton2;
	private JTextField jTextField1;
	private JTextField jTextField2;
	private ButtonGroup useGroup;

	/** Creates new form CalendarChooser */
	public CalendarChooser() {
		initComponents();
	}

	public CalendarChooser(Configuration configuration, CalendarHandler calendarHandler) {
		initComponents();
		
		try {
			Vector<String> v = new Vector<String>();
			for (CalendarEntry e : calendarHandler.getCalendars()) {
				v.add(e.getTitle().getPlainText());
			}
			calendarsCombobox.setModel(new DefaultComboBoxModel(v));
		} catch (IOException ex) {

		} catch (ServiceException ex) {

		}
	}

	private void initComponents() {

		useGroup = new ButtonGroup();
		calendarsCombobox = new JComboBox();
		jRadioButton1 = new JRadioButton();
		jRadioButton2 = new JRadioButton();
		jLabel1 = new JLabel();
		jTextField1 = new JTextField();
		jTextField2 = new JTextField();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jButton1 = new JButton();

		useGroup.add(jRadioButton1);
		jRadioButton1.setSelected(true);
		jRadioButton1.setText("use existing calendar");

		useGroup.add(jRadioButton2);
		jRadioButton2.setText("create new calendar");

		jLabel1.setText("Choose calendar");

		jLabel2.setText("Name");

		jLabel3.setText("Summary");

		jButton1.setText("ok");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jRadioButton1)
							.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addGap(21, 21, 21)
								.addComponent(jLabel1)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(calendarsCombobox, 0, 94, Short.MAX_VALUE)
								.addGap(14, 14, 14)))
						.addContainerGap())
					.addComponent(jRadioButton2)
					.addGroup(layout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
							.addComponent(jLabel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(jLabel3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jTextField1, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
							.addComponent(jTextField2, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
							.addGroup(layout.createSequentialGroup()
								.addGap(10, 10, 10)
								.addComponent(jButton1)))
						.addContainerGap())))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jRadioButton1)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jLabel1)
					.addComponent(calendarsCombobox))
				.addGap(18, 18, 18)
				.addComponent(jRadioButton2)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
					.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jLabel3)
					.addComponent(jTextField2))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jButton1)
				.addContainerGap())
		);
	}
	
}
