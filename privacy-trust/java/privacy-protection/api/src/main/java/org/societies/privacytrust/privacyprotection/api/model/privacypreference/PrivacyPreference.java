/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.privacytrust.privacyprotection.api.model.privacypreference;


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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrivacyPreference \n[userObject=");
		builder.append(userObject);
		builder.append("\n, children=");
		builder.append(children);
		builder.append("]");
		return builder.toString();
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
