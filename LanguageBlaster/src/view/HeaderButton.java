/**
 * 
 */
package view;

import javax.swing.JRadioButton;

/**
 * @author aleach
 *
 */
public class HeaderButton extends JRadioButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3200464068778323831L;
	
	/** Private field to hold the index of the HeaderButton.*/
	private int myHeaderIndex = 0;
	
	
	public HeaderButton(final int theHeaderIndex) {
		super();
		myHeaderIndex = theHeaderIndex;
	}
	
	
	public int getHeaderIndex() {
		return myHeaderIndex;
	}



}
