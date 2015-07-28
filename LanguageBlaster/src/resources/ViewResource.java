/**
 * 
 */
package resources;

/**
 * @author aleach
 *
 */
public enum ViewResource {
	WRITE_SLA_PROMPT("Write to SLA Overview File?"), READ_BOOK_BUTTON(
			"Choose Monthly Master Workbook"), RUN_PARSER_BUTTON(
			"Run Book Parser"), FILE_OUTPUT_LABEL("File Path of Master Book"), DATE_PANEL(
			"Date"), FILE_PANEL("File"), EXECUTION_PANEL("Execute"), HEADER_PANEL(
			"Header"), SLA_PANEL("SLA"), WIDGET_PANEL("Widgets"), BASE_PANEL("View Base");

	public final String text;

	ViewResource(String theMessage) {
		text = theMessage;
	}
}
