/**
 * 
 */
package view.master;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import model.Email;
import model.SchoolData;
import view.components.EmailButton;
import view.components.PrintButton;
import view.components.SLAButton;

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
		
		editEmails.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent theEvent) {

				frame.add(eP);
				frame.setSize(new Dimension((int) (myFrame.getWidth() * 1.5),
						(int) (myFrame.getHeight() * 1.5)));
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLocationRelativeTo(myFrame);
				frame.setResizable(true);

			}
		});

		add(new EmailButton(myDataStack));
		
		add(new PrintButton(myFileList));
	
		add(new SLAButton(myDataStack));
	
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
