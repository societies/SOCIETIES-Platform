package org.societies.privacytrust.privacyprotection.api.model.preference;


import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @author Elizabeth
 *
 */
public class PrivacyPreference extends DefaultMutableTreeNode implements IPrivacyPreference {

	public PrivacyPreference(){
		super();
	}
	
	public PrivacyPreference(IPrivacyPreferenceCondition condition){
		super(condition);
	}
	
	public PrivacyPreference(IPrivacyOutcome outcome){
		super(outcome, false);
	}
	

	@Override
	public void add(IPrivacyPreference p) {
		super.add(p);
		
	}


	@Override
	public Enumeration<IPrivacyPreference> breadthFirstEnumeration() {
		return super.breadthFirstEnumeration();
	}


	@Override
	public Enumeration<IPrivacyPreference> depthFirstEnumeration() {
		return super.depthFirstEnumeration();
	}


	@Override
	public IPrivacyPreferenceCondition getCondition() {
		if (this.isLeaf()){
			return null;
		}
		return (IPrivacyPreferenceCondition) this.userObject;
	}


	@Override
	public int getDepth() {
		return super.getDepth();
	}


	@Override
	public int getLevel() {
		return super.getLevel();
	}


	@Override
	public IPrivacyOutcome getOutcome() {
		if (this.isBranch()){
			return null;
		}
		return (IPrivacyOutcome) this.userObject;
	}


	@Override
	public IPrivacyPreference getRoot() {
		// TODO Auto-generated method stub
		return (IPrivacyPreference) super.getRoot();
	}


	@Override
	public Object getUserObject() {
		// TODO Auto-generated method stub
		return super.getUserObject();
	}


	@Override
	public Object[] getUserObjectPath() {
		// TODO Auto-generated method stub
		return super.getUserObjectPath();
	}


	@Override
	public boolean isBranch() {
		return (this.getUserObject() instanceof IPrivacyPreferenceCondition);
	}


	@Override
	public boolean isLeaf() {
		return (this.getUserObject() instanceof IPrivacyOutcome);
	}


	@Override
	public void remove(IPrivacyPreference p) {
		super.remove(p);

	}

	@Override
	public void removeFromParent() {
		super.removeFromParent();

	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		super.setParent(newParent);

	}

	@Deprecated
	public void setUserObject(Object object){

	}

	@Override
	public Enumeration children() {
		return super.children();
	}

	@Override
	public boolean getAllowsChildren() {
		if (this.userObject instanceof IPrivacyOutcome){
			return false;
		}
		return true;
	}

	@Override
	public int getChildCount() {
		// TODO Auto-generated method stub
		return super.getChildCount();
	}

	@Override
	public int getIndex(TreeNode node) {
		return super.getIndex(node);
	}

	@Override
	public TreeNode getParent() {
		return super.getParent();
	}
	
	@Override
	public Enumeration<IPrivacyPreference> preorderEnumeration(){
		return super.preorderEnumeration();
	}

}
