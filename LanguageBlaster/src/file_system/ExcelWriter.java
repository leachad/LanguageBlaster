package file_system;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import model.Email;
import model.NativeLanguage;
import model.SLACount;
import model.SchoolData;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import view.LBDate;

public class ExcelWriter {

	/** Static field to hold a column integer where the student name is found. */
	private static final int STUDENT_CONSTANT = 1;

	/** Static field to hold a column integer where building is found. */
	private static final int BUILDING_CONSTANT = 2;

	/** Static field to hold a header constant. */
	private static final int HEADER_CONSTANT = 3;

	/** static field to hold a column integer where language is found. */
	private static final int LANGUAGE_CONSTANT = 4;

	/** Holds a reference to the number of columns in the sheet. */
	private static final int NUM_COLUMNS = 7;

	/** Private field to hold the excel file. */
	private File myFile;

	/** Private field to hold a list of native languages. */
	private Set<NativeLanguage> myLanguageSet;

	/** Private field to hold a current school. */
	private String myCurrentSchool;

	/** Private field to hold a current workbook. */
	private Workbook myCurrentBook;

	/** Private field to hold the row index to begin parsing data. */
	private int mySelectedStartIndex;

	/** Private field to hold the current sheet. */
	private Sheet mySheet;

	/** Private field to hold a file output stream. */
	private FileOutputStream myOutputStream;

	/** Private field to hold the current School data. */
	private SchoolData mySchoolData;

	/** Private field to hold the current file path. */
	private String myCurrentFilePath;

	/** Private field to hold a Stack of schools and data. */
	private ArrayDeque<SchoolData> myDataStack;

	/** Private field to hold an ArrayList of schools. */
	private ArrayList<File> myFileList;

	/** Private field to hold an email list. */
	private List<Email> myEmailList;

	/** Private field to hold a reference to the parentDirectoryFilePath. */
	private String myParentFolderPath;

	/**
	 * Private field to hold a SortedMap of student cell data. The key is the
	 * students name.
	 */
	private Map<String, Cell[]> myCellMap;

	/** Private field to hold an SLACount object for initiating counting. */
	private SLACount mySLACount;

	public ExcelWriter() {

		myDataStack = new ArrayDeque<>();
		myFileList = new ArrayList<>(64);
		myCellMap = new TreeMap<>();

	}

	/**
	 * Method to set the filePath as determined by the external calling code.
	 * 
	 * @param theMasterFile
	 *            is the Monthly Master File selected by the calling code.
	 */
	public void setMonthlyMasterFilePath(final File theMasterFile) {
		myFile = theMasterFile;
		myParentFolderPath = LocalStorage.createDirectory();
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
		mySLACount = new SLACount(LocalStorage.getSLABlankPath(),
				LocalStorage.createDirectory(), theDate);

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
		return myDataStack;
	}

	/**
	 * Private void method to read and parse the data in the buffer. Ensure to
	 * update other data sets accordingly.
	 * 
	 * @param theSheet
	 *            is the current workbook sheet TODO Deprecate this method and
	 *            use one that builds upon the ArrayList Buffer used for
	 *            storage.
	 * @throws IOException
	 */
	public Map<String, Cell[]> parseWorkSheet(final Sheet theSheet)
			throws IOException {
		/*
		 * First instance of calling change school to get the primary school to
		 * set. (Should always be Arlington)
		 */

		int columnIndex = 0, rowIndex = 2;

		for (Row row : theSheet) {

			if (row.getRowNum() == 0) { // set the first school

				changeSchool(theSheet.getRow(HEADER_CONSTANT), theSheet);

			} else if (row.getRowNum() <= theSheet.getLastRowNum()
						&& row.getRowNum() >= HEADER_CONSTANT) {

				checkSchool(row, theSheet);

				if (rowIndex == 2) { // creates the title index

					addColumnHeaders(rowIndex, columnIndex);

				}

				final Cell[] rowCells = new Cell[NUM_COLUMNS];
				String studentName = null;

				for (Cell cell : row) {

					rowCells[columnIndex] = cell;

					if (cell.getColumnIndex() == STUDENT_CONSTANT) {

						studentName = cell.getStringCellValue();

					}

					else if (cell.getColumnIndex() == LANGUAGE_CONSTANT) {
						evaluateNativeLanguages(cell);

					}

					columnIndex++;

				}
				myCellMap.put(studentName, rowCells);

				rowIndex++;

				columnIndex = 0;

			}

		}

		closeStream();

		return myCellMap;
	}

	/**
	 * Private method used to build the Set of NativeLanguages for a specific
	 * school.
	 * 
	 */
	private void evaluateNativeLanguages(final Cell theCurrentCell) {
		String curLanguage = theCurrentCell.getStringCellValue();

		/*
		 * Run the set and see if the languages match or don't match.
		 */
		final Iterator<NativeLanguage> setIterator = myLanguageSet.iterator();
		boolean found = false;

		while (setIterator.hasNext()) {

			final NativeLanguage language = setIterator.next();

			if (language.getLanguageLabel().equals(curLanguage)) {

				language.increment();
				found = true;
			}
		}

		if (!found) {
			myLanguageSet.add(new NativeLanguage(curLanguage));
		}
	}

	/**
	 * Private method to read the workbook into a buffer for easier sorting and
	 * extraction of key data.
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 * 
	 */
	public void readWorkBookIntoBuffer() throws InvalidFormatException,
			IOException {

		final Workbook wb = WorkbookFactory.create(myFile);
		final Sheet current = wb.getSheetAt(0);
		List<Row> rowBuffer = new ArrayList<>(3000);
		for (int i = 0; i < current.getPhysicalNumberOfRows(); i++) {
			if (i >= mySelectedStartIndex) {
				rowBuffer.add(current.getRow(i));
				printRow(current.getRow(i));
			}
		}

	}

	/**
	 * Method for testing and debugging.
	 */
	private void printRow(final Row theRow) {
		Iterator<Cell> cells = theRow.iterator();
		while (cells.hasNext())
			System.out.print(cells.next().getStringCellValue());
		System.out.println();
	}

	/**
	 * Private method to add headers to each worksheet.
	 */
	private void addColumnHeaders(final int theColumnIndex,
			final int theRowIndex) {

		final Cell titleCell = mySheet.createRow(theRowIndex - 2).createCell(
				theColumnIndex);
		titleCell.setCellValue(myCurrentSchool + "--" + LBDate.myCurrentMonth
				+ "-" + LBDate.myCurrentDay + "-" + LBDate.myCurrentYear);
		mySheet.addMergedRegion(new CellRangeAddress(theRowIndex - 2,
				theRowIndex - 2, theColumnIndex, theColumnIndex + 2));

		final Row header = mySheet.createRow(theRowIndex - 1);
		final Cell cell = header.createCell(theColumnIndex);
		cell.setCellValue("SID");

		final Font font = myCurrentBook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		final CellStyle style = myCurrentBook.createCellStyle();
		style.setFont(font);
		cell.setCellStyle(style);

		final Cell cell1 = header.createCell(theColumnIndex + 1);
		cell1.setCellValue("Student");
		cell1.setCellStyle(style);

		final Cell cell2 = header.createCell(theColumnIndex + 2);
		cell2.setCellValue("School");
		cell2.setCellStyle(style);

		final Cell cell3 = header.createCell(theColumnIndex + 2 + 1);
		cell3.setCellValue("Birthdate");
		cell3.setCellStyle(style);

		final Cell cell4 = header.createCell(theColumnIndex + 2 + 2);
		cell4.setCellValue("Language");
		cell4.setCellStyle(style);

		final Cell cell5 = header.createCell(theColumnIndex + 2 + 2 + 1);
		cell5.setCellValue("Program");
		cell5.setCellStyle(style);

		final Cell cell6 = header.createCell(theColumnIndex + 2 + 2 + 2);
		cell6.setCellValue("Grade");
		cell6.setCellStyle(style);

	}

	/**
	 * Private method to check the school against the school observed in the
	 * file read.
	 * 
	 * @param theRow
	 *            is the row to check for a new school.
	 * @throws IOException
	 */
	private void checkSchool(final Row theRow,
			final org.apache.poi.ss.usermodel.Sheet theSheet)
			throws IOException {

		final String school = theSheet.getRow(theRow.getRowNum())
				.getCell(BUILDING_CONSTANT).getStringCellValue();

		if (school != myCurrentSchool) {

			changeSchool(theRow, theSheet);

		}

	}

	/**
	 * Private method to change the school observed in the file read. Also
	 * creates a new instance of a Language Set for counting student languages.
	 * 
	 * @throws IOException
	 */
	private void changeSchool(final Row theRow,
			final org.apache.poi.ss.usermodel.Sheet theSheet)
			throws IOException {

		if (myOutputStream != null) {

			closeStream();

		}

		myLanguageSet = new LinkedHashSet<NativeLanguage>();

		myCurrentSchool = theSheet.getRow(theRow.getRowNum())
				.getCell(BUILDING_CONSTANT).getStringCellValue();
		// System.out.println("CURRENT SCHOOL--->" + myCurrentSchool);
		myCurrentBook = new HSSFWorkbook();

		final String school = myCurrentSchool;

		final String[] schoolName = school.split("-");

		myCurrentFilePath = LocalStorage.myOutputFolder.getAbsolutePath()
				.concat(File.separator).concat(schoolName[1].trim())
				.concat(".xls");

		myOutputStream = new FileOutputStream(myCurrentFilePath);

		final String name = WorkbookUtil
				.createSafeSheetName(LBDate.myCurrentMonth.concat(" ")
						.concat(LBDate.myCurrentYear).concat("--")
						.concat(myCurrentSchool));
		mySheet = myCurrentBook.createSheet(name);

		final HSSFPrintSetup printSetup = (HSSFPrintSetup) mySheet
				.getPrintSetup();
		printSetup.setLandscape(true);
		printSetup.setFitHeight((short) 0);
		printSetup.setFitWidth((short) 1);

		mySheet.setAutobreaks(true);

		myCellMap.clear();

	}

	/**
	 * Private method to write out all the sorted rows to the workbook created
	 * from a portion of the other work book.
	 */
	private void writeSortedRows(final Workbook theBook) {

		Iterator<String> iterator = myCellMap.keySet().iterator();

		int rowIndex = 3;

		while (iterator.hasNext()) {

			Row row = theBook.getSheetAt(0).createRow(rowIndex);
			final Cell[] cells = myCellMap.get(iterator.next());

			for (int i = 0; i < 7; i++) {

				row.createCell(i).setCellValue(cells[i].getStringCellValue());

			}
			rowIndex++;
		}

	}

	/**
	 * Private method to close the current stream.
	 * 
	 * @throws IOException
	 * @param theSheet
	 *            is the sheet passed in for column autosizing.
	 */
	private void closeStream() throws IOException {

		writeSortedRows(myCurrentBook);
		countLanguages();

		final FileOutputStream oldStream = myOutputStream;

		final Workbook oldBook = myCurrentBook;

		for (int i = 0; i < NUM_COLUMNS; i++) {

			oldBook.getSheetAt(0).autoSizeColumn(i, false);

		}

		final String name = myCurrentSchool;
		final String[] schoolName = name.split("-");

		myFileList.add(new File(LocalStorage.myOutputFolder.getAbsolutePath()
				.concat(File.separator).concat(schoolName[1].trim())
				.concat(".xls")));

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {

			myCurrentBook.write(bos);

		} finally {

			bos.close();

		}

		oldBook.write(oldStream);

		oldStream.close();
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
							myCurrentSchool.substring(
									myCurrentSchool.indexOf("-") + 1,
									myCurrentSchool.length()).trim())) {

				index = i;
				i = myEmailList.size();
				isESL = true;
			}
		}

		if (isESL) {

			final String[] emails = myEmailList.get(index).getEmails();
			mySchoolData = new SchoolData(myLanguageSet, myCurrentSchool,
					bytes, emails, myCurrentFilePath, LBDate.myCurrentMonth);
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
			final int rowIndex, final int currentCount) {
		int runningCount = 0;
		final Row newRow = mySheet.createRow(rowIndex);
		newRow.createCell(LANGUAGE_CONSTANT).setCellValue(
				theLanguage.getLanguageLabel());

		runningCount = theLanguage.getCountLabel() + currentCount;
		newRow.createCell(LANGUAGE_CONSTANT + 1).setCellValue(
				theLanguage.getCountLabel());

		if (mySLACount != null) {
			mySLACount.writeToSheet(theLanguage.getLanguageLabel(),
					myCurrentSchool, theLanguage.getCountLabel());

		}

		return runningCount;
	}

	/**
	 * Private method to create a count row using the Apache POI library.
	 * 
	 * @param theTotalCount
	 *            is the number of languages to display.
	 */
	private void createCountRow(final int theTotalCount) {
		// Create the total row
		final Row row = mySheet.createRow(mySheet.getLastRowNum() + 2);

		final Cell cell = row.createCell(LANGUAGE_CONSTANT);
		cell.setCellValue("Total");

		final Font font = myCurrentBook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		final CellStyle style = myCurrentBook.createCellStyle();
		style.setFont(font);

		cell.setCellStyle(style);

		final Cell countCell = row.createCell(LANGUAGE_CONSTANT + 1);
		countCell.setCellValue(theTotalCount);
	}

	/**
	 * Private method to count the languages and display them at the bottom of
	 * the page for totalling and data.
	 * 
	 * @param theSheet
	 *            is the sheet passed in for adding this data.
	 */
	private void countLanguages() {

		final Iterator<NativeLanguage> iterator = myLanguageSet.iterator();

		int i = mySheet.getLastRowNum() + 3;

		int totalCount = 0;

		while (iterator.hasNext()) {

			final NativeLanguage language = iterator.next();

			totalCount = totalCount
					+ createLanguageRow(language, i, totalCount);

			i++;

		}

		createCountRow(totalCount);

	}

}
