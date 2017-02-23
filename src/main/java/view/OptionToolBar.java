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
import java.util.ArrayDeque;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import exceptions.BlasterError;
import file_system.LocalStorage;
import lbresources.ViewResource;
import model.SchoolData;

/**
 * Implements the toolbar for the user.
 * 
 * @author Andrew Leach
 * @version July 9th, 2014
 */
public class OptionToolBar extends JToolBar {

	private static final double FRAME_SCALAR = 1.5;

	/** Private field to hold the email Address of the Office Coordinator. */
	private static final String OFFICE_ADMIN_ADDRESS = "phulst@tacoma.k12.wa.us";

	private static final String EMAIL_SUBJECT = "SLA%20Count";

	private static final String EMAIL_BODY = "Attached%20is%20your%20current%20";

	private static final String EMAIL_BODY_TEACHER = "Attached%20is%20your%20current%20";

	private static final String EMAIL_BODY_LIST = "%20class%20list%20for%20";

	private static final String PRINTER_ALERT = "Remember to set your default printer to \n " + "the office Xerox machine.";

	/** Private field to hold an Alert Constant. */
	private static final String KEY_ALERT = "alert";

	/** Private field to hold a Data stack. */
	private ArrayDeque<SchoolData> myDataStack;

	/** Private field to hold an ArrayList of files. */
	private List<File> myFileList;

	/** Private field to hold a reference to the frame. */
	private JFrame myFrame;

	/** Private field to hold a reference to the print button. */
	private JButton myPrintButton;

	/** Private field used to hold the result button. */
	private JButton myResultButton;

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public OptionToolBar(final JFrame theFrame, final ArrayDeque<SchoolData> theDataStack, final List<File> theFileList) {

		myFrame = theFrame;
		myDataStack = theDataStack;
		myFileList = theFileList;

		addComponents();
	}

	/**
	 * Public method to allow LB main to update the stack to display the correct buttons and make the DataStack non null.
	 * 
	 * @param theDataStack
	 *        is the fresh stack of data being passed in.
	 */
	public void updateToolBar(final ArrayDeque<SchoolData> theDataStack, final List<File> theFileList) {

		myDataStack = theDataStack;
		myFileList = theFileList;
		myPrintButton.setEnabled(true);
		myResultButton.setEnabled(true);
	}

	/**
	 * Private method to add components to the toolbar.
	 */
	private void addComponents() {
		add(getPrintButton());
		add(getResultButton());

	}

	/**
	 * Private method used to return a Result Button to the the OptionToolBar which can be moved around at the User's discretion.
	 */
	private JButton getResultButton() {
		myResultButton = new JButton(ViewResource.VIEW_FILE_BUTTON.text);
		myResultButton.setEnabled(false);
		myResultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				Process p;
				try {
					p = Runtime.getRuntime().exec(ViewResource.VIEW_FILES_CMD.text + LocalStorage.getOutputDirectory());
					p.waitFor();
				}
				catch(IOException | InterruptedException e) {
					JOptionPane.showMessageDialog(null, BlasterError.EXEC_ERROR + e.getMessage());
				};

			}
		});

		return myResultButton;
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

				final int choice = JOptionPane.showConfirmDialog(null, PRINTER_ALERT, KEY_ALERT, JOptionPane.OK_CANCEL_OPTION);

				if(choice == JOptionPane.OK_OPTION) {

					printFiles();

				}

			}

		});
		return myPrintButton;
	}

	/**
	 * Private method used to print all the Notebook Files using the java.awt.Desktop class and the print function.
	 */
	private void printFiles() {
		for(int i = 0; i < myFileList.size(); i++) {

			try {

				Desktop.getDesktop().print(myFileList.get(i));

			}
			catch(final IOException e) {

				JOptionPane.showMessageDialog(null, BlasterError.PRINT_FILE_ERROR.text + e.getMessage());

			}
		}
	}
}
