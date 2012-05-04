/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel;


public interface SocialPage {
	
	/**
     * returns the name of the fan page, this is actually the page id from facebook
     * @return String fanpage name
     */
	public String getName();
	
	
	/**
     * Sets the fanpage name,this is actually the page id from facebook
     * @param name
     *            name of fanpage
     *                 */
	
	void setName( String name );

	/**
	 * returns the real name of the fan page , the oteher one , name of fan page is totally different,the id
	 * 
	 * @return realName
	 * 				the real name of the fan page
	 */
	
	public String getRealName();
	
	/**
	 *  set the real Name of the fan page
	 * @param realName
	 * 			real name of the fan Page
	 */
	
	public void setRealName(String realName);
	
	/**
	 * returns the type of the fan page
	 * @return	String type of fanPage
	 */
	
	public String getType();
	
	/**
	 * set the type of the fanPage
	 * @param type
	 				type of FanPage
	 */
	
	public void setType(String type);

}
