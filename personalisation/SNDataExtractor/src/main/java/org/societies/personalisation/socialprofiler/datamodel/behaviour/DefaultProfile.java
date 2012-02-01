package org.societies.personalisation.socialprofiler.datamodel.behaviour;

import org.neo4j.graphdb.Node;
import org.societies.personalisation.socialprofiler.datamodel.NodeProperties;

public class DefaultProfile implements Profile, NodeProperties{

	private final Node underlyingNode;
	private final int id;
	
	public DefaultProfile(Node underlyingNode) {
		super();
		this.underlyingNode = underlyingNode;
		this.id =ProfileUtils.DEFAULT_PROFILE_ID;
		underlyingNode.setProperty( NAME_PROPERTY, ProfileUtils.DEFAULT_PROFILE_NAME );
		
	}

	
	/**
	 * returns the underlyong node of the profile
	 * @return	Node underlyingNode
	 */
	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	
	@Override
	public void getName(String name) {
		underlyingNode.setProperty( NAME_PROPERTY, name );
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
	public int getId() {
		return this.id;
	}

	@Override
	public int getFrequency() {
		return Integer.parseInt(underlyingNode.getProperty( FREQUENCY_PROPERTY ).toString());
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
		return (Integer)underlyingNode.getProperty( NUMBER_PROPERTY );
	}

	@Override
	public void setNumer(int number) {
		underlyingNode.setProperty(NAME_PROPERTY, number);
	}

	@Override
	public void setFrequency(int frequency) {
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
