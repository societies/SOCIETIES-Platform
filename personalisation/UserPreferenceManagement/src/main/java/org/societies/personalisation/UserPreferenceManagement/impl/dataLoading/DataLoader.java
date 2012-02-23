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
package org.societies.personalisation.UserPreferenceManagement.impl.dataLoading;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;

public class DataLoader{
	
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final ICtxBroker broker;
	
	
	public DataLoader(ICtxBroker broker){
		this.broker = broker;
		
	}
	
	

	
	public void loadDataInContext(){
        if (null==broker){
        	this.log("PM: Could not get instance of CtxBroker. Not loading any preferences");
        	return;
        }
        try{
        	Future<CtxEntity> futureEntPerson = broker.createEntity("Person");
	        CtxEntity entPerson = futureEntPerson.get();
			this.logging.info("created Person entity:"+entPerson.toString());
			Future<CtxAssociation> futureAssoc = broker.createAssociation("hasPreferences"); 
			CtxAssociation assoc = futureAssoc.get();
			this.logging.info("created Assoc hasPreferences:"+assoc.toString());
			
			Future<CtxEntity> futureEntPref = broker.createEntity("Preference");
			CtxEntity entPref = futureEntPref.get();
			this.logging.info("created Entity Preference:"+entPref.toString());
			assoc.setParentEntity(entPerson.getId());
			assoc.addEntity(entPref.getId());
			assoc = (CtxAssociation) broker.update(assoc);
			this.logging.info("updated assoc hasPreferences: "+assoc.toString());
			Future<CtxAttribute> futureAttr =broker.createAttribute(entPref.getId(), "serviceTypeserviceIDprefName"); 
			CtxAttribute attr = futureAttr.get();
			attr.setBinaryValue(this.toByteArray(this.getASamplePreference()));
			this.logging.info("created attr :"+attr.toString());
        
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: replace info with debug
 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private PreferenceTreeNode getASamplePreference(){
		String ctxId = "loc";
		OperatorConstants op = OperatorConstants.EQUALS;
		String value = "work";
		//String type = "context";
		String name = "location";
		
		try{
			Future<List<CtxIdentifier>> futureAttrList = broker.lookup(CtxModelType.ATTRIBUTE, name);
			List<CtxIdentifier> attrList = futureAttrList.get();
			if (attrList.size()>0){
				CtxIdentifier id = (CtxIdentifier) attrList.get(0);
				ContextPreferenceCondition pc = new ContextPreferenceCondition((CtxAttributeIdentifier) id, op, value, name);
				PreferenceTreeNode ptn = new PreferenceTreeNode(pc);
				PreferenceOutcome po = new PreferenceOutcome("volume","50");
				PreferenceTreeNode leaf = new PreferenceTreeNode(po);
				ptn.add(leaf);
			
				ctxId = "activity";
				
				value = "busy";
				
				name = "Activity";
				
				Future<List<CtxIdentifier>> futureAttrs = broker.lookup(CtxModelType.ATTRIBUTE, name); 
				List<CtxIdentifier> attrs = futureAttrs.get();
				if (attrs.size()>0){
					CtxIdentifier id1 = (CtxIdentifier) attrs.get(0);
					pc = new ContextPreferenceCondition((CtxAttributeIdentifier) id1, op, value, name);
					PreferenceTreeNode ptn1 = new PreferenceTreeNode(pc);
					ptn1.add(ptn);
					return ptn1;
				}
				return ptn;
			}
		}
		catch(CtxException ce){
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}
		
	
	
	private byte[] toByteArray(Object obj){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush(); 
			oos.close(); 
			bos.close();
			this.logging.debug("Trying to store preference of size: "+bos.size());
			return bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
}

