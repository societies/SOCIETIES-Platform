/**
 * 
 */
package org.societies.personalization.socialprofiler.datamodel.impl;


import org.neo4j.graphdb.Node;
import org.societies.personalization.socialprofiler.datamodel.NodeProperties;
import org.societies.personalization.socialprofiler.datamodel.PageCategory;


public class PageCategoryImpl implements PageCategory, NodeProperties {

	private final Node underlyingNode;
	
	
	
	/**
	 * contructor of FanPagecategory
	 * @param underlyingNode
	 */
	public PageCategoryImpl(Node underlyingNode) {
		super();
		this.underlyingNode = underlyingNode;
	}

	/**
	 * returns the underlying node of a fanpage category
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
	
}
