/**
 * 
 */
package view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import file_system.LocalStorage;

/**
 * @author aleach
 *
 */
public class HeaderTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8974549762036798969L;

	private List<List<String>> myHeaderRows;

	public HeaderTableModel(final List<List<String>> theRows) {
		myHeaderRows = theRows;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return LocalStorage.getNumColumns();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return myHeaderRows.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int theRow, final int theColumn) {
		return myHeaderRows.get(theRow).get(theColumn);
	}

}
