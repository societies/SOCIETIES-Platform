/**
 * Copyright (c) 2011, SOCIETIES Consortium
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;

/**
 * @author Elizabeth
 * 
 */
public class PreferenceRetriever {
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxBroker broker; 

	public PreferenceRetriever(ICtxBroker broker){
		this.broker = broker;
	}
	
	public Registry retrieveRegistry(Identity dpi){
		try {
			Future<List<CtxIdentifier>> futureAttrList = broker.lookup(CtxModelType.ATTRIBUTE, "PREFERENCE_REGISTRY");
			List<CtxIdentifier> attrList = futureAttrList.get();
			if (null!=attrList){
				if (attrList.size()>0){
					CtxIdentifier identifier = attrList.get(0);
					CtxAttribute attr = (CtxAttribute) broker.retrieve(identifier);
					Object obj = this.convertToObject(attr.getBinaryValue());
					
					if (obj==null){
						this.logging.debug("PreferenceRegistry not found in DB for dpi:"+dpi.toString()+". Creating new registry");
						return new Registry();
					}else{
						if (obj instanceof Registry){
							this.logging.debug("PreferenceRegistry found in DB for dpi:"+dpi.toString());
							return (Registry) obj;
						}else{
							return new Registry();
						}
					}
				}
				this.logging.debug("PreferenceRegistry not found in DB for dpi:"+dpi.toString()+". Creating new registry");
				return new Registry();
			}
			this.logging.debug("PreferenceRegistry not found in DB for dpi:"+dpi.toString()+". Creating new registry");
			return new Registry();
		} catch (CtxException e) {
			this.logging.debug("Exception while loading PreferenceRegistry from DB for dpi:"+dpi.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Registry();
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
	public IPreferenceTreeModel retrievePreference(CtxIdentifier id){
		try{
			//retrieve directly the attribute in context that holds the preference as a blob value
			CtxAttribute attrPref = (CtxAttribute) broker.retrieve(id);
			//cast the blob value to type IPreference and return it
			Object obj = this.convertToObject(attrPref.getBinaryValue());
			if (null!=obj){
				if (obj instanceof IPreferenceTreeModel){
					return (IPreferenceTreeModel) obj;
				}
			}
		}
		catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//returns null if no preference is found in the database.
		return null;
	}
	
	
	
	

	

}

