/**
 * 
 */
package exceptions;

/**
 * @author aleach
 *
 */
public enum BlasterError {
	PARSE_ERROR("Unable to Parse the Workbook"), READ_ERROR("Unable to Read the Workbook"), EXEC_ERROR("Unable to Execute in System Environment"),
	CREATE_ERROR("Unable to Create the Workbook");
	
	public final String text;
	
	BlasterError(String theError) {
		text = theError;
	}
}
