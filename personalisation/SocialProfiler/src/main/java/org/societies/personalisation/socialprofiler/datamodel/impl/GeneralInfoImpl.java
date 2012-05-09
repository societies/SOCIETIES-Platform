/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel.impl;


import org.neo4j.graphdb.Node;
import org.societies.personalisation.socialprofiler.datamodel.GeneralInfo;
import org.societies.personalisation.socialprofiler.datamodel.utils.NodeProperties;


public class GeneralInfoImpl implements GeneralInfo, NodeProperties{

	private final Node underlyingNode;
	
	/**
	 * constructor of General Info
	 * @param underlyingNode
	 * 			Node
	 */			
	public GeneralInfoImpl(Node underlyingNode) {
		super();
		this.underlyingNode = underlyingNode;
	}

	/**
	 * returns the underlying node of the GeneralInfo
	 * @return	Node underlyingNode
	 */
	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	//@Override
	public String getName() {
		return (String) underlyingNode.getProperty( NAME_PROPERTY );
	}

	//@Override
	public void setName(String name) {
		underlyingNode.setProperty( NAME_PROPERTY, name );
	}

	//@Override
	public String getBirthday() {
		return (String) underlyingNode.getProperty( BIRTHDAY_PROPERTY );
	}

	//@Override
	public String getCurrentLocation() {
		return (String) underlyingNode.getProperty( CURRENT_LOCATION_PROPERTY );
	}

	//@Override
	public String getFirstName() {
		return (String) underlyingNode.getProperty( FIRST_NAME_PROPERTY);
	}

	//@Override
	public String getHometown() {
		return (String) underlyingNode.getProperty( HOMETOWN_PROPERTY);
	}

	//@Override
	public String getLastName() {
		return (String) underlyingNode.getProperty( LAST_NAME_PROPERTY );
	}

	//@Override
	public String getPolitical() {
		return (String) underlyingNode.getProperty( POLITICAL_PROPERTY);
	}

	//@Override
	public String getReligion() {
		return (String) underlyingNode.getProperty( RELIGION_PROPERTY );
	}

	//@Override
	public String getGender() {
		return (String) underlyingNode.getProperty( GENDER_PROPERTY );
	}

	//@Override
	public void setBirthday(String birthday) {
		underlyingNode.setProperty( BIRTHDAY_PROPERTY, birthday );
	}

	//@Override
	public void setCurrentLocation(String currentLocation) {
		underlyingNode.setProperty( CURRENT_LOCATION_PROPERTY, currentLocation );
	}

	//@Override
	public void setFirstName(String firstName) {
		underlyingNode.setProperty( FIRST_NAME_PROPERTY, firstName );
	}

	//@Override
	public void setHometown(String hometown) {
		underlyingNode.setProperty( HOMETOWN_PROPERTY, hometown );
	}

	//@Override
	public void setLastName(String lastName) {
		underlyingNode.setProperty( LAST_NAME_PROPERTY, lastName );
	}

	//@Override
	public void setPolitical(String political) {
		underlyingNode.setProperty( POLITICAL_PROPERTY, political );
	}

	//@Override
	public void setReligion(String religion) {
		underlyingNode.setProperty( RELIGION_PROPERTY, religion );	
	}

	//@Override
	public void setGender(String gender) {
		underlyingNode.setProperty( GENDER_PROPERTY, gender );
	}
}
