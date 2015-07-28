/**
 * 
 */
package file_system;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import model.NativeLanguage;
import model.Student;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import resources.HeaderCase;
import view.LBDate;

/**
 * @author aleach
 *
 */
public class LBParser {

	/**
	 * Private field to hold a reference to the Default Instructional Model,
	 * should one not exist.
	 */
	private static final String DEFAULT_MODEL = "D";

	/**
	 * Private field to hold a reference to the CellMap used for organizing the
	 * rows.
	 */
	private Map<String, List<String[]>> myCellMap;

	/**
	 * Private field used to hold a reference to the Language Map used for
	 * counting student languages.
	 * 
	 */
	private Map<String, Set<NativeLanguage>> myLanguageMap;

	public LBParser() {
		myCellMap = new TreeMap<>();
		myLanguageMap = new TreeMap<>();
	}

	/**
	 * Public method to return the cell map to the calling class.
	 * 
	 * @return theCellMap
	 */
	public Map<String, List<String[]>> getCellMap() {
		return myCellMap;
	}

	/**
	 * Public method to return the Language Map to the caling class.
	 * 
	 * @return theLanguageMap
	 */
	public Map<String, Set<NativeLanguage>> getLanguageMap() {
		return myLanguageMap;
	}

	/**
	 * Private method to read the workbook into a buffer for easier sorting and
	 * extraction of key data.
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 * 
	 */
	public void readWorkBookIntoBuffer(final File theFile,
			final int theSelectedStartIndex) throws InvalidFormatException,
			IOException {

		final Workbook wb = WorkbookFactory.create(theFile);
		final Sheet current = wb.getSheetAt(0);
		final List<Student> rowBuffer = new ArrayList<>(3000);
		for (int i = 0; i < current.getPhysicalNumberOfRows(); i++) {
			if (i >= theSelectedStartIndex) {
				rowBuffer.add(createNewStudent(current.getRow(i)));

			}
		}

		constructCellMap(rowBuffer);

	}

	/**
	 * Public method used to read the potential sorting rows for selection by
	 * the user.
	 * 
	 * @throws IOException
	 * @throws InvalidFormatException
	 * 
	 */
	public List<Row> readPotentialSortingRows(final int theTopRows,
			final File theFile) throws InvalidFormatException, IOException {
		final Sheet current = WorkbookFactory.create(theFile).getSheetAt(0);
		final List<Row> topRows = new ArrayList<>(theTopRows);
		for (int i = 0; i < theTopRows; i++) {
			topRows.add(current.getRow(i));
		}

		return topRows;

	}

	// Debugging
	// private void printRow(Row theRow) {
	// Iterator<Cell> cells = theRow.cellIterator();
	// while (cells.hasNext())
	// System.out.print(cells.next() + " ");
	// System.out.println();
	// }

	/**
	 * Private method used to create a New Student and return it to the
	 * RowBuffer.
	 * 
	 * @param theCurrentRow
	 * @return theNewStudent
	 */
	private Student createNewStudent(final Row theCurrentRow) {
		int studentID = Integer.parseInt(theCurrentRow.getCell(0)
				.getStringCellValue());
		String name = theCurrentRow.getCell(1).getStringCellValue();
		String building = theCurrentRow.getCell(2).getStringCellValue();
		String bdate = theCurrentRow.getCell(3).getStringCellValue();
		String language = theCurrentRow.getCell(4).getStringCellValue();
		String model = theCurrentRow.getCell(5).getStringCellValue();
		if (model.length() == 0)
			model = DEFAULT_MODEL;
		String grade = theCurrentRow.getCell(6).getStringCellValue();
		Student current = new Student(studentID, name, building, bdate,
				language, model.charAt(0), grade);

		return current;
	}

	/**
	 * Private method to construct a cell map from the rowBuffer of Students.
	 * 
	 * @param theRowBuffer
	 *            is the List of Students prepared for Sorting.
	 * 
	 */
	private void constructCellMap(final List<Student> theRowBuffer) {

		// Create School Headers for the First School
		String currentBuilding = theRowBuffer.get(0).getMyBuilding();
		createNewSchoolHeaders(currentBuilding);
		createNewSchoolLanguageMap(currentBuilding);

		for (int i = 0; i < theRowBuffer.size(); i++) {
			System.out.println(theRowBuffer.get(i).toString());
			if (currentBuilding != theRowBuffer.get(i).getMyBuilding()) {
				currentBuilding = theRowBuffer.get(i).getMyBuilding();
				createNewSchoolHeaders(currentBuilding);
				createNewSchoolLanguageMap(currentBuilding);
				myCellMap.get(currentBuilding).add(
						createStudentData(theRowBuffer.get(i)));
				evaluateNativeLanguages(theRowBuffer.get(i).getMyLanguage(),
						currentBuilding);

			} else {
				myCellMap.get(currentBuilding).add(
						createStudentData(theRowBuffer.get(i)));
				evaluateNativeLanguages(theRowBuffer.get(i).getMyLanguage(),
						currentBuilding);
			}

		}
	}

	/**
	 * Private method used to create a new LanguageMap which will count all the
	 * languages in any given school.
	 * 
	 * @param theCurrentSchool
	 *            is the title of the current school in question.
	 */
	private void createNewSchoolLanguageMap(final String theCurrentSchool) {
		myLanguageMap
				.put(theCurrentSchool, new LinkedHashSet<NativeLanguage>());
	}

	/**
	 * Private method to create a String[] representing Student Data that will
	 * be representative of StudentData in a future excel spreadsheet.
	 * 
	 * @param student
	 * @return theCurrentStudent as represented with Strings
	 */
	private String[] createStudentData(final Student student) {
		return new String[] { Integer.toString(student.getMyStudentID()),
				student.getMyStudentName(), student.getMyBuilding(),
				student.getMyBirthdate(), student.getMyLanguage(),
				Character.toString(student.getMyInstructionalModel()),
				student.getMyGrade() };
	}

	/**
	 * Private method to create a new Linked for a school and the corresponding
	 * list of String[] which represents a future excel worksheet.
	 * 
	 * @param theCurrentSchool
	 *            is the Current School Title
	 */
	private void createNewSchoolHeaders(final String theCurrentSchool) {
		myCellMap.put(theCurrentSchool, new LinkedList<String[]>());
		myCellMap.get(theCurrentSchool).add(getTitleCells(theCurrentSchool));
		myCellMap.get(theCurrentSchool).add(getHeaderCells(theCurrentSchool));
	}

	/**
	 * Private method to return an array of Strings that represents the current
	 * Worksheet Title of the list of String[] that will represent a future
	 * excel worksheet.
	 * 
	 * @param theCurrentSchool
	 * @param currentDay
	 * @param currentMonth
	 * @param currentYear
	 * @return
	 */
	private String[] getTitleCells(String theCurrentSchool) {
		return new String[] {
				theCurrentSchool + "--" + LBDate.getCurrentMonth() + "-"
						+ LBDate.getCurrentDay() + "-"
						+ LBDate.getCurrentYear(), null, null, null, null,
				null, null };
	}

	/**
	 * Private method to return the Header Cells to the Beginning of the List
	 * for a new School.
	 * 
	 * @param theCurrentSchool
	 *            is the Current School Title
	 * @return theHeaderCellArray
	 */
	private String[] getHeaderCells(final String theCurrentSchool) {
		return new String[] { HeaderCase.ID_HEADER.text,
				HeaderCase.NAME_HEADER.text, HeaderCase.BUILDING_HEADER.text,
				HeaderCase.BIRTHDATE_HEADER.text,
				HeaderCase.LANGUAGE_HEADER.text, HeaderCase.MODEL_HEADER.text,
				HeaderCase.GRADE_HEADER.text };
	}

	/**
	 * 
	 * TODO This function should be used with writing Private method used to
	 * build the Set of NativeLanguages for a specific school.
	 * 
	 */
	private void evaluateNativeLanguages(final String theCurrentLanguage,
			final String theCurrentSchool) {

		/*
		 * Run the set and see if the languages match or don't match.
		 */
		final Iterator<NativeLanguage> iterator = myLanguageMap.get(
				theCurrentSchool).iterator();
		boolean found = false;

		while (iterator.hasNext()) {

			final NativeLanguage language = iterator.next();
			System.out.println("Evaluating " + language.getLanguageLabel()
					+ " against " + theCurrentLanguage);
			if (language.getLanguageLabel().equals(theCurrentLanguage)) {

				language.increment();
				found = true;
			}
		}

		if (!found) {
			myLanguageMap.get(theCurrentSchool).add(
					new NativeLanguage(theCurrentLanguage));
		}
	}

}
