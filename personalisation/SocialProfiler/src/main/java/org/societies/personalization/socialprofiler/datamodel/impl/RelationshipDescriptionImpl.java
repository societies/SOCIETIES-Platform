/**
 * 
 */
package org.societies.personalization.socialprofiler.datamodel;

import org.neo4j.graphdb.Relationship;



public class DescriptionImpl implements Description, NodeProperties  {

	private final Relationship underlyingRel;
    
	
	/**
	 * @param underlyingRel Constructor using the relationship
	 */
	public DescriptionImpl(final Relationship underlyingRel) {
		this.underlyingRel = underlyingRel;
	}

	// return the relationship between the 2 users
	public Relationship getUnderlyingRel() {
		return underlyingRel;
	}

	
	//@Override
	public String getName() {
		if ( underlyingRel.hasProperty( DESCRIPTION_PROPERTY ) )
        {
            return (String) underlyingRel.getProperty( DESCRIPTION_PROPERTY );
        }
        return null;
	}

	//@Override
	public void setName(String name) {
		underlyingRel.setProperty( DESCRIPTION_PROPERTY, name );
	}
	
	//@Override
	public double getCost() {
		if ( underlyingRel.hasProperty( COST_PROPERTY ) )
        {
            return (double) Double.parseDouble(underlyingRel.getProperty( COST_PROPERTY ).toString());
        }
        return -5;
	}

	//@Override
	public void setCost(Double cost) {
		underlyingRel.setProperty( COST_PROPERTY, cost );
	}
	
	
	
	
	/**
	 * *return the first person implied in this relationship			
	 */
	//public Person getFirstPerson();
	/**
	 * *return the second person implied in this relationship
	 */
	//public Person getSecondPerson();
	
	
	
		   
}
