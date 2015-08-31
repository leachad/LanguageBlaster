package file_system;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import model.Email;
import model.SLACount;
import model.SchoolData;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class LBFileController {

	/** Static field to hold a reference to the Default Start Index. */
	private static final int DEFAULT_START_INDEX = 3;

	/** Private static final String used to hold an error message. */
	private static final String ERROR_MESSAGE = "Sorry, unable to parse that file.";

	/** Private field to hold a reference to the LBParser object. */
	private LBParser myLBParser;

	/** Private field to hold a reference to the LBWriter object. */
	private LBWriter myLBWriter;

	/** Private field to hold the excel file. */
	private File myFile;

	/** Private field to hold the row index to begin parsing data. */
	private int mySelectedStartIndex = DEFAULT_START_INDEX;

	/** Private field to hold a reference to the parentDirectoryFilePath. */
	private String myParentFolderPath;

	/** Private field to hold an SLACount object for initiating counting. */
	private SLACount mySLACount;

	public LBFileController() {
		myLBParser = new LBParser();
		myLBWriter = new LBWriter();

	}

	/**
	 * Method to set the filePath as determined by the external calling code.
	 * 
	 * @param theMasterFile
	 *            is the Monthly Master File selected by the calling code.
	 */
	public void setMonthlyMasterFilePath(final File theMasterFile) {
		myFile = theMasterFile;
		myParentFolderPath = LocalStorage.getWorkingDirectory();
	}

	/**
	 * Public method to close the SLA Workbook accessible to the scope of the
	 * ExcelWriter.
	 */
	public void closeSummaryWorkBook() {
		if (mySLACount != null)
			mySLACount.closeBook();
	}

	/**
	 * Public method to set the Index at which the ExcelWriter will began
	 * reading from the master workbook.
	 * 
	 * @param theIndex
	 *            is the startIndex.
	 */
	public void setStartIndex(final int theIndex) {
		mySelectedStartIndex = theIndex;
	}

	/**
	 * Public method to create an instance of an SLA Workbook should the calling
	 * code necessitate it.
	 * 
	 * @param theDate
	 *            is the date used to create a Unique Folder ID.
	 */
	public void createNewOverViewWorkbook(final String theDate) {
		//TODO get the current directory instead of creating a new directory
		mySLACount = new SLACount(LocalStorage.getSLABlankPath(),
				LocalStorage.getWorkingDirectory(), theDate);

	}

	/**
	 * Public method to get the path of the folder that contains the other files
	 * within a current execution of the application.
	 * 
	 * @return myParentFolderPath
	 */
	public String getMyParentFolderPath() {
		return myParentFolderPath;
	}

	/**
	 * Public method to return the DataStack for updating purposes.
	 */
	public ArrayDeque<SchoolData> getDataStack() {
		return myLBWriter.getDataStack();
	}

	/**
	 * Public method to return the List of files for updating purposes.
	 */
	public List<File> getFileList() {
		return myLBWriter.getFileList();
	}

	/**
	 * Public method to return the List of Email's for updating purposes.
	 */
	public Map<String, Email> getEmailMap() {
		return LocalStorage.getEmailMap();
	}

	/**
	 * Public method used to read and return the top 'n' rows as selected by the
	 * User. This will be the first call when the file is selected. When the
	 * User selects one of the top 'n' rows, the workbook will be read. Does not
	 * guarantee that the list will not be null.
	 */
	public List<List<String>> getPotentialSortingRows(final int theTopRows) {
		List<List<String>> topRows = new ArrayList<>();
		try {
			topRows = myLBParser.readPotentialSortingRows(theTopRows, myFile);
		} catch (InvalidFormatException | IOException e) {
			JOptionPane.showMessageDialog(null, ERROR_MESSAGE);
		}

		return topRows;
	}

	/**
	 * Public method to parse the selected worksheet.
	 * 
	 * @param theFile
	 *            is the current File object of Master Book.
	 */
	public void executeBatchPublish() {
		if (mySLACount == null)
			myLBWriter.writeCountData(myLBParser.getCellMap(), null,
					myLBParser.getLanguageMap());
		else
			myLBWriter.writeCountData(myLBParser.getCellMap(), mySLACount,
					myLBParser.getLanguageMap());
	}

	/**
	 * Public method to read the workbook into a buffer using the method of the
	 * internal class LBParser.
	 */
	public void readWorkbook() {
		try {
			myLBParser.readWorkBookIntoBuffer(myFile, mySelectedStartIndex);
		} catch (InvalidFormatException | IOException e) {
			JOptionPane.showMessageDialog(null, ERROR_MESSAGE);
		}
	}

}
