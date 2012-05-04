/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel;

/**
 * used to describe a relation between 2 persons , 
 * what they have in common
 * Note : for facebook all the relationships are symetric , there 
 * is no INCOMING or OUTGOIING but only BOTH
 */
public interface RelationshipDescription  {
	/**
	 * return name of description 
	 */
	public String getName();
	/**
	 * *set's the name for the description
	 */
	public void setName(String name);
	

	public double getCost() ;
	
	public void setCost(Double cost);


}
