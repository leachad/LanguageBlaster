package model;

public class Email {
	
	/**Private field to hold the String*/
	private String myEmailAddress;
	
	/**Private field to hold the name of the school.*/
	private String mySchool;
	
	public Email(final String theSchool,
			final String theEmail) {
		
		myEmailAddress = theEmail;
		mySchool = theSchool;
	}
	
	/**Public method to return 
	 * the array of email addresses.
	 * @return myEmailAddresses is the
	 * array of Email addresses 
	 * to send out.
	 */
	public String getEmails() {
		
		return myEmailAddress;
	}
	
	/**Public method to return
	 * the name of the school for
	 * a flag variable when deciding
	 * where to send out the current
	 * email and attachment.
	 * @return mySchool is the school
	 * name.
	 */
	public String getSchool() {
		
		return mySchool;
		
	}


}
