/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel.impl;


import org.neo4j.graphdb.Node;
import org.societies.personalisation.socialprofiler.datamodel.SocialPage;
import org.societies.personalisation.socialprofiler.datamodel.utils.NodeProperties;

public class SocialPageImpl implements SocialPage, NodeProperties {

	private final Node underlyingNode;
	
	
	
	/**
	 * contructor of FanPage
	 * @param underlyingNode
	 */
	public SocialPageImpl(Node underlyingNode) {
		super();
		this.underlyingNode = underlyingNode;
	}

	/**
	 * returns the underlying node of a fanpage
	 * @return Node underlying node
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
	public String getRealName() {
		return (String) underlyingNode.getProperty( REAL_NAME_PROPERTY );
	}

	//@Override
	public String getType() {
		return (String) underlyingNode.getProperty( TYPE_PROPERTY );
	}

	//@Override
	public void setRealName(String realName) {
		underlyingNode.setProperty( REAL_NAME_PROPERTY, realName );
		
	}

	//@Override
	public void setType(String type) {
		underlyingNode.setProperty( TYPE_PROPERTY, type );
		
	}

}
