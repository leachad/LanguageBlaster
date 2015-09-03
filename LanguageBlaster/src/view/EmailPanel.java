/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import model.Email;
import resources.School;
import file_system.LocalStorage;

/**
 * @author aleach
 *
 */
public class EmailPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Private field to hold a default dimension. */
	private static final int DEFAULT_WIDTH = 600;

	/** Private field to hold a default dimension. */
	private static final int DEFAULT_HEIGHT = 300;

	/** Private field to hold a height for textfield. */
	private static final int TEXTFIELD_HEIGHT = 30;

	/** Private field to hold a number of columns. */
	private static final int NUM_COLUMNS = 20;

	/** Private field to hold the base panel. */
	private JPanel myBasePanel;

	/** Private field to hold a scrollpane reference. */
	private JScrollPane myScrollPane;

	/** Private field to hold an array of Email address objects. */
	private Map<String, Email> myEmailMap;

	/**
	 * Private field to hold a reference to the List of Text Fields showing
	 * Emails.
	 */
	private Map<String, JTextField> myFieldMap;

	/** Private field to hold a reference to the base frame. */
	private JFrame myBaseFrame;

	public EmailPanel(final JFrame theBaseFrame) {

		myBaseFrame = theBaseFrame;
		myFieldMap = new HashMap<>();
		start();
		setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	/**
	 * Private method to start construction of the email panel. Reads all the
	 * names from a text file on the home directory. Also writes to the text
	 * file on the home directory.
	 */
	private void start() {

		myEmailMap = LocalStorage.getEmailMap();

		add(addSaveButton());
		add(addNewSchoolButton());
		add(displaySavedEmails());
		setPanelLayout();

	}

	/**
	 * Private method to add a save button to the base panel.
	 */
	private JPanel addSaveButton() {

		final JPanel savePanel = new JPanel();
		final JLabel direction = new JLabel();
		direction.setText("Separate Multiple Emails with a ';'");
		direction.setOpaque(true);
		direction.setForeground(Color.red);

		savePanel.add(direction);

		final JButton saveButton = new JButton("Save");
		savePanel.add(saveButton);

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				writeEmailsToFile();
				myBaseFrame.dispose();

			}
		});

		return savePanel;
	}

	private JPanel createSchoolEmailRow(final Email theEmail) {
		final JPanel rowPanel = new JPanel();

		final JTextField showSchool = new JTextField(theEmail.getSchool());
		showSchool.setEditable(false);
		showSchool.setColumns(NUM_COLUMNS);
		rowPanel.add(showSchool);

		final JTextField emailField = new JTextField();
		emailField.setPreferredSize(new Dimension(DEFAULT_WIDTH / 2,
				TEXTFIELD_HEIGHT));
		rowPanel.add(emailField);
		emailField.setText(theEmail.toString());

		final JButton deleteButton = new JButton("X");
		deleteButton.setOpaque(true);
		deleteButton.setForeground(Color.RED);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {

				int selection = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to delete this Center School?");
				if (selection == JOptionPane.OK_OPTION) {
					myEmailMap.remove(theEmail.getSchool());
					myFieldMap.remove(theEmail.getSchool());
					myBasePanel.remove(rowPanel);
					myBasePanel.revalidate();
					myBasePanel.repaint();
					
				}

			}
		});
		rowPanel.add(deleteButton);
		myFieldMap.put(theEmail.getSchool(), emailField);

		return rowPanel;
	}

	/**
	 * Private method to add a New School button to the base panel.
	 */
	private JPanel addNewSchoolButton() {

		final JPanel schoolPanel = new JPanel();

		final JButton addButton = new JButton("Add Program School");
		schoolPanel.add(addButton);

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {
				JLabel schoolLabel = new JLabel(
						"Please Choose the Name of the Center School");
				JComboBox<School> schoolName = new JComboBox<School>(
						getUnusedSchools(School.values()));
				JLabel emailLabel = new JLabel(
						"Please Enter the Emails separated by ';' ");
				emailLabel.setOpaque(true);
				emailLabel.setForeground(Color.red);
				JTextField schoolEmails = new JTextField();

				Object[] message = { schoolLabel, schoolName, emailLabel,
						schoolEmails };
				int option = JOptionPane
						.showConfirmDialog(null, message,
								"Add Center School Email",
								JOptionPane.OK_CANCEL_OPTION);

				if (option == JOptionPane.OK_OPTION) {
					myBasePanel.add(createSchoolEmailRow(new Email(
							((School) schoolName.getSelectedItem()).text,
							buildEmailArray(schoolEmails))));
					myBasePanel.revalidate();
					myBasePanel.repaint();
				}
			}
		});

		return schoolPanel;
	}

	private String[] buildEmailArray(final JTextField theSchoolEmails) {
		return theSchoolEmails.getText().split(";");
	}

	private School[] getUnusedSchools(final School[] allSchools) {
		List<School> schools = new LinkedList<>();

		for (int i = 0; i < allSchools.length; i++) {
			if (myFieldMap.get(allSchools[i].text) == null)
				schools.add(allSchools[i]);
		}

		return schools.toArray(new School[schools.size()]);
	}

	/**
	 * Private method to set the panel layout of the email panel for display and
	 * editing purposes. Probably should be in a grid layout to best display and
	 * mount on a scrollpane to achieve expandability.
	 */
	private void setPanelLayout() {

		myScrollPane = new JScrollPane(myBasePanel);
		myScrollPane.setPreferredSize(new Dimension(myBasePanel.getWidth(),
				DEFAULT_HEIGHT));
		myScrollPane.setVisible(true);

		add(myScrollPane, BorderLayout.CENTER);

	}

	/**
	 * Private method to display the emails with their schools on the panel.
	 * TODO: Issue is happening in parseline
	 */
	private JPanel displaySavedEmails() {

		myBasePanel = new JPanel(new GridLayout(0, 1));
		myBasePanel.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		Iterator<String> keySet = myEmailMap.keySet().iterator();

		while (keySet.hasNext()) {
			Email curEmail = myEmailMap.get(keySet.next());
			System.out.println("Adding Email " + curEmail.getSchool() + "--"
					+ curEmail.toString());
			myBasePanel.add(createSchoolEmailRow(curEmail));

		}

		return myBasePanel;

	}

	/**
	 * Private method to write all the new emails out to the text file that the
	 * program initially reads from.
	 */
	private void writeEmailsToFile() {
		LocalStorage.saveEmailState(modifyEmailMap());

	}

	/**
	 * Private method to modify the email map based on the display.
	 */
	private Map<String, Email> modifyEmailMap() {
		Iterator<String> fieldIterator = myFieldMap.keySet().iterator();
		while (fieldIterator.hasNext()) {
			String key = fieldIterator.next();
			myEmailMap.put(key, buildEmail(key, myFieldMap.get(key)));

		}

		return myEmailMap;
	}

	private Email buildEmail(final String theSchoolName,
			final JTextField theInput) {
		return new Email(theSchoolName, theInput.getText().trim().split(";"));
	}

	/**
	 * Public method to get the list of emails from the email panel.
	 * 
	 * @return myEmailList is the map of all email address with schoolName as
	 *         the key.
	 */
	public Map<String, Email> getEmails() {

		return myEmailMap;
	}

}
