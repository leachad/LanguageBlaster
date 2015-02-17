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
public class EmailButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7582835499503961505L;

	/** Private field to hold a reference to the copy of the Data Stack. */
	private ArrayDeque<SchoolData> myDataStack;

	public EmailButton(final ArrayDeque<SchoolData> theDataStack) {
		myDataStack = theDataStack;
		construct();

	}

	private void construct() {
		
		setText("Email Lists");
		setVisible(false);
		setSize(new Dimension(getPreferredSize()));

		addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {

				while (!myDataStack.isEmpty()) {

					SchoolData sd = myDataStack.pop();

					Desktop desktop;
					if (Desktop.isDesktopSupported()
							&& (desktop = Desktop.getDesktop())
									.isSupported(Desktop.Action.MAIL)) {

						try {

							URI mailto = new URI(sd.getURIString());

							desktop.mail(mailto);

						} catch (URISyntaxException e) {
							JOptionPane.showMessageDialog(null,
									"Sorry, couldn't parse that Address");
						} catch (IOException e) {
							JOptionPane
									.showMessageDialog(null,
											"Sorry, couldn't open the default email client");
						}

					} else {
						throw new RuntimeException(
								"desktop doesn't support mailto; mail is dead anyway ;)");
					}

				}
			}
		});
	}
}
