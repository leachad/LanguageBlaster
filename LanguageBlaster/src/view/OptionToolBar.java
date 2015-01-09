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
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

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

	/** Private field to hold a Data stack. */
	private ArrayDeque<SchoolData> myDataStack;

	/** Private field to hold an ArrayList of files. */
	private ArrayList<File> myFileList;

	/** Private field to hold a reference to the frame. */
	private JFrame myFrame;

	/** Private field to hold a current list of emails. */
	private ArrayList<Email> myEmailList;

	/** Private field to hold an email button. */
	private JButton myEmailButton;

	/** Private field to hold an email sla to pam button. */
	private JButton mySlaButton;

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public OptionToolBar(final JFrame theFrame,
			final ArrayDeque<SchoolData> theDataStack,
			final ArrayList<File> theFileList) {

		myFrame = theFrame;
		// myCurrentMonth = theCurrentMonth;
		myDataStack = theDataStack;
		myFileList = theFileList;

		addComponents();
	}

	/**
	 * Public method to allow LB main to update the stack to display the correct
	 * buttons and make the DataStack non null.
	 * 
	 * @param theDataStack
	 *            is the fresh stack of data being passed in.
	 */
	public void updateDataStack(final ArrayDeque<SchoolData> theDataStack) {

		myDataStack = theDataStack;
		myEmailButton.setVisible(true);
		mySlaButton.setVisible(true);
	}

	/**
	 * Private method to add components to the toolbar.
	 */
	private void addComponents() {

		final JButton editEmails = new JButton("Edit Emails");
		editEmails.setSize(new Dimension(editEmails.getPreferredSize()));
		add(editEmails);

		final JFrame frame = new JFrame();
		final EmailPanel eP = new EmailPanel();

		myEmailList = eP.getEmails();
		// opens up a new email window.
		editEmails.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent theEvent) {

				frame.add(eP);
				frame.setSize(new Dimension((int) (myFrame.getWidth() * 1.5),
						(int) (myFrame.getHeight() * 1.5)));
				frame.setVisible(true);
				frame.setLocationRelativeTo(myFrame);
				frame.setResizable(true);

			}
		});

		myEmailButton = new JButton("Email Lists");
		myEmailButton.setVisible(false);
		myEmailButton.setSize(new Dimension(myEmailButton.getPreferredSize()));
		add(myEmailButton);

		myEmailButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				while (!myDataStack.isEmpty()) {

					SchoolData sd = myDataStack.pop();

					Desktop desktop;
					if (Desktop.isDesktopSupported()
							&& (desktop = Desktop.getDesktop())
									.isSupported(Desktop.Action.MAIL)) {

						try {

							URI mailto;
							StringBuilder addresses = new StringBuilder();
							String[] teacherEmails = sd.getEmailAddresses();

							for (int i = 0; i < sd.getEmailAddresses().length; i++) {
								addresses.append(teacherEmails[i].trim())
										.append(";").append("%20").trimToSize();
							}
							System.out.println("Addresses "
									+ addresses.toString());
							mailto = new URI("mailto:"
									+ addresses.toString().trim() + "?subject="
									+ sd.getCurrentMonth() + "%20Counts"
									+ "&body=Attached%20is%20your%20current%20"
									+ sd.getCurrentMonth() + "%20class%20list"
									+ "%20for%20" + sd.getEmailName());

							desktop.mail(mailto);

						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						// TODO fallback to some Runtime.exec(..) voodoo?
						throw new RuntimeException(
								"desktop doesn't support mailto; mail is dead anyway ;)");
					}

				}
			}
		});

		final JButton printCounts = new JButton("Print Lists");
		printCounts.setSize(new Dimension(printCounts.getPreferredSize()));
		add(printCounts);

		printCounts.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				final int choice = JOptionPane.showConfirmDialog(null,
						"Remember to set your default printer to \n "
								+ "the office Xerox machine.", "alert",
						JOptionPane.OK_CANCEL_OPTION);

				if (choice == JOptionPane.OK_OPTION) {

					for (int i = 0; i < myFileList.size(); i++) {

						try {

							Desktop.getDesktop().print(myFileList.get(i));

						} catch (final IOException e) {

							System.err
									.println("Sorry, couldn't find that file...");

						}
					}
				}

			}

		});

		mySlaButton = new JButton("Email SLA to Pam");
		mySlaButton.setSize(new Dimension(mySlaButton.getPreferredSize()));
		add(mySlaButton);
		mySlaButton.setVisible(false);

		mySlaButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				try {

					URI mailto;
					mailto = new URI("mailto:phulst@tacoma.k12.wa.us"
							+ "?subject="
							+ myDataStack.getFirst().getCurrentMonth()
							+ "%20Counts"
							+ "&body=Attached%20is%20your%20current%20"
							+ myDataStack.getFirst().getCurrentMonth()
							+ "%20SLA%20Count");
					Desktop.getDesktop().mail(mailto);

				} catch (final IOException e) {

					System.err
							.println("Sorry, can't use the default mail client");
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Public method to get the email list from the email panel.
	 * 
	 * @return myEmailMap is the list of emails.
	 */
	public ArrayList<Email> getEmails() {

		return myEmailList;
	}
}
