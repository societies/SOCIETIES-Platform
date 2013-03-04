package org.societies.context.dataInit.impl;

public class User3 extends BaseUser{

	@Override
	public String getName() {
		
		return "User3";
	}

	@Override
	public String getSex() {
		
		return "male";
	}

	@Override
	public String getAge() {
		
		return "18";
	}

	@Override
	public String getLanguages() {
		
		return "english,chinese,japanese";
	}

	@Override
	public String getInterests() {
	
		return "soccer,football,sports";
	}

	@Override
	public String getMovies() {

		return ",ironman,superman";
	}

	@Override
	public String getOccupation() {

		return "unemployed";
	}

	@Override
	public String getStatus() {
		
		return "available";
	}

	@Override
	public String getEmail() {
		
		return "";
	}

	@Override
	public String getBirthday() {
		
		return "25/12/1979";
	}

	@Override
	public String getPoliticalViews() {
		
		return null;
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
		
		return "";
	}

	/* (non-Javadoc)
	 * @see org.societies.context.dataInit.impl.BaseUser#getSkills()
	 */
	@Override
	public String getSkills() {
		return "disaster relief,google maps,translating";
	}
	
}
