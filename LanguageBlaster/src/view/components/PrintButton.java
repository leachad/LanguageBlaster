/**
 * 
 */
package view.components;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * @author aleach
 *
 */
public class PrintButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1974747356716387453L;
	
	/** Private field to hold a reference to the list of files.*/
	private ArrayList<File> myFileList;

	public PrintButton(ArrayList<File> theFileList) {
		myFileList = theFileList;
		construct();
	}
	
	public void construct() {
		
		setText("Print Lists");
		setSize(new Dimension(getPreferredSize()));

		addActionListener(new ActionListener() {
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
	}
}
