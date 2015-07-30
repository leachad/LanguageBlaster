/**
 * 
 */
package view;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * @author aleach
 *
 */
public class LBMenu extends JMenuBar {
	
	private static final String FILE_MENU = "File";
	
	//private static final String SYSTEM_FILE = "View Current Month";
	
	//private static final String SYSTEM_PRINTER = "Set Default Printer";
	
	private static final String EDIT_MENU = "Edit";
	
	private static final String HELP_MENU = "Help";
	
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
		
		return helpMenu;
	}
	
	

}
