/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel;


public interface GeneralInfo {
	/**
     * returns the id of the generalInfo node, a.k.a the method is called getName just to stay coeherent with the
     * others nodes
     * @return GeneralInfo node id
     */
	public String getName();
		
	
	/**
     * Sets the GeneralInfo node id , a.k.a the method is called getName just to stay coeherent with the
     * others nodes
     * @param name
     *            id of the GeneralInfo node
     */
	public void setName( String name );

	/**
	 * returns the first name of the user 
	 * @return String firstName
	 */
	public String getFirstName();

	/**
	 * sets the FirstName of the user, 
	 * @param firstName
	 * 			firstName of the user
	 */
	public void setFirstName(String firstName);

	/**
	 * returns the lastName of the user
	 * @return String lastName
	 */
	public String getLastName();
	
	/**
	 * sets the lastName of the user
	 * @param lastName
	 * 			lastName of the user
	 */
	public void setLastName(String lastName);
	
	/**
	 * returns the birthday of the user
	 * @return String birthday
	 */
	public String getBirthday();
	
	/**
	 * set the birthday of the user
	 * @param birthday
	 * 			String birthday of the user
	 */
	public void setBirthday(String birthday);
	
	/**
	 * returns the sex of the user
	 * @return String sex
	 */
	public String getGender();
	
	/**
	 * set the sex of the user
	 * @param sex
	 * 			String sex of user
	 */	
	public void setGender(String sex);
	
	/**
	 * returns the hometown of the user
	 * @return String hometown
	 */
	public String getHometown();
	
	/**
	 * set the hometown of the user
	 * @param hometown
	 * 			String hometown of the user
	 */
	public void setHometown(String hometown);
	
	/**
	 * returns the current location of the user
	 * @return String current location 
	 */
	public String getCurrentLocation();
	
	/**
	 * sets the current location of the user
	 * @param String current location
	 */
	public void setCurrentLocation(String currentLocation);

	/**
	 * returns the political of the user
	 * @return String political 
	 */
	public String getPolitical();
	
	/**
	 * sets the political of the user
	 * @param String political
	 */
	public void setPolitical(String political);
	
	/**
	 * returns the religious of the user
	 * @return String religious
	 */
	public String getReligion();
	
	/**
	 * sets the religious of the user
	 * @param String religious
	 */
	public void setReligion(String religious);
}
