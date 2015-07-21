/**
 * 
 */
package view;

/**
 * @author aleach
 *
 */
public enum ViewResource {
	WRITE_SLA_PROMPT("Write to SLA Overview File?"), READ_BOOK_BUTTON("Choose Monthly Master Workbook"),
	RUN_PARSER_BUTTON("Run Book Parser");
	
	public final String text;
	
	ViewResource(String theMessage) {
		text = theMessage;
	}
}
