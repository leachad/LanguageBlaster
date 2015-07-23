/**
 * 
 */
package resources;

/**
 * @author aleach
 *
 */
public enum ViewResource {
	WRITE_SLA_PROMPT("Write to SLA Overview File?"), READ_BOOK_BUTTON("Choose Monthly Master Workbook"),
	RUN_PARSER_BUTTON("Run Book Parser"), FILE_OUTPUT_LABEL("File Path of Master Book");
	
	public final String text;
	
	ViewResource(String theMessage) {
		text = theMessage;
	}
}
