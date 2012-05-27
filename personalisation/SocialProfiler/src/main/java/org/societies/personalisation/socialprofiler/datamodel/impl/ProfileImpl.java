package org.societies.personalisation.socialprofiler.datamodel.impl;


import org.neo4j.graphdb.Node;
import org.societies.personalisation.socialprofiler.datamodel.behaviour.Profile;
import org.societies.personalisation.socialprofiler.datamodel.behaviour.ProfileUtils;
import org.societies.personalisation.socialprofiler.datamodel.utils.NodeProperties;

public class ProfileImpl implements Profile, NodeProperties{

	private final Node underlyingNode;
	private final Profile.Type type;
	
	public ProfileImpl(Node underlyingNode, Profile.Type type, String name) {
		super();
		this.underlyingNode = underlyingNode;
		this.type = type;
		setName(name);
	}

	
	/**
	 * returns the underlying node of the profile
	 * @return	Node underlyingNode
	 */
	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	
	@Override
	public void setName(String name) {
		underlyingNode.setProperty(NAME_PROPERTY, name );
	}
	
	@Override
	public String getName() {
		return (String) underlyingNode.getProperty( NAME_PROPERTY );
	}

	@Override
	public String getDescription() {
		return (String) underlyingNode.getProperty( DESCR_PROPERTY );
	}

	@Override
	public Profile.Type getType() {
		return this.type;
	}

	@Override
	public long getFrequency() {
		try {
			return Long.parseLong(underlyingNode.getProperty( FREQUENCY_PROPERTY ).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	
	@Override
	public void updateFrequency(int numberOfActions) {
		// to be done ....s
	}

	@Override
	public String getLastTime() {
		return (String) underlyingNode.getProperty( LAST_TIME_PROPERTY );
	}

	@Override
	public void setLastTime(String lastTime) {
		underlyingNode.setProperty( LAST_TIME_PROPERTY,lastTime );
	}

	@Override
	public int getNumber() {
		try {
			return Integer.parseInt(underlyingNode.getProperty( NUMBER_PROPERTY).toString());
		} catch (Exception e){
			return 0;
		}
	}

	@Override
	public void setNumber(String number) {
		underlyingNode.setProperty(NUMBER_PROPERTY, number);
	}

	@Override
	public void setFrequency(String frequency) {
		underlyingNode.setProperty( FREQUENCY_PROPERTY, frequency );
		
	}

	@Override
	public void incrementNumber() {
		underlyingNode.setProperty( NUMBER_PROPERTY, getNumber()+1 );
	}

	@Override
	public void setDescription(String description) {
		underlyingNode.setProperty( LAST_TIME_PROPERTY, description);
	}

	

}
