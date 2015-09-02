/**
 * 
 */
package view;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import resources.FileResource;

/**
 * @author aleach
 *
 */
public class LBMenu extends JMenuBar {
	
	private static final String FILE_MENU = "File";
	
	/** Private field to hold an Error Message. */
	private static final String EMAIL_ERROR = "Sorry, there was a problem executing your request";
	
	private static final String EDIT_MENU = "Edit";
	
	private static final String HELP_MENU = "Help";
	
	private static final String HELP_PROMPT = "Email Andy...";
	
	private static final String HELP_EMAIL = "type3dude@gmail.com";
	
	private static final String EMAIL_SUBJECT = "Help with Language Blaster";
	
	//private static final String ABOUT_LB = "About Language Blaster";
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8164135578910934966L;

	public LBMenu() {
		super();
		createMenuComponents();
		
	}
	
	private void createMenuComponents() {
		add(createFileMenu());
		add(createEditMenu());
		add(createHelpMenu());
	}
	
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu();
		fileMenu.setText(FILE_MENU);
		
		
		return fileMenu;
		
	}
	
	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu();
		editMenu.setText(EDIT_MENU);
		
		return editMenu;
	}
	
	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu();
		helpMenu.setText(HELP_MENU);
		
		helpMenu.add(createHelpMenuItem());
		return helpMenu;
	}
	
	private JMenuItem createHelpMenuItem() {
		JMenuItem helpItem = new JMenuItem();
		helpItem.setText(HELP_PROMPT);
		helpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent theEvent) {
				initiateEmailClient();
			}
		});
		
		return helpItem;
	}
	
	private void initiateEmailClient() {
		Desktop desktop;
		if (Desktop.isDesktopSupported()
				&& (desktop = Desktop.getDesktop())
						.isSupported(Desktop.Action.MAIL)) {
			try {

				URI mailto;
				
				mailto = new URI(buildMailToURI(HELP_EMAIL));
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
				+ LBDate.getCurrentMonth() + FileResource.ENCODED_SPACE.text + EMAIL_SUBJECT;
	}
	

}
