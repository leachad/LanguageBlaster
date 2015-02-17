/**
 * 
 */
package view.components;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import model.SchoolData;

/**
 * @author aleach
 *
 */
public class SLAButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5489746152432556414L;

	/** Private field to hold a reference to the Data Stack.*/
	private ArrayDeque<SchoolData> myDataStack;
	
	public SLAButton(final ArrayDeque<SchoolData> theDataStack) {
		myDataStack = theDataStack;
		construct();
	}
	
	public void construct() {
		
		setText("Email SLA to Pam");
		setSize(new Dimension(getPreferredSize()));
		setVisible(false);

		addActionListener(new ActionListener() {
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
					JOptionPane.showMessageDialog(null, "Sorry, Pam's email address is invalid");
				}
			}
		});
	}
	}

