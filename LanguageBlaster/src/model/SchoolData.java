package model;

import java.util.Set;

/**
 * This class holds the necessary data for the schools. ie Languages and school
 * name. It has a field for a Set of Native Languages for access later when the
 * language counts get written to the SLA form.
 * 
 * @author Andy
 * @version July 2nd, 2014
 *
 */
public class SchoolData {

	/**
	 * Private field to hold a set of Native Languages per school.
	 */
	private Set<NativeLanguage> myLanguageSet;

	/** Private String to hold the correct school name. */
	private String mySchoolName;

	/**
	 * Private field to hold a reference to the Email object held within the
	 * school data.
	 */
	private Email myEmail;

	/** Private field to hold a byte array for output. */
	private byte[] myBytes;

	/** Private field to hold a link to the file address. */
	private String myFileAddress;

	/** Private field to hold a String with current month. */
	private String myCurrentMonth;

	public SchoolData(final Set<NativeLanguage> theLanguageSet,
			final Email theEmail, final byte[] theBytes,
			final String theFileAddress, final String theCurrentMonth) {

		myFileAddress = theFileAddress;

		myLanguageSet = theLanguageSet;

		myEmail = theEmail;

		myBytes = theBytes;

		myCurrentMonth = theCurrentMonth;

	}

	/**
	 * Public method to get the Language Set.
	 * 
	 * @return myLanguageSet will set the correct counts in the SLA scount form.
	 */
	public Set<NativeLanguage> getLanguages() {

		return myLanguageSet;
	}

	/**
	 * Public method to get the school name. Should be compared to the current
	 * row the excel spreadsheet is pointing to.
	 * 
	 * @return mySchoolName will help confirm that the pointer is in the right
	 *         row.
	 */
	public String getSchoolName() {

		return myEmail.getSchool();
	}

	/**
	 * Public method to return the current school name without the building code
	 * for easy distribution via email. Returns the "bare" name of the school
	 * separated from all appended numeric digits, characters, etc.
	 */
	public String getEmailName() {

		return mySchoolName.substring(mySchoolName.indexOf("-") + 1).trim()
				.replaceAll(" ", "%20");
	}

	/**
	 * Public method to return the file address to return the address on the
	 * domain for obtaining the current file for the teacher.
	 * 
	 * @return myFileAddress is the local domain address.
	 */
	public String getFileAddress() {

		return myFileAddress;
	}

	/**
	 * Public method to return the byte array when new email address is being
	 * called. This class will also store an email address for the teacher that
	 * the email will be sent to.
	 * 
	 * @return myBytes is the byte array to generate the attachment for the
	 *         emails.
	 * 
	 */
	public byte[] getByteArray() {

		return myBytes;
	}

	/**
	 * Public method to return an array of Strings which will contain the emails
	 * to send to the teachers.
	 * 
	 * @return myAddresses is the list of emails. (usually 1, but sometimes
	 *         there are coteachers.)
	 */
	public String[] getEmailAddresses() {

		return myEmail.getEmails();
	}

	/**
	 * Public method to return the current month to the end user for use in an
	 * email subject line.
	 * 
	 * @return myCurrentMonth is the current month of reports being run.
	 */
	public String getCurrentMonth() {

		return myCurrentMonth;

	}

}
