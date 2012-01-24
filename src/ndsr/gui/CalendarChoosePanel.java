package ndsr.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import ndsr.CalendarHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.calendar.model.CalendarListEntry;

public class CalendarChoosePanel extends CardChildPanel {
	private static final long serialVersionUID = 661830924278829542L;
	private static final Logger LOG = LoggerFactory.getLogger(CalendarChoosePanel.class);
	
	private JTextField newCalendarNameText;
	private JComboBox calendarsCombo;
	private JLabel existingCalendarNameLabel;
	private JRadioButton useExistingRadio;
	private JRadioButton createNewRadio;
	private JTextArea newCalendarDescriptionText;
	private JButton backButton;
	private JButton nextButton;
	private JLabel newCalendarNameLabel;
	private JLabel newCalendarDescriptionLabel;
	private ButtonGroup buttonGroup;
	
	private final WelcomeFrame welcomeFrame;
	private final CalendarHelper calendarHelper;
	
	private HashMap<String, String> calendarNameIdMap = new HashMap<String, String>();
	
	public CalendarChoosePanel(WelcomeFrame w, CalendarHelper c) {
		welcomeFrame = w;
		calendarHelper = c;
		
		setSize(new Dimension(450, 300));
		
		buttonGroup = new ButtonGroup();
		// radio
		useExistingRadio = new JRadioButton("Use existing calendar");
		createNewRadio = new JRadioButton("Create new one (NOT IMPLEMENTED YET. STAY TUNNED)");
		createNewRadio.setEnabled(false);
		// combo
		calendarsCombo = new JComboBox();
		// label
		existingCalendarNameLabel = new JLabel("Name");
		newCalendarNameLabel = new JLabel("Name");
		newCalendarNameLabel.setEnabled(false);
		newCalendarDescriptionLabel = new JLabel("Description");
		newCalendarDescriptionLabel.setEnabled(false);
		// text
		newCalendarNameText = new JTextField();
		newCalendarNameText.setEnabled(false);
		newCalendarDescriptionText = new JTextArea();
		newCalendarDescriptionText.setEnabled(false);
		// button
		nextButton = new JButton("Next");
		nextButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String name = (String) calendarsCombo.getSelectedItem();
				String calendarId = calendarNameIdMap.get(name);
				calendarHelper.setCalendar(calendarId);
				welcomeFrame.showNextPanel();
				
			}
		});
		backButton = new JButton("Back");
		backButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				calendarHandler.reset();
				welcomeFrame.showPreviousPanel();
			}
		});
		
		buttonGroup.add(useExistingRadio);
		buttonGroup.add(createNewRadio);
		
		useExistingRadio.setSelected(true);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(createNewRadio)
							.addContainerGap())
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(useExistingRadio)
								.addContainerGap())
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
									.addGroup(groupLayout.createSequentialGroup()
										.addGap(21)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
											.addComponent(newCalendarDescriptionLabel, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
											.addComponent(newCalendarNameLabel, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
										.addGap(18)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
											.addComponent(newCalendarNameText)
											.addComponent(newCalendarDescriptionText, GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)))
									.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
										.addComponent(backButton)
										.addPreferredGap(ComponentPlacement.RELATED, 316, Short.MAX_VALUE)
										.addComponent(nextButton))
									.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
										.addGap(21)
										.addComponent(existingCalendarNameLabel, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(calendarsCombo, 0, 271, Short.MAX_VALUE)))
								.addGap(14)))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(useExistingRadio)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(existingCalendarNameLabel)
						.addComponent(calendarsCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(29)
					.addComponent(createNewRadio)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(newCalendarNameText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(newCalendarNameLabel))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(newCalendarDescriptionText, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
						.addComponent(newCalendarDescriptionLabel))
					.addPreferredGap(ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(nextButton)
						.addComponent(backButton))
					.addContainerGap())
		);
		setLayout(groupLayout);
	}

	
	@Override
	public void refresh() {
		initComponentsValues();
		useExistingRadio.requestFocusInWindow();
	}


	private void initComponentsValues() {
		try {
			Vector<String> items = new Vector<String>();
			if (calendarHelper != null) {
				List<CalendarListEntry> calendarList = calendarHelper.getCalendarList();
				for (CalendarListEntry entry : calendarList) {
					String name = entry.getSummary();
					String id = entry.getId();
					LOG.debug("summary = {}", name);
					LOG.debug("id = {}", id);
					calendarNameIdMap.put(name, id);
					items.add(name);
				}
				DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(items);
				calendarsCombo.setModel(comboBoxModel);
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
	}
	
	
}
