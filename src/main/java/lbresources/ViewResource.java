/**
 * 
 */
package lbresources;

/**
 * @author aleach
 *
 */
public enum ViewResource {
	WRITE_SLA_PROMPT("Write to SLA Overview File?"), READ_BOOK_BUTTON(
			"Choose Monthly Master Workbook"), RUN_PARSER_BUTTON(
			"Run Book Parser"), FILE_OUTPUT_LABEL("File Path of Master Book"), DATE_PANEL(
			"Date"), FILE_PANEL("File"), EXECUTION_PANEL("Execute"), HEADER_PANEL(
			"Header"), SLA_PANEL("SLA"), WIDGET_PANEL("Widgets"), BASE_PANEL(
			"View Base"), ROW_PROMPT_LABEL("Choose the Row To Sort On"), VIEW_FILE_BUTTON(
			"View Generated Spreadsheets"), VIEW_FILES_CMD("explorer "), BATCH_COMPLETE_ALERT(
			"Completed Batch Publish"), EMAIL_LISTS_BUTTON("Email Lists"), EMAIL_SLA_BUTTON(
			"Email SLA to Pam"), PRINT_LISTS_BUTTON("Print Lists"), EDIT_EMAILS_BUTTON(
			"Edit Emails");

	public final String text;

	ViewResource(String theMessage) {
		text = theMessage;
	}
}
