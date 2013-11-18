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
package org.societies.personalisation.UserPreferenceManagement.impl.management;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.personalisation.model.PreferenceTreeModelBean;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.util.PreferenceUtils;

/**
 * @author Elizabeth
 * 
 */
public class PreferenceRetriever {

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxBroker ctxBroker; 

	public PreferenceRetriever(ICtxBroker broker){
		this.ctxBroker = broker;
	}

	public Registry retrieveRegistry(){
		try {
			Future<List<CtxIdentifier>> futureAttrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, "PREFERENCE_REGISTRY");
			if (futureAttrList==null){
				return new Registry();
			}
			List<CtxIdentifier> attrList = futureAttrList.get();
			if (null!=attrList){
				if (attrList.size()>0){
					CtxIdentifier identifier = attrList.get(0);
					CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier).get();

					Registry registry = (Registry) SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
					if (null==registry){
						if(this.logging.isDebugEnabled()){
							this.logging.debug("Error retrieving binary value from attribute");
						}
						return new Registry();
					}

					return registry;

				}
				if(this.logging.isDebugEnabled()){
					this.logging.debug("PreferenceRegistry not found in DB. Creating new registry");
				}
				return new Registry();
			}
			if(this.logging.isDebugEnabled()){
				this.logging.debug("PreferenceRegistry not found in DB. Creating new registry");
			}
			return new Registry();
		} catch (CtxException e) {
			if(this.logging.isDebugEnabled()){
				this.logging.debug("Exception while loading PreferenceRegistry from DB");
			}
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Registry();
	}


	private Object convertToObject(byte[] byteArray){
		try {
			return SerialisationHelper.deserialise(byteArray, this.getClass().getClassLoader());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*
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
		 */


		return null;
	}


	/*
	 * retrieves a preference object using that preference object's context identifier to find it
	 * @param id
	 * @return
	 */
	public IPreferenceTreeModel retrievePreference(CtxIdentifier id){
		try{
			//retrieve directly the attribute in context that holds the preference as a blob value
			CtxAttribute attrPref = (CtxAttribute) ctxBroker.retrieve(id).get();
			
			if (attrPref==null){
				if (this.logging.isDebugEnabled()){
					this.logging.debug("Retrieve: "+id.toUriString()+" not found in DB");
				}
				return null;
			}else{
				if (this.logging.isDebugEnabled()){
					this.logging.debug("Length of byte array retrieved : "+attrPref.getBinaryValue().length);
				}
			}



			//PreferenceTreeModelBean modelBean = (PreferenceTreeModelBean) SerialisationHelper.deserialise(attrPref.getBinaryValue(), this.getClass().getClassLoader());
			PreferenceTreeModel model = (PreferenceTreeModel) SerialisationHelper.deserialise(attrPref.getBinaryValue(), this.getClass().getClassLoader());
/*			if (this.logging.isDebugEnabled()){
				if (modelBean == null){
					this.logging.debug("Deserialised modelBean is null");
				}else{
					this.logging.debug("Deserialised modelBean is NOT null");

					if (modelBean.getPreference()==null){
						this.logging.debug("Deserialised modelBean.getPreference() is null");
					}else{
						this.logging.debug("Deserialised modelBean.getPreference is NOT null");
						this.logging.debug("Deserialised modelBean.getPreference has "+modelBean.getPreference().getChildren().size()+" children");
					}
					
					if (modelBean.getPreferenceDetails() == null){
						this.logging.debug("Deserialised modelBean.getPreferenceDetails is null");
					}else{
						this.logging.debug("Deserialised modelBean.getPreferenceDetails is NOT null");
					}
				}
			}*/
			//PreferenceTreeModel model = PreferenceUtils.toPreferenceTreeModel(modelBean);
			if (this.logging.isDebugEnabled()){
				if (model == null){
					this.logging.debug("Deserialised model is null");
				}else{
					this.logging.debug("Deserialised model is NOT null");

					if (model.getRootPreference()==null){
						this.logging.debug("Deserialised model.getRootPreference() is null");
					}else{
						this.logging.debug("Deserialised model.getRootPreference is NOT null");
						this.logging.debug("Deserialised model.getRootPreference has: "+model.getChildCount(model.getRootPreference())+" children");
					}
					
					if (model.getPreferenceDetails() == null){
						this.logging.debug("Deserialised model.getPreferenceDetails is null");
					}else{
						this.logging.debug("Deserialised model.getPreferenceDetails is NOT null");
					}
				}
			}			
			return model;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//returns null if no preference is found in the database.
		return null;
	}







}

