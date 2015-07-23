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

import view.LBDate;

/**
 * @author aleach
 *
 */
public class LBWriter {

	/** Static field to hold a reference to an error message. */
	private static final String WORKBOOK_ERROR_MESSAGE = "Unable to create Workbook: ";
	
	/** Static field to hold a reference to a total label for display. */
	private static final String LABEL_TOTAL = "Total ";

	/** Static field to hold a reference to a Buffer Multiplier. */
	private static final int SUMMARY_BUFFER = 3;

	/** static field to hold a column integer where language is found. */
	private static final int LANGUAGE_CONSTANT = 4;

	/** Holds a reference to the number of columns in the sheet. */
	private static final int NUM_COLUMNS = 7;
	
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
	private List<Email> myEmailList;

	/** Private field to hold an SLACount object for initiating counting. */
	private SLACount mySLACount;

	/**
	 * Constructor to initialize an instance of the LBWriter.
	 */
	public LBWriter() {
		myDataStack = new ArrayDeque<>();
		myFileList = new ArrayList<>();
		myEmailList = new ArrayList<>();
		mySLACount = null;
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
		System.out.println("WRITING COUNT DATA TO FILE SYSTEM.");
		
		while (keySetIterator.hasNext()) {
			String currentSchool = keySetIterator.next();
			List<String[]> currentSheetData = theCellMap.get(currentSchool);
			writeSortedRows(currentSchool, currentSheetData,
					theLanguageMap.get(currentSchool));

		}
		
		JOptionPane.showMessageDialog(null, "COMPLETED BATCH PUBLISH");

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

		final String[] schoolName = theCurrentSchool.split("-");

		myCurrentFilePath = LocalStorage.myOutputFolder.getAbsolutePath()
				.concat(File.separator).concat(schoolName[1].trim())
				.concat(".xls");

		myOutputStream = new FileOutputStream(myCurrentFilePath);

		final String name = WorkbookUtil.createSafeSheetName(LBDate
				.getCurrentMonth().concat(" ").concat(LBDate.getCurrentYear())
				.concat("--").concat(theCurrentSchool));
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
					WORKBOOK_ERROR_MESSAGE + e.getCause());
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

		final String[] schoolName = theCurrentSchool.split("-");

		myFileList.add(new File(LocalStorage.myOutputFolder.getAbsolutePath()
				.concat(File.separator).concat(schoolName[1].trim())
				.concat(".xls")));

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

		boolean isESL = false;
		int index = 0;

		for (int i = 0; i < myEmailList.size(); i++) {

			if (myEmailList
					.get(i)
					.getSchool()
					.equalsIgnoreCase(
							theCurrentSchool.substring(
									theCurrentSchool.indexOf("-") + 1,
									theCurrentSchool.length()).trim())) {

				index = i;
				i = myEmailList.size();
				isESL = true;
			}
		}

		if (isESL) {

			final String[] emails = myEmailList.get(index).getEmails();
			mySchoolData = new SchoolData(theLanguageSet, theCurrentSchool,
					bytes, emails, myCurrentFilePath, LBDate.getCurrentMonth());
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
		System.out.println("Running Count is " + runningCount + " count label in set is " + theLanguage.getCountLabel() + " current count is " + theCurrentTotal);
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
		// Create the total row
		Row currentRow = theCurrentBook.getSheetAt(TOP_SHEET_INDEX).createRow(lastRowNum + SUMMARY_BUFFER);
	

		currentRow.createCell(LANGUAGE_CONSTANT).setCellValue(LABEL_TOTAL);

		currentRow.createCell(LANGUAGE_CONSTANT + 1).setCellValue(theTotalCount);
		
		//System.out.println("TOTAL COUNT FOR " + theCurrentBook.getSheetName(TOP_SHEET_INDEX) + " IS " + theTotalCount);
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
			//System.out.println("Total count for " + theCurrentSchool + " is " + totalCount + " lanaguage is " + language.getLanguageLabel());
			totalCount = createLanguageRow(language, rowCounter, totalCount,
							theCurrentBook, theCurrentSchool);

			rowCounter++;

		}

		createCountRow(totalCount, theCurrentBook, rowCounter);

	}

	
}
