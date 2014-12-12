/**
 * 
 */
package model;


/**
 * @author aleach
 *
 */
public class NativeLanguage {

	/**Private field to hold the language type.*/
	private String myLanguage;
	
	/**Private field to hold a language count.*/
	private int myCount;

	/**Constructor takes in a new  
	 * @param theLanguage if the data 
	 * structure cannot find it.
	 * Every time a new NativeLanguage is
	 * constructed myCount is initialized
	 * to one and then if other lines
	 * need to access it they call a separate
	 * increment method.
	 */
	public NativeLanguage(final String theLanguage) {
		
		myLanguage = theLanguage;
		myCount++;
	}
	
	/**Public method to increment
	 * the count of languages.
	 * If the language already 
	 * exists in the data structure
	 * then the LanguageFrame 
	 * calls this method so that an
	 * additional increment will be
	 * completed.
	 */
	public void increment() {
		
		myCount++;
	}
	
	/**Public method to 
	 * get the Language
	 * Label content for adding
	 * to the excel workbook.
	 * @return myLanguage is
	 * the String to return.
	 */
	public String getLanguageLabel() {
		
		return myLanguage;
	}
	
	/**Public method to 
	 * return the count of 
	 * students that speak
	 * this native language.
	 * @return myCount is the 
	 * current count of languages.
	 */
	public int getCountLabel() {
	
		return myCount;
	}
}
