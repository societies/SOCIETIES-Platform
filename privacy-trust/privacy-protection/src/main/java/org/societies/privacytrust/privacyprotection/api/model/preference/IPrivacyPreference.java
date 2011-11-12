package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
/**
 * @author Elizabeth
 *
 */
public interface IPrivacyPreference extends MutableTreeNode, Serializable{
	public IPrivacyPreferenceCondition getCondition();
	public IPrivacyOutcome getOutcome();
	public boolean isLeaf();
	public boolean isBranch();
	public Object[] getUserObjectPath();
	public Object getUserObject();
	public void add(IPrivacyPreference p);
	public void remove(IPrivacyPreference p);
	public Enumeration<IPrivacyPreference> depthFirstEnumeration();
	public Enumeration<IPrivacyPreference> breadthFirstEnumeration();
	public Enumeration<IPrivacyPreference> postorderEnumeration();
	public Enumeration<IPrivacyPreference> preorderEnumeration();
	public IPrivacyPreference getRoot();
	public int getLevel();
	public int getDepth();
}
