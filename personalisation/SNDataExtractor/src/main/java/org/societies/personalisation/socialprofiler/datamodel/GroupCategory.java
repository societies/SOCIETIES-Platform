package org.societies.personalisation.socialprofiler.datamodel;


public interface GroupCategory {
	
	
	/**
     * returns the name of the group category, this is actually the group type from facebook
     * @return String groupCategory name
     */
	public String getName();
	
	
	/**
     * Sets the GroupCategory name,this is actually the  group type from facebook
     * @param name
     *            name of group category
     *                 */
	
	void setName( String name );
}
