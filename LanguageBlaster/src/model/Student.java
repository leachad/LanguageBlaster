package model;

public class Student {

	/** Private fields of a Student Object.*/
	private int myStudentID;
	private String myStudentName;
	private String myBuilding;
	private String myBirthdate;
	private String myLanguage;
	private char myInstructionalModel;
	private String myGrade;
	
	public Student(final int theStudentID, final String theStudentName,
			final String theBuilding, final String theBirthdate,
			final String theLanguage, final char theInstructionalModel,
			final String theGrade) {
		myStudentID = theStudentID;
		myStudentName = theStudentName;
		myBuilding = theBuilding;
		myBirthdate = theBirthdate;
		myLanguage = theLanguage;
		myInstructionalModel = theInstructionalModel;
		myGrade = theGrade;
	}
	
	public int getMyStudentID() {
		return myStudentID;
	}

	public String getMyStudentName() {
		return myStudentName;
	}

	public String getMyBuilding() {
		return myBuilding;
	}

	public String getMyBirthdate() {
		return myBirthdate;
	}

	public String getMyLanguage() {
		return myLanguage;
	}

	public char getMyInstructionalModel() {
		return myInstructionalModel;
	}

	public String getMyGrade() {
		return myGrade;
	}
	
	@Override
	public String toString() {
		return "Name: " + myStudentName + "\n"
				+ "ID: " + myStudentID + "\n"
				+ "Building: " + myBuilding + "\n"
				+ "Grade: " + myGrade + "\n"
				+ "Instructional Model: " + myInstructionalModel + "\n"
				+ "Language: " + myLanguage + "\n"
				+ "Birthdate: " + myBirthdate + "\n-----------------";
	}
}
