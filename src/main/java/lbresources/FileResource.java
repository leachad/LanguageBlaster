/**
 * 
 */
package lbresources;

/**
 * @author aleach
 *
 */
public enum FileResource {
	XLS("xls"), XLSX("xlsx"), ODT("odt"), TXT("txt"), EXT_CONCAT("."), DASH("-"),
	SPACE(" "), ENCODED_SPACE("%20"), SEMI_COLON(";"), MAIL_TO("mailto:"), SUBJECT("subject"),
	BODY("body"), ASSIGN_ARGS("="), START_ARGS("?"), APPEND_ARGS("&");
	
	public String text;
	
	FileResource(final String theText) {
		text = theText;
	}
}
