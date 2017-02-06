package model;

public class Student {

	/** Private fields of a Student Object.*/
	private int myStudentID;
	private String myStudentName;
	private String myBuilding;
	private String myBirthdate;
	private String myLanguage;
	private String myInstructionalModel;
	private String myGrade;
	
	public Student(final int theStudentID, final String theStudentName,
			final String theBuilding, final String theBirthdate,
			final String theLanguage, final String theInstructionalModel,
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

	public String getMyInstructionalModel() {
		return myInstructionalModel;
	}

	public String getMyGrade() {
		return myGrade;
	}
	
	@Override
	public String toString() {
		return "Name: " + myStudentName + "\n"
				+ "ID: " + myStudentID + "\n"
				+ "Grade: " + myGrade + "\n"
				+ "Building: " + myBuilding + "\n"
				+ "Birthdate: " + myBirthdate + "\n"
				+ "Language: " + myLanguage + "\n"
				+ "Instructional Model: " + myInstructionalModel + "\n-----------------";
	}
}
