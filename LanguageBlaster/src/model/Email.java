package model;

public class Email {
	
	/**Private field to hold the String*/
	private String[] myEmailAddresses;
	
	/**Private field to hold the name of the school.*/
	private String mySchool;
	
	public Email(final String theSchool,
			final String[] theEmails) {
		
		myEmailAddresses = theEmails;
		mySchool = theSchool;
	}
	
	/**Public method to return 
	 * the array of email addresses.
	 * @return myEmailAddresses is the
	 * array of Email addresses 
	 * to send out.
	 */
	public String[] getEmails() {
		
		return myEmailAddresses;
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
	
	/**
	 * Overrides toString method to 
	 * print out the emails in the 
	 * array with appropriate delimiting
	 * tokens.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < myEmailAddresses.length; i++) {
			sb.append(myEmailAddresses[i] + ';');
		}
		return sb.toString();
	}


}
