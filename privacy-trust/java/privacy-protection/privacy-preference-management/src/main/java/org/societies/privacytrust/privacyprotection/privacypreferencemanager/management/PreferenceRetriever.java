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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.management;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPrivacyPreferenceTreeModelBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.RegistryBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.util.PrivacyPreferenceUtils;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;

/**
 * @author Elizabeth
 * 
 */
public class PreferenceRetriever {
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxBroker ctxBroker;
	private final IIdentityManager idMgr; 

	public PreferenceRetriever(ICtxBroker ctxBroker, IIdentityManager idMgr){
		this.ctxBroker = ctxBroker;
		this.idMgr = idMgr;
	}
	
	public Registry retrieveRegistry(){
		try {
			Future<List<CtxIdentifier>> futureAttrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY); 
			List<CtxIdentifier> attrList = futureAttrList.get();
			if (null!=attrList){
				if (attrList.size()>0){
					CtxIdentifier identifier = attrList.get(0);
					CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier).get();
					Object obj = this.convertToObject(attr.getBinaryValue());
					
					if (obj==null){
						this.logging.debug("PreferenceRegistry not found in DB. Creating new registry");
						return new Registry();
					}
					
					if (obj instanceof RegistryBean){
						this.logging.debug("PreferenceRegistry found in DB ");
						Registry registry = Registry.fromBean((RegistryBean) obj, idMgr);
						return registry;
					}else{
						this.logging.debug("PreferenceRegistry not found in DB. Creating new registry");
						return new Registry();
					}
				}
				this.logging.debug("PreferenceRegistry not found in DB. Creating new registry");
				return new Registry();
			}
			this.logging.debug("PreferenceRegistry not found in DB. Creating new registry");
			return new Registry();
		} catch (CtxException e) {
			this.logging.debug("Exception while loading PreferenceRegistry from DB ");
			e.printStackTrace();
			return new Registry();
		} catch (InterruptedException e) {
			this.logging.debug("Exception while loading PreferenceRegistry from DB ");
			e.printStackTrace();
			return new Registry();
		} catch (ExecutionException e) {
			this.logging.debug("Exception while loading PreferenceRegistry from DB ");
			e.printStackTrace();
			return new Registry();
		}
	}
	
	private Object convertToObject(byte[] byteArray){
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteArray));
			Object obj = ois.readObject();
			return obj;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * retrieves a preference object using that preference object's context identifier to find it
	 * @param id
	 * @return
	 */
	public IPrivacyPreferenceTreeModel retrievePreference(CtxIdentifier id){
		try{
			//retrieve directly the attribute in context that holds the preference as a blob value
			CtxAttribute attrPref = (CtxAttribute) ctxBroker.retrieve(id).get();
			//cast the blob value to type IPreference and return it
			Object obj = this.convertToObject(attrPref.getBinaryValue());
			if (null!=obj){
				if (obj instanceof IPrivacyPreferenceTreeModel){
					return (IPrivacyPreferenceTreeModel) obj;
				}
				
				
				if (obj instanceof PPNPrivacyPreferenceTreeModelBean){
					return PrivacyPreferenceUtils.toPPNPrivacyPreferenceTreeModel((PPNPrivacyPreferenceTreeModelBean) obj, this.idMgr);
				}
				
				if (obj instanceof IDSPrivacyPreferenceTreeModelBean){
					return PrivacyPreferenceUtils.toIDSPrivacyPreferenceTreeModel((IDSPrivacyPreferenceTreeModelBean) obj, idMgr);
				}
				
				if (obj instanceof DObfPrivacyPreferenceTreeModelBean){
					return PrivacyPreferenceUtils.toDObfPreferenceTreeModel((DObfPrivacyPreferenceTreeModelBean) obj, idMgr);
				}
				
				if (obj instanceof AccessControlPreferenceTreeModelBean){
					return PrivacyPreferenceUtils.toAccCtrlPreferenceTreeModel((AccessControlPreferenceTreeModelBean) obj, idMgr);
				}
			}
		}
		catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//returns null if no preference is found in the database.
		return null;
	}
	
	
	
	

	

}

