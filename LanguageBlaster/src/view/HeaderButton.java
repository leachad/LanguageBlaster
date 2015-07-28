/**
 * 
 */
package view;

import javax.swing.JRadioButton;

import org.apache.poi.ss.usermodel.Row;

/**
 * @author aleach
 *
 */
public class HeaderButton extends JRadioButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3200464068778323831L;
	
	private static final String DELIMITER = ", ";
	
	/** Private field to hold the index of the HeaderButton.*/
	private int myHeaderIndex = 0;
	
	
	public HeaderButton(final int theHeaderIndex, final Row theCurrentRow) {
		super(createRowRepresentation(theCurrentRow));
		myHeaderIndex = theHeaderIndex;
		
	}
	
	
	public int getHeaderIndex() {
		return myHeaderIndex;
	}
	
	private static String createRowRepresentation(final Row theCurrentRow) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < theCurrentRow.getPhysicalNumberOfCells(); i++) 
			sb.append(theCurrentRow.getCell(i).getStringCellValue() + DELIMITER);
		
		return sb.toString();
	}
	
	



}
