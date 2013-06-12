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
package org.societies.personalisation.preference.api.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class Util {

	public static void printPreference(IPreferenceTreeModel model){
		System.out.println("*** Preference Details ***");
		System.out.println("Service: "+model.getPreferenceDetails().getServiceID().getIdentifier());
		System.out.println("Service Type: "+model.getPreferenceDetails().getServiceType());
		System.out.println("Preference: "+model.getPreferenceDetails().getPreferenceName());
		IPreference node = model.getRootPreference();
		Enumeration<IPreference> eDepth = node.preorderEnumeration();
		
		while (eDepth.hasMoreElements()){
			IPreference p = eDepth.nextElement();
			printNode(p, p.getLevel());
		}
		
		

	}
	
	private static void printNode(IPreference node, int depth){
		
		if (node.getUserObject() instanceof IPreferenceCondition){
			IPreferenceCondition cond = node.getCondition();
			String s = "";
			for (int i=0; i<depth; i++){
				s = s.concat("\t");
			}
			IPreference parent = (IPreference) node.getParent();
			/*if (null==parent.getUserObject()){
				s = s.concat("IF ");
			}else{
				if (parent.getUserObject() instanceof IPreferenceCondition){
					s = s.concat("AND ");
				}
			}*/
			s = s.concat("IF ");
			s = s.concat(cond.getname()+" "+cond.getoperator()+" "+cond.getvalue());
			System.out.println(s);
			//System.out.println(node.getCondition().toString());
		}else if (node.getUserObject() instanceof IPreferenceOutcome){
			IPreferenceOutcome out = node.getOutcome();
			String s = "";
			for (int i=0; i<depth; i++){
				s = s.concat("\t");
			}
			IPreference parent = (IPreference) node.getParent();
			if (null!=parent.getUserObject()){
				if (parent.getUserObject() instanceof IPreferenceCondition){
					s = s.concat("THEN ");
				}
			}
			s = s.concat(out.getparameterName()+" = "+out.getvalue());
			System.out.println(s);
			
			//System.out.println(node.getOutcome().toString());
		}else{
			if (node.getUserObject()==null){
				if (node.getRoot().equals(node)){
					System.out.println("Empty root node - Split Tree");
				}else{
					System.out.println("Preference is corrupted - Null user object on a non root node");
				}
			}
		}
	}
	
	/**
	 * for testing purposes only
	 */
	private PreferenceTreeNode preference;
	private CtxEntity preferenceEntity;
	private CtxAttribute preferenceAttribute;
	private CtxAttribute statusAttribute;
	private CtxAttribute symLocAttribute;
	private CtxEntity personEntity;
	private ServiceResourceIdentifier serviceId;
	private final String VOLUME = "volume";
	private final String Service_Type = "media";
	
	
	
	protected IPreferenceTreeModel createTestPreference() {
		this.createServiceID();
		this.createPersonEntity();
		this.createStatusAttribute();
		this.createSymLocAttribute();
		IPreferenceOutcome outcome0 = new PreferenceOutcome(this.serviceId, Service_Type, VOLUME, "0");
		IPreferenceOutcome outcome50  = new PreferenceOutcome(this.serviceId, Service_Type, VOLUME, "50");
		IPreferenceOutcome outcome100  = new PreferenceOutcome(this.serviceId, Service_Type, VOLUME, "100");
		System.out.println(this.symLocAttribute.getId().toUriString());
		System.out.println(this.statusAttribute.getId().toUriString());
		
		IPreferenceCondition locationHomeCondition = new ContextPreferenceCondition(this.symLocAttribute.getId(), OperatorConstants.EQUALS, "home", this.symLocAttribute.getType());
		System.out.println(locationHomeCondition.getCtxIdentifier().toUriString());
		IPreferenceCondition locationWorkCondition = new ContextPreferenceCondition(this.symLocAttribute.getId(), OperatorConstants.EQUALS, "work", this.symLocAttribute.getType());
		IPreferenceCondition statusFreeCondition = new ContextPreferenceCondition(this.statusAttribute.getId(), OperatorConstants.EQUALS, "free", this.statusAttribute.getType());
		IPreferenceCondition statusBusyCondition = new ContextPreferenceCondition(this.statusAttribute.getId(), OperatorConstants.EQUALS, "busy", this.statusAttribute.getType());
		
		/*
		 * creating preference:
		 * IF (location==home) AND (status==free)
		 * THEN volume=100
		 * ELSE IF (location==home) AND (status==busy)
		 * THEN volume=50
		 * ELSE IF (location==work) AND (status == free)
		 * THEN volume=50
		 * ELSE IF (location==work) AND (status==busy)
		 * THEN volume=0
		 * ELSE
		 * volume=50
		 */
		
		/*
		 * top node:
		 */
		preference = new PreferenceTreeNode();
		
		/*
		 * condition nodes:
		 */
		PreferenceTreeNode homeLocNode = new PreferenceTreeNode(locationHomeCondition);
		PreferenceTreeNode workLocNode = new PreferenceTreeNode(locationWorkCondition);
		PreferenceTreeNode statusFreeNode = new PreferenceTreeNode(statusFreeCondition);
		PreferenceTreeNode statusBusyNode = new PreferenceTreeNode(statusBusyCondition);
		
		PreferenceTreeNode statusFreeNode1 = new PreferenceTreeNode(statusFreeCondition);
		PreferenceTreeNode statusBusyNode1 = new PreferenceTreeNode(statusBusyCondition);
		/*
		 * IF (location==home) AND (status==free) 
		 * THEN volume=100
		 */
		
		statusFreeNode.add(new PreferenceTreeNode(outcome100));
		homeLocNode.add(statusFreeNode);
		
		/*
		 * ELSE IF (location==home) AND (status==busy)
		 * THEN volume=50
		 */
		statusBusyNode.add(new PreferenceTreeNode(outcome50));
		homeLocNode.add(statusBusyNode);
		
		/*
		 * ELSE IF (location==work) AND (status == free)
		 * THEN volume=50
		 */
		statusFreeNode1.add(new PreferenceTreeNode(outcome50));
		workLocNode.add(statusFreeNode1);
		
		/*
		 * ELSE IF (location==work) AND (status==busy)
		 * THEN volume=0
		 */
		
		statusBusyNode1.add(new PreferenceTreeNode(outcome0));
		workLocNode.add(statusBusyNode1);
		
		/*
		 * ELSE
		 * volume=50
		 */
		this.preference.add(new PreferenceTreeNode(outcome50));
		
		this.preference.add(homeLocNode);
		this.preference.add(workLocNode);
		
		CtxEntityIdentifier ctxPreferenceEntityID = new CtxEntityIdentifier("css://example@domain.com", "PREFERENCE", new Long(1));
		preferenceEntity = new CtxEntity(ctxPreferenceEntityID);
		CtxAttributeIdentifier preferenceAttributeId = new CtxAttributeIdentifier(this.personEntity.getId(), "prefAttribute", new Long(1));
		preferenceAttribute = new CtxAttribute(preferenceAttributeId);
		
		System.out.println(preference.toString());
		PreferenceDetails details = new PreferenceDetails();
		details.setPreferenceName(VOLUME);
		details.setServiceID(serviceId);
		details.setServiceType(Service_Type);
		IPreferenceTreeModel model = new PreferenceTreeModel(details, preference);
		return model;
	}
	
	private void createStatusAttribute() {
		CtxAttributeIdentifier ctxStatusAttributeId = new CtxAttributeIdentifier(this.personEntity.getId(), CtxAttributeTypes.STATUS, new Long(1));
		statusAttribute = new CtxAttribute(ctxStatusAttributeId);
		statusAttribute.setStringValue("free");
		
		
	}

	private void createSymLocAttribute() {
		CtxAttributeIdentifier ctxSymLocationAttributeId = new CtxAttributeIdentifier(this.personEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		symLocAttribute = new CtxAttribute(ctxSymLocationAttributeId);
		symLocAttribute.setStringValue("home");
	}

	private void createPersonEntity() {
		CtxEntityIdentifier ctxPersonId = new CtxEntityIdentifier("css://example@domain.com", "Person", new Long(1));
		personEntity = new CtxEntity(ctxPersonId);
		
	}
	
	private void createServiceID(){
		serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws IOException{
		IPreferenceTreeModel p = new Util().createTestPreference();
		
		Util.printPreference(p);
	}
}
