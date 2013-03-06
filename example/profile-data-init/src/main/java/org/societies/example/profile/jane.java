package org.societies.example.profile;

public class jane extends BaseUser{

	@Override
	public String getName() {
		
		return "jane";
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
		
		return "jane@societies.org";
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

	@Override
	public String getSkills() {
		
		return "pilot,videoGames";
	}
	
	@Override
	public String[] getFullyTrustedUsers() {
		
		return new String[] { "john.societies.local" };
	}
	 
	@Override
	public String[] getMarginallyTrustedUsers() {
		
		return new String[] {};
	}
	
	@Override
	public String[] getNonTrustedUsers() {
		
		return new String[] {};
	}
	 
	@Override
	public String[] getFullyTrustedCommunities() {
		
		return new String[] {};
	}
	 
	@Override
	public String[] getMarginallyTrustedCommunities() {
		
		return new String[] {};
	}
	 
	@Override
	public String[] getNonTrustedCommunities() {
		
		return new String[] {};
	}
	 
	@Override
	public String[] getFullyTrustedServices() {
		
		return new String[] {};
	}
	 
	@Override
	public String[] getMarginallyTrustedServices() {
		
		return new String[] {};
	}
	 
	@Override
	public String[] getNonTrustedServices() {
		
		return new String[] {};
	}
}
