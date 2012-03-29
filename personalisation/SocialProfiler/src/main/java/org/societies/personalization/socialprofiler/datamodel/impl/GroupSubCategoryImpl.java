/**
 * 
 */
package org.societies.personalization.socialprofiler.datamodel.impl;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.societies.personalization.socialprofiler.datamodel.GroupSubCategory;
import org.societies.personalization.socialprofiler.datamodel.NodeProperties;

/**
 * @author X0145160
 *
 */
public class GroupSubCategoryImpl implements GroupSubCategory,NodeProperties {

	private static final Logger logger = Logger.getLogger(GroupSubCategoryImpl.class);
	private final Node underlyingNode;
	
	
	
	/**
	 * contructor of GroupSubCategory
	 * @param underlyingNode
	 */
	public GroupSubCategoryImpl(Node underlyingNode) {
		super();
		this.underlyingNode = underlyingNode;
	}

	/**
	 * returns the underlying node of the group sub category
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
