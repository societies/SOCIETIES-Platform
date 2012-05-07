/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel;


public interface Interests {
	/**
     * returns the id of the interests node, a.k.a the method is called getName just to stay coeherent with the
     * others nodes
     * @return Interests node id
     */
	public String getName();
		
	
	/**
     * Sets the Interests node id , a.k.a the method is called getName just to stay coeherent with the
     * others nodes
     * @param name
     *            id of the interests node
     */
	public void setName( String name );

	/**
	 * returns the activities of the user 
	 * @return String activities
	 */
	public String getActivities();

	/**
	 * sets the activities of the user, 
	 * @param activities
	 * 			activities of the user
	 */
	public void setActivities(String activities);

	/**
	 * returns the interests of the user
	 * @return String interests
	 */
	public String getInterests();
	
	/**
	 * sets the interests of the user
	 * @param interests
	 * 			interests of the user
	 */
	public void setInterests(String interests);
	
	/**
	 * returns the music of the user
	 * @return String music
	 */
	public String getMusic();
	
	/**
	 * set the music of the user
	 * @param music
	 * 			String music of the user
	 */
	public void setMusic(String music);
	
	/**
	 * returns the movies of the user
	 * @return String movies
	 */
	public String getMovies();
	
	/**
	 * set the movies of the user
	 * @param movies
	 * 			String movies of user
	 */	
	public void setMovies(String movies);
	
	/**
	 * returns the books of the user
	 * @return String books
	 */
	public String getBooks();
	
	/**
	 * set the boooks of the user
	 * @param books
	 * 			String books of the user
	 */
	public void setBooks(String books);
	
	/**
	 * returns the quotation of the user
	 * @return String quotations 
	 */
	public String getQuotations();
	
	/**
	 * sets the quotations of the user
	 * @param String quotations
	 */
	public void setQuotations(String quotations);

	/**
	 * returns the about me of the user
	 * @return String aboutMe 
	 */
	public String getAboutMe();
	
	/**
	 * sets the aboutMe of the user
	 * @param String aboutMe
	 */
	public void setAboutMe(String aboutMe);
	
	/**
	 * returns the profile update time
	 * @return String profileUpdatetime 
	 */
	public String getProfileUpdateTime();
	
	/**
	 * sets the profile update time of the user
	 * @param String profileUpdateTime
	 */
	public void setProfileUpdateTime(String profileUpdateTime);
}
