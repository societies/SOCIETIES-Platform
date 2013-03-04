package org.societies.context.dataInit.impl;

public class User5 extends BaseUser{

	@Override
	public String getName() {
		
		return "User5";
	}

	@Override
	public String getSex() {
		
		return "male";
	}

	@Override
	public String getAge() {
		
		return "48";
	}

	@Override
	public String getLanguages() {
		
		return "english,spanish,japanese";
	}

	@Override
	public String getInterests() {
	
		return null;
	}

	@Override
	public String getMovies() {

		return "superman,forrest gump";
	}

	@Override
	public String getOccupation() {

		return "employed";
	}

	@Override
	public String getStatus() {
		
		return "free";
	}

	@Override
	public String getEmail() {
		
		return "";
	}

	@Override
	public String getBirthday() {
		
		return "";
	}

	@Override
	public String getPoliticalViews() {
		
		return "";
	}

	@Override
	public String getLocationSymbolic() {
		
		return "HWLectureTheater1";
	}

	@Override
	public String getLocationCoordinates() {
		
		return "1234,1234";
	}

	@Override
	public String getFriends() {
		
		return "User1";
	}

	/* (non-Javadoc)
	 * @see org.societies.context.dataInit.impl.BaseUser#getSkills()
	 */
	@Override
	public String getSkills() {
		return "watching TV,soccer";
	}
	
}
