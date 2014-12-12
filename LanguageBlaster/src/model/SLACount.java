/**
 * 
 */
package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 
 * This class wwrites all the 
 * @author Andrew Leach
 * @version 1.0.
 *
 */
public class SLACount {

	/** Private field to hold the constant where the other row is 
	 * found on the SLA Sheet.
	 */
	private static final int OTHER_ROW_INDEX = 61;
	
	/** Private field to hold a date row constant.*/
	private static final int DATE_ROW_INDEX = 1;
	
	/** Private field to hold an instance of a language. */
	private final SLACounter myVietCounter = new SLACounter(1);

	/** Private field to hold an instance of a language. */
	private final SLACounter myUkrCounter = new SLACounter(2);

	/** Private field to hold an instance of a language. */
	private final SLACounter myTagCounter = new SLACounter(3);

	/** Private field to hold an instance of a language. */
	private final SLACounter mySpanCounter = new SLACounter(4);

	/** Private field to hold an instance of a language. */
	private final SLACounter mySamCounter = new SLACounter(5);

	/** Private field to hold an instance of a language. */
	private final SLACounter myRussCounter = new SLACounter(6);

	/** Private field to hold an instance of a language. */
	private final SLACounter myMoldCounter = new SLACounter(7);

	/** Private field to hold an instance of a language. */
	private final SLACounter myLaoCounter = new SLACounter(8);

	/** Private field to hold an instance of a language. */
	private final SLACounter myKorCounter = new SLACounter(9);

	/** Private field to hold an instance of a language. */
	private final SLACounter myCambCounter = new SLACounter(10);

	/** Private field to hold an instance of a language. */
	private final SLACounter myArabCounter = new SLACounter(11);

	/** Private field to hold an instance of a language. */
	private final SLACounter myOtherCounter = new SLACounter(12);

	/** Private field to hold a reference to the file path. */
	private final String inFilePath;
	
	/** Private field to hold the date that the report is run.*/
	private final String myReportDate;

	private FileOutputStream myOutput;

	/** Private field to hold a reference to the input stream. */
	private FileInputStream myInput;

	/** Private field to hold a reference to the active work book. */
	private HSSFWorkbook mySlaBook;

	/** Private field to hold a reference to the writeable SLA sheet. */
	private HSSFSheet mySLASheet;

	/** Private field to hold a reference to the file path. */
	private String outFilePath;

	public SLACount(final String theFilePath, final String theDirectory, final String theReportDate) {

		inFilePath = theFilePath;
		outFilePath = theDirectory.concat(File.separator).concat(
				"SLA_COUNT.xls");
		myReportDate = theReportDate;
		createWorkSheet();
		createOutputFile();
	}

	/**
	 * Private helper method to creat the worksheet to read all language counts
	 * over to the SLA count form Pam uses for her audits.
	 */
	private void createWorkSheet() {

		try {

			myInput = new FileInputStream(new File(inFilePath));

			mySlaBook = new HSSFWorkbook(myInput);
			mySLASheet = mySlaBook.getSheetAt(0);
			writeReportDate();

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Private method to write the date that the report is run and distributed
	 * monthly for the ESL teachers.
	 */
	private void writeReportDate() {
		
		for (Row row : mySLASheet) {
			
			if (row.getRowNum() == DATE_ROW_INDEX) {
				
				for (Cell cell : row) {
					
					if (cell.getColumnIndex() == 0) {
						cell.setAsActiveCell();
						cell.setCellValue(myReportDate);
					}
				}
			}
		}
	}

	/**
	 * Private method to create an output file in the same folder path as the
	 * generated excel documents.
	 * 
	 */
	private void createOutputFile() {

		try {
			myOutput = new FileOutputStream(new File(outFilePath));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Public method to read in a set of data to the SLA Sheet based upon what
	 * is read in from the main worksheet.
	 */
	public void writeToSheet(final String theLanguage, String theSchool,
			final int theCount) {
		//System.out.println(theSchool);
		int columnIndex = 0;

		boolean schoolFound = false;
		boolean otherLanguage = false;
		boolean otherSchool = false;
		boolean otherOther = false;

		for (Row row : mySLASheet) {

			for (Cell cell : row) {

				if (cell.getColumnIndex() == 0 
						& cell.getCellType() == Cell.CELL_TYPE_STRING) {
								
					final String name = theSchool.substring(theSchool.indexOf("-") + 1).trim();
					//System.out.println("School name is " + theSchool + "--Cell " + cell.getStringCellValue());
					columnIndex = chooseLanguage(theLanguage);
					
					if (columnIndex < myOtherCounter.getIndex()
							&& chooseSchool(cell.getStringCellValue().trim(), name)) {

						//System.out.println("C-SCHOOL " + cell.getStringCellValue() + " SCHOOL --> " + theSchool + " LANGUAGE --> " + theLanguage);
						schoolFound = true;

					} else if (columnIndex == myOtherCounter.getIndex() 
							&& chooseSchool(cell.getStringCellValue().trim(), name)) {
						////System.out.println("C-SCHOOL " + cell.getStringCellValue() + " SCHOOL --> " + theSchool + " LANGUAGE --> " + theLanguage);
						otherLanguage = true;

					} else if (columnIndex < myOtherCounter.getIndex()
							&& findOther(name)
							&& row.getRowNum() == OTHER_ROW_INDEX){
						//System.out.println("Row is " + row.getRowNum() + "column-->" + columnIndex +" language--> " + theLanguage);
						otherSchool = true;

					} else if (columnIndex == myOtherCounter.getIndex()
							&& findOther(name)
							&& row.getRowNum() == OTHER_ROW_INDEX) {
						//System.out.println("!!!Other other--Row is " + row.getRowNum() + "column-->" + columnIndex+ " language--> " + theLanguage);
						otherOther = true;

					}


				} else {

					if (schoolFound
							&& cell.getColumnIndex() == columnIndex) {

						//cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setAsActiveCell();
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue((int) theCount);
						
						System.out.println(theSchool + " " + cell.getNumericCellValue());

						schoolFound = false;


					} else if (otherLanguage && cell.getColumnIndex() == columnIndex) {

						
						int otherCount = (int) cell.getNumericCellValue();
						otherCount = otherCount + theCount;
						
						cell.setAsActiveCell();
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(otherCount);
						//cell.setAsActiveCell();


						otherCount = 0;
						otherLanguage = false;


					} else if (otherSchool
							&& cell.getColumnIndex() == columnIndex) {
						System.out.println("OTHERSCHOOL--->Index " + columnIndex);
						int otherCount = (int) cell.getNumericCellValue();
						otherCount = otherCount + theCount;
						
						cell.setAsActiveCell();
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(otherCount);
						System.out.println("OTHER SCHOOL cell value " + cell.getNumericCellValue());
						//cell.setAsActiveCell();
						
						otherSchool = false;

					} else if (otherOther
							&& cell.getColumnIndex() == columnIndex) {
						System.out.println("OTHEROTHER--->Index	" + columnIndex);
						int otherCount = (int) cell.getNumericCellValue();
						otherCount = otherCount + theCount;
						
						cell.setAsActiveCell();
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(otherCount);
						System.out.println("OTHER OTHER cell value " + cell.getNumericCellValue());
						//cell.setAsActiveCell();
		
						otherCount = 0;
						
						otherOther = false;
					}
				}

			}
		}
	}
	
	
	/**
	 * Private method to find the other equivalents.
	 */
	private boolean findOther(final String theSchool) {
		
		
		boolean other = false;
		switch(theSchool) {
		case"Exit Release":
			other = true;
			break;
		case"Re-engagement Center":
			other = true;
			break;
		case"Day Reporting School":
			other = true;
			break;
		case"Home-Based":
			other = true;
			break;	
		}
		
		return other;
	}

	/**
	 * Private method to choose whether or not this school is equal to 
	 * the string cell value. 
	 */
	private boolean chooseSchool(final String theCell, final String theSchool) {

		return theCell.equalsIgnoreCase(theSchool);
	}

	/**
	 * Private method to switch between different possibilities for languages.
	 */
	private int chooseLanguage(final String theLanguage) {
		SLACounter sla = null;

		switch (theLanguage) {
		case "Vietnamese":
			sla = myVietCounter;
			break;
		case "Ukrainian":
			sla = myUkrCounter;
			break;
		case "Tagalog":
			sla = myTagCounter;
			break;
		case "Spanish":
			sla = mySpanCounter;
			break;
		case "Samoan":
			sla = mySamCounter;
			break;
		case "Russian":
			sla = myRussCounter;
			break;
		case "Moldavian":
			sla = myMoldCounter;
			break;
		case "Laotian":
			sla = myLaoCounter;
			break;
		case "Korean":
			sla = myKorCounter;
			break;
		case "Cambodian":
			sla = myCambCounter;
			break;
		case "Arabic":
			sla = myArabCounter;
			break;
		case "Other":
			sla = myOtherCounter;
			break;
		default:
			sla = myOtherCounter;
			break;
		}

		return sla.getIndex();
	}

	/**
	 * Public method to close the stream and write everything out to the current
	 * filePath.
	 */
	public void closeBook() {

		FormulaEvaluator evaluator = mySlaBook.getCreationHelper().createFormulaEvaluator();
		for(int sheetNum = 0; sheetNum < mySlaBook.getNumberOfSheets(); sheetNum++) {
			Sheet sheet = mySlaBook.getSheetAt(sheetNum);
			for(Row r : sheet) {
				for(Cell c : r) {
					if(c.getCellType() == Cell.CELL_TYPE_FORMULA) {
						evaluator.evaluateFormulaCell(c);
					}
				}
			}
		}
		
		try {

			mySlaBook.write(myOutput);
			myOutput.close();

		} catch (final FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Private class to create an SLA count object. Associates a specific column
	 * with a specific language.
	 * 
	 */
	private class SLACounter {

		/** Private field to hold the column index. */
		private int myIndex;

		public SLACounter(final int theIndex) {
			myIndex = theIndex;
		}

		/**
		 * Public method to return the index constant.
		 */
		public int getIndex() {
			return myIndex;
		}
	}

}
