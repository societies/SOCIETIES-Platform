package org.societies.personalisation.preference.api;

import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;

/**
 * Interface that represents a preference object. This is used in the Preference
 * Manager
 * @see MutableTreeNode
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
public interface IPreference extends MutableTreeNode, Serializable {
	/**
	 * Method to return the condition included in this node
	 * @return the condition object if this node is a branch, null otherwise 
	 */
	public IPreferenceCondition getCondition();
	/**
	 * Method to return the outcome included in this node
	 * @return	the outcome object if this node is a leaf, null otherwise
	 */
	public IPreferenceOutcome getOutcome();
	/**
	 * Method that checks if this preference has an outcome or a condition object included
	 * @return  true if it has an outcome, false if it has a condition
	 */
	public boolean isLeaf();
	/**
	 * Method that checks if this preference has an outcome or a condition object included
	 * 
	 * @return true if it has a condition, false if it has an outcome
	 */
	public boolean isBranch();
	/**
	 * Method that returns an array of Objects that can be a {@link IPreferenceCondition} objects or {@link IPreferenceOutcome} objects
	 * from the root of the tree to this node
	 * @see DefaultMutableTreeNode#getUserObjectPath()
	 * @return an array of objects that can be IPreferenceCondition objects or IPreferenceOutcome objects
	 */
	public Object[] getUserObjectPath();
	
	/**
	 * Returns the object included in this node. It can be a {@link IPreferenceCondition} or a {@link IPreferenceOutcome}
	 * @return the object included in this node
	 */
	public Object getUserObject();
	
	/**
	 * Method to add a child preference node to this node
	 * @param p	the preference node to add to this node
	 * 
	 */
	public void add(IPreference p);
	
	/**
	 * Method to remove a preference node child from this node
	 * @param p		the preference node to remove from this node
	 */
	public void remove(IPreference p);
	
	/**
	 * @see DefaultMutableTreeNode#depthFirstEnumeration()
	 * @return an enumeration of IPreference node objects traversed in depth-first order
	 */
	public Enumeration<IPreference> depthFirstEnumeration();
	
	/**
	 * @see DefaultMutableTreeNode#breadthFirstEnumeration()
	 * @return	an enumeration of IPreference node objects traversed in breadth-first order 
	 */ 
	public Enumeration<IPreference> breadthFirstEnumeration();
	
	/**
	 * Method to return the root node of this Preference tree. @see DefaultMutableTreeNode#getRoot()  
	 * @return	the IPreference root node object
	 */
	public IPreference getRoot();
	
	/**
	 * Method to return the distance from the IPreference root node to this node.
	 * @see DefaultMutableTreeNode#getLevel()
	 * @return  If this node is the root, returns 0. 
	 */
	public int getLevel();
	
	/**
	 * Method to return the depth of the tree rooted at this node -- the longest distance from this node to a leaf.
	 * @see DefaultMutableTreeNode#getDepth()
	 * @return  If this node has no children, returns 0.
	 */
	public int getDepth();
	
	/**
	 * @see DefaultMutableTreeNode#preorderEnumeration()
	 * @return	an enumeration of IPreference node objects traversed in pre-order
	 */
	public Enumeration<IPreference> preorderEnumeration();
	
	/**
	 * @see DefaultMutableTreeNode#postorderEnumeration()
	 * @return	an enumeration of IPreference node objects traversed in post-order
	 */
	public Enumeration<IPreference> postorderEnumeration();
	
	/**
	 * Method used for printing the tree rooted at this node
	 * @return	a string representation of the Tree rooted at this node (preorder enumeration)
	 */
	public String toTreeString();
}

