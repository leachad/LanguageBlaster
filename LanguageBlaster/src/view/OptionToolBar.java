/**
 * 
 */
package view;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import resources.FileResource;
import resources.ViewResource;
import exceptions.BlasterError;
import file_system.LocalStorage;
import model.Email;
import model.SchoolData;

/**
 * Implements the toolbar for the user.
 * 
 * @author Andrew Leach
 * @version July 9th, 2014
 *
 */
public class OptionToolBar extends JToolBar {
	
	private static final double FRAME_SCALAR = 1.5;

	/** Private field to hold the email Address of the Office Coordinator. */
	private static final String OFFICE_ADMIN_ADDRESS = "phulst@tacoma.k12.wa.us";
	
	private static final String EMAIL_SUBJECT = "SLA%20Count";
	
	private static final String EMAIL_BODY = "Attached%20is%20your%20current%20";
	
	private static final String EMAIL_BODY_TEACHER = "Attached%20is%20your%20current%20";
	
	private static final String EMAIL_BODY_LIST = "%20class%20list%20for%20";

	/** Private field to hold an Error Message. */
	private static final String EMAIL_ERROR = "Sorry, there was a problem executing your request";

	/** Private field to hold a reference to a File Error. */
	private static final String FILE_ERROR = "Sorry, couldn't find that file...";

	/** Private field to hold a Warning Message. */
	private static final String PRINTER_ALERT = "Remember to set your default printer to \n "
			+ "the office Xerox machine.";

	/** Private field to hold an Alert Constant. */
	private static final String KEY_ALERT = "alert";

	/** Private field to hold a Data stack. */
	private ArrayDeque<SchoolData> myDataStack;

	/** Private field to hold an ArrayList of files. */
	private List<File> myFileList;

	/** Private field to hold a reference to the frame. */
	private JFrame myFrame;

	/** Private field to hold a current list of emails. */
	private List<Email> myEmailList;

	/** Private field to hold an email sla to pam button. */
	private JButton mySlaButton;

	/** Private field used to hold an email button. */
	private JButton myEmailButton;

	/** Private field to hold a reference to the print button. */
	private JButton myPrintButton;

	/** Private field used to hold the result button. */
	private JButton myResultButton;

	/** Private field to hold a reference to the Current Month. */
	private String myCurrentMonth;

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public OptionToolBar(final JFrame theFrame,
			final ArrayDeque<SchoolData> theDataStack, final List<File> list,
			final List<Email> list2) {

		myFrame = theFrame;
		myDataStack = theDataStack;
		myFileList = list;
		myEmailList = list2;

		addComponents();
	}

	/**
	 * Public method to allow LB main to update the stack to display the correct
	 * buttons and make the DataStack non null.
	 * 
	 * @param theDataStack
	 *            is the fresh stack of data being passed in.
	 */
	public void updateToolBar(final ArrayDeque<SchoolData> theDataStack,
			final List<Email> theEmailList, final List<File> theFileList) {

		myDataStack = theDataStack;
		myEmailList = theEmailList;
		myFileList = theFileList;
		myCurrentMonth = theDataStack.peek().getCurrentMonth();
		myEmailButton.setEnabled(true);
		mySlaButton.setEnabled(true);
		myPrintButton.setEnabled(true);
		myResultButton.setEnabled(true);
	}

	/**
	 * Private method to add components to the toolbar.
	 */
	private void addComponents() {

		add(getEditEmailButton());
		add(getEmailButton());
		add(getSLAButton());
		add(getPrintButton());
		add(getResultButton());

	}

	/**
	 * Private method used to return a Result Button to the the OptionToolBar
	 * which can be moved around at the User's discretion.
	 */
	private JButton getResultButton() {
		myResultButton = new JButton(ViewResource.VIEW_FILE_BUTTON.text);
		myResultButton.setEnabled(false);
		myResultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				Process p;
				try {
					p = Runtime.getRuntime().exec(
							ViewResource.VIEW_FILES_CMD.text
									+ LocalStorage.getOutputDirectory());
					p.waitFor();
				} catch (IOException | InterruptedException e) {
					JOptionPane.showMessageDialog(null, BlasterError.EXEC_ERROR
							+ e.getMessage());
				}
				;

			}
		});

		return myResultButton;
	}

	/**
	 * Method used to build the email button.
	 * 
	 * @return the button used populate email client windows
	 */
	private JButton getEmailButton() {
		myEmailButton = new JButton(ViewResource.EMAIL_LISTS_BUTTON.text);
		myEmailButton.setEnabled(false);
		myEmailButton.setSize(new Dimension(myEmailButton.getPreferredSize()));

		myEmailButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				while (!myDataStack.isEmpty()) {

					SchoolData sd = myDataStack.pop();

					initiateDesktopClient(sd);

				}
			}
		});

		return myEmailButton;
	}

	/**
	 * Method used to build the SLA button.
	 * 
	 * @return the button used to bring up email Client to emailSLA to Pam.
	 */
	private JButton getSLAButton() {
		mySlaButton = new JButton(ViewResource.EMAIL_SLA_BUTTON.text);
		mySlaButton.setSize(new Dimension(mySlaButton.getPreferredSize()));
		mySlaButton.setEnabled(false);

		mySlaButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				try {

					URI mailto;
					mailto = new URI(buildMailToURI(OFFICE_ADMIN_ADDRESS));
					Desktop.getDesktop().mail(mailto);

				} catch (final IOException | URISyntaxException e) {
					JOptionPane.showMessageDialog(null,
							EMAIL_ERROR + e.getMessage());
				}
			}
		});

		return mySlaButton;
	}

	/**
	 * Method used to build the print button.
	 * 
	 * @return the button used to print all files constructed using LBWriter
	 */
	private JButton getPrintButton() {
		myPrintButton = new JButton(ViewResource.PRINT_LISTS_BUTTON.text);
		myPrintButton.setSize(new Dimension(myPrintButton.getPreferredSize()));
		myPrintButton.setEnabled(false);

		myPrintButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				final int choice = JOptionPane.showConfirmDialog(null,
						PRINTER_ALERT, KEY_ALERT, JOptionPane.OK_CANCEL_OPTION);

				if (choice == JOptionPane.OK_OPTION) {

					printFiles();

				}

			}

		});
		return myPrintButton;
	}

	/**
	 * Method used to build the edit email button.
	 * 
	 * @return the button used to bring up the edit email frame
	 */
	private JButton getEditEmailButton() {
		final JButton editEmails = new JButton(ViewResource.EDIT_EMAILS_BUTTON.text);
		editEmails.setSize(new Dimension(editEmails.getPreferredSize()));

		final JFrame frame = new JFrame();
		final EmailPanel eP = new EmailPanel();

		myEmailList = eP.getEmails();
		// opens up a new email window.
		editEmails.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent theEvent) {

				frame.add(eP);
				frame.setSize(new Dimension((int) (myFrame.getWidth() * FRAME_SCALAR),
						(int) (myFrame.getHeight() * FRAME_SCALAR)));
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLocationRelativeTo(myFrame);
				frame.setResizable(true);

			}
		});
		return editEmails;
	}

	/**
	 * Private method used to print all the Notebook Files using the
	 * java.awt.Desktop class and the print function.
	 */
	private void printFiles() {
		for (int i = 0; i < myFileList.size(); i++) {

			try {

				Desktop.getDesktop().print(myFileList.get(i));

			} catch (final IOException e) {

				JOptionPane
						.showMessageDialog(null, FILE_ERROR + e.getMessage());

			}
		}
	}

	/**
	 * Method used to initiate the Desktop Client for bringing up Default Email
	 * Windows.
	 */
	private void initiateDesktopClient(final SchoolData theSchoolData) {
		Desktop desktop;
		if (Desktop.isDesktopSupported()
				&& (desktop = Desktop.getDesktop())
						.isSupported(Desktop.Action.MAIL)) {

			try {

				URI mailto;
				StringBuilder addresses = new StringBuilder();
				String[] teacherEmails = theSchoolData.getEmailAddresses();

				for (int i = 0; i < theSchoolData.getEmailAddresses().length; i++) {
					addresses.append(teacherEmails[i].trim()).append(FileResource.SEMI_COLON.text)
							.append(FileResource.ENCODED_SPACE.text).trimToSize();
				}
				
				mailto = new URI(buildMailToURI(addresses.toString().trim(),
						theSchoolData));
				desktop.mail(mailto);

			} catch (URISyntaxException | IOException e) {
				JOptionPane.showMessageDialog(null,
						EMAIL_ERROR + e.getMessage());
			}

		} else {
			throw new RuntimeException(EMAIL_ERROR);
		}
	}

	/**
	 * Method used to construct the MailTo URI.
	 */
	private String buildMailToURI(final String theEmailAddress) {
		return FileResource.MAIL_TO.text + theEmailAddress + FileResource.START_ARGS.text 
				+ FileResource.SUBJECT.text + FileResource.ASSIGN_ARGS.text
				+ myCurrentMonth + FileResource.ENCODED_SPACE.text + EMAIL_SUBJECT 
				+ FileResource.APPEND_ARGS.text + FileResource.BODY.text
				+ FileResource.ASSIGN_ARGS.text + EMAIL_BODY
				+ myCurrentMonth + EMAIL_SUBJECT;
	}

	private String buildMailToURI(final String theEmailAddress,
			final SchoolData theSchoolData) {	
		return FileResource.MAIL_TO.text + theEmailAddress + FileResource.START_ARGS.text 
		+ FileResource.SUBJECT.text + FileResource.ASSIGN_ARGS.text
		+ theSchoolData.getCurrentMonth() + FileResource.ENCODED_SPACE.text + EMAIL_SUBJECT 
		+ FileResource.APPEND_ARGS.text + FileResource.BODY.text
		+ FileResource.ASSIGN_ARGS.text + EMAIL_BODY_TEACHER
		+ theSchoolData.getCurrentMonth() + EMAIL_BODY_LIST + theSchoolData.getEmailName();
	}

	/**
	 * Public method to get the email list from the email panel.
	 * 
	 * @return myEmailMap is the list of emails.
	 */
	public List<Email> getEmails() {

		return myEmailList;
	}
}
