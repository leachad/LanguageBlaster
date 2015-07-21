/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import resources.BlasterCalendar;

/**
 * @author aleach
 *
 */
public class LBDate {

	/** Fields to hold the current Month Day and Year.*/
	public static String myCurrentMonth;
	public static String myCurrentDay;
	public static String myCurrentYear;

	/**
	 * Private method to add the date picker widgets to the frame.
	 */
	public static JPanel addDatePickerWidgets() {
		final JPanel topPanel = new JPanel(new GridLayout(1, 3));

		final JLabel displayLabel = new JLabel("OUTPUT FILE PATH");
		displayLabel.setBackground(Color.gray);

		final JComboBox<String> monthBox = new JComboBox<>(BlasterCalendar.COUNT_MONTHS);
		monthBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				myCurrentMonth = (String) theEvent.getItem();

			}

		});
		monthBox.setSelectedIndex(0);

		final JComboBox<String> dayBox = new JComboBox<>(BlasterCalendar.COUNT_DAYS);
		dayBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				myCurrentDay = (String) theEvent.getItem();

			}
		});
		dayBox.setSelectedIndex(0);

		final JComboBox<String> yearBox = new JComboBox<>(BlasterCalendar.COUNT_YEARS);
		yearBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				myCurrentYear = (String) theEvent.getItem();

			}

		});
		yearBox.setSelectedIndex(0);

		if (dayBox.getSelectedIndex() == 0) {

			myCurrentDay = "1";
		}

		if (monthBox.getSelectedIndex() == 0) {

			SimpleDateFormat formatter = new SimpleDateFormat("MM");
			myCurrentMonth = formatter.format(new Date());
		}

		if (yearBox.getSelectedIndex() == 0) {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			myCurrentYear = formatter.format(new Date());
		}

		topPanel.add(displayLabel);
		topPanel.add(monthBox);
		topPanel.add(dayBox);
		topPanel.add(yearBox);

		return topPanel;
	}
}
