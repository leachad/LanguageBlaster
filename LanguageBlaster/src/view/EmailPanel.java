/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import model.Email;

/**
 * @author aleach
 *
 */
public class EmailPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String CARRIAGE_RETURN = "/r/n";
	private static final String DELIMITER = ",";
	private static final String EMAIL_STORAGE = "src/view/emailList.txt";

	/** Private field to hold a default dimension. */
	private static final int DEFAULT_WIDTH = 600;

	/** Private field to hold a default dimension. */
	private static final int DEFAULT_HEIGHT = 300;

	/** Private field to hold a height for textfield. */
	private static final int TEXTFIELD_HEIGHT = 30;

	/** Private field to hold the base panel. */
	private JPanel myBasePanel;

	/** Private field to hold a scrollpane reference. */
	private JScrollPane myScrollPane;

	/** Private field to hold an array of Email address objects. */
	private ArrayList<Email> myEmailList;

	public EmailPanel() {

		start();
		setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	/**
	 * Private method to start construction of the email panel. Reads all the
	 * names from a text file on the home directory. Also writes to the text
	 * file on the home directory.
	 */
	private void start() {

		myEmailList = new ArrayList<>();

		myBasePanel = new JPanel(new GridLayout(myEmailList.size(), 2));
		myBasePanel.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		addSaveButton();

		readEmailFile();
		displayEmails();
		setPanelLayout();

	}

	/**
	 * Private method to add a save button to the base panel.
	 */
	private void addSaveButton() {

		final JLabel direction = new JLabel();
		direction.setText("Press Enter if you change an Email String");
		add(direction);

		final JLabel dir2 = new JLabel();
		dir2.setText("Hit Save to Save the archive of email addresses");
		add(dir2);

		final JButton saveButton = new JButton("Save");
		add(saveButton);

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				writeEmailsToFile();

			}
		});
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
	 * Private method to read the emails from a text file. Format is:
	 * 
	 * SchoolName , SchoolEmails-SchoolEmails-SchoolEmails
	 */
	private void readEmailFile() {

		try {

			final BufferedReader br = new BufferedReader(new FileReader(
					new File("src/view/emailList.txt")));

			while (br.ready()) {

				final String line = br.readLine();
				myEmailList.add(parseLine(line, false));

			}
			br.close();

		} catch (FileNotFoundException f) {

			System.err.println("Sorry, couldn't find that file...");

		} catch (IOException e) {

			System.err.println("Sorry, couldn't find that file...");
		}

	}

	/**
	 * Private method to display the emails with their schools on the panel.
	 * TODO: Issue is happening in parseline
	 */
	private void displayEmails() {

		for (int i = 0; i < myEmailList.size(); i++) {
			final Email email = myEmailList.get(i);
			final JTextField showSchool = new JTextField(email.getSchool());
			showSchool.setEditable(false);
			showSchool.setPreferredSize(new Dimension(DEFAULT_WIDTH / 2,
					TEXTFIELD_HEIGHT));
			myBasePanel.add(showSchool);

			final JTextField emailField = new JTextField();
			emailField.setPreferredSize(new Dimension(DEFAULT_WIDTH / 2,
					TEXTFIELD_HEIGHT));
			myBasePanel.add(emailField);
			emailField.setText(email.toString());

			emailField.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent theEvent) {
					final String newEmail = emailField.getText();
					final String newEntry = showSchool.getText() + ", "
							+ newEmail;
					final Email newMail = parseLine(newEntry, true);
					replaceEmail(newMail);

					emailField.setText(newMail.toString());
				}
			});

		}

	}

	/**
	 * Private method to replace the email in the Map.
	 * 
	 * @param newMail
	 *            is the new Email object for rewriting out to the directory.
	 */

	private void replaceEmail(Email newMail) {

		for (int i = 0; i < myEmailList.size(); i++) {
			if (myEmailList.get(i).getSchool()
					.equalsIgnoreCase(newMail.getSchool())) {
				myEmailList.remove(i);
				myEmailList.add(newMail);
			}
		}
	}

	/**
	 * Private method to parse a new line from the email document.
	 * 
	 * @param theLine
	 *            is the string to parse.
	 * @param isOutgoing
	 *            is the boolean determining if the write is out bound or the
	 *            read is in bound. true is out bound.
	 */
	private Email parseLine(final String theLine, final boolean isOutgoing) {

		final String[] firstSplit = theLine.split(","); // split the line

		final String school = firstSplit[0].trim(); // save the school

		String[] emails = new String[firstSplit[1].split(";").length];
		if (!isOutgoing) {
			String[] secondSplit = firstSplit[1].split(";");
			for (int i = 0; i < secondSplit.length; i++) {
				emails[i] = secondSplit[i];
			}

		} else {
			String[] secondSplit = firstSplit[1].split(";");
			for (int i = 0; i < secondSplit.length; i++) {
				emails[i] = secondSplit[i];
			}

		}

		return new Email(school, emails);

	}

	/**
	 * Private method to write all the new emails out to the text file that the
	 * program initially reads from.
	 */
	private void writeEmailsToFile() {
		FileWriter out = null;
		
		try {
			out = new FileWriter(new File(EMAIL_STORAGE));
			for (int i = 0; i < myEmailList.size(); i++) {
				out.write(myEmailList.get(i).getSchool().concat(DELIMITER)
						.concat(myEmailList.get(i).toString())
						+ CARRIAGE_RETURN);
			}
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Public method to get the list of emails from the email panel.
	 * 
	 * @return myEmailList is the map of all email address with schoolName as
	 *         the key.
	 */
	public ArrayList<Email> getEmails() {

		return myEmailList;
	}

}
