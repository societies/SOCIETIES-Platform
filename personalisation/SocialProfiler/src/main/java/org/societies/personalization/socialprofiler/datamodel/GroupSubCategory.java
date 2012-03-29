/**
 * 
 */
package org.societies.personalization.socialprofiler.datamodel;


public interface GroupSubCategory {

	/**
     * returns the name of the group sub category, this is actually the group sub-type from facebook
     * @return String groupSubCategory name
     */
	public String getName();
	
	
	/**
     * Sets the GroupSubCategory name,this is actually the  group sub-type from facebook
     * @param name
     *            name of group sub category
     *                 */
	
	void setName( String name );
	
}
