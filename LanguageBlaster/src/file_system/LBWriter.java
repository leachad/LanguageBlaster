/**
 * 
 */
package file_system;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import model.Email;
import model.NativeLanguage;
import model.SLACount;
import model.SchoolData;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import resources.FileResource;
import resources.ViewResource;
import view.LBDate;
import exceptions.BlasterError;

/**
 * @author aleach
 *
 */
public class LBWriter {
	
	/** Static field to hold a reference to a total label for display. */
	private static final String LABEL_TOTAL = "Total ";

	/** Static field to hold a reference to a Buffer Multiplier. */
	private static final int SUMMARY_BUFFER = 3;

	/** static field to hold a column integer where language is found. */
	private static final int LANGUAGE_CONSTANT = 4;

	/** Holds a reference to the number of columns in the sheet. */
	private static final int NUM_COLUMNS = 7;
	
	/** Holds a reference to the number of rows included in the title buffer. */
	private static final int HEADER_BUFFER = 2;
	
	/** Private field to hold a reference to the Index of the Sheet in the Workbook.*/
	private static final int TOP_SHEET_INDEX = 0;

	/** Private field to hold a file output stream. */
	private FileOutputStream myOutputStream;

	/** Private field to hold the current School data. */
	private SchoolData mySchoolData;

	/** Private field to hold the current file path. */
	private String myCurrentFilePath;

	/** Private field to hold a Stack of schools and data. */
	private ArrayDeque<SchoolData> myDataStack;

	/** Private field to hold an ArrayList of schools. */
	private List<File> myFileList;

	/** Private field to hold an email list. */
	private Map<String, Email> myEmailMap;

	/** Private field to hold an SLACount object for initiating counting. */
	private SLACount mySLACount;

	/**
	 * Constructor to initialize an instance of the LBWriter.
	 */
	public LBWriter() {
		myDataStack = new ArrayDeque<>();
		myFileList = new ArrayList<>();
		myEmailMap = LocalStorage.getEmailMap();;
		mySLACount = null;
	}
	
	public List<File> getFileList() {
		return myFileList;
	}
	
	public Map<String, Email> getEmailList() {
		return myEmailMap;
	}
	
	public ArrayDeque<SchoolData> getDataStack() {
		return myDataStack;
	}

	/**
	 * Publicly accessible method that will be called by the LB Parser at the
	 * completion of it's reading and sorting operation.
	 */
	public void writeCountData(final Map<String, List<String[]>> theCellMap,
			final SLACount theSLACount,
			final Map<String, Set<NativeLanguage>> theLanguageMap) {
		Iterator<String> keySetIterator = theCellMap.keySet().iterator();
		mySLACount = theSLACount;
		
		while (keySetIterator.hasNext()) {
			String currentSchool = keySetIterator.next();
			List<String[]> currentSheetData = theCellMap.get(currentSchool);
			writeSortedRows(currentSchool, currentSheetData,
					theLanguageMap.get(currentSchool));

		}
		
		JOptionPane.showMessageDialog(null, ViewResource.BATCH_COMPLETE_ALERT.text);

	}

	/**
	 * Private method to change the school observed in the file read. Also
	 * creates a new instance of a Language Set for counting student languages.
	 * 
	 * @throws IOException
	 */
	private Workbook createNewWorkbook(final String theCurrentSchool)
			throws IOException {

		Workbook currentBook = new HSSFWorkbook();
		myCurrentFilePath = LocalStorage.myOutputFolder.getAbsolutePath()
				.concat(File.separator).concat(theCurrentSchool.trim())
				.concat(FileResource.EXT_CONCAT.text + FileResource.XLS.text);

		myOutputStream = new FileOutputStream(myCurrentFilePath);

		final String name = WorkbookUtil.createSafeSheetName(LBDate
				.getCurrentMonth().concat(FileResource.SPACE.text).concat(LBDate.getCurrentYear())
				.concat(FileResource.DASH.text + FileResource.DASH.text).concat(theCurrentSchool));
		final Sheet currentSheet = currentBook.createSheet(name);

		final HSSFPrintSetup printSetup = (HSSFPrintSetup) currentSheet
				.getPrintSetup();
		printSetup.setLandscape(true);
		printSetup.setFitHeight((short) 0);
		printSetup.setFitWidth((short) 1);

		currentSheet.setAutobreaks(true);

		return currentBook;
	}

	/**
	 * Private method used to style the title row.
	 */
	private void styleTitleRow(final Row theRow, final Workbook theCurrentBook) {
		theCurrentBook.getSheetAt(TOP_SHEET_INDEX)
				.addMergedRegion(
						new CellRangeAddress(theRow.getRowNum(), theRow
								.getRowNum(), 0, NUM_COLUMNS - 1));
	}

	/**
	 * Private method to add headers to each worksheet.
	 */
	private void styleHeaderRow(final Row theRow, final Workbook theCurrentBook) {

		final Font font = theCurrentBook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		final CellStyle style = theCurrentBook.createCellStyle();
		style.setFont(font);
		theRow.setRowStyle(style);

	}

	/**
	 * Private method to write out all the sorted rows to the workbook created
	 * from a portion of the other work book. Does not guarantee sorted order
	 * for the String[]'s
	 */
	private void writeSortedRows(final String theCurrentSchool,
			final List<String[]> theSheetCellData,
			final Set<NativeLanguage> theLanguages) {

		
		
		Collections.sort(theSheetCellData.subList(HEADER_BUFFER, theSheetCellData.size()), new StudentComparator());
		Iterator<String[]> iterator = theSheetCellData.iterator();
		

		Workbook currentBook = null;
		try {
			currentBook = createNewWorkbook(theCurrentSchool);
			int rowCounter = 0;
			while (iterator.hasNext()) {

				Row row = currentBook.getSheetAt(TOP_SHEET_INDEX).createRow(rowCounter);
				final String[] cellData = iterator.next();

				for (int i = 0; i < NUM_COLUMNS; i++) {
					row.createCell(i).setCellValue(cellData[i]);
				}

				if (rowCounter == 0) {
					styleTitleRow(row, currentBook);
				} else if (rowCounter == 1) {
					styleHeaderRow(row, currentBook);
				}
				rowCounter++;
			}

			countLanguages(currentBook, theCurrentSchool, theLanguages, rowCounter);
			closeStream(currentBook, theCurrentSchool, theLanguages);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					BlasterError.CREATE_BOOK_ERROR.text + e.getMessage());
		}

	}

	/**
	 * Private method to close the current stream.
	 * 
	 * @throws IOException
	 * @param theSheet
	 *            is the sheet passed in for column autosizing.
	 */
	private void closeStream(Workbook theCurrentBook,
			final String theCurrentSchool, final Set<NativeLanguage> theLanguageSet) throws IOException {

		for (int i = 0; i < NUM_COLUMNS; i++) {

			theCurrentBook.getSheetAt(0).autoSizeColumn(i, false);

		}

		final String[] schoolName = theCurrentSchool.split(FileResource.DASH.text);

		myFileList.add(new File(LocalStorage.myOutputFolder.getAbsolutePath()
				.concat(File.separator).concat(schoolName[1].trim())
				.concat(FileResource.EXT_CONCAT.text + FileResource.XLS.text)));

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {

			theCurrentBook.write(bos);

		} finally {

			bos.close();

		}

		theCurrentBook.write(myOutputStream);

		myOutputStream.close();
		byte[] bytes = bos.toByteArray();

		/*
		 * Create a new SchoolData object and add it to the data stack when
		 * Language set is fully constructed and closeStream() is called by the
		 * changeSchool method.
		 */
		
		if (myEmailMap.get(theCurrentSchool) != null) {
			final Email curEmail = myEmailMap.get(theCurrentSchool);
			mySchoolData = new SchoolData(theLanguageSet, curEmail,
					bytes, myCurrentFilePath, LBDate.getCurrentMonth());
			myDataStack.push(mySchoolData);

		}

	}

	/**
	 * Private method to create a language row used for displaying student
	 * language data.
	 * 
	 * @param theLanguage
	 *            is the NativeLanguage.
	 */
	private int createLanguageRow(final NativeLanguage theLanguage,
			final int rowIndex, final int theCurrentTotal,
			final Workbook theCurrentBook, final String theCurrentSchool) {
		int runningCount = 0;
		final Row newRow = theCurrentBook.getSheetAt(TOP_SHEET_INDEX).createRow(rowIndex);
		newRow.createCell(LANGUAGE_CONSTANT).setCellValue(
				theLanguage.getLanguageLabel());

		runningCount = theLanguage.getCountLabel() + theCurrentTotal;
		newRow.createCell(LANGUAGE_CONSTANT + 1).setCellValue(
				theLanguage.getCountLabel());

		if (mySLACount != null) {
			mySLACount.writeToSheet(theLanguage.getLanguageLabel(),
					theCurrentSchool, theLanguage.getCountLabel());

		}

		return runningCount;
	}

	/**
	 * Private method to create a count row using the Apache POI library.
	 * 
	 * @param theTotalCount
	 *            is the number of languages to display.
	 */
	private void createCountRow(final int theTotalCount,
			final Workbook theCurrentBook, final int lastRowNum) {
		
		Row currentRow = theCurrentBook.getSheetAt(TOP_SHEET_INDEX).createRow(lastRowNum + SUMMARY_BUFFER);

		currentRow.createCell(LANGUAGE_CONSTANT).setCellValue(LABEL_TOTAL);

		currentRow.createCell(LANGUAGE_CONSTANT + 1).setCellValue(theTotalCount);
		
	}	
	

	/**
	 * Private method to count the languages and display them at the bottom of
	 * the page for totalling and data.
	 * 
	 * @param theSheet
	 *            is the sheet passed in for adding this data.
	 */
	private void countLanguages(final Workbook theCurrentBook,
			final String theCurrentSchool, final Set<NativeLanguage> theLanguageSet, final int lastRowNum) {

		final Iterator<NativeLanguage> iterator = theLanguageSet.iterator();

		int rowCounter = lastRowNum + SUMMARY_BUFFER;

		int totalCount = 0;

		while (iterator.hasNext()) {

			final NativeLanguage language = iterator.next();
			totalCount = createLanguageRow(language, rowCounter, totalCount,
							theCurrentBook, theCurrentSchool);

			rowCounter++;

		}

		createCountRow(totalCount, theCurrentBook, rowCounter);

	}
	
	private class StudentComparator implements Comparator<String[]> {

		private static final int COMPARE_INDEX = 1;
		
		@Override
		public int compare(String[] thisStudent, String[] thatStudent) {
			return thisStudent[COMPARE_INDEX].compareTo(thatStudent[COMPARE_INDEX]);
		}

		
		
	}

	
}
