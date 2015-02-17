package view.master;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Email;
import model.NativeLanguage;
import model.SLACount;
import model.SchoolData;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;

@SuppressWarnings("deprecation")
public class LBMain {

	/** Static field to hold a column integer where the student name is found. */
	private static final int STUDENT_CONSTANT = 1;

	/** Static field to hold a column integer where building is found. */
	private static final int BUILDING_CONSTANT = 2;

	/** Static field to hold a header constant. */
	private static final int HEADER_CONSTANT = 3;

	/** static field to hold a column integer where language is found. */
	private static final int LANGUAGE_CONSTANT = 4;

	/** Static field to hold a month array. */
	private static final String[] COUNT_MONTHS = { "CHOOSE MONTH", "September",
		"October", "November", "December", "January", "February", "March",
		"April", "May", "June", "July", "August"};

	/** Static field to hold a day array. */
	private static final String[] COUNT_DAYS = { "CHOOSE DAY", "1", "2", "3",
		"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
		"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26",
		"27", "28", "29", "30", "31" };

	/** Private field to hold the count years. */
	private static final String[] COUNT_YEARS = { "CHOOSE YEAR", "2014",
		"2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022",
		"2023", "2024" };

	/** Private String count month. */
	private String myCurrentMonth;

	/** Private String to hold a current day. */
	private String myCurrentDay;

	/** Private String myCurrentYear. */
	private String myCurrentYear;

	/** Private field to hold a JFrame for showing all of the GUI elements. */
	private JFrame myFrame;

	/** Private field to hold the excel file. */
	private File myFile;

	/** Private field to hold the outputfolder. */
	private File myOutputFolder;

	/** Private field to hold the output folder string. */
	private String myFilePath;

	/** Private field to hold a list of native languages. */
	private Set<NativeLanguage> myLanguageSet;

	/** Private field to hold a current school. */
	private String myCurrentSchool;

	/** Private field to hold a current workbook. */
	private Workbook myCurrentBook;

	/** Private field to hold an index counter. */
	private int myRowIndex = 2;

	/** Private field to hold an index counter. */
	private int myColumnIndex;

	/** Private field to hold the current sheet. */
	private org.apache.poi.ss.usermodel.Sheet mySheet;

	/** Private field to hold a file output stream. */
	private FileOutputStream myOutputStream;

	/** Private field to hold a String. */
	private String myLanguage;

	/** Private field to hold the current School data. */
	private SchoolData mySchoolData;

	/** Private field to hold an option tool bar. */
	private OptionToolBar myToolBar;

	/** Private field to hold the current file path. */
	private String myCurrentFilePath;

	/** Private field to hold a Stack of schools and data. */
	private ArrayDeque<SchoolData> myDataStack;

	/** Private field to hold an ArrayList of schools. */
	private ArrayList<File> myFileList;

	/** Private field to hold an email list. */
	private List<Email> myEmailList;

	/** Private field to hold a SortedMap of student cell data. 
	 *  The key is the students name.*/
	private Map<String, Cell[]> myCellMap;

	/** Private field to hold an SLACount object for initiating counting.*/
	private SLACount mySLACount; 

	public LBMain() {

		myDataStack = new ArrayDeque<>();
		myFileList = new ArrayList<>(64);
		myCellMap = new TreeMap<>();

		start();
	}

	/**
	 * Private method to begin the creation of the methods that read and write
	 * all of the excel sheet data.
	 */
	private void start() {

		myFrame = new JFrame();

		addToolbar();

		addComponents();

		myFrame.setVisible(true);
		myFrame.setResizable(false);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setPreferredSize(new Dimension(500, 300));
		myFrame.pack();
		myFrame.setLocationRelativeTo(null);

	}

	/**
	 * Private method to set the toolbar and add appropriate listeners.
	 */
	private void addToolbar() {

		myToolBar = new OptionToolBar(myFrame, myDataStack, myFileList);
		myFrame.add(myToolBar, BorderLayout.SOUTH);

		myEmailList = myToolBar.getEmails();

	}

	/**
	 * Private method to add the window components to the JFrame.
	 */
	private void addComponents() {

		final JPanel searchPanel = new JPanel(new GridLayout(5, 1));
		searchPanel.setPreferredSize(myFrame.getSize());
		searchPanel.setBackground(Color.gray);

		final JPanel topPanel = new JPanel(new GridLayout(1, 3));

		final JLabel displayLabel = new JLabel("OUTPUT FILE PATH");
		displayLabel.setBackground(Color.gray);

		final JComboBox<String> monthBox = new JComboBox<>(COUNT_MONTHS);
		monthBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				myCurrentMonth = (String) theEvent.getItem();

			}

		});
		monthBox.setSelectedIndex(0);

		final JComboBox<String> dayBox = new JComboBox<>(COUNT_DAYS);
		dayBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				myCurrentDay = (String) theEvent.getItem();

			}
		});
		dayBox.setSelectedIndex(0);

		final JComboBox<String> yearBox = new JComboBox<>(COUNT_YEARS);
		yearBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				myCurrentYear = (String) theEvent.getItem();

			}

		});
		yearBox.setSelectedIndex(0);

		if (dayBox.getSelectedIndex() == 0) {

			myCurrentDay = "1";
		}

		if (monthBox.getSelectedIndex() == 0) {

			SimpleDateFormat formatter = new SimpleDateFormat("MM");
			myCurrentMonth = formatter.format(new Date());
		}

		if (yearBox.getSelectedIndex() == 0) {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			myCurrentYear = formatter.format(new Date());
		}

		topPanel.add(displayLabel);
		topPanel.add(monthBox);
		topPanel.add(dayBox);
		topPanel.add(yearBox);

		searchPanel.add(topPanel);

		final JTextField displayField = new JTextField();
		searchPanel.add(displayField);

		final JButton selectButton = new JButton("CHOOSE MASTER BOOK");
		selectButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent theEvent) {

				JFileChooser chooser = new JFileChooser(
						"Choose the Master Book");

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					myFile = chooser.getSelectedFile();

					myFilePath = createDirectory();
					displayField.setText(myFilePath);

					try {

						readWorkBook();

					} catch (InvalidFormatException e) {

						e.printStackTrace();

					} catch (IOException e) {

						e.printStackTrace();
					}

				}

				if (mySLACount != null) {
					mySLACount.closeBook();
				}
			}
		});

		searchPanel.add(selectButton);

		final JCheckBox writeToSLA = new JCheckBox("Write to SLA Form?");
		writeToSLA.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent theEvent) {

				if (theEvent.getStateChange() == ItemEvent.SELECTED) {

					final String date = myCurrentMonth.concat(" ")
							.concat(myCurrentDay).concat(", ")
							.concat(myCurrentYear);

					mySLACount = new SLACount(
							"G:\\groups\\Elem Education\\ESL\\SLA Blank [DO NOT MOVE OR DELETE]\\SLA_Blank.xls",
							createDirectory(), date);
				}		
			}
		});

		searchPanel.add(writeToSLA);

		myFrame.add(searchPanel);

	}

	/**
	 * Private method to create a directory used to store all newly created
	 * excel books.
	 */
	private String createDirectory() {

		myOutputFolder = new File("G:\\groups\\Elem Education\\ESL"
				.concat(File.separator).concat(chooseParentFolder())
				.concat(File.separator).concat("ESL_Counts--")
				.concat(myCurrentMonth).concat("-").concat(myCurrentDay)	
				.concat("-").concat(myCurrentYear));

		myOutputFolder.mkdir();

		return myOutputFolder.toString();

	}

	/**
	 * Private method used to place the outputfolder into the
	 * correct parent folder in the g drive.
	 * @return the String of the parent folder.
	 */
	private String chooseParentFolder()	{
		final StringBuilder sb = new StringBuilder();
		final int year = Integer.parseInt(myCurrentYear) + 1;

		switch(myCurrentMonth) {
		case "September":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "October":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "November":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "December":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "January":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		case "February":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		case "March":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		case "April":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		case "May":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		case "June":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		case "July":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		case "August":
			sb.append(myCurrentYear + " " + "ESL_Counts");
			break;
		default:
			sb.append(makeNewParentFolder());
			break;


		}

		return sb.toString();
	}

	/**
	 * Private method to create a new parent folder in case a month
	 * is not chosen.
	 * @return file path of a new parent folder.
	 */
	private String makeNewParentFolder() {
		final StringBuilder sb = new StringBuilder();
		sb.append(myCurrentYear + " " + "ESL_Counts");

		return sb.toString();
	}

	/**
	 * Private method to read the workbook used for creating new workbooks in
	 * the output folder.
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 * 
	 */
	private void readWorkBook() throws InvalidFormatException, IOException {

		final Workbook wb = WorkbookFactory.create(myFile);

		final org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);

		/*
		 * First instance of calling change school to get the primary school to
		 * set. (Should always be Arlington)
		 */

		for (Row row : sheet) {

			if (row.getRowNum() == 0) { // set the first school

				changeSchool(sheet.getRow(HEADER_CONSTANT), sheet);

			}

			if (row.getRowNum() <= sheet.getLastRowNum()
					&& row.getRowNum() >= HEADER_CONSTANT) {

				checkSchool(row, sheet);

				if (myRowIndex == 2) { // creates the title index

					addColumnHeaders();

				}

				final Cell[] rowCells = new Cell[7];
				String studentName = null;

				for (Cell cell : row) {

					rowCells[myColumnIndex] = cell; 

					if (cell.getColumnIndex() == STUDENT_CONSTANT) {

						studentName = cell.getStringCellValue();

					}

					// Create a map of rows to sort after each sheet is written_ALL ROWS ARE ADDED TO THE MAP
					if (cell.getColumnIndex() == LANGUAGE_CONSTANT) {

						myLanguage = cell.getStringCellValue();

						/*
						 * Run the set and see if the languages match or don't
						 * match.
						 */
						final Iterator<NativeLanguage> setIterator = myLanguageSet
								.iterator();
						boolean found = false;

						while (setIterator.hasNext()) {

							final NativeLanguage language = setIterator.next();

							if (language.getLanguageLabel().equals(myLanguage)) {

								language.increment();
								found = true;
							}
						}

						if (!found) {
							myLanguageSet.add(new NativeLanguage(myLanguage));
						}
					}

					myColumnIndex++;

				}
				myCellMap.put(studentName, rowCells);

				myRowIndex++;

				myColumnIndex = 0;

			}

		}

		closeStream();

		myToolBar.updateDataStack(myDataStack);

	}

	/**
	 * Private method to add headers to each worksheet.
	 */
	private void addColumnHeaders() {

		final Cell titleCell = mySheet.createRow(myRowIndex - 2).createCell(
				myColumnIndex);
		titleCell.setCellValue(myCurrentSchool + "--" + myCurrentMonth + "-"
				+ myCurrentDay + "-" + myCurrentYear);
		mySheet.addMergedRegion(new CellRangeAddress(myRowIndex - 2,
				myRowIndex - 2, myColumnIndex, myColumnIndex + 2));

		final Row header = mySheet.createRow(myRowIndex - 1);
		final Cell cell = header.createCell(myColumnIndex);
		cell.setCellValue("SID");

		final Font font = myCurrentBook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		final CellStyle style = myCurrentBook.createCellStyle();
		style.setFont(font);
		cell.setCellStyle(style);

		final Cell cell1 = header.createCell(myColumnIndex + 1);
		cell1.setCellValue("Student");
		cell1.setCellStyle(style);

		final Cell cell2 = header.createCell(myColumnIndex + 2);
		cell2.setCellValue("School");
		cell2.setCellStyle(style);

		final Cell cell3 = header.createCell(myColumnIndex + 2 + 1);
		cell3.setCellValue("Birthdate");
		cell3.setCellStyle(style);

		final Cell cell4 = header.createCell(myColumnIndex + 2 + 2);
		cell4.setCellValue("Language");
		cell4.setCellStyle(style);

		final Cell cell5 = header.createCell(myColumnIndex + 2 + 2 + 1);
		cell5.setCellValue("Program");
		cell5.setCellStyle(style);

		final Cell cell6 = header.createCell(myColumnIndex + 2 + 2 + 2);
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
		//System.out.println("CURRENT SCHOOL--->" + myCurrentSchool);
		myCurrentBook = new HSSFWorkbook();


		final String school = myCurrentSchool;

		final String[] schoolName = school.split("-");

		myCurrentFilePath = myOutputFolder.getAbsolutePath()
				.concat(File.separator).concat(schoolName[1].trim())
				.concat(".xls");

		myOutputStream = new FileOutputStream(myCurrentFilePath);

		final String name = WorkbookUtil.createSafeSheetName(myCurrentMonth
				.concat(" ").concat(myCurrentYear).concat("--")
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
	 * Private method to write out all the sorted
	 * rows to the workbook created from a portion 
	 * of the other work book.
	 */
	private void writeSortedRows(final Workbook theBook) {

		Iterator<String> iterator = myCellMap.keySet().iterator();

		int rowIndex = 3;

		while (iterator.hasNext()) {

			Row row = theBook.getSheetAt(0).createRow(rowIndex);
			final Cell[] cells = myCellMap.get(iterator.next());

			for (int i = 0; i < 7; i++) {

				row.createCell(i).setCellValue(cells[i].getStringCellValue());
				//System.out.print("cell " + row.getCell(i).getStringCellValue());
			}
			//System.out.println();
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

		myRowIndex = 2;
		myColumnIndex = 0;

		final FileOutputStream oldStream = myOutputStream;

		final Workbook oldBook = myCurrentBook;

		for (int i = 0; i < 7; i++) {

			oldBook.getSheetAt(0).autoSizeColumn(i, false);

		}

		final String name = myCurrentSchool;
		final String[] schoolName = name.split("-");

		myFileList.add(new File(myOutputFolder.getAbsolutePath()
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
					bytes, emails, myCurrentFilePath, myCurrentMonth);
			myDataStack.push(mySchoolData);

		}

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

			final Row newRow = mySheet.createRow(i);
			newRow.createCell(LANGUAGE_CONSTANT).setCellValue(
					language.getLanguageLabel());

			totalCount = language.getCountLabel() + totalCount;
			newRow.createCell(LANGUAGE_CONSTANT + 1).setCellValue(
					language.getCountLabel());

			if (mySLACount != null) {
				//System.out.println("Passing " + myCurrentSchool);
				mySLACount.writeToSheet(language.getLanguageLabel(),
						myCurrentSchool, language.getCountLabel());

			}

			i++;

		}

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
		countCell.setCellValue(totalCount);

	}

}
