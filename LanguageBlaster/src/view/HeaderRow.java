/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import file_system.LocalStorage;

/**
 * @author aleach
 *
 */
public class HeaderRow extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3200464068778323831L;

	private static final int BUTTON_DIMENSION = 20;

	private static final String NO_DATA = "No Valid Cells in this Row";
	
	private static final String SORT_PROMPT = "Sort Excel sheet on Row ";

	private static final Color NO_DATA_COLOR = Color.orange;

	private JRadioButton mySelector;

	/** Private field to hold a reference to the BorderSize. */
	private static final int BORDER_SIZE = 1;
	
	/** Private field to hold the index of the HeaderButton. */
	private int myHeaderIndex = 0;

	public HeaderRow(final int theHeaderIndex, final Row theCurrentRow) {
		super();

		myHeaderIndex = theHeaderIndex;
		addSelector();
		setLayout(new GridLayout());
		createCells(theCurrentRow);
		setPreferredSize(getPreferredSize());
		validate();

	}

	public int getHeaderIndex() {
		return myHeaderIndex;
	}

	private void addSelector() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		mySelector = new JRadioButton();
		mySelector.setSize(new Dimension(BUTTON_DIMENSION, BUTTON_DIMENSION));
		buttonPanel.add(mySelector, BorderLayout.EAST);
		buttonPanel.add(new JLabel(SORT_PROMPT + myHeaderIndex), BorderLayout.CENTER);
		add(buttonPanel);
		
	}

	private void createCells(final Row theCurrentRow) {

		Iterator<Cell> theCells = theCurrentRow.cellIterator();
		boolean noCells = true;
		while (theCells.hasNext()) {
			add(getCell(theCells.next().getStringCellValue().trim()));
			noCells = false;
		}

		if (noCells) {
			for (int i = 0; i < LocalStorage.getNumColumns(); i++)
				add(getCell(NO_DATA, NO_DATA_COLOR));
		}
	}

	private JLabel getCell(final String theCellValue) {
		JLabel current = new JLabel(theCellValue);
		current.setBorder(BorderFactory.createLineBorder(Color.BLACK,
				BORDER_SIZE, false));
		current.setBackground(Color.WHITE);
		current.setOpaque(true);
		return current;
	}

	private JLabel getCell(final String theCellValue, final Color theError) {
		JLabel current = new JLabel(theCellValue);
		current.setBorder(BorderFactory.createLineBorder(Color.BLACK,
				BORDER_SIZE, false));
		current.setBackground(theError);
		current.setOpaque(true);
		return current;
	}

	public JRadioButton getSelector() {
		return mySelector;
	}

}
