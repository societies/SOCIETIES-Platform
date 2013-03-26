package org.societies.context.dataInit.impl;

public class User1 extends BaseUser{

	@Override
	public String getName() {
		
		return "User1";
	}

	@Override
	public String getSex() {
		
		return "female";
	}

	@Override
	public String getAge() {
		
		return "28";
	}

	@Override
	public String getLanguages() {
		
		return "english,french,greek";
	}

	@Override
	public String getInterests() {
	
		return "cooking,reading,music";
	}

	@Override
	public String getMovies() {

		return "batman,superman";
	}

	@Override
	public String getOccupation() {

		return "unemployed";
	}

	@Override
	public String getStatus() {
		
		return "free";
	}

	@Override
	public String getEmail() {
		
		return "user1.societies.local@ict-societies.eu";
	}

	@Override
	public String getBirthday() {
		
		return "15/6/1981";
	}

	@Override
	public String getPoliticalViews() {
		
		return "liberal";
	}

	@Override
	public String getLocationSymbolic() {
		
		return null;
	}

	@Override
	public String getLocationCoordinates() {
		
		return null;
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
		return "UNSARAG,translation,quadrotors";
	}
	
}
