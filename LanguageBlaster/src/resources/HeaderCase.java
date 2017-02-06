/**
 * 
 */
package resources;

/**
 * @author aleach
 *
 */
public enum HeaderCase {
	BLANK_CELL(Integer.toString(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK)),
	REPORT_TITLE("Report"),
	BUILDING_HEADER("Building"),
	ID_HEADER("Student ID"),
	NAME_HEADER("Student Name"),
	BIRTHDATE_HEADER("Birth Date"),
	LANGUAGE_HEADER("Home Language"),
	MODEL_HEADER("Instructional Model"),
	GRADE_HEADER("Grade");
	
	public String text;
	
	HeaderCase(String message) {
		text = message;
	}
}
