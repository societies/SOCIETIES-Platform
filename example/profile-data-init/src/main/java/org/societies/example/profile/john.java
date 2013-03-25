package org.societies.example.profile;

public class john extends BaseUser {

	@Override
	public String getName() {
		
		return "john";
	}

	@Override
	public String getSex() {

		return "male";
	}

	@Override
	public String getAge() {
		
		return "30";
	}

	@Override
	public String getLanguages() {
		
		return "english,french,portuguese";
	}

	@Override
	public String getInterests() {
		
		return "sports,cooking,reading";
	}

	@Override
	public String getMovies() {
		
		return "finding nemo,the godfather";
	}

	@Override
	public String getOccupation() {
		
		return "researcher";
	}

	@Override
	public String getStatus() {
		
		return "busy";
	}

	@Override
	public String getEmail() {

		return "john@societies.org";
	}

	@Override
	public String getBirthday() {
		
		return "10/1/1980";
	}

	@Override
	public String getPoliticalViews() {
		
		return "democratic";
	}

	@Override
	public String getLocationSymbolic() {
		
		return "HWCampus";
	}

	@Override
	public String getLocationCoordinates() {
		
		return "1234,1234";
	}

	@Override
	public String getFriends() {
		
		return null;
	}

	@Override
	public String getSkills() {
		
		return "scuba,aerobic";
	}
	
	@Override
	public String[] getFullyTrustedUsers() {
		
		return new String[] {};
	}
	 
	@Override
	public String[] getMarginallyTrustedUsers() {
		
		return new String[] { "jane.societies.local" };
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