/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel;


public interface SocialPageCategory {
	
	/**
     * returns the name of the fan page category, this is actually the page category type from facebook
     * @return String fanpagecategory name
     */
	public String getName();
	
	
	/**
     * Sets the fanpagecategory name,this is actually the page category type from facebook
     * @param name
     *            name of fanpage category
     *                 */
	
	void setName( String name );

}
