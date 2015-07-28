/**
 * 
 */
package exceptions;

/**
 * @author aleach
 *
 */
public enum BlasterError {
	PARSE_ERROR("Unable to Parse the Workbook"), READ_ERROR("Unable to Read the Workbook");
	
	public final String text;
	
	BlasterError(String theError) {
		text = theError;
	}
}