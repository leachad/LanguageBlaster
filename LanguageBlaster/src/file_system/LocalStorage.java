/**
 * 
 */
package file_system;

import java.io.File;

import view.LBDate;

/**
 * @author aleach
 *
 */
public class LocalStorage {

	/** static field to hold a reference to the store path. */
	private static final String STORAGE_PATH = "G:\\groups\\Elem Education\\ESL";
	
	/** static field to hold the SLA Blank. */
	private static final String SLA_BLANK_PATH = "G:\\groups\\Elem Education\\ESL\\SLA Blank [DO NOT MOVE OR DELETE]\\SLA_Blank.xls";
	
	public static File myOutputFolder;

	/** Static field to hold a day array. */
	public static final String[] SELECT_DATA_BEGINNING = {"SELECT ROW TO START", "1", "2", "3",
			"4", "5", "6", "7", "8", "9", "10"};

	/**
	 * Private method to create a directory used to store all newly created
	 * excel books.
	 */
	public static String createDirectory() {

		myOutputFolder = new File(STORAGE_PATH.concat(File.separator)
				.concat(chooseParentFolder()).concat(File.separator)
				.concat("ESL_Counts--").concat(LBDate.getCurrentMonth())
				.concat("-").concat(LBDate.getCurrentDay()).concat("-")
				.concat(LBDate.getCurrentYear()));

		myOutputFolder.mkdir();

		return myOutputFolder.toString();

	}
	
	/**
	 * Returns the File Path of the SLA Blank XLS Book.
	 * @return theSLABlankPath
	 */
	public static String getSLABlankPath() {
		return SLA_BLANK_PATH;
	}

	/**
	 * Private method used to place the outputfolder into the correct parent
	 * folder in the g drive.
	 * 
	 * @return the String of the parent folder.
	 */
	private static String chooseParentFolder() {
		final StringBuilder sb = new StringBuilder();
		final int year = Integer.parseInt(LBDate.getCurrentYear()) + 1;

		switch (LBDate.getCurrentMonth()) {
		case "September":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "October":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "November":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "December":
			sb.append(Integer.toString(year) + " " + "ESL_Counts");
			break;
		case "January":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		case "February":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		case "March":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		case "April":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		case "May":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		case "June":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		case "July":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		case "August":
			sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");
			break;
		default:
			sb.append(makeNewParentFolder());
			break;

		}

		return sb.toString();
	}

	/**
	 * Private method to create a new parent folder in case a month is not
	 * chosen.
	 * 
	 * @return file path of a new parent folder.
	 */
	private static String makeNewParentFolder() {
		final StringBuilder sb = new StringBuilder();
		sb.append(LBDate.getCurrentYear() + " " + "ESL_Counts");

		return sb.toString();
	}

}
