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
import java.io.IOException;
import java.io.PrintWriter;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Private field to hold a default dimension.*/
	private static final int DEFAULT_WIDTH = 600;

	/**Private field to hold a default dimension.*/
	private static final int DEFAULT_HEIGHT = 300;
	
	/**Private field to hold a height for textfield.*/
	private static final int TEXTFIELD_HEIGHT = 30;
	
	/**Private field to hold the base panel.*/
	private JPanel myBasePanel;
	
	/**Private field to hold a scrollpane reference.*/
	private JScrollPane myScrollPane;

	/**Private field to hold an array of Email address objects.*/
	private ArrayList<Email> myEmailList;

	

	public EmailPanel() {
		
		
		start();
		setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	/**Private method to start construction of the email panel.
	 * Reads all the names from a text file on the home directory.
	 * Also writes to the text file on the home directory.
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
	
	/**Private method to add a save button 
	 * to the base panel.
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

	/**Private method to set the panel layout
	 * of the email panel for display
	 * and editing purposes.  Probably
	 * should be in a grid layout to best display
	 * and mount on a scrollpane to achieve 
	 * expandability. 
	 */
	private void setPanelLayout() {
		
		

		myScrollPane = new JScrollPane(myBasePanel);
		myScrollPane.setPreferredSize(new Dimension(myBasePanel.getWidth(), DEFAULT_HEIGHT));
		myScrollPane.setVisible(true);
		
		add(myScrollPane, BorderLayout.CENTER);

	}

	/**Private method to 
	 * read the emails from a text
	 * file.  Format is:
	 * 
	 * SchoolName , SchoolEmails-SchoolEmails-SchoolEmails
	 */
	private void readEmailFile() {

		try {
			
			final BufferedReader br = new BufferedReader(new FileReader(new File("src/view/emailList.txt")));
					
			while (br.ready()) {

				final String line = br.readLine();
				myEmailList.add(parseLine(line, false));
				
			}
			br.close();

		} catch(FileNotFoundException f) {

			System.err.println("Sorry, couldn't find that file...");

		} catch (IOException e) {

			System.err.println("Sorry, couldn't find that file...");
		}

	}

	/**Private method to display
	 * the emails with their schools
	 * on the panel.
	 */
	private void displayEmails() {

		for (int i = 0; i < myEmailList.size(); i++) {

			//School will never be editable
			final Email email = myEmailList.get(i);
			final JTextField showSchool = new JTextField(email.getSchool());
			showSchool.setEditable(false);
			showSchool.setPreferredSize(new Dimension(DEFAULT_WIDTH / 2, TEXTFIELD_HEIGHT));
			myBasePanel.add(showSchool);

			
			final JTextField emailField = new JTextField();  //Displays the current emails
			emailField.setPreferredSize(new Dimension(DEFAULT_WIDTH / 2, TEXTFIELD_HEIGHT));
			myBasePanel.add(emailField);
			
			StringBuilder theseEmails = new StringBuilder(); //string builder to store a chain of emails
			theseEmails.append(email.getEmails());
			
			emailField.setText(theseEmails.toString());
			
			
			emailField.addActionListener(new ActionListener() { //ActionListener to set all the new emails
				public void actionPerformed(final ActionEvent theEvent) {
					
					final String newEmail = emailField.getText();
					final String newEntry = showSchool.getText() + ", " + newEmail;

					final Email newMail = parseLine(newEntry, true);
					
					replaceEmail(newMail);
					
					emailField.setText(newMail.getEmails());				
				}	
			});

			

		}

	}
	/**Private method to replace the email
	 * in the Map.
	 * @param newMail is the new Email 
	 * object for rewriting out to the directory.
	 */
	
	private void replaceEmail(Email newMail) {
		
		for (int i = 0; i < myEmailList.size(); i++) {
			
			System.out.println("STORED " + myEmailList.get(i).getSchool() + "NEW " + newMail.getSchool());
			if (myEmailList.get(i).getSchool().equalsIgnoreCase(newMail.getSchool())) {
				
				myEmailList.set(i, newMail);
				System.out.println("New address is " + newMail);
				myEmailList.remove(i);
				myEmailList.add(newMail);
			}
			System.out.println("Retained address at " + myEmailList.get(i).toString());
			
		}
		
	}


	/**Private method to parse a new 
	 * line from the email document.
	 * @param theLine is the string
	 * to parse.
	 * @param isOutgoing is the boolean
	 * determining if the write is out bound
	 * or the read is in bound. true is 
	 * out bound.
	 */
	private Email parseLine(final String theLine, final boolean isOutgoing) {

		final String[] firstSplit = theLine.split(",");  //split the line
		
		final String school = firstSplit[0].trim(); //save the school

		String emails = null;
		if (!isOutgoing) {
			
		emails = firstSplit[1];

		} else {
			
			emails = firstSplit[1];
			
		}
		final Email email = new Email(school, emails);

		return email;

	}
	
	/**Private method to write all the new emails 
	 * out to the text file that the 
	 * program initially reads from. 
	 */
	private void writeEmailsToFile() {
		
		
		try {
			
			final PrintWriter pw = new PrintWriter(new File(System.getProperty("user.home").
					concat(File.separator).concat("emailList.txt")));
			System.out.println("The size of the email list is " + myEmailList.size());
			for (int i = 0; i < myEmailList.size(); i++) {
				
				pw.write(myEmailList.get(i).getSchool().concat(",").concat(myEmailList.get(i).getEmails()) + "\r\n");
				
			}
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	/**Public method to get the list
	 * of emails from the email panel.
	 * @return myEmailList is the map of
	 * all email address with schoolName as 
	 * the key.
	 */
	public ArrayList<Email> getEmails() {
		
		return myEmailList;
	}

}
