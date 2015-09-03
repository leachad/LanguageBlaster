/**
 * 
 */
package file_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import model.Email;
import view.LBDate;
import exceptions.BlasterError;

/**
 * @author aleach
 *
 */
public class LocalStorage {

	/** static field to hold a reference to the store path. */
	private static final String STORAGE_PATH = "G:\\groups\\Elem Education\\ESL";

	/** static field to hold a reference to the email path. */
	private static final String EMAIL_PATH = System.getProperty("user.dir")
			.concat("\\src\\file_system\\emailList.txt");
	
	/** static fields to hold references to the file delimiters.*/
	private static final String NEW_LINE = "\n";
	private static final String DELIMITER_ONE = ",";
	private static final String DELIMITER_TWO = ";";

	/** static field to hold the SLA Blank. */
	private static final String SLA_BLANK_PATH = "G:\\groups\\Elem Education\\ESL\\SLA Blank [DO NOT MOVE OR DELETE]\\SLA_Blank.xls";

	/** static File used as a Directory to hold the outputfolder.*/
	public static File myOutputFolder;

	private static Map<String, Email> myEmailMap;
	
	private static int myNumColumns;

	/** Static field to hold a day array. */
	public static final String[] SELECT_DATA_BEGINNING = {
			"SELECT ROW TO START", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10" };

	
	public static void setNumColumns(final int theColumns) {
		myNumColumns = theColumns;
	}
	
	public static int getNumColumns() {
		return myNumColumns;
	}
	/**
	 * Private method to create a directory used to store all newly created
	 * excel books.
	 */
	private static String createDirectory() {

		myOutputFolder = new File(STORAGE_PATH.concat(File.separator)
				.concat(chooseParentFolder()).concat(File.separator)
				.concat("ESL_Counts--").concat(LBDate.getCurrentMonth())
				.concat("-").concat(LBDate.getCurrentDay()).concat("-")
				.concat(LBDate.getCurrentYear()));

		myOutputFolder.mkdir();

		return myOutputFolder.toString();

	}
	
	public static String getOutputDirectory() {
		return myOutputFolder.toString();
	}
	
	public static String getWorkingDirectory() {
		if (myOutputFolder == null)
			return createDirectory();
		else
			return myOutputFolder.toString();
	}

	/**
	 * Method used to return the list of emails to the calling code.
	 */
	public static Map<String, Email> getEmailMap() {
		if (myEmailMap != null)
			return myEmailMap;
		else
			return readEmailFile();
	}
	
	/**
	 * Method used to write out the list of emails to a .ser file.
	 */
	public static void saveEmailState(final Map<String, Email> theEmailMap) {
		FileWriter out = null;

		try {
			out = new FileWriter(new File(EMAIL_PATH));
			Iterator<String> iterator = theEmailMap.keySet().iterator();
			
			while (iterator.hasNext()) {
				String nextkey = iterator.next();
				out.write(buildEmailLine(myEmailMap.get(nextkey)));
			}
			

			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, Email> readEmailFile() {
		myEmailMap = new HashMap<>();
		
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					new File(EMAIL_PATH)));

			while (reader.ready()) {

				final String line = reader.readLine();
				Email current = parseLine(line, false);
				myEmailMap.put(current.getSchool(), current);

			}
			reader.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, BlasterError.EMAIL_FILE_ERROR.text + e.getMessage());
		}

		return myEmailMap;

	}

	private static String buildEmailLine(final Email theEmail) {
		StringBuilder sb = new StringBuilder();
		sb.append(theEmail.getSchool());
		sb.append(DELIMITER_ONE);
		for (int i = 0; i < theEmail.getEmails().length; i++) {
			sb.append(theEmail.getEmails()[i]);
			sb.append(DELIMITER_TWO);
		}
		
		sb.append(NEW_LINE);
		
		return sb.toString();
	}
	/**
	 * Private method to parse a new line from the email document.
	 * 
	 * @param theLine
	 *            is the string to parse.
	 * @param isOutgoing
	 *            is the boolean determining if the write is out bound or the
	 *            read is in bound. true is out bound.
	 */
	private static Email parseLine(final String theLine,
			final boolean isOutgoing) {

		final String[] firstSplit = theLine.split(DELIMITER_ONE); // split the
																	// line

		final String school = firstSplit[0].trim(); // save the school

		String[] emails = new String[firstSplit[1].split(DELIMITER_TWO).length];
		if (!isOutgoing) {
			String[] secondSplit = firstSplit[1].split(DELIMITER_TWO);
			for (int i = 0; i < secondSplit.length; i++) {
				emails[i] = secondSplit[i];
			}

		} else {
			String[] secondSplit = firstSplit[1].split(DELIMITER_TWO);
			for (int i = 0; i < secondSplit.length; i++) {
				emails[i] = secondSplit[i];
			}

		}

		return new Email(school, emails);

	}

	/**
	 * Returns the File Path of the SLA Blank XLS Book.
	 * 
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
