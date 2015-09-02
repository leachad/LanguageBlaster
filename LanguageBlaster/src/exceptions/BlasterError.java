/**
 * 
 */
package exceptions;

/**
 * @author leachad
 *
 */
public enum BlasterError {
	PARSE_BOOK_ERROR("Unable to Parse the Workbook"), READ_BOOK_ERROR("Unable to Read the Workbook"), EXEC_ERROR("Unable to Execute in System Environment"),
	CREATE_BOOK_ERROR("Unable to Create the Workbook"), EMPTY_CELL("NULL"), PARSE_FILE_ERROR("Unable to Parse that File"), READ_FILE_ERROR("Unable to Read that File"),
	CREATE_FILE_ERROR("Unable to Create the File"), SEND_EMAIL_ERROR("Unable to Send that Email"), EMAIL_FILE_ERROR("Unable to Read that flat file of Emails"),
	FILE_LOCATE_ERROR("Unable to Locate that File"), PRINT_FILE_ERROR("Unable to Print that File(s)");
	
	public final String text;
	
	BlasterError(String theError) {
		text = theError;
	}
}
