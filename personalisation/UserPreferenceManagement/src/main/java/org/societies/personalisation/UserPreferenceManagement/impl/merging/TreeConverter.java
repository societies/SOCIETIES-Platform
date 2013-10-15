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
package org.societies.personalisation.UserPreferenceManagement.impl.merging;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.personalisation.model.IAction;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IQualityofPreference;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.societies.personalisation.preference.api.model.QualityofPreference;

/**
 * @author Elizabeth
 *
 */
public class TreeConverter {


	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	public IPreference convertToPreferenceTree(DefaultMutableTreeNode root){
		IPreference pref = new PreferenceTreeNode();
		pref = convertUserObject(root);
		return this.convertToPreferenceTree(pref, root);
	}
	public IPreference convertToPreferenceTree(IPreference pref,DefaultMutableTreeNode node){
		
		
		Enumeration<DefaultMutableTreeNode> children = node.children();
		
		while (children.hasMoreElements()){
			DefaultMutableTreeNode child = children.nextElement();
			IPreference temp = this.convertUserObject(child);
			pref.add(temp);
			//log("Level: "+temp.getLevel());
			//log("Depth: "+temp.getDepth());
			 convertToPreferenceTree(temp, child);
			 
		}
		if(this.logging.isDebugEnabled()){
			this.logging.debug("Converted tree:");
		}
		Enumeration<IPreference> e = pref.postorderEnumeration();
		
		while (e.hasMoreElements()){
			IPreference p = e.nextElement();
			if(this.logging.isDebugEnabled()){
				this.logging.debug(p.toString());
			}
		}
		return pref;
	}
	
	private PreferenceTreeNode convertUserObject(DefaultMutableTreeNode node){
		
		PreferenceTreeNode n = new PreferenceTreeNode();
		
		if (node.getUserObject()==null){
			//log("Null and is leaf: "+node.isLeaf());
			return n;
		}
		if (node.getUserObject() instanceof IPreferenceCondition){
			//log("Instance of Condition: "+node.getUserObject().toString()+" and isLeaf: "+node.isLeaf());
			n.setUserObject(node.getUserObject());
			return n;
		}
		
		if (node.getUserObject() instanceof IAction){
			
			IAction a = (IAction) node.getUserObject();
			//log("Instance of IAction: "+a.toString()+" and isleaf: "+node.isLeaf());
			PreferenceOutcome outcome = new PreferenceOutcome(a.getServiceID(), a.getServiceType(), a.getparameterName(), a.getvalue());
			outcome.setConfidenceLevel(51);
			outcome.setServiceID(a.getServiceID());
			outcome.setServiceType(a.getServiceType());
			IQualityofPreference qop = new QualityofPreference();
			outcome.setQualityofPreference(qop);
			n.setUserObject(outcome);
			return n;
		}
		
		return n;
	}
	
	
	
/*	public static void main(String[] args) throws IOException{
		TreeConverter tc = new TreeConverter();
		
		DefaultMutableTreeNode leaf = new DefaultMutableTreeNode();
		Action a = new Action("key","value");
		leaf.setUserObject(a);

		DefaultMutableTreeNode nodeB = new DefaultMutableTreeNode();
		IPreferenceCondition con = tc.getSampleCondition("loc",OperatorConstants.EQUALS,"home");
		nodeB.setUserObject(con);
		nodeB.add(leaf);
		
		DefaultMutableTreeNode leaf2 = new DefaultMutableTreeNode();
		Action a2 = new Action("key2", "value2");
		leaf2.setUserObject(a2);
		
		DefaultMutableTreeNode nodeC = new DefaultMutableTreeNode();
		IPreferenceCondition con2 = tc.getSampleCondition("loc", OperatorConstants.EQUALS, "work");
		nodeC.setUserObject(con2);
		nodeC.add(leaf2);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		root.add(nodeB);
		root.add(nodeC);
		print(root);
		IPreference p = tc.convertToPreferenceTree(root);
		//print((DefaultMutableTreeNode) p);
		log(p.toString());
	}
	
	IPreferenceCondition getSampleCondition(String type, OperatorConstants op, String value){
		return new TemporaryCondition(type, op, value);
	}
	
	static void print (DefaultMutableTreeNode node ){
		Enumeration<DefaultMutableTreeNode> children = node.children();
		log(node.toString());
		log(node.getDepth());
		while (children.hasMoreElements()){
			print(children.nextElement());
		}
	}
	 class TemporaryCondition implements IPreferenceCondition{

		private String type;
		private String value;
		private OperatorConstants oper;
		public TemporaryCondition(String type, OperatorConstants op, String value){
			this.type = type;
			this.value = value;
			this.oper = op;
		}
		
		public String toString(){
			return type+" == "+value;
		}
		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#equals(org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition)
		 
		@Override
		public boolean equals(IPreferenceCondition arg0) {
			return true;
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#equalsIgnoreValue(org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition)
		 
		@Override
		public boolean equalsIgnoreValue(IPreferenceCondition arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#getCtxIdentifier()
		 
		@Override
		public CtxAttributeIdentifier getCtxIdentifier() {
			// TODO Auto-generated method stub
			return null;
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#getType()
		 
		@Override
		public String getType() {
			return this.type;
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#getname()
		 
		@Override
		public String getname() {
			// TODO Auto-generated method stub
			return null;
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#getoperator()
		 
		@Override
		public OperatorConstants getoperator() {
			return this.oper;
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#getvalue()
		 
		@Override
		public String getvalue() {
			return this.value;
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#setCtxIdentifier(org.personalsmartspace.cm.model.api.pss3p.CtxAttributeIdentifier)
		 
		@Override
		public void setCtxIdentifier(CtxAttributeIdentifier arg0) {
			// TODO Auto-generated method stub
			
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#setname(java.lang.String)
		 
		@Override
		public void setname(String arg0) {
			
			
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#setoperator(org.personalsmartspace.pm.prefmodel.api.platform.OperatorConstants)
		 
		@Override
		public void setoperator(OperatorConstants arg0) {
			this.oper = arg0;
			
		}

		 (non-Javadoc)
		 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceCondition#setvalue(java.lang.String)
		 
		@Override
		public void setvalue(String arg0) {
			this.value = arg0;
			
		}
		
	}*/
}
